CREATE TABLE active_channel
(
    channel VARCHAR(64) NOT NULL,
    joined  BOOLEAN DEFAULT FALSE
);

CREATE TABLE event
(
    id          UUID PRIMARY KEY,
    event_type  VARCHAR(32) NOT NULL,
    object_type VARCHAR(32) NOT NULL,
    version     VARCHAR(8)  NOT NULL,
    time        VARCHAR(64) NOT NULL,
    raw_data    TEXT        NOT NULL
);

CREATE TABLE last_read
(
    name VARCHAR(64) NOT NULL,
    id   VARCHAR(64) NOT NULL
);