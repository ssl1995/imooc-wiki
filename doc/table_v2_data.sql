INSERT INTO `wiki-dev`.category (id, parent, name, sort) VALUES (100, 0, '前端开发', 100);
INSERT INTO `wiki-dev`.category (id, parent, name, sort) VALUES (101, 100, 'Vue', 101);
INSERT INTO `wiki-dev`.category (id, parent, name, sort) VALUES (102, 100, 'HTML & CSS', 102);
INSERT INTO `wiki-dev`.category (id, parent, name, sort) VALUES (200, 0, 'Java', 200);
INSERT INTO `wiki-dev`.category (id, parent, name, sort) VALUES (201, 200, '基础应用', 201);
INSERT INTO `wiki-dev`.category (id, parent, name, sort) VALUES (202, 200, '框架应用', 202);
INSERT INTO `wiki-dev`.category (id, parent, name, sort) VALUES (300, 0, 'Python', 300);
INSERT INTO `wiki-dev`.category (id, parent, name, sort) VALUES (301, 300, '基础应用', 301);
INSERT INTO `wiki-dev`.category (id, parent, name, sort) VALUES (302, 300, '进阶方向应用', 302);
INSERT INTO `wiki-dev`.category (id, parent, name, sort) VALUES (400, 0, '数据库', 400);
INSERT INTO `wiki-dev`.category (id, parent, name, sort) VALUES (401, 400, 'MySQL', 401);
INSERT INTO `wiki-dev`.category (id, parent, name, sort) VALUES (500, 0, '古树名木', 500);
INSERT INTO `wiki-dev`.category (id, parent, name, sort) VALUES (501, 500, '图片上传', 501);
INSERT INTO `wiki-dev`.category (id, parent, name, sort) VALUES (502, 500, '图片管理', 502);
INSERT INTO `wiki-dev`.category (id, parent, name, sort) VALUES (355744362908487680, 400, 'Oracle', 402);

INSERT INTO `wiki-dev`.content (id, content) VALUES (1, '<p>什么是SpringBoot</p><ul><li>学习网站：https://www.bilibili.com/video/BV1PE411i7CV?p=1</li></ul>');
INSERT INTO `wiki-dev`.content (id, content) VALUES (2, '<ul><li>1、基于POJO的轻量级和最小侵入性编程，所有东西都是bean；</li><li>2、通过IOC，依赖注入（DI）和面向接口实现松耦合；</li><li>3、基于切面（AOP）和惯例进行声明式编程；</li><li>4、通过切面和模版减少样式代码，RedisTemplate，xxxTemplate；</li></ul>');

INSERT INTO `wiki-dev`.doc (id, ebook_id, parent, name, sort, view_count, vote_count) VALUES (1, 1, 0, 'HelloWord', 1, 2, 0);
INSERT INTO `wiki-dev`.doc (id, ebook_id, parent, name, sort, view_count, vote_count) VALUES (2, 1, 1, 'Spring是怎么简化开发', 2, 1, 0);
INSERT INTO `wiki-dev`.doc (id, ebook_id, parent, name, sort, view_count, vote_count) VALUES (3, 1, 0, '文档2', 2, 0, 0);
INSERT INTO `wiki-dev`.doc (id, ebook_id, parent, name, sort, view_count, vote_count) VALUES (4, 1, 3, '文档2.1', 1, 0, 0);
INSERT INTO `wiki-dev`.doc (id, ebook_id, parent, name, sort, view_count, vote_count) VALUES (5, 1, 3, '文档2.2', 2, 0, 0);
INSERT INTO `wiki-dev`.doc (id, ebook_id, parent, name, sort, view_count, vote_count) VALUES (6, 1, 5, '文档2.2.1', 1, 0, 0);

