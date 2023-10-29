--liquibase formatted sql

--changeset Sabir:1
create table USERS
(
    id serial primary key,
    login varchar(30) not null unique,
    password varchar not null,
    role varchar(20) not null
);
--rollback drop table users;

--changeset Sabir:2
create table FILES
(
    id serial primary key,
    name varchar(20) not null,
    type varchar not null,
    size integer not null,
    user_id integer references users(id),
    remote boolean default false
);
--rollback drop table files;