# 电子书表
drop table if exists `ebook`;
create table `ebook`
(
    `id`           bigint not null comment 'id',
    `name`         varchar(50) comment '名称',
    `category1_id` bigint comment '分类1',
    `category2_id` bigint comment '分类2',
    `description`  varchar(200) comment '描述',
    `cover`        varchar(200) comment '封面',
    `doc_count`    int    not null default 0 comment '文档数',
    `view_count`   int    not null default 0 comment '阅读数',
    `vote_count`   int    not null default 0 comment '点赞数',
    primary key (`id`)
) engine = innodb
  default charset = utf8mb4 comment ='电子书';

insert into `ebook` (id, name, description)
values (1, 'Spring Boot 入门教程', '零基础入门 Java 开发，企业级应用开发最佳首选框架');
insert into `ebook` (id, name, description)
values (2, 'Vue 入门教程', '零基础入门 Vue 开发，企业级应用开发最佳首选框架');
insert into `ebook` (id, name, description)
values (3, 'Python 入门教程', '零基础入门 Python 开发，企业级应用开发最佳首选框架');
insert into `ebook` (id, name, description)
values (4, 'Mysql 入门教程', '零基础入门 Mysql 开发，企业级应用开发最佳首选框架');
insert into `ebook` (id, name, description)
values (5, 'Oracle 入门教程', '零基础入门 Oracle 开发，企业级应用开发最佳首选框架');

# 分类表
drop table if exists `category`;
create table `category`
(
    `id`     bigint      not null comment 'id',
    `parent` bigint      not null default 0 comment '父id',
    `name`   varchar(50) not null comment '名称',
    `sort`   int comment '顺序',
    primary key (`id`)
) engine = innodb
  default charset = utf8mb4 comment ='分类';

insert into `category` (id, parent, name, sort)
values (100, 000, '前端开发', 100);
insert into `category` (id, parent, name, sort)
values (101, 100, 'Vue', 101);
insert into `category` (id, parent, name, sort)
values (102, 100, 'HTML & CSS', 102);
insert into `category` (id, parent, name, sort)
values (200, 000, 'Java', 200);
insert into `category` (id, parent, name, sort)
values (201, 200, '基础应用', 201);
insert into `category` (id, parent, name, sort)
values (202, 200, '框架应用', 202);
insert into `category` (id, parent, name, sort)
values (300, 000, 'Python', 300);
insert into `category` (id, parent, name, sort)
values (301, 300, '基础应用', 301);
insert into `category` (id, parent, name, sort)
values (302, 300, '进阶方向应用', 302);
insert into `category` (id, parent, name, sort)
values (400, 000, '数据库', 400);
insert into `category` (id, parent, name, sort)
values (401, 400, 'MySQL', 401);
insert into `category` (id, parent, name, sort)
values (500, 000, '其它', 500);
insert into `category` (id, parent, name, sort)
values (501, 500, '服务器', 501);
insert into `category` (id, parent, name, sort)
values (502, 500, '开发工具', 502);
insert into `category` (id, parent, name, sort)
values (503, 500, '热门服务端语言', 503);


-- 文档表
drop table if exists `doc`;
create table `doc`
(
    `id`         bigint      not null comment 'id',
    `ebook_id`   bigint      not null default 0 comment '电子书id',
    `parent`     bigint      not null default 0 comment '父id',
    `name`       varchar(50) not null comment '名称',
    `sort`       int comment '顺序',
    `view_count` int                  default 0 comment '阅读数',
    `vote_count` int                  default 0 comment '点赞数',
    primary key (`id`)
) engine = innodb
  default charset = utf8mb4 comment ='文档';

insert into `doc` (id, ebook_id, parent, name, sort, view_count, vote_count)
values (1, 1, 0, '文档1', 1, 0, 0);
insert into `doc` (id, ebook_id, parent, name, sort, view_count, vote_count)
values (2, 1, 1, '文档1.1', 1, 0, 0);
insert into `doc` (id, ebook_id, parent, name, sort, view_count, vote_count)
values (3, 1, 0, '文档2', 2, 0, 0);
insert into `doc` (id, ebook_id, parent, name, sort, view_count, vote_count)
values (4, 1, 3, '文档2.1', 1, 0, 0);
insert into `doc` (id, ebook_id, parent, name, sort, view_count, vote_count)
values (5, 1, 3, '文档2.2', 2, 0, 0);
insert into `doc` (id, ebook_id, parent, name, sort, view_count, vote_count)
values (6, 1, 5, '文档2.2.1', 1, 0, 0);

