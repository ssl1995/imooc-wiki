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
INSERT INTO category (id, parent, name, sort) VALUES (500, 0, '古树名木', 500);
INSERT INTO category (id, parent, name, sort) VALUES (501, 500, '图片上传', 501);
INSERT INTO category (id, parent, name, sort) VALUES (502, 500, '图片管理', 502);

-- auto-generated definition
create table content
(
    id      bigint     not null comment '文档id'
        primary key,
    content mediumtext not null comment '内容'
)
    comment '文档内容' charset = utf8mb4;
INSERT INTO content (id, content) VALUES (1, '<p>什么是SpringBoot</p><ul><li>学习网站：https://www.bilibili.com/video/BV1PE411i7CV?p=1</li></ul>');
INSERT INTO content (id, content) VALUES (2, '<ul><li>1、基于POJO的轻量级和最小侵入性编程，所有东西都是bean；</li><li>2、通过IOC，依赖注入（DI）和面向接口实现松耦合；</li><li>3、基于切面（AOP）和惯例进行声明式编程；</li><li>4、通过切面和模版减少样式代码，RedisTemplate，xxxTemplate；</li></ul>');

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
INSERT INTO doc (id, ebook_id, parent, name, sort, view_count, vote_count) VALUES (1, 1, 0, 'HelloWord', 1, 2, 0);
INSERT INTO doc (id, ebook_id, parent, name, sort, view_count, vote_count) VALUES (2, 1, 1, 'Spring是怎么简化开发', 2, 1, 0);
INSERT INTO doc (id, ebook_id, parent, name, sort, view_count, vote_count) VALUES (3, 1, 0, '文档2', 2, 0, 0);
INSERT INTO doc (id, ebook_id, parent, name, sort, view_count, vote_count) VALUES (4, 1, 3, '文档2.1', 1, 0, 0);
INSERT INTO doc (id, ebook_id, parent, name, sort, view_count, vote_count) VALUES (5, 1, 3, '文档2.2', 2, 0, 0);
INSERT INTO doc (id, ebook_id, parent, name, sort, view_count, vote_count) VALUES (6, 1, 5, '文档2.2.1', 1, 0, 0);

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

INSERT INTO ebook (id, name, category1_id, category2_id, description, cover, doc_count, view_count, vote_count) VALUES (1, 'Spring Boot 入门教程', 200, 202, '零基础入门 Java 开发，企业级应用开发最佳首选框架', null, 6, 3, 0);
INSERT INTO ebook (id, name, category1_id, category2_id, description, cover, doc_count, view_count, vote_count) VALUES (2, 'Vue 入门教程', 100, 101, '零基础入门 Vue 开发，企业级应用开发最佳首选框架', null, 0, 0, 0);
INSERT INTO ebook (id, name, category1_id, category2_id, description, cover, doc_count, view_count, vote_count) VALUES (3, 'Python 入门教程', 300, 301, '零基础入门 Python 开发，企业级应用开发最佳首选框架', null, 0, 0, 0);
INSERT INTO ebook (id, name, category1_id, category2_id, description, cover, doc_count, view_count, vote_count) VALUES (4, 'Mysql 入门教程', 400, 401, '零基础入门 Mysql 开发，企业级应用开发最佳首选框架', null, 0, 0, 0);
INSERT INTO ebook (id, name, category1_id, category2_id, description, cover, doc_count, view_count, vote_count) VALUES (5, 'Oracle 入门教程', 400, 355744362908487680, '零基础入门 Oracle 开发，企业级应用开发最佳首选框架', null, 0, 0, 0);

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

INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (1, 1, '2023-09-09', 1, 0, 1, 0);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (2, 2, '2023-09-09', 0, 0, 0, 0);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (3, 3, '2023-09-09', 0, 0, 0, 0);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (4, 4, '2023-09-09', 0, 0, 0, 0);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (5, 5, '2023-09-09', 0, 0, 0, 0);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (8, 1, '2022-05-20', 89, 15, 89, 15);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (9, 2, '2022-05-20', 0, 1, 0, 0);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (10, 3, '2022-05-20', 0, 2, 0, 0);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (11, 4, '2022-05-20', 2, 0, 23, 0);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (12, 5, '2022-05-20', 231, 0, 12, 23);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (13, 1, '2022-05-10', 223, 213, 123, 123);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (14, 2, '2022-05-10', 0, 1, 312, 0);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (15, 3, '2022-05-10', 123, 2, 213, 0);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (16, 4, '2022-05-10', 2, 0, 23, 123);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (17, 5, '2022-05-10', 231, 0, 12, 23);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (18, 1, '2023-09-10', 3, 0, 2, 0);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (19, 2, '2023-09-10', 0, 0, 0, 0);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (20, 3, '2023-09-10', 0, 0, 0, 0);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (21, 4, '2023-09-10', 0, 0, 0, 0);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (22, 5, '2023-09-10', 0, 0, 0, 0);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (23, 1, '2025-03-29', 3, 0, 3, 0);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (24, 2, '2025-03-29', 0, 0, 0, 0);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (25, 3, '2025-03-29', 0, 0, 0, 0);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (26, 4, '2025-03-29', 0, 0, 0, 0);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (27, 5, '2025-03-29', 0, 0, 0, 0);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (28, 1, '2026-04-09', 3, 0, 3, 0);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (29, 2, '2026-04-09', 0, 0, 0, 0);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (30, 3, '2026-04-09', 0, 0, 0, 0);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (31, 4, '2026-04-09', 0, 0, 0, 0);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (32, 5, '2026-04-09', 0, 0, 0, 0);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (33, 1, '2026-05-15', 3, 0, 3, 0);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (34, 2, '2026-05-15', 0, 0, 0, 0);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (35, 3, '2026-05-15', 0, 0, 0, 0);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (36, 4, '2026-05-15', 0, 0, 0, 0);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (37, 5, '2026-05-15', 0, 0, 0, 0);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (38, 1, '2026-05-17', 3, 0, 3, 0);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (39, 2, '2026-05-17', 0, 0, 0, 0);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (40, 3, '2026-05-17', 0, 0, 0, 0);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (41, 4, '2026-05-17', 0, 0, 0, 0);
INSERT INTO ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (42, 5, '2026-05-17', 0, 0, 0, 0);

-- auto-generated definition
create table image
(
    id          bigint auto_increment comment '主键'
        primary key,
    tree_id     bigint            not null comment '关联树木ID',
    image_path  varchar(256)      not null comment '图像存储路径',
    hash_code   varchar(128)      null comment '128位哈希码字符串',
    hash_bits   binary(16)        null comment '16字节二进制哈希位串',
    is_delete   tinyint default 0 not null comment '是否逻辑删除 0:否 1:是',
    create_time bigint            not null comment '创建时间',
    update_time bigint            not null comment '更新时间'
)
    comment '图像表' charset = utf8mb4;

create index idx_tree_id
    on image (tree_id);


INSERT INTO image (id, tree_id, image_path, hash_code, hash_bits, is_delete, create_time, update_time) VALUES (1, 1, 'algorithm/data_samples/bfath_demo/001_景山万春亭古柏/image.jpg', '10111101101111101000011011011000000110001001110000100101110111100010010100110001010010010001010000000010011000011101111001100001', 0x00000000000000000000000000000000, 0, 1715000000000, 1715000000000);
INSERT INTO image (id, tree_id, image_path, hash_code, hash_bits, is_delete, create_time, update_time) VALUES (2, 2, 'algorithm/data_samples/bfath_demo/002_天坛九龙柏/image.jpg', '10111101101111101000011011011000000110001001110000100101110111100010010100110001010010010001010000000010011000011101111001100001', 0x00000000000000000000000000000000, 0, 1715000000000, 1715000000000);
INSERT INTO image (id, tree_id, image_path, hash_code, hash_bits, is_delete, create_time, update_time) VALUES (3, 3, 'algorithm/data_samples/bfath_demo/003_潭柘寺帝王银杏/image.jpg', '10111101101111101000011011011000000110001001110000100101110111100010010100110001010010010001010000000010011000011101111001100001', 0x00000000000000000000000000000000, 0, 1715000000000, 1715000000000);
INSERT INTO image (id, tree_id, image_path, hash_code, hash_bits, is_delete, create_time, update_time) VALUES (4, 4, 'algorithm/data_samples/bfath_demo/004_大觉寺千年银杏/image.jpg', '10111101101111101000010011011000000110001001110000100101110111100010010100110001010010010001010000000010011000011101111001100001', 0x00000000000000000000000000000000, 0, 1715000000000, 1715000000000);
INSERT INTO image (id, tree_id, image_path, hash_code, hash_bits, is_delete, create_time, update_time) VALUES (5, 5, 'algorithm/data_samples/bfath_demo/005_北海团城古白皮松/image.jpg', '10111101101111101000011011011000000110001001110000100101110111100010010100110001010010010001010000000010011000011101111001100001', 0x00000000000000000000000000000000, 0, 1715000000000, 1715000000000);
INSERT INTO image (id, tree_id, image_path, hash_code, hash_bits, is_delete, create_time, update_time) VALUES (6, 6, 'algorithm/data_samples/bfath_demo/006_颐和园佛香阁古柏/image.jpg', '10111101101111101000011011011000000110001001110000100101110111100010010100110001010010010001010000000010011000011101111001100001', 0x00000000000000000000000000000000, 0, 1715000000000, 1715000000000);
INSERT INTO image (id, tree_id, image_path, hash_code, hash_bits, is_delete, create_time, update_time) VALUES (7, 7, 'algorithm/data_samples/bfath_demo/007_中山公园古柏/image.jpg', '10111101101111101000011011011000000110001001110000100101110111100010010100110001010010010001010000000010011000011101111001100001', 0x00000000000000000000000000000000, 0, 1715000000000, 1715000000000);
INSERT INTO image (id, tree_id, image_path, hash_code, hash_bits, is_delete, create_time, update_time) VALUES (8, 8, 'algorithm/data_samples/bfath_demo/008_圆明园古柏/image.jpg', '10111101101111101000011011011000000110001001110000100101110111100010010100110001010010010011010000000010011000011101111001100001', 0x00000000000000000000000000000000, 0, 1715000000000, 1715000000000);

