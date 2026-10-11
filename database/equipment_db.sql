-- run inside database: equipment_db
CREATE TABLE IF NOT EXISTS equipment_table (
    equipment_id          VARCHAR(30) PRIMARY KEY,
    equipment_name        VARCHAR(100),
    category              VARCHAR(100),
    owner_unit            VARCHAR(50),
    location              VARCHAR(100),
    operational_condition VARCHAR(30),
    status                VARCHAR(30)
);

INSERT INTO equipment_table VALUES
('EQP001', 'Generator 10kVA',   'Power',         'UNIT-A', 'Chennai',   'GOOD', 'AVAILABLE'),
('EQP002', 'Field Radio Set',   'Communication', 'UNIT-B', 'Pune',      'GOOD', 'AVAILABLE'),
('EQP003', 'Water Purifier',    'Utility',       'UNIT-B', 'Delhi',     'POOR', 'AVAILABLE'),
('EQP004', 'Medical Kit Large', 'Medical',       'UNIT-C', 'Hyderabad', 'FAIR', 'AVAILABLE'),
('EQP005', 'Tent 20-Man',       'Shelter',       'UNIT-C', 'Bengaluru', 'GOOD', 'ALLOCATED')
ON CONFLICT DO NOTHING;
