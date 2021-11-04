CREATE TYPE family AS ENUM ('bitcoin','ethereum','ripple');
CREATE TYPE network_type AS ENUM ('main','test');

CREATE TABLE coin (
    ticker VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    symbol VARCHAR NOT NULL,
    family family NOT NULL,
    attributes json NOT NULL,

    PRIMARY KEY (ticker, name)
);