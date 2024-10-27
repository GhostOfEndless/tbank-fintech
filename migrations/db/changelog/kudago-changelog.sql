--liquibase formatted sql

--changeset ghostofendless:1
CREATE SCHEMA IF NOT EXISTS kudago;

--changeset ghostofendless:2
CREATE SEQUENCE IF NOT EXISTS kudago.id_table_seq START WITH 1 INCREMENT BY 50 CACHE 50 NO CYCLE;
CREATE SEQUENCE IF NOT EXISTS kudago.events_seq START WITH 1 INCREMENT BY 50 CACHE 50 NO CYCLE;
CREATE SEQUENCE IF NOT EXISTS kudago.locations_seq START WITH 1 INCREMENT BY 50 CACHE 50 NO CYCLE;
CREATE SEQUENCE IF NOT EXISTS kudago.categories_seq START WITH 1 INCREMENT BY 50 CACHE 50 NO CYCLE;

--changeset ghostofendless:3
CREATE TABLE kudago.t_locations
(
    id     BIGINT DEFAULT nextval('kudago.locations_seq') PRIMARY KEY,
    c_slug TEXT NOT NULL UNIQUE,
    c_name TEXT NOT NULL
);

--changeset ghostofendless:4
CREATE TABLE kudago.t_events
(
    id            BIGINT DEFAULT nextval('kudago.events_seq') PRIMARY KEY,
    c_name        TEXT      NOT NULL,
    c_start_date  TIMESTAMP NOT NULL,
    c_price       TEXT,
    c_free        BOOLEAN   NOT NULL,
    c_location_id BIGINT    NOT NULL,
    CONSTRAINT fk_event_location FOREIGN KEY (c_location_id) REFERENCES kudago.t_locations (id)
);

--changeset ghostofendless:5
CREATE INDEX idx_event_start_date ON kudago.t_events (c_start_date);
CREATE INDEX idx_event_location ON kudago.t_events (c_location_id);
CREATE INDEX idx_location_slug ON kudago.t_locations (c_slug);

--changeset ghostofendless:6
CREATE TABLE kudago.t_categories
(
    id     BIGINT DEFAULT nextval('kudago.categories_seq') PRIMARY KEY,
    c_slug TEXT NOT NULL UNIQUE,
    c_name TEXT NOT NULL
);