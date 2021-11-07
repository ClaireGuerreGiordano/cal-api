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

CREATE TABLE token (
    ticker VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    blockchain_name VARCHAR NOT NULL,
    contract_address VARCHAR NOT NULL,
    attributes json NOT NULL,

    PRIMARY KEY (ticker, blockchain_name, contract_address)
);

CREATE TABLE dapp (
    chain_id INT NOT NULL,
    name VARCHAR NOT NULL,
    contracts json NOT NULL,

    PRIMARY KEY (chain_id, name)
);