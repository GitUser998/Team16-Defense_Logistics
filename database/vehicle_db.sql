-- run inside database: vehicle_db
CREATE TABLE IF NOT EXISTS vehicle_table (
    vehicle_id      VARCHAR(30) PRIMARY KEY,
    vehicle_type    VARCHAR(100),
    registration_no VARCHAR(50),
    driver_name     VARCHAR(100),
    location        VARCHAR(100),
    status          VARCHAR(30)
);

INSERT INTO vehicle_table VALUES
('VEH001', 'Truck 5-Ton',   'TN-01-AB-1001', 'Arjun Singh', 'Chennai',   'AVAILABLE'),
('VEH002', 'Jeep',          'TN-01-AB-1002', 'Manoj Yadav', 'Chennai',   'AVAILABLE'),
('VEH003', 'Ambulance',     'MH-12-CD-2001', 'Ravi Kumar',  'Pune',      'AVAILABLE'),
('VEH004', 'Troop Carrier', 'DL-03-EF-3001', 'Suresh Nair', 'Delhi',     'MAINTENANCE'),
('VEH005', 'Fuel Tanker',   'KA-05-GH-4001', 'Deepak Rao',  'Bengaluru', 'AVAILABLE')
ON CONFLICT DO NOTHING;
