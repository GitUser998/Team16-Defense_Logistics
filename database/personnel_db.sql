-- run inside database: personnel_db
CREATE TABLE IF NOT EXISTS personnel_table (
    personnel_id     VARCHAR(30) PRIMARY KEY,
    full_name        VARCHAR(100),
    personnel_role   VARCHAR(100),
    unit_code        VARCHAR(50),
    posting_location VARCHAR(100),
    availability     VARCHAR(30)
);

INSERT INTO personnel_table VALUES
('P001', 'Arjun Singh',  'Driver',            'UNIT-A', 'Chennai',   'AVAILABLE'),
('P002', 'Ravi Kumar',   'Technician',        'UNIT-A', 'Chennai',   'AVAILABLE'),
('P003', 'Imran Khan',   'Signal Officer',    'UNIT-B', 'Pune',      'DEPLOYED'),
('P004', 'Suresh Nair',  'Logistics Officer', 'UNIT-B', 'Delhi',     'AVAILABLE'),
('P005', 'Manoj Yadav',  'Driver',            'UNIT-C', 'Bengaluru', 'ON_LEAVE'),
('P006', 'Deepak Rao',   'Medic',             'UNIT-C', 'Hyderabad', 'AVAILABLE')
ON CONFLICT DO NOTHING;
