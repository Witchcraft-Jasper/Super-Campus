create table blogs
(
    message_id  int auto_increment
        primary key,
    user_id     int          not null,
    content     varchar(255) null,
    title       varchar(255) not null,
    create_time varchar(255) null,
    tag         varchar(255) null,
    type        int          null
);

create table comment
(
    comment_id      int auto_increment
        primary key,
    create_time     varchar(255) null,
    comment_content varchar(255) null,
    message_id      int          null,
    comment_user_id int          null,
    reply_user_id   int          null
);

create table email_code
(
    id          bigint auto_increment
        primary key,
    email       varchar(255) null,
    code        varchar(6)   null,
    create_time bigint       null
)
    charset = utf8;

create table notification
(
    id          int auto_increment
        primary key,
    title       varchar(255) null,
    content     varchar(255) null,
    create_time varchar(255) null
);

create table pictures
(
    message_id int          null,
    url        varchar(255) null
);

create table schedule
(
    schedule_id int auto_increment
        primary key,
    user_id     int          not null,
    name        varchar(20)  not null,
    room        varchar(20)  not null,
    teacher     varchar(20)  not null,
    start       int          not null,
    step        int          not null,
    day         int          not null,
    week        varchar(255) not null
)
    charset = utf8;

create table users
(
    id        int auto_increment
        primary key,
    name      varchar(20)  not null,
    email     varchar(30)  not null,
    password  varchar(50)  not null,
    type      int          not null,
    real_name varchar(20)  not null,
    sex       varchar(2)   null,
    avatar    varchar(255) null,
    constraint UQE_user_email
        unique (email),
    constraint UQE_user_name
        unique (name)
)
    charset = utf8;

