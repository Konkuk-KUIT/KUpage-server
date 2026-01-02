CREATE TABLE processed_events (
                                  id BIGINT NOT NULL AUTO_INCREMENT,
                                  event_id VARCHAR(64) NOT NULL,
                                  discord_user_id VARCHAR(32) NULL,
                                  discord_login_id VARCHAR(64) NULL,
                                  event_type VARCHAR(64) NULL,
                                  occurred_at DATETIME(6) NULL,
                                  created_at DATETIME(6) NOT NULL,
                                  modified_at DATETIME(6) NOT NULL,
                                  CONSTRAINT pk_processed_events PRIMARY KEY (id),
                                  CONSTRAINT uk_processed_events_event_id UNIQUE (event_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;