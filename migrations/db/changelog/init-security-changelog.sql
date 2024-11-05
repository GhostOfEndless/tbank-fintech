--liquibase formatted sql

--changeset ghostofendless:1
INSERT INTO security.t_app_users(c_display_name, c_login, c_hashed_password, c_role)
VALUES ('Test User', 'user', '$2a$10$CCHSA0UfwSSm2sRrAyakAOEMGyxa7o5eXhTJqeTxuItwFKGbF1O7q', 'USER');