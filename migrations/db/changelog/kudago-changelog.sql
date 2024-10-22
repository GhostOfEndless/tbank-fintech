--liquibase formatted sql

--changeset ghostofendless:1
CREATE SCHEMA IF NOT EXISTS kudago;

--changeset ghostofendless:2
CREATE TABLE kudago.t_locations
(
    id   BIGSERIAL PRIMARY KEY,
    c_slug TEXT NOT NULL UNIQUE,
    c_name TEXT NOT NULL
);

--changeset ghostofendless:3
CREATE TABLE kudago.t_events
(
    id          BIGSERIAL PRIMARY KEY,
    c_name        TEXT      NOT NULL,
    c_start_date  TIMESTAMP NOT NULL,
    c_price       TEXT,
    c_free        BOOLEAN   NOT NULL DEFAULT false,
    c_location_id BIGINT    NOT NULL,
    CONSTRAINT fk_event_location FOREIGN KEY (c_location_id) REFERENCES kudago.t_locations (id)
);
