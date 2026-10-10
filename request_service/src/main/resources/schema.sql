CREATE SEQUENCE IF NOT EXISTS request_no_seq START 1;

CREATE TABLE IF NOT EXISTS logistics_request (
    id                   BIGSERIAL PRIMARY KEY,
    request_no           VARCHAR(30)  NOT NULL UNIQUE,
    requesting_unit_id   BIGINT       NOT NULL,
    requesting_unit_name VARCHAR(150) NOT NULL,
    request_type         VARCHAR(20)  NOT NULL,   -- PERSONNEL, EQUIPMENT, TRANSPORT, SUPPLY, MIXED
    priority             VARCHAR(10)  NOT NULL,   -- LOW, MEDIUM, HIGH, URGENT
    requested_by         VARCHAR(100) NOT NULL,
    justification        TEXT,
    source_location      VARCHAR(150),
    destination_location VARCHAR(150),
    required_from        TIMESTAMP    NOT NULL,
    required_to          TIMESTAMP    NOT NULL,
    status               VARCHAR(20)  NOT NULL,   -- SUBMITTED, APPROVED, REJECTED, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED
    approved_by          VARCHAR(100),
    approved_at          TIMESTAMP,
    created_at           TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at           TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS request_item (
    id                BIGSERIAL PRIMARY KEY,
    request_id        BIGINT       NOT NULL REFERENCES logistics_request(id) ON DELETE CASCADE,
    resource_type     VARCHAR(20)  NOT NULL,      -- PERSONNEL, EQUIPMENT, VEHICLE, SUPPLY
    category_or_type  VARCHAR(100) NOT NULL,      -- e.g. Truck, Generator
    quantity          INT          NOT NULL,
    resource_id       BIGINT,                     -- filled when a resource is assigned
    assignment_status VARCHAR(20)  NOT NULL DEFAULT 'PENDING'
);

CREATE TABLE IF NOT EXISTS request_status_history (
    id         BIGSERIAL PRIMARY KEY,
    request_id BIGINT      NOT NULL REFERENCES logistics_request(id) ON DELETE CASCADE,
    old_status VARCHAR(20),
    new_status VARCHAR(20) NOT NULL,
    changed_by VARCHAR(100),
    comment    TEXT,
    changed_at TIMESTAMP   NOT NULL DEFAULT now()
);