-- auto-generated definition
create table per_role
(
    id              bigint auto_increment comment '主键'
        primary key,
    role_name       varchar(256)      not null comment '角色名称',
    permission_desc text              null comment '权限描述',
    is_delete       tinyint default 0 not null comment '是否逻辑删除 0:否 1:是',
    create_time     bigint            not null comment '创建时间',
    update_time     bigint            not null comment '更新时间'
)
    comment '权限角色表' charset = utf8mb4;

INSERT INTO per_role (id, role_name, permission_desc, is_delete, create_time, update_time) VALUES (1, '管理员', '拥有系统全部操作权限，包括用户管理、古树名木管理、权限分配等', 0, 1715000000000, 1715000000000);
INSERT INTO per_role (id, role_name, permission_desc, is_delete, create_time, update_time) VALUES (2, '普通用户', '可执行古树名木检索、图像上传、个人信息管理等操作', 0, 1715000000000, 1715000000000);

-- auto-generated definition
create table tree
(
    id          bigint auto_increment comment '主键'
        primary key,
    tree_code   varchar(20)       null comment '古树编号（如001、002，用于前端固定匹配）',
    name        varchar(100)      null comment '古树名称',
    species     varchar(256)      null comment '树种名称',
    age         int               null comment '树龄',
    height      varchar(64)       null comment '树高',
    latitude    decimal(10, 8)    null comment '纬度',
    longitude   decimal(11, 8)    null comment '经度',
    `desc`      text              null comment '描述信息',
    is_delete   tinyint default 0 not null comment '是否逻辑删除 0:否 1:是',
    create_time bigint            not null comment '创建时间',
    update_time bigint            not null comment '更新时间'
)
    comment '树木表' charset = utf8mb4;

