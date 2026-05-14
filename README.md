# 古树名木多模态哈希检索系统

## 项目简介

基于深度哈希学习的古树名木多模态检索系统，融合图像与地理位置元数据，实现以图搜图（I2I）、以图搜位置（I2L）和以位置搜图（L2I）三种检索模式。

## 技术栈

- **后端**：Spring Boot + MySQL
- **前端**：Vue 3 + TypeScript + Ant Design Vue
- **算法模型**：DHLAM（Deep Hashing Learning Adaptive Multi-modal Network）

## 项目结构

```
imooc-wiki/
├── src/                    # 后端源码（Spring Boot）
│   └── main/
│       └── java/com/jiawa/wiki/config/WikiApplication.java
├── web/                    # 前端源码（Vue 3）
│   ├── package.json
│   └── src/
├── doc/                    # 数据库SQL脚本
├── algorithm/              # DHLAM模型训练与评估代码
└── word/                   # 论文文档
```

## 快速启动

### 1. 启动后端服务

**方式一：IDEA 直接运行**

打开 `src/main/java/com/jiawa/wiki/config/WikiApplication.java`，右键运行 `main` 方法。

**方式二：命令行启动**

```bash
# 进入项目根目录
mvn spring-boot:run
```

后端默认运行在 `http://localhost:8880`

### 2. 启动前端服务

```bash
# 进入前端目录
cd web

# 安装依赖（如未安装）
npm install

# 开发模式启动
npm run serve-dev
```

前端默认运行在 `http://localhost:8080`

### 3. 前端打包（生产环境）

```bash
cd web
npm run build-prod
```

打包后的静态文件位于 `web/dist/` 目录。

## 常用命令速查

| 命令 | 说明 | 目录 |
|:---|:---|:---|
| `mvn spring-boot:run` | 启动后端服务 | 项目根目录 |
| `npm run serve-dev` | 前端开发模式启动 | `web/` |
| `npm run serve-prod` | 前端生产模式预览 | `web/` |
| `npm run build-prod` | 前端生产打包 | `web/` |
| `npm run lint` | 前端代码检查 | `web/` |

## 数据库初始化

执行 `doc/table.sql` 和 `doc/table_v2.sql` 中的SQL语句初始化MySQL数据库。

## 论文相关

论文修改文档位于 `word/第0章/二审/` 目录。
