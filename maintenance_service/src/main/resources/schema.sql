-- Rules drive preventive maintenance: "every Truck needs service every 90 days"
CREATE TABLE IF NOT EXISTS maintenance_rule (
    id               BIGSERIAL PRIMARY KEY,
    asset_type       VARCHAR(20)  NOT NULL,      -- VEHICLE, EQUIPMENT
    category_or_type VARCHAR(100) NOT NULL,      -- e.g. Truck, Generator
    interval_days    INT          NOT NULL,
    description      TEXT
);

CREATE TABLE IF NOT EXISTS maintenance_schedule (
    id                  BIGSERIAL PRIMARY KEY,
    asset_type          VARCHAR(20)  NOT NULL,   -- VEHICLE, EQUIPMENT
    asset_id            BIGINT       NOT NULL,   -- id in equipment-service or vehicle-service
    asset_label         VARCHAR(100) NOT NULL,   -- snapshot: asset tag or registration no
    category_or_type    VARCHAR(100),            -- used to find the matching rule (small addition to the architecture)
    maintenance_type    VARCHAR(20)  NOT NULL,   -- PREVENTIVE, CORRECTIVE
    priority            VARCHAR(10)  NOT NULL,   -- LOW, MEDIUM, HIGH, URGENT
    scheduled_date      DATE         NOT NULL,
    description         TEXT,
    assigned_technician VARCHAR(100),
    status              VARCHAR(20)  NOT NULL,   -- SCHEDULED, IN_PROGRESS, COMPLETED, OVERDUE, CANCELLED
    started_at          TIMESTAMP,
    completed_at        TIMESTAMP,
    created_at          TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at          TIMESTAMP    NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_sched_asset  ON maintenance_schedule(asset_type, asset_id);
CREATE INDEX IF NOT EXISTS idx_sched_status ON maintenance_schedule(status);

CREATE TABLE IF NOT EXISTS service_history (
    id             BIGSERIAL PRIMARY KEY,
    maintenance_id BIGINT       NOT NULL REFERENCES maintenance_schedule(id),
    asset_type     VARCHAR(20)  NOT NULL,
    asset_id       BIGINT       NOT NULL,
    asset_label    VARCHAR(100) NOT NULL,
    performed_on   DATE         NOT NULL,
    work_done      TEXT         NOT NULL,
    parts_used     TEXT,
    cost           NUMERIC(12,2),
    performed_by   VARCHAR(100) NOT NULL,
    next_due_date  DATE
);
CREATE INDEX IF NOT EXISTS idx_history_asset ON service_history(asset_type, asset_id);