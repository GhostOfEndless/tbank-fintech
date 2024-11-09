--liquibase formatted sql

--changeset ghostofendless:1
CREATE SCHEMA IF NOT EXISTS security;

--changeset ghostofendless:2
CREATE SEQUENCE IF NOT EXISTS security.app_users_seq START WITH 1 INCREMENT BY 50 CACHE 1000 NO CYCLE;
CREATE SEQUENCE IF NOT EXISTS security.tokens_seq START WITH 1 INCREMENT BY 50 CACHE 1000 NO CYCLE;

--changeset ghostofendless:3
CREATE TABLE security.t_app_users
(
    id                BIGINT DEFAULT nextval('security.app_users_seq') PRIMARY KEY,
    c_display_name    TEXT        NOT NULL,
    c_login           TEXT UNIQUE NOT NULL,
    c_hashed_password TEXT        NOT NULL,
    c_role            TEXT        NOT NULL
);

--changeset ghostofendless:4
CREATE TABLE security.t_tokens
(
    id            BIGINT DEFAULT nextval('security.tokens_seq') PRIMARY KEY,
    c_token       TEXT    NOT NULL,
    c_revoked     BOOLEAN NOT NULL,
    c_app_user_id BIGINT  NOT NULL,
    CONSTRAINT fk_app_user FOREIGN KEY (c_app_user_id) REFERENCES security.t_app_users (id)
);