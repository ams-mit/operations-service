-- V2: Migrate user-ID columns from BIGINT to VARCHAR(36) for UUID compatibility.
-- All user IDs are sourced from the Identity Access Service (sub claim = UUID string).
-- VARCHAR(36) matches Hibernate's mapping of String fields (ddl-auto=validate).
-- All six columns were nullable in V1 and have no indexes or constraints, so MODIFY is safe.
-- Safe to run on an empty database; if rows exist a data migration must precede this script.

ALTER TABLE booking
    MODIFY COLUMN requested_by_user_id VARCHAR(36) NULL,
    MODIFY COLUMN decided_by_user_id   VARCHAR(36) NULL;

ALTER TABLE maintenance_request
    MODIFY COLUMN requested_by_user_id VARCHAR(36) NULL;

ALTER TABLE status_history
    MODIFY COLUMN changed_by_user_id VARCHAR(36) NULL;

ALTER TABLE work_order
    MODIFY COLUMN assigned_technician_user_id VARCHAR(36) NULL;

ALTER TABLE work_log_entry
    MODIFY COLUMN technician_id VARCHAR(36) NULL;
