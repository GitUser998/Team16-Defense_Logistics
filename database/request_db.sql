-- run inside database: request_db
CREATE TABLE IF NOT EXISTS request_table (
    request_id          VARCHAR(50) PRIMARY KEY,
    requester_unit      VARCHAR(50),
    item_requested      VARCHAR(30),
    description         VARCHAR(500),
    priority            VARCHAR(30),
    request_date        DATE,
    status              VARCHAR(30),
    assigned_to         VARCHAR(100),
    resolution_notes    TEXT,
    allocated_vehicle   VARCHAR(30),
    allocated_equipment VARCHAR(30),
    location            VARCHAR(100),
    personnel_count     VARCHAR(10),
    supply_item_id      VARCHAR(30),
    supply_quantity     VARCHAR(10),
    allocated_personnel VARCHAR(500),
    allocated_supply    VARCHAR(100)
);

-- if the table was made by an older script, add the newer columns
ALTER TABLE request_table ADD COLUMN IF NOT EXISTS location            VARCHAR(100);
ALTER TABLE request_table ADD COLUMN IF NOT EXISTS personnel_count     VARCHAR(10);
ALTER TABLE request_table ADD COLUMN IF NOT EXISTS supply_item_id      VARCHAR(30);
ALTER TABLE request_table ADD COLUMN IF NOT EXISTS supply_quantity     VARCHAR(10);
ALTER TABLE request_table ADD COLUMN IF NOT EXISTS allocated_personnel VARCHAR(500);
ALTER TABLE request_table ADD COLUMN IF NOT EXISTS allocated_supply    VARCHAR(100);

INSERT INTO request_table (request_id, requester_unit, item_requested, description, priority,
                           request_date, status, location, personnel_count, supply_item_id, supply_quantity)
VALUES ('REQ001', 'UNIT-A', 'BOTH', 'Field exercise at Sector 7', 'HIGH',
        CURRENT_DATE, 'SUBMITTED', 'Chennai', '2', 'ITM001', '50')
ON CONFLICT DO NOTHING;
