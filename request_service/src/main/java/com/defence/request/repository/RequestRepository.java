package com.defence.request.repository;

import com.defence.requests.model.LogisticsRequest;
import com.defence.requests.model.RequestItem;
import com.defence.requests.model.StatusHistory;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
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
public class RequestRepository {

    private final JdbcTemplate jdbc;

    public RequestRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ---------- row mappers: turn a database row into a Java object ----------

    private final RowMapper<LogisticsRequest> requestMapper = (rs, i) -> new LogisticsRequest(
            rs.getLong("id"),
            rs.getString("request_no"),
            rs.getLong("requesting_unit_id"),
            rs.getString("requesting_unit_name"),
            rs.getString("request_type"),
            rs.getString("priority"),
            rs.getString("requested_by"),
            rs.getString("justification"),
            rs.getString("source_location"),
            rs.getString("destination_location"),
            rs.getObject("required_from", LocalDateTime.class),
            rs.getObject("required_to", LocalDateTime.class),
            rs.getString("status"),
            rs.getString("approved_by"),
            rs.getObject("approved_at", LocalDateTime.class),
            null);   // items are loaded separately

    private final RowMapper<RequestItem> itemMapper = (rs, i) -> new RequestItem(
            rs.getLong("id"),
            rs.getString("resource_type"),
            rs.getString("category_or_type"),
            rs.getInt("quantity"),
            rs.getObject("resource_id", Long.class),
            rs.getString("assignment_status"));

    private final RowMapper<StatusHistory> historyMapper = (rs, i) -> new StatusHistory(
            rs.getLong("id"),
            rs.getString("old_status"),
            rs.getString("new_status"),
            rs.getString("changed_by"),
            rs.getString("comment"),
            rs.getObject("changed_at", LocalDateTime.class));

    // ---------- insert ----------

    /** Saves the request (status SUBMITTED) and its items. Returns the new id. */
    public Long save(LogisticsRequest r) {
        Long seq = jdbc.queryForObject("select nextval('request_no_seq')", Long.class);
        String requestNo = String.format("REQ-%06d", seq);

        String sql = "insert into logistics_request (request_no, requesting_unit_id, requesting_unit_name, "
                + "request_type, priority, requested_by, justification, source_location, destination_location, "
                + "required_from, required_to, status) values (?,?,?,?,?,?,?,?,?,?,?,'SUBMITTED')";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[] {"id"});
            ps.setString(1, requestNo);
            ps.setLong(2, r.requestingUnitId());
            ps.setString(3, r.requestingUnitName());
            ps.setString(4, r.requestType());
            ps.setString(5, r.priority());
            ps.setString(6, r.requestedBy());
            ps.setString(7, r.justification());
            ps.setString(8, r.sourceLocation());
            ps.setString(9, r.destinationLocation());
            ps.setObject(10, r.requiredFrom());
            ps.setObject(11, r.requiredTo());
            return ps;
        }, keyHolder);
        Long id = keyHolder.getKey().longValue();

        for (RequestItem item : r.items()) {
            jdbc.update("insert into request_item (request_id, resource_type, category_or_type, quantity, resource_id) "
                            + "values (?,?,?,?,?)",
                    id, item.resourceType(), item.categoryOrType(), item.quantity(), item.resourceId());
        }
        return id;
    }

    // ---------- read ----------

    public List<LogisticsRequest> findAll(String status) {
        if (status == null) {
            return jdbc.query("select * from logistics_request order by id desc", requestMapper);
        }
        return jdbc.query("select * from logistics_request where status = ? order by id desc",
                requestMapper, status);
    }

    public Optional<LogisticsRequest> findById(Long id) {
        return jdbc.query("select * from logistics_request where id = ?", requestMapper, id)
                .stream().findFirst();
    }

    public List<RequestItem> findItems(Long requestId) {
        return jdbc.query("select * from request_item where request_id = ? order by id", itemMapper, requestId);
    }

    public List<StatusHistory> findHistory(Long requestId) {
        return jdbc.query("select * from request_status_history where request_id = ? order by changed_at, id",
                historyMapper, requestId);
    }

    public Map<String, Long> countByStatus() {
        Map<String, Long> result = new LinkedHashMap<>();
        jdbc.query("select status, count(*) as c from logistics_request group by status",
                rs -> {
                    result.put(rs.getString("status"), rs.getLong("c"));
                });
        return result;
    }

    // ---------- update ----------

    public void updateStatus(Long id, String newStatus) {
        jdbc.update("update logistics_request set status = ?, updated_at = now() where id = ?", newStatus, id);
    }

    public void setApprover(Long id, String approvedBy) {
        jdbc.update("update logistics_request set approved_by = ?, approved_at = now() where id = ?",
                approvedBy, id);
    }

    public void addHistory(Long requestId, String oldStatus, String newStatus, String changedBy, String comment) {
        jdbc.update("insert into request_status_history (request_id, old_status, new_status, changed_by, comment) "
                + "values (?,?,?,?,?)", requestId, oldStatus, newStatus, changedBy, comment);
    }
}
