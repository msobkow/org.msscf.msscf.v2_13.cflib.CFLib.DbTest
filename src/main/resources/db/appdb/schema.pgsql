-- Application Database Schema
CREATE SCHEMA IF NOT EXISTS appdb;
SET search_path TO appdb;

-- Create data_blob table
CREATE TABLE IF NOT EXISTS data_blob (
    pid BYTEA(32) NOT NULL UNIQUE PRIMARY KEY,
    dataname VARCHAR(1023) NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by BYTEA(32) NOT NULL, -- References secdb.users(pid)
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by BYTEA(32) NOT NULL, -- References secdb.users(pid)
    datablob BYTEA(1048576)
);

-- Foreign Key Constraints
ALTER TABLE data_blob
    ADD CONSTRAINT data_blob_created_by_fkey FOREIGN KEY (created_by) REFERENCES secdb.sec_user(pid) ON DELETE SET NULL;
ALTER TABLE data_blob
    ADD CONSTRAINT data_blob_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES secdb.sec_user(pid) ON DELETE SET NULL;

-- Indexes for data_blob
CREATE UNIQUE INDEX data_blob_pid_pxis ON data_blob(pid);
CREATE UNIQUE INDEX data_blob_dataname_axis ON data_blob(dataname);
CREATE INDEX data_blob_created_by_axis ON data_blob(created_by);
CREATE INDEX data_blob_updated_by_axis ON data_blob(updated_by);
