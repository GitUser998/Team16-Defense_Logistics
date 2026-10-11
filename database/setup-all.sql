-- creates the 7 databases and loads the tables and sample data
-- run it from the project folder with: psql -U postgres -f database/setup-all.sql
-- (or just use ./setup-database.sh)
\set ON_ERROR_STOP on

SELECT 'CREATE DATABASE personnel_db'   WHERE NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'personnel_db') \gexec
SELECT 'CREATE DATABASE equipment_db'   WHERE NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'equipment_db') \gexec
SELECT 'CREATE DATABASE vehicle_db'     WHERE NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'vehicle_db') \gexec
SELECT 'CREATE DATABASE inventory_db'   WHERE NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'inventory_db') \gexec
SELECT 'CREATE DATABASE request_db'     WHERE NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'request_db') \gexec
SELECT 'CREATE DATABASE maintenance_db' WHERE NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'maintenance_db') \gexec
SELECT 'CREATE DATABASE deployment_db'  WHERE NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'deployment_db') \gexec

\c personnel_db
\ir personnel_db.sql
\c equipment_db
\ir equipment_db.sql
\c vehicle_db
\ir vehicle_db.sql
\c inventory_db
\ir inventory_db.sql
\c request_db
\ir request_db.sql
\c maintenance_db
\ir maintenance_db.sql
\c deployment_db
\ir deployment_db.sql

\echo 'DONE - all 7 databases are ready'
