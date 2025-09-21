CREATE DATABASE `springtest`;

SET DATABASE `springtest`;

CREATE TABLE product (
    id UUID PRIMARY KEY,
    name VARCHAR(255),
    price DOUBLE PRECISION NOT NULL,
    items INTEGER NOT NULL,
    created_dt TIMESTAMP,
    updated_dt TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 0
);
