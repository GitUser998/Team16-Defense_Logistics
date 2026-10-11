-- run inside database: inventory_db
CREATE TABLE IF NOT EXISTS inventory_table (
    item_id       VARCHAR(30) PRIMARY KEY,
    item_name     VARCHAR(100),
    category      VARCHAR(100),
    quantity      INT,
    reorder_level INT,
    unit          VARCHAR(30),
    location      VARCHAR(100)
);

INSERT INTO inventory_table VALUES
('ITM001', 'Rations Pack',    'Food',    500, 100, 'packs',  'Chennai'),
('ITM002', 'Diesel Fuel',     'Fuel',     40, 100, 'litres', 'Delhi'),
('ITM003', 'Medical Bandage', 'Medical', 300,  50, 'boxes',  'Hyderabad'),
('ITM004', 'Drinking Water',  'Food',     20,  80, 'cases',  'Pune'),
('ITM005', 'Spare Tyres',     'Vehicle',  25,  10, 'pieces', 'Bengaluru')
ON CONFLICT DO NOTHING;
