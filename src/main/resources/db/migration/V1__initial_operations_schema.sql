CREATE TABLE booking (
                         id BIGINT NOT NULL AUTO_INCREMENT,
                         created_at DATETIME(6),
                         decided_at DATETIME(6),
                         decided_by_user_id BIGINT,
                         decision_note VARCHAR(255),
                         end_time DATETIME(6),
                         facility_id BIGINT,
                         guest_count INT,
                         purpose VARCHAR(255),
                         requested_by_user_id BIGINT,
                         start_time DATETIME(6),
                         status ENUM('APPROVED','CANCELLED','COMPLETED','PENDING','REJECTED'),
                         unit_id BIGINT,
                         updated_at DATETIME(6),
                         version BIGINT NOT NULL,
                         PRIMARY KEY (id)
);

CREATE TABLE facility (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          capacity INT,
                          closes_at TIME,
                          location_note VARCHAR(255),
                          max_advance_days INT,
                          name VARCHAR(255) NOT NULL,
                          opens_at TIME,
                          requires_approval BOOLEAN,
                          slot_duration_minutes INT,
                          status ENUM('ACTIVE','INACTIVE','UNDER_MAINTENANCE') NOT NULL,
                          type ENUM('BBQ_AREA','FUNCTION_HALL','GYM','MEETING_ROOM','POOL') NOT NULL,
                          PRIMARY KEY (id)
);

CREATE TABLE maintenance_request (
                                     id BIGINT NOT NULL AUTO_INCREMENT,
                                     attachment_url VARCHAR(255),
                                     category VARCHAR(255),
                                     created_at DATETIME(6),
                                     description VARCHAR(255),
                                     priority VARCHAR(255),
                                     requested_by_user_id BIGINT,
                                     status ENUM('ACKNOWLEDGED','ASSIGNED','CANCELLED','CLOSED','IN_PROGRESS','REJECTED','RESOLVED','SUBMITTED'),
                                     unit_id BIGINT,
                                     updated_at DATETIME(6),
                                     PRIMARY KEY (id)
);

CREATE TABLE status_history (
                                id BIGINT NOT NULL AUTO_INCREMENT,
                                changed_at DATETIME(6),
                                changed_by_user_id BIGINT,
                                maintenance_request_id BIGINT,
                                new_status ENUM('ACKNOWLEDGED','ASSIGNED','CANCELLED','CLOSED','IN_PROGRESS','REJECTED','RESOLVED','SUBMITTED'),
                                old_status ENUM('ACKNOWLEDGED','ASSIGNED','CANCELLED','CLOSED','IN_PROGRESS','REJECTED','RESOLVED','SUBMITTED'),
                                PRIMARY KEY (id)
);

CREATE TABLE work_log_entry (
                                id BIGINT NOT NULL AUTO_INCREMENT,
                                logged_at DATETIME(6),
                                note VARCHAR(255) NOT NULL,
                                technician_id BIGINT,
                                work_order_id BIGINT,
                                PRIMARY KEY (id)
);

CREATE TABLE work_order (
                            id BIGINT NOT NULL AUTO_INCREMENT,
                            assigned_technician_user_id BIGINT,
                            maintenance_request_id BIGINT,
                            resolution_notes VARCHAR(255),
                            scheduled_date DATE,
                            status ENUM('ASSIGNED','CANCELLED','COMPLETED','CREATED','IN_PROGRESS','REASSIGNED','VERIFIED'),
                            PRIMARY KEY (id)
);

CREATE INDEX idx_booking_facility_time_status
    ON booking (facility_id, start_time, end_time, status);