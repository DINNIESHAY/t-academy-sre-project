--liquibase formatted sql

--changeset dinnieshay:00-initial-schema
create table if not exists users (
     id bigserial primary key,
     username varchar(20) not null,
     email varchar(50) not null,
     password varchar(255) not null,
     role varchar(20) not null
);

create table if not exists profiles (
    id bigserial primary key,
    user_id bigint not null references users (id) on delete cascade,
    first_name varchar(100),
    last_name varchar(100),
    contact varchar(100),
    birth_date date,
    country varchar(100),
    city varchar(100),
    avatar_url varchar(255),
    bio text
);

create table if not exists trips (
    id bigserial primary key,
    owner_id bigint references users (id) on delete cascade,
    destination varchar(500),
    departure_point varchar(500),
    start_at date,
    end_at date,
    budget numeric(15, 2),
    max_fellows int,
    description text,
    tags jsonb
);

create table if not exists favorites (
    id bigserial primary key,
    user_id bigint not null references users (id) on delete cascade,
    trip_id bigint not null references trips (id) on delete cascade,
    unique (user_id, trip_id)
);

create table if not exists trip_applications (
    id bigserial primary key,
    applicant_id bigint not null references users (id) on delete cascade,
    trip_id bigint not null references trips (id) on delete cascade,
    message text,
    contacts text not null,
    unique(applicant_id, trip_id)
);

create table if not exists trip_members (
    id bigserial primary key,
    trip_id bigint not null references trips (id) on delete cascade,
    user_id bigint not null references users (id) on delete cascade,
    joined_at timestamp default now(),
    unique (trip_id, user_id)
);

--rollback drop table trip_applications;
--rollback drop table trip_members;
--rollback drop table favorites;
--rollback drop table trips;
--rollback drop table users_info;
--rollback drop table users;