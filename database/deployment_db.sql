-- run inside database: deployment_db
CREATE TABLE IF NOT EXISTS deployment_table (
    deployment_id   VARCHAR(60) PRIMARY KEY,
    unit_code       VARCHAR(50),
    destination     VARCHAR(500),
    status          VARCHAR(30),
    deployment_date DATE
);

INSERT INTO deployment_table VALUES
('DEP-SAMPLE', 'UNIT-B', 'Forward base Alpha', 'COMPLETED', CURRENT_DATE - 10)
ON CONFLICT DO NOTHING;
