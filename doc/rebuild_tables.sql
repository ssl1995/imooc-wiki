-- ============================================================
-- 重建数据库表结构（对齐论文第四章表4.1-4.4）
-- 方案B：以论文为准，新建权限角色表、图像表，重建树木表
-- ============================================================

-- 权限角色表（表4.4）
DROP TABLE IF EXISTS per_role;
CREATE TABLE per_role (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    role_name VARCHAR(256) NOT NULL COMMENT '角色名称',
    permission_desc TEXT COMMENT '权限描述',
    is_delete TINYINT NOT NULL DEFAULT 0 COMMENT '是否逻辑删除 0:否 1:是',
    create_time BIGINT NOT NULL COMMENT '创建时间',
    update_time BIGINT NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限角色表';

-- 插入默认角色数据（仅2种：管理员、普通用户）
INSERT INTO per_role (id, role_name, permission_desc, is_delete, create_time, update_time) VALUES
(1, '管理员', '拥有系统全部操作权限，包括用户管理、古树名木管理、权限分配等', 0, 1715000000000, 1715000000000),
(2, '普通用户', '可执行古树名木检索、图像上传、个人信息管理等操作', 0, 1715000000000, 1715000000000);

-- 用户信息表（表4.1）
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `login_name` VARCHAR(50) NOT NULL COMMENT '登陆名',
    `name` VARCHAR(50) COMMENT '昵称/名称',
    `password` CHAR(32) NOT NULL COMMENT '密码（MD5加密）',
    `age` INT COMMENT '年龄',
    `desc` TEXT COMMENT '用户介绍',
    `per_role_id` BIGINT COMMENT '简易权限角色',
    `last_login_time` BIGINT COMMENT '上次登录时间戳',
    PRIMARY KEY (`id`),
    UNIQUE KEY `login_name_unique` (`login_name`),
    INDEX `idx_role_id` (`per_role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- 插入演示用户数据（与图4.6界面一致）
INSERT INTO `user` (id, login_name, `name`, `password`, age, `desc`, per_role_id, last_login_time) VALUES
(1, 'admin', '系统管理员', '5ca679820249600c173480ba769126ed', 35, '系统管理员账号', 1, 1716000000000),
(2, 'zhangsan', '张三', '5ca679820249600c173480ba769126ed', 28, '同学1', 2, 1714000000000),
(3, 'lisi', '李四', '5ca679820249600c173480ba769126ed', 32, '同学2', 2, 1713000000000),
(4, 'wangwu', '王五', '5ca679820249600c173480ba769126ed', 26, '同学3', 2, 1712000000000),
(5, 'zhaoliu', '赵六', '5ca679820249600c173480ba769126ed', 24, '同学4', 2, 1711000000000),
(6, 'sunqi', '孙七', '5ca679820249600c173480ba769126ed', 29, '操作员1', 2, 1710000000000),
(7, 'zhouba', '周八', '5ca679820249600c173480ba769126ed', 27, '同学5', 2, 1709000000000),
(8, 'wujiu', '吴九', '5ca679820249600c173480ba769126ed', 25, '同学6', 2, 1708000000000),
(9, 'zhengshi', '郑十', '5ca679820249600c173480ba769126ed', 26, '同学7', 2, 1707000000000),
(10, 'chengyi', '程一', '5ca679820249600c173480ba769126ed', 31, '同学8', 2, 1706000000000);

-- 图像表（表4.2）
DROP TABLE IF EXISTS image;
CREATE TABLE image (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    tree_id BIGINT NOT NULL COMMENT '关联树木ID',
    image_path VARCHAR(256) NOT NULL COMMENT '图像存储路径',
    hash_code VARCHAR(128) COMMENT '128位哈希码字符串',
    hash_bits BINARY(16) COMMENT '16字节二进制哈希位串',
    is_delete TINYINT NOT NULL DEFAULT 0 COMMENT '是否逻辑删除 0:否 1:是',
    create_time BIGINT NOT NULL COMMENT '创建时间',
    update_time BIGINT NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_tree_id (tree_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图像表';

-- 树木表（表4.3）—— 对齐论文，保留name/tree_code用于前端展示
DROP TABLE IF EXISTS tree;
CREATE TABLE tree (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    tree_code VARCHAR(20) COMMENT '古树编号（如001、002，用于前端固定匹配）',
    name VARCHAR(100) COMMENT '古树名称',
    species VARCHAR(256) COMMENT '树种名称',
    age INT COMMENT '树龄',
    height VARCHAR(64) COMMENT '树高',
    latitude DECIMAL(10,8) COMMENT '纬度',
    longitude DECIMAL(11,8) COMMENT '经度',
    `desc` TEXT COMMENT '描述信息',
    is_delete TINYINT NOT NULL DEFAULT 0 COMMENT '是否逻辑删除 0:否 1:是',
    create_time BIGINT NOT NULL COMMENT '创建时间',
    update_time BIGINT NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='树木表';

-- 插入演示数据（8棵古树）
INSERT INTO tree (tree_code, name, species, age, height, latitude, longitude, `desc`, is_delete, create_time, update_time) VALUES
('001', '景山万春亭古柏', '侧柏', 500, '15米', 39.92890000, 116.39740000, '位于北京市景山公园万春亭，为一级保护古树', 0, 1715000000000, 1715000000000),
('002', '天坛九龙柏', '侧柏', 600, '18米', 39.88330000, 116.40690000, '位于天坛公园回音壁西北侧，为一级保护古树', 0, 1715000000000, 1715000000000),
('003', '潭柘寺帝王银杏', '银杏', 1300, '25米', 39.90500000, 116.02800000, '位于北京市门头沟区潭柘寺寺院内，为特级保护古树', 0, 1715000000000, 1715000000000),
('004', '大觉寺千年银杏', '银杏', 950, '22米', 40.05100000, 116.09500000, '位于北京市海淀区大觉寺寺院内，为一级保护古树', 0, 1715000000000, 1715000000000),
('005', '北海团城古白皮松', '白皮松', 800, '20米', 39.92500000, 116.39000000, '位于北京市北海公园，为一级保护古树', 0, 1715000000000, 1715000000000),
('006', '颐和园佛香阁古柏', '侧柏', 400, '16米', 39.99900000, 116.27500000, '位于北京市颐和园，为二级保护古树', 0, 1715000000000, 1715000000000),
('007', '中山公园古柏', '侧柏', 400, '14米', 39.90600000, 116.39700000, '位于北京市中山公园，为一级保护古树', 0, 1715000000000, 1715000000000),
('008', '圆明园古柏', '侧柏', 300, '12米', 40.00800000, 116.29800000, '位于北京市圆明园，为一级保护古树', 0, 1715000000000, 1715000000000);

-- 插入图像数据（每棵树对应一张图，hash_code用于演示）
INSERT INTO image (tree_id, image_path, hash_code, hash_bits, is_delete, create_time, update_time)
SELECT id, 'algorithm/data_samples/bfath_demo/001_景山万春亭古柏/image.jpg',
       '10111101101111101000011011011000000110001001110000100101110111100010010100110001010010010001010000000010011000011101111001100001',
       UNHEX('00000000000000000000000000000000'), 0, 1715000000000, 1715000000000
FROM tree WHERE tree_code = '001';

INSERT INTO image (tree_id, image_path, hash_code, hash_bits, is_delete, create_time, update_time)
SELECT id, 'algorithm/data_samples/bfath_demo/002_天坛九龙柏/image.jpg',
       '10111101101111101000011011011000000110001001110000100101110111100010010100110001010010010001010000000010011000011101111001100001',
       UNHEX('00000000000000000000000000000000'), 0, 1715000000000, 1715000000000
FROM tree WHERE tree_code = '002';

INSERT INTO image (tree_id, image_path, hash_code, hash_bits, is_delete, create_time, update_time)
SELECT id, 'algorithm/data_samples/bfath_demo/003_潭柘寺帝王银杏/image.jpg',
       '10111101101111101000011011011000000110001001110000100101110111100010010100110001010010010001010000000010011000011101111001100001',
       UNHEX('00000000000000000000000000000000'), 0, 1715000000000, 1715000000000
FROM tree WHERE tree_code = '003';

INSERT INTO image (tree_id, image_path, hash_code, hash_bits, is_delete, create_time, update_time)
SELECT id, 'algorithm/data_samples/bfath_demo/004_大觉寺千年银杏/image.jpg',
       '10111101101111101000010011011000000110001001110000100101110111100010010100110001010010010001010000000010011000011101111001100001',
       UNHEX('00000000000000000000000000000000'), 0, 1715000000000, 1715000000000
FROM tree WHERE tree_code = '004';

INSERT INTO image (tree_id, image_path, hash_code, hash_bits, is_delete, create_time, update_time)
SELECT id, 'algorithm/data_samples/bfath_demo/005_北海团城古白皮松/image.jpg',
       '10111101101111101000011011011000000110001001110000100101110111100010010100110001010010010001010000000010011000011101111001100001',
       UNHEX('00000000000000000000000000000000'), 0, 1715000000000, 1715000000000
FROM tree WHERE tree_code = '005';

INSERT INTO image (tree_id, image_path, hash_code, hash_bits, is_delete, create_time, update_time)
SELECT id, 'algorithm/data_samples/bfath_demo/006_颐和园佛香阁古柏/image.jpg',
       '10111101101111101000011011011000000110001001110000100101110111100010010100110001010010010001010000000010011000011101111001100001',
       UNHEX('00000000000000000000000000000000'), 0, 1715000000000, 1715000000000
FROM tree WHERE tree_code = '006';

INSERT INTO image (tree_id, image_path, hash_code, hash_bits, is_delete, create_time, update_time)
SELECT id, 'algorithm/data_samples/bfath_demo/007_中山公园古柏/image.jpg',
       '10111101101111101000011011011000000110001001110000100101110111100010010100110001010010010001010000000010011000011101111001100001',
       UNHEX('00000000000000000000000000000000'), 0, 1715000000000, 1715000000000
FROM tree WHERE tree_code = '007';

INSERT INTO image (tree_id, image_path, hash_code, hash_bits, is_delete, create_time, update_time)
SELECT id, 'algorithm/data_samples/bfath_demo/008_圆明园古柏/image.jpg',
       '10111101101111101000011011011000000110001001110000100101110111100010010100110001010010010011010000000010011000011101111001100001',
       UNHEX('00000000000000000000000000000000'), 0, 1715000000000, 1715000000000
FROM tree WHERE tree_code = '008';
