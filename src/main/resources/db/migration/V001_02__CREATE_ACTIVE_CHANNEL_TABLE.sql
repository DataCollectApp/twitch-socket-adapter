DROP TABLE active_channel;

CREATE TABLE active_channel
(
    user_id     BIGINT PRIMARY KEY,
    username    VARCHAR(64) NOT NULL,
    joined      BOOLEAN     NOT NULL DEFAULT FALSE,
    joined_time VARCHAR(64)
);