CREATE TYPE family AS ENUM ('bitcoin','ethereum','ripple');
CREATE TYPE network_type AS ENUM ('main','test');

CREATE TABLE coin (
    ticker VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    symbol VARCHAR NOT NULL,
    family family NOT NULL,
    coin_type integer NOT NULL,
    has_segwit boolean NOT NULL,
    has_token boolean NOT NULL,
    units json NOT NULL,
    networks json NOT NULL,

    PRIMARY KEY (ticker, name)
);