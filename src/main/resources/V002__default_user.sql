--liquibase formatted sql

--changeset Sabir:1
INSERT INTO USERS (login, password, role)
VALUES ('user', '$2a$10$NSodYdlIiLqXQdfobcE1G.Xvy8BslbDLyO.lnDQtS4lbU0.LYNLdu', 'USER');
--rollback drop default user;