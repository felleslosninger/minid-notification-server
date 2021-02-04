ALTER TABLE device CHANGE token fcm_token VARCHAR(4096);
ALTER TABLE device ADD COLUMN apns_token VARCHAR(4096) AFTER fcm_token;

CREATE INDEX IDX_3 ON device (apns_token(255));
