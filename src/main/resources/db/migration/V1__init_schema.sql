CREATE TABLE users
(
    id       CHAR(36)     PRIMARY KEY NOT NULL,
    login    VARCHAR(100) UNIQUE      NOT NULL,
    password VARCHAR(100)             NOT NULL
);

CREATE TABLE items
(
    id       CHAR(36)     PRIMARY KEY NOT NULL,
    name     VARCHAR(255)             NOT NULL,
    owner_id CHAR(36)                 NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES users (id)
);