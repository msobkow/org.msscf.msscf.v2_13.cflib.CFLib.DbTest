
-- Security Database Schema
CREATE SCHEMA IF NOT EXISTS secdb;
SET search_path TO secdb;

-- Create sec_user and sec_mgr tables
CREATE TABLE IF NOT EXISTS sec_user (
    user_type INTEGER not null default 0,
    pid BYTEA(32) NOT NULL UNIQUE PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    email VARCHAR(1023) NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    member_deptcode VARCHAR(32),
    prev_pid BYTEA(32) REFERENCES sec_user(pid) ON DELETE SET NULL,
    next_pid BYTEA(32) REFERENCES sec_user(pid) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS sec_mgr (
    pid BYTEA(32) NOT NULL REFERENCES sec_user(pid) PRIMARY KEY ON DELETE CASCADE,
    title VARCHAR(64) NOT NULL,
    deptcode VARCHAR(32) NOT NULL UNIQUE,
    subdeptof BYTEA(32) REFERENCES sec_mgr(pid) ON DELETE SET NULL,
);

-- Foreign Key Constraints
ALTER TABLE sec_user
    ADD CONSTRAINT sec_user_member_deptcode_fkey FOREIGN KEY (member_deptcode) REFERENCES sec_mgr(deptcode);

-- Indexes for sec_user
CREATE UNIQUE INDEX sec_user_pid_pxis ON sec_user(pid);
CREATE INDEX sec_user_type_axisd ON sec_user(user_type);
CREATE UNIQUE INDEX sec_user_username_axis ON sec_user(username);
CREATE UNIQUE INDEX sec_user_email_axis ON sec_user(email);
CREATE INDEX sec_user_member_deptcode_axis ON sec_user(member_deptcode);

-- Indexes for sec_mgr
CREATE INDEX sec_mgr_title_axis ON sec_mgr(title);  
CREATE UNIQUE INDEX sec_mgr_deptcode_axis ON sec_mgr(deptcode);
CREATE INDEX sec_mgr_subdeptof_axis ON sec_mgr(subdeptof);
CREATE UNIQUE INDEX sec_mgr_user_pid_axis ON sec_mgr(pid);