INSERT INTO tree (id, tree_code, name, species, age, height, latitude, longitude, `desc`, is_delete, create_time, update_time) VALUES (1, '001', '景山万春亭古柏', '侧柏', 500, '15米', 39.92890000, 116.39740000, '位于北京市景山公园万春亭，为一级保护古树', 0, 1715000000000, 1715000000000);
INSERT INTO tree (id, tree_code, name, species, age, height, latitude, longitude, `desc`, is_delete, create_time, update_time) VALUES (2, '002', '天坛九龙柏', '侧柏', 600, '18米', 39.88330000, 116.40690000, '位于天坛公园回音壁西北侧，为一级保护古树', 0, 1715000000000, 1715000000000);
INSERT INTO tree (id, tree_code, name, species, age, height, latitude, longitude, `desc`, is_delete, create_time, update_time) VALUES (3, '003', '潭柘寺帝王银杏', '银杏', 1300, '25米', 39.90500000, 116.02800000, '位于北京市门头沟区潭柘寺寺院内，为特级保护古树', 0, 1715000000000, 1715000000000);
INSERT INTO tree (id, tree_code, name, species, age, height, latitude, longitude, `desc`, is_delete, create_time, update_time) VALUES (4, '004', '大觉寺千年银杏', '银杏', 950, '22米', 40.05100000, 116.09500000, '位于北京市海淀区大觉寺寺院内，为一级保护古树', 0, 1715000000000, 1715000000000);
INSERT INTO tree (id, tree_code, name, species, age, height, latitude, longitude, `desc`, is_delete, create_time, update_time) VALUES (5, '005', '北海团城古白皮松', '白皮松', 800, '20米', 39.92500000, 116.39000000, '位于北京市北海公园，为一级保护古树', 0, 1715000000000, 1715000000000);
INSERT INTO tree (id, tree_code, name, species, age, height, latitude, longitude, `desc`, is_delete, create_time, update_time) VALUES (6, '006', '颐和园佛香阁古柏', '侧柏', 400, '16米', 39.99900000, 116.27500000, '位于北京市颐和园，为二级保护古树', 0, 1715000000000, 1715000000000);
INSERT INTO tree (id, tree_code, name, species, age, height, latitude, longitude, `desc`, is_delete, create_time, update_time) VALUES (7, '007', '中山公园古柏', '侧柏', 400, '14米', 39.90600000, 116.39700000, '位于北京市中山公园，为一级保护古树', 0, 1715000000000, 1715000000000);
INSERT INTO tree (id, tree_code, name, species, age, height, latitude, longitude, `desc`, is_delete, create_time, update_time) VALUES (8, '008', '圆明园古柏', '侧柏', 300, '12米', 40.00800000, 116.29800000, '位于北京市圆明园，为一级保护古树', 0, 1715000000000, 1715000000000);

-- auto-generated definition
create table user
(
    id              bigint auto_increment comment '主键ID'
        primary key,
    login_name      varchar(50) not null comment '登陆名',
    name            varchar(50) null comment '昵称/名称',
    password        char(32)    not null comment '密码（MD5加密）',
    age             int         null comment '年龄',
    `desc`          text        null comment '用户介绍',
    per_role_id     bigint      null comment '简易权限角色',
    last_login_time bigint      null comment '上次登录时间戳',
    constraint login_name_unique
        unique (login_name)
)
    comment '用户信息表' charset = utf8mb4;

create index idx_role_id
    on user (per_role_id);
    comment '用户' charset = utf8mb4;
            
INSERT INTO user (id, login_name, name, password, age, `desc`, per_role_id, last_login_time) VALUES (1, 'admin', '系统管理员', '5ca679820249600c173480ba769126ed', 31, '系统管理员账号', 1, 1779031613802);
INSERT INTO user (id, login_name, name, password, age, `desc`, per_role_id, last_login_time) VALUES (2, 'zhangsan', '张三', '5ca679820249600c173480ba769126ed', 28, '同学1', 2, 1779029574552);
INSERT INTO user (id, login_name, name, password, age, `desc`, per_role_id, last_login_time) VALUES (3, 'lisi', '李四', '5ca679820249600c173480ba769126ed', 32, '同学2', 2, 1713000000000);
INSERT INTO user (id, login_name, name, password, age, `desc`, per_role_id, last_login_time) VALUES (4, 'wangwu', '王五', '5ca679820249600c173480ba769126ed', 26, '同学3', 2, 1712000000000);
INSERT INTO user (id, login_name, name, password, age, `desc`, per_role_id, last_login_time) VALUES (5, 'zhaoliu', '赵六', '5ca679820249600c173480ba769126ed', 24, '同学4', 2, 1711000000000);
INSERT INTO user (id, login_name, name, password, age, `desc`, per_role_id, last_login_time) VALUES (6, 'sunqi', '孙七', '5ca679820249600c173480ba769126ed', 29, '操作员1', 2, 1710000000000);
INSERT INTO user (id, login_name, name, password, age, `desc`, per_role_id, last_login_time) VALUES (7, 'zhouba', '周八', '5ca679820249600c173480ba769126ed', 27, '同学5', 2, 1709000000000);
INSERT INTO user (id, login_name, name, password, age, `desc`, per_role_id, last_login_time) VALUES (8, 'wujiu', '吴九', '5ca679820249600c173480ba769126ed', 25, '同学6', 2, 1708000000000);
INSERT INTO user (id, login_name, name, password, age, `desc`, per_role_id, last_login_time) VALUES (9, 'zhengshi', '郑十', '5ca679820249600c173480ba769126ed', 26, '同学7', 2, 1707000000000);
INSERT INTO user (id, login_name, name, password, age, `desc`, per_role_id, last_login_time) VALUES (10, 'chengyi', '程一', '5ca679820249600c173480ba769126ed', 31, '同学8', 2, 1706000000000);


