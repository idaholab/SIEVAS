CREATE TABLE user_info
(
    id bigserial not null,
    username varchar(128) NOT NULL UNIQUE,
    password varchar(512) NOT NULL,
    first_name varchar(64) NOT NULL,
    last_name varchar(64) NOT NULL,
    edipi bigint NULL,
    expired boolean NOT NULL,
    locked boolean NOT NULL,
    enabled boolean NOT NULL,
    PRIMARY KEY(id)
);
