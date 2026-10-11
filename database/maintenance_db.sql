-- run inside database: maintenance_db
CREATE TABLE IF NOT EXISTS maintenance_table (
    maintenance_id   VARCHAR(30) PRIMARY KEY,
    asset_type       VARCHAR(30),
    asset_id         VARCHAR(30),
    maintenance_type VARCHAR(30),
    priority         VARCHAR(30),
    scheduled_date   DATE,
    description      VARCHAR(500),
    technician       VARCHAR(100),
    status           VARCHAR(30),
    completed_date   DATE,
    work_done        VARCHAR(500)
);

INSERT INTO maintenance_table VALUES
('MNT001', 'VEHICLE',   'VEH002', 'PREVENTIVE', 'MEDIUM', CURRENT_DATE + 3,  'Routine service', 'Hav. Kumar', 'SCHEDULED', NULL, NULL),
('MNT002', 'EQUIPMENT', 'EQP003', 'CORRECTIVE', 'HIGH',   CURRENT_DATE - 2,  'Filter repair',   'Sgt. Rao',   'SCHEDULED', NULL, NULL),
('MNT003', 'VEHICLE',   'VEH001', 'PREVENTIVE', 'LOW',    CURRENT_DATE - 30, 'Tyre check',      'Hav. Kumar', 'COMPLETED', CURRENT_DATE - 29, 'Tyres rotated')
ON CONFLICT DO NOTHING;
