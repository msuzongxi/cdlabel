-- Run this while connected as a PostgreSQL superuser, e.g. the postgres account.
-- If the role/database already exist, skip the corresponding statement.

CREATE ROLE cgdi WITH LOGIN PASSWORD 'cgdi';
CREATE DATABASE cgdi OWNER cgdi;
