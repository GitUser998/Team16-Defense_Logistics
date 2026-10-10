package com.defence.maintenance.repository;

import com.defence.maintenance.model.DueAsset;
import com.defence.maintenance.model.MaintenanceSchedule;
import com.defence.maintenance.model.ServiceHistory;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class MaintenanceRepository {

    private final JdbcTemplate jdbc;

    public MaintenanceRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ---------- row mappers ----------

    private final RowMapper<MaintenanceSchedule> scheduleMapper = (rs, i) -> new MaintenanceSchedule(
            rs.getLong("id"),
            rs.getString("asset_type"),
            rs.getLong("asset_id"),
            rs.getString("asset_label"),
            rs.getString("category_or_type"),
            rs.getString("maintenance_type"),
            rs.getString("priority"),
            rs.getObject("scheduled_date", LocalDate.class),
            rs.getString("description"),
            rs.getString("assigned_technician"),
            rs.getString("status"),
            rs.getObject("started_at", LocalDateTime.class),
            rs.getObject("completed_at", LocalDateTime.class));

    private final RowMapper<ServiceHistory> historyMapper = (rs, i) -> new ServiceHistory(
            rs.getLong("id"),
            rs.getLong("maintenance_id"),
            rs.getString("asset_type"),
            rs.getLong("asset_id"),
            rs.getString("asset_label"),
            rs.getObject("performed_on", LocalDate.class),
            rs.getString("work_done"),
            rs.getString("parts_used"),
            rs.getBigDecimal("cost"),
            rs.getString("performed_by"),
            rs.getObject("next_due_date", LocalDate.class));

    // ---------- schedules ----------

    /** Saves a new schedule with status SCHEDULED. Returns the new id. */
    public Long save(MaintenanceSchedule s) {
        String sql = "insert into maintenance_schedule (asset_type, asset_id, asset_label, category_or_type, "
                + "maintenance_type, priority, scheduled_date, description, assigned_technician, status) "
                + "values (?,?,?,?,?,?,?,?,?,'SCHEDULED')";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[] {"id"});
            ps.setString(1, s.assetType());
            ps.setLong(2, s.assetId());
            ps.setString(3, s.assetLabel());
            ps.setString(4, s.categoryOrType());
            ps.setString(5, s.maintenanceType());
            ps.setString(6, s.priority());
            ps.setObject(7, s.scheduledDate());
            ps.setString(8, s.description());
            ps.setString(9, s.assignedTechnician());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public Optional<MaintenanceSchedule> findById(Long id) {
        return jdbc.query("select * from maintenance_schedule where id = ?", scheduleMapper, id)
                .stream().findFirst();
    }

    /** Any filter can be null. */
    public List<MaintenanceSchedule> search(String status, String assetType, LocalDate from, LocalDate to) {
        StringBuilder sql = new StringBuilder("select * from maintenance_schedule where 1=1");
        List<Object> args = new ArrayList<>();
        if (status != null) {
            sql.append(" and status = ?");
            args.add(status);
        }
        if (assetType != null) {
            sql.append(" and asset_type = ?");
            args.add(assetType);
        }
        if (from != null) {
            sql.append(" and scheduled_date >= ?");
            args.add(from);
        }
        if (to != null) {
            sql.append(" and scheduled_date <= ?");
            args.add(to);
        }
        sql.append(" order by scheduled_date, id");
        return jdbc.query(sql.toString(), scheduleMapper, args.toArray());
    }

    /** SCHEDULED items due between today and today + days. */
    public List<MaintenanceSchedule> findUpcoming(int days) {
        return jdbc.query("select * from maintenance_schedule where status = 'SCHEDULED' "
                        + "and scheduled_date between ? and ? order by scheduled_date, id",
                scheduleMapper, LocalDate.now(), LocalDate.now().plusDays(days));
    }

    /** Not finished and the date has passed. */
    public List<MaintenanceSchedule> findOverdue() {
        return jdbc.query("select * from maintenance_schedule where status in ('SCHEDULED','OVERDUE') "
                + "and scheduled_date < ? order by scheduled_date, id", scheduleMapper, LocalDate.now());
    }

    /**
     * Open maintenance that clashes with the window: a SCHEDULED one falling inside it,
     * or one already IN_PROGRESS / OVERDUE (the asset is not free yet).
     */
    public List<MaintenanceSchedule> findConflicts(String assetType, Long assetId, LocalDate from, LocalDate to) {
        return jdbc.query("select * from maintenance_schedule where asset_type = ? and asset_id = ? and ("
                        + " (status = 'SCHEDULED' and scheduled_date between ? and ?)"
                        + " or (status in ('IN_PROGRESS','OVERDUE') and scheduled_date <= ?)) order by scheduled_date",
                scheduleMapper, assetType, assetId, from, to, to);
    }

    public void markStarted(Long id) {
        jdbc.update("update maintenance_schedule set status = 'IN_PROGRESS', started_at = now(), "
                + "updated_at = now() where id = ?", id);
    }

    public void markCompleted(Long id) {
        jdbc.update("update maintenance_schedule set status = 'COMPLETED', completed_at = now(), "
                + "updated_at = now() where id = ?", id);
    }

    public void markCancelled(Long id) {
        jdbc.update("update maintenance_schedule set status = 'CANCELLED', updated_at = now() where id = ?", id);
    }

    /** Daily job step: SCHEDULED items whose date has passed become OVERDUE. Returns how many. */
    public int markOverdue() {
        return jdbc.update("update maintenance_schedule set status = 'OVERDUE', updated_at = now() "
                + "where status = 'SCHEDULED' and scheduled_date < ?", LocalDate.now());
    }

    public Map<String, Long> countByStatus() {
        Map<String, Long> result = new LinkedHashMap<>();
        jdbc.query("select status, count(*) as c from maintenance_schedule group by status",
                rs -> {
                    result.put(rs.getString("status"), rs.getLong("c"));
                });
        return result;
    }

    // ---------- service history ----------

    public void saveHistory(ServiceHistory h) {
        jdbc.update("insert into service_history (maintenance_id, asset_type, asset_id, asset_label, performed_on, "
                        + "work_done, parts_used, cost, performed_by, next_due_date) values (?,?,?,?,?,?,?,?,?,?)",
                h.maintenanceId(), h.assetType(), h.assetId(), h.assetLabel(), h.performedOn(),
                h.workDone(), h.partsUsed(), h.cost(), h.performedBy(), h.nextDueDate());
    }

    public List<ServiceHistory> findHistory(String assetType, Long assetId) {
        return jdbc.query("select * from service_history where asset_type = ? and asset_id = ? "
                + "order by performed_on desc, id desc", historyMapper, assetType, assetId);
    }

    // ---------- rules + preventive job ----------

    /** Interval in days from the rule for this asset type and category, if there is one. */
    public Optional<Integer> findRuleInterval(String assetType, String categoryOrType) {
        return jdbc.query("select interval_days from maintenance_rule where asset_type = ? and category_or_type = ?",
                (rs, i) -> rs.getInt("interval_days"), assetType, categoryOrType).stream().findFirst();
    }

    /**
     * Assets whose latest service has a next_due_date on or before the given date
     * and that have no open maintenance already.
     */
    public List<DueAsset> findDueForPreventive(LocalDate until) {
        String sql = "select h.asset_type, h.asset_id, h.asset_label, h.next_due_date, s.category_or_type "
                + "from service_history h join maintenance_schedule s on s.id = h.maintenance_id "
                + "where h.next_due_date is not null and h.next_due_date <= ? "
                + "and h.id = (select max(h2.id) from service_history h2 "
                + "             where h2.asset_type = h.asset_type and h2.asset_id = h.asset_id) "
                + "and not exists (select 1 from maintenance_schedule o "
                + "                where o.asset_type = h.asset_type and o.asset_id = h.asset_id "
                + "                and o.status in ('SCHEDULED','IN_PROGRESS','OVERDUE'))";
        return jdbc.query(sql, (rs, i) -> new DueAsset(
                rs.getString("asset_type"),
                rs.getLong("asset_id"),
                rs.getString("asset_label"),
                rs.getString("category_or_type"),
                rs.getObject("next_due_date", LocalDate.class)), until);
    }
}
