CREATE TABLE user (
  id                  BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  person_identifier   VARCHAR(11) NOT NULL UNIQUE,
  active              TINYINT default 1,
  created             BIGINT(20),
  last_updated        BIGINT(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table device (
   id                 BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   device_description VARCHAR(128) NOT NULL,
   message_token      VARCHAR(4096) NOT NULL,
   user_id            BIGINT NOT NULL,
   active             TINYINT default 1,
   created            BIGINT(20),
   last_updated       BIGINT(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE device ADD FOREIGN KEY FK_1 (user_id) REFERENCES user(id);

CREATE INDEX IDX_1 ON user (id, person_identifier);
CREATE INDEX IDX_2 ON device (id, user_id, active);


