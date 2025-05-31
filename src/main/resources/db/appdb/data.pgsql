SET search_path TO appdb;

INSERT INTO data_blob (pid, dataname, created_at, created_by, updated_at, updated_by, datablob)
SELECT
    decode('fedcba9876543210fedcba9876543210fedcba9876543210fedcba9876543210', 'hex'),
    'sampledata',
    CURRENT_TIMESTAMP,
    u.id,
    CURRENT_TIMESTAMP,
    u.id,
    decode('48656c6c6f2c20576f726c6421', 'hex') -- Sample binary data (Hello, World!)
FROM secdb.sec_user u
WHERE u.username = 'admin'
ON CONFLICT (pid) DO NOTHING
ON CONFLICT (dataname) DO NOTHING;
COMMIT;
