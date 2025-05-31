SET search_path TO secdb;

INSERT INTO sec_user ( user_type, pid, username, email, created_at, updated_at, member_deptcode, prev_pid, next_pid)
VALUES (0, 
    decode('0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef', 'hex'),
    'admin',
    'root@localhost',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
    'root',
    NULL,
    NULL)
    ON CONFLICG(pid) DO NOTHING
    ON CONFLICT (username) DO NOTHING;
INSERT INTO sec_mgr (pid, title, deptcode, subdeptof)
VALUES (
    decode('0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef', 'hex'),
    'System Administrator'
    'root',
    NULL)
    ON CONFLICT (pid) DO NOTHING
    ON CONFLICT (deptcode) DO NOTHING;
COMMIT;