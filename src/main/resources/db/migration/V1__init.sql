create table if not exists device (
   id                 INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   person_identifier  VARCHAR(11) NOT NULL,
   app_identifier     VARCHAR(64) NOT NULL,
   token              VARCHAR(4096) NOT NULL,
   description        VARCHAR(128) NOT NULL,
   os                 VARCHAR(64) NOT NULL,
   os_version         VARCHAR(64) NOT NULL,
   created            TIMESTAMP NOT NULL,
   last_updated       TIMESTAMP NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE INDEX IDX_1 ON device (person_identifier);
CREATE INDEX IDX_2 ON device (token(255));

ALTER TABLE device ADD UNIQUE UIDX_1 (person_identifier, app_identifier, token(255));


