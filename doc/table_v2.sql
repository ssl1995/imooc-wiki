create schema `wiki-dev` collate utf8mb4_unicode_ci;

-- auto-generated definition
create table category
(
    id     bigint           not null comment 'id'
        primary key,
    parent bigint default 0 not null comment '父id',
    name   varchar(50)      not null comment '名称',
    sort   int              null comment '顺序'
)
    comment '分类' charset = utf8mb4;


-- auto-generated definition
create table content
(
    id      bigint     not null comment '文档id'
        primary key,
    content mediumtext not null comment '内容'
)
    comment '文档内容' charset = utf8mb4;


-- auto-generated definition
create table doc
(
    id         bigint           not null comment 'id'
        primary key,
    ebook_id   bigint default 0 not null comment '电子书id',
    parent     bigint default 0 not null comment '父id',
    name       varchar(50)      not null comment '名称',
    sort       int              null comment '顺序',
    view_count int    default 0 null comment '阅读数',
    vote_count int    default 0 null comment '点赞数'
)
    comment '文档' charset = utf8mb4;

-- auto-generated definition
create table ebook
(
    id           bigint        not null comment 'id'
        primary key,
    name         varchar(50)   null comment '名称',
    category1_id bigint        null comment '分类1',
    category2_id bigint        null comment '分类2',
    description  varchar(200)  null comment '描述',
    cover        varchar(200)  null comment '封面',
    doc_count    int default 0 not null comment '文档数',
    view_count   int default 0 not null comment '阅读数',
    vote_count   int default 0 not null comment '点赞数'
)
    comment '电子书' charset = utf8mb4;

-- auto-generated definition
create table ebook_snapshot
(
    id            bigint auto_increment comment 'id'
        primary key,
    ebook_id      bigint default 0 not null comment '电子书id',
    date          date             not null comment '快照日期',
    view_count    int    default 0 not null comment '阅读数',
    vote_count    int    default 0 not null comment '点赞数',
    view_increase int    default 0 not null comment '阅读增长',
    vote_increase int    default 0 not null comment '点赞增长',
    constraint ebook_id_date_unique
        unique (ebook_id, date)
)
    comment '电子书快照表' charset = utf8mb4;

-- auto-generated definition
create table user
(
    id         bigint      not null comment 'ID'
        primary key,
    login_name varchar(50) not null comment '登陆名',
    name       varchar(50) null comment '昵称',
    password   char(32)    not null comment '密码',
    constraint login_name_unique
        unique (login_name)
)
    comment '用户' charset = utf8mb4;