-- 文档内容
drop table if exists `content`;
create table `content`
(
    `id`      bigint     not null comment '文档id',
    `content` mediumtext not null comment '内容',
    primary key (`id`)
) engine = innodb
  default charset = utf8mb4 comment ='文档内容';


-- 用户表
drop table if exists `user`;
create table `user`
(
    `id`         bigint      not null comment 'ID',
    `login_name` varchar(50) not null comment '登陆名',
    `name`       varchar(50) comment '昵称',
    `password`   char(32)    not null comment '密码',
    primary key (`id`),
    unique key `login_name_unique` (`login_name`)
) engine = innodb
  default charset = utf8mb4 comment ='用户';

insert into `user` (id, `login_name`, `name`, `password`)
values (1, 'admin', '管理员', 'e70e2222a9d67c4f2eae107533359aa4');

# 更新文档数、阅读数、点赞数
update ebook t1 ,(select ebook_id, count(1) as doc_count, sum(view_count) as view_count, sum(vote_count) as vote_count
                  from doc
                  group by ebook_id) as t2
set t1.doc_count  = t2.doc_count,
    t1.view_count=t2.view_count,
    t1.vote_count = t2.vote_count
where t1.id = t2.ebook_id;

-- 电子书快照表
drop table if exists `ebook_snapshot`;
create table `ebook_snapshot`
(
    `id`            bigint auto_increment not null comment 'id',
    `ebook_id`      bigint                not null default 0 comment '电子书id',
    `date`          date                  not null comment '快照日期',
    `view_count`    int                   not null default 0 comment '阅读数',
    `vote_count`    int                   not null default 0 comment '点赞数',
    `view_increase` int                   not null default 0 comment '阅读增长',
    `vote_increase` int                   not null default 0 comment '点赞增长',
    primary key (`id`),
    unique key `ebook_id_date_unique` (`ebook_id`, `date`)
) engine = innodb
  default charset = utf8mb4 comment ='电子书快照表';

# 统计报表
-- 1.快照表插入新数据,让id连续的写法
insert into ebook_snapshot(ebook_id, date, view_count, vote_count, view_increase, vote_increase)
select t1.id, curdate(), 0, 0, 0, 0
from ebook t1
where not exists(select 1 from ebook_snapshot t2 where t1.id = t2.ebook_id and t2.date = curdate());

-- 2.更新快照表
update ebook_snapshot t1,ebook t2
set t1.view_count = t2.view_count,
    t1.vote_count=t2.vote_count
where t1.date = curdate()
  and t1.ebook_id = t2.id;

-- 3.获取昨天的数据
update ebook_snapshot t1 left join (select ebook_id, view_count, vote_count
                                    from ebook_snapshot
                                    where date = date_sub(CURDATE(), interval 1 day)) t2
    on t1.ebook_id = t2.ebook_id
set t1.view_increase = (t1.view_count - IFNULL(t2.view_count, 0)),
    t1.vote_increase = (t1.vote_count - IFNULL(t2.vote_count, 0))
where t1.date = curdate();

-- 查前30天数据
select t1.`date`             as `date`,
       sum(t1.view_increase) as viewIncrease,
       sum(t1.vote_increase) as voteIncrease
from ebook_snapshot t1
where t1.`date` between date_sub(curdate(), interval 30 day) and date_sub(curdate(), interval 1 day)
group by t1.`date`
order by t1.`date` asc;

-- 快照表造假数据
INSERT INTO ebook_snapshot (ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (1, '2022-05-20', 89, 15, 89, 15);
INSERT INTO ebook_snapshot (ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (2, '2022-05-20', 0, 1, 0, 0);
INSERT INTO ebook_snapshot (ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (3, '2022-05-20', 0, 2, 0, 0);
INSERT INTO ebook_snapshot (ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (4, '2022-05-20', 2, 0, 23, 0);
INSERT INTO ebook_snapshot (ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (5, '2022-05-20', 231, 0, 12, 23);

INSERT INTO ebook_snapshot (ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (1, '2022-05-10', 223, 213, 123, 123);
INSERT INTO ebook_snapshot (ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (2, '2022-05-10', 0, 1, 312, 0);
INSERT INTO ebook_snapshot (ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (3, '2022-05-10', 123, 2, 213, 0);
INSERT INTO ebook_snapshot (ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (4, '2022-05-10', 2, 0, 23, 123);
INSERT INTO ebook_snapshot (ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (5, '2022-05-10', 231, 0, 12, 23);