INSERT INTO `wiki-dev`.ebook (id, name, category1_id, category2_id, description, cover, doc_count, view_count, vote_count) VALUES (1, 'Spring Boot 入门教程', 200, 202, '零基础入门 Java 开发，企业级应用开发最佳首选框架', null, 6, 3, 0);
INSERT INTO `wiki-dev`.ebook (id, name, category1_id, category2_id, description, cover, doc_count, view_count, vote_count) VALUES (2, 'Vue 入门教程', 100, 101, '零基础入门 Vue 开发，企业级应用开发最佳首选框架', null, 0, 0, 0);
INSERT INTO `wiki-dev`.ebook (id, name, category1_id, category2_id, description, cover, doc_count, view_count, vote_count) VALUES (3, 'Python 入门教程', 300, 301, '零基础入门 Python 开发，企业级应用开发最佳首选框架', null, 0, 0, 0);
INSERT INTO `wiki-dev`.ebook (id, name, category1_id, category2_id, description, cover, doc_count, view_count, vote_count) VALUES (4, 'Mysql 入门教程', 400, 401, '零基础入门 Mysql 开发，企业级应用开发最佳首选框架', null, 0, 0, 0);
INSERT INTO `wiki-dev`.ebook (id, name, category1_id, category2_id, description, cover, doc_count, view_count, vote_count) VALUES (5, 'Oracle 入门教程', 400, 355744362908487680, '零基础入门 Oracle 开发，企业级应用开发最佳首选框架', null, 0, 0, 0);

INSERT INTO `wiki-dev`.ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (1, 1, '2023-09-09', 1, 0, 1, 0);
INSERT INTO `wiki-dev`.ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (2, 2, '2023-09-09', 0, 0, 0, 0);
INSERT INTO `wiki-dev`.ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (3, 3, '2023-09-09', 0, 0, 0, 0);
INSERT INTO `wiki-dev`.ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (4, 4, '2023-09-09', 0, 0, 0, 0);
INSERT INTO `wiki-dev`.ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (5, 5, '2023-09-09', 0, 0, 0, 0);
INSERT INTO `wiki-dev`.ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (8, 1, '2022-05-20', 89, 15, 89, 15);
INSERT INTO `wiki-dev`.ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (9, 2, '2022-05-20', 0, 1, 0, 0);
INSERT INTO `wiki-dev`.ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (10, 3, '2022-05-20', 0, 2, 0, 0);
INSERT INTO `wiki-dev`.ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (11, 4, '2022-05-20', 2, 0, 23, 0);
INSERT INTO `wiki-dev`.ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (12, 5, '2022-05-20', 231, 0, 12, 23);
INSERT INTO `wiki-dev`.ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (13, 1, '2022-05-10', 223, 213, 123, 123);
INSERT INTO `wiki-dev`.ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (14, 2, '2022-05-10', 0, 1, 312, 0);
INSERT INTO `wiki-dev`.ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (15, 3, '2022-05-10', 123, 2, 213, 0);
INSERT INTO `wiki-dev`.ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (16, 4, '2022-05-10', 2, 0, 23, 123);
INSERT INTO `wiki-dev`.ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (17, 5, '2022-05-10', 231, 0, 12, 23);
INSERT INTO `wiki-dev`.ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (18, 1, '2023-09-10', 3, 0, 2, 0);
INSERT INTO `wiki-dev`.ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (19, 2, '2023-09-10', 0, 0, 0, 0);
INSERT INTO `wiki-dev`.ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (20, 3, '2023-09-10', 0, 0, 0, 0);
INSERT INTO `wiki-dev`.ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (21, 4, '2023-09-10', 0, 0, 0, 0);
INSERT INTO `wiki-dev`.ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (22, 5, '2023-09-10', 0, 0, 0, 0);
INSERT INTO `wiki-dev`.ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (23, 1, '2025-03-29', 3, 0, 3, 0);
INSERT INTO `wiki-dev`.ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (24, 2, '2025-03-29', 0, 0, 0, 0);
INSERT INTO `wiki-dev`.ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (25, 3, '2025-03-29', 0, 0, 0, 0);
INSERT INTO `wiki-dev`.ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (26, 4, '2025-03-29', 0, 0, 0, 0);
INSERT INTO `wiki-dev`.ebook_snapshot (id, ebook_id, date, view_count, vote_count, view_increase, vote_increase) VALUES (27, 5, '2025-03-29', 0, 0, 0, 0);

INSERT INTO `wiki-dev`.user (id, login_name, name, password) VALUES (1, 'admin', '管理员', '5ca679820249600c173480ba769126ed');
