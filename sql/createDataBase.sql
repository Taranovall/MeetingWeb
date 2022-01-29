--DROP TABLE IF EXISTS user_roles CASCADE;
--DROP TABLE IF EXISTS users CASCADE ;
--DROP TABLE IF EXISTS roles CASCADE ;
DROP TABLE IF EXISTS meetings CASCADE;
DROP TABLE IF EXISTS speaker_topics CASCADE;
DROP TABLE IF EXISTS topics CASCADE;
DROP TABLE IF EXISTS free_topics CASCADE;
DROP TABLE IF EXISTS meeting_topics CASCADE;



create table users
(
    id       bigserial           not null primary key,
    login    varchar(256) unique not null,
    password varchar(256)        not null
);

create table roles
(
    id   serial       not null primary key,
    name varchar(256) not null
);

create table meetings
(
    id    serial       not null primary key,
    name  varchar(256) not null,
    date  varchar(256) not null,
    time  varchar(256) not null,
    place varchar(256) not null,
    photo_path varchar(256) not null
);


create table topics
(
    id   serial       not null primary key,
    name varchar(256) not null
);

create table free_topics
(
    meeting_id int references meetings (id) on delete cascade,
    topic_id   int references topics (id) on delete cascade,
    UNIQUE (meeting_id, topic_id)
);

create table speaker_topics
(
    speaker_id int references users (id) on delete cascade,
    topic_id   int references topics (id) on delete cascade,
    invitation bool
);

create table meeting_topics (
    meeting_id int references meetings (id) on delete cascade,
    topic_id int references topics (id) on delete cascade
);


create table user_roles
(
    user_id bigint references users (id) on delete cascade,
    role_id int references roles (id) on delete cascade,
    unique (user_id, role_id)
);

INSERT INTO roles (name)
values ('User');
INSERT INTO roles (name)
values ('Speaker');
INSERT INTO roles (name)
values ('Moderator');




