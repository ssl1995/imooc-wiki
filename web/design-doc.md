# 古树名木多模态检索系统 - Web界面设计文档

## 一、页面设计概述

本文档描述古树名木多模态检索系统的两个核心页面设计：
- **图4.8**：数据上传页面（管理员使用）
- **图4.9**：检索页面（用户使用，支持三种检索模式）

## 二、图4.8 数据上传页面设计

### 2.1 页面布局

```
┌─────────────────────────────────────────────────────────────────┐
│  古树名木数据上传 - 管理员数据录入界面                            │
├──────────────────────────────────────┬──────────────────────────┤
│                                      │                          │
│  ┌────────────────────────────────┐  │  ┌────────────────────┐  │
│  │      基础信息录入               │  │  │     图像上传        │  │
│  ├────────────────────────────────┤  │  ├────────────────────┤  │
│  │  古树名称 * [______________]   │  │  │                    │  │
│  │  物种分类 * [▼选择物种_______]  │  │  │   [拖拽上传区域]    │  │
│  │                                │  │  │                    │  │
│  │  地理位置 *                    │  │  │   📁 点击或拖拽     │  │
│  │  纬度 [________] 经度 [_______]│  │  │      文件到此区域   │  │
│  │  💡 示例：纬度39.9042°N...     │  │  │                    │  │
│  │                                │  │  │   支持单张或多张    │  │
│  │  树龄 [________] 年            │  │  │   仅支持JPG/PNG    │  │
│  │  树高 [________] 米            │  │  │                    │  │
│  │                                │  │  └────────────────────┘  │
│  │  保护级别 ○一级 ○二级 ○三级    │  │  │    图片预览区域     │  │
│  │                                │  │  │  [图1] [图2] [图3]  │  │
│  │  拍摄日期 [选择日期________]   │  │  │   ×    ×    ×     │  │
│  │                                │  │  └────────────────────┘  │
│  │  备注 [____________________]   │  │  ┌────────────────────┐  │
│  │       [____________________]   │  │  │  [提交数据] [重置]  │  │
│  └────────────────────────────────┘  │  └────────────────────┘  │
│                                      │                          │
└──────────────────────────────────────┴──────────────────────────┘
```

### 2.2 组件清单

| 组件名称 | 类型 | 用途 |
|---------|------|------|
| `a-page-header` | Ant Design Vue | 页面标题栏 |
| `a-card` | Ant Design Vue | 信息卡片容器 |
| `a-form` / `a-form-item` | Ant Design Vue | 表单布局 |
| `a-input` | Ant Design Vue | 文本输入（古树名称） |
| `a-select` | Ant Design Vue | 物种分类下拉选择 |
| `a-input-number` | Ant Design Vue | 数字输入（经纬度、树龄、树高） |
| `a-radio-group` / `a-radio` | Ant Design Vue | 保护级别单选 |
| `a-date-picker` | Ant Design Vue | 拍摄日期选择 |
| `a-textarea` | Ant Design Vue | 备注多行文本 |
| `a-upload-dragger` | Ant Design Vue | 拖拽上传区域 |
| `a-image` / `a-image-preview-group` | Ant Design Vue | 图片预览 |
| `a-button` | Ant Design Vue | 提交/重置按钮 |
| `FileImageOutlined` | @ant-design/icons-vue | 古树图标 |
| `EnvironmentOutlined` | @ant-design/icons-vue | 位置图标 |
| `InboxOutlined` | @ant-design/icons-vue | 上传图标 |
| `DeleteOutlined` | @ant-design/icons-vue | 删除图标 |
| `UploadOutlined` | @ant-design/icons-vue | 提交图标 |
| `ReloadOutlined` | @ant-design/icons-vue | 重置图标 |

### 2.3 关键代码结构

```vue
<template>
  <div class="data-upload-container">
    <a-page-header title="古树名木数据上传" />
    <a-row :gutter="24">
      <!-- 左侧：表单 -->
      <a-col :span="14">
        <a-card title="基础信息录入">
          <a-form>
            <!-- 表单字段 -->
          </a-form>
        </a-card>
      </a-col>
      <!-- 右侧：上传 -->
      <a-col :span="10">
        <a-card title="图像上传">
          <a-upload-dragger />
          <!-- 预览区域 -->
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>
```

## 三、图4.9 检索页面设计

### 3.1 页面布局

```
┌─────────────────────────────────────────────────────────────────┐
│                    🌳 古树名木多模态检索系统                      │
│              融合图像与地理位置元数据的智能检索                    │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│     [以图搜图(I2I)]  [以图搜位置(I2L)]  [以位置搜图(L2I)]        │
│                                                                 │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────────┐  ┌──────────────────────────────────────┐ │
│  │   上传查询图像    │  │           检索结果                    │ │
│  ├──────────────────┤  ├──────────────────────────────────────┤ │
│  │                  │  │                                      │ │
│  │  [拖拽上传区域]   │  │  ┌──────┐ ┌──────┐ ┌──────┐         │ │
│  │                  │  │  │ 图1  │ │ 图2  │ │ 图3  │         │ │
│  │   📷 点击或拖拽   │  │  │银杏  │ │古柏  │ │九龙松│         │ │
│  │      上传图片    │  │  │98.5% │ │93.2% │ │88.7% │         │ │
│  │                  │  │  └──────┘ └──────┘ └──────┘         │ │
│  │  支持JPG/PNG     │  │                                      │ │
│  │                  │  │  ┌──────┐ ┌──────┐ ┌──────┐         │ │
│  │  [图片预览]      │  │  │ 图4  │ │ 图5  │ │ 图6  │         │ │
│  │                  │  │  │唐槐  │ │宋柏  │ │明槐  │         │ │
│  ├──────────────────┤  │  │85.3% │ │82.1% │ │78.9% │         │ │
│  │   检索参数        │  │  └──────┘ └──────┘ └──────┘         │ │
│  │                  │  │                                      │ │
│  │  返回结果数      │  │                                      │ │
│  │  [====●====] 20  │  │                                      │ │
│  │                  │  │                                      │ │
│  │  相似度阈值      │  │                                      │ │
│  │  [====●====] 0.7 │  │                                      │ │
│  │                  │  │                                      │ │
│  │  [开始检索]      │  │                                      │ │
│  └──────────────────┘  └──────────────────────────────────────┘ │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 3.2 三种检索模式

#### 模式1：I2I (Image-to-Image) - 以图搜图
- **输入**：上传一张古树图片
- **参数**：返回结果数、相似度阈值
- **输出**：相似图片列表（带相似度百分比）

#### 模式2：I2L (Image-to-Location) - 以图搜位置
- **输入**：上传一张古树图片
- **输出**：
  - 预测位置（经纬度）
  - 置信度进度条
  - 误差范围
  - 地址描述
  - 地图显示区域

#### 模式3：L2I (Location-to-Image) - 以位置搜图
- **输入**：
  - 纬度、经度输入框
  - 搜索半径选择（<1km, <5km, <10km, <50km）
  - 返回结果数
  - 快速选择按钮（北京、上海、成都、广州）
- **输出**：附近古树图片列表（带距离标签）

### 3.3 组件清单

| 组件名称 | 类型 | 用途 |
|---------|------|------|
| `a-radio-group` / `a-radio-button` | Ant Design Vue | 模式切换标签 |
| `a-card` | Ant Design Vue | 卡片容器 |
| `a-upload-dragger` | Ant Design Vue | 图片上传 |
| `a-image` | Ant Design Vue | 图片显示 |
| `a-slider` | Ant Design Vue | 参数滑块 |
| `a-button` | Ant Design Vue | 操作按钮 |
| `a-input-number` | Ant Design Vue | 坐标输入 |
| `a-radio-group` / `a-radio` | Ant Design Vue | 半径选择 |
| `a-descriptions` | Ant Design Vue | 位置信息展示 |
| `a-progress` | Ant Design Vue | 置信度进度条 |
| `a-alert` | Ant Design Vue | 结果数量提示 |
| `a-tag` | Ant Design Vue | 相似度/距离标签 |
| `a-empty` | Ant Design Vue | 空状态 |
| `a-divider` | Ant Design Vue | 分隔线 |
| `PartitionOutlined` | @ant-design/icons-vue | 系统图标 |
| `PictureOutlined` | @ant-design/icons-vue | I2I模式图标 |
| `FileImageOutlined` | @ant-design/icons-vue | I2L模式图标 |
| `EnvironmentOutlined` | @ant-design/icons-vue | L2I模式图标 |
| `CameraOutlined` | @ant-design/icons-vue | 上传图标 |
| `SearchOutlined` | @ant-design/icons-vue | 检索图标 |
| `CalendarOutlined` | @ant-design/icons-vue | 树龄图标 |
| `GlobalOutlined` | @ant-design/icons-vue | 地图图标 |

### 3.4 关键代码结构

```vue
<template>
  <div class="retrieval-container">
    <!-- 页面头部 -->
    <div class="retrieval-header">
      <h1>古树名木多模态检索系统</h1>
    </div>
    
    <!-- 模式切换 -->
    <div class="mode-tabs">
      <a-radio-group v-model:value="currentMode">
        <a-radio-button value="i2i">以图搜图</a-radio-button>
        <a-radio-button value="i2l">以图搜位置</a-radio-button>
        <a-radio-button value="l2i">以位置搜图</a-radio-button>
      </a-radio-group>
    </div>
    
    <!-- I2I 模式 -->
    <div v-if="currentMode === 'i2i'">
      <a-row :gutter="24">
        <a-col :span="8"><!-- 上传区域 --></a-col>
        <a-col :span="16"><!-- 结果展示 --></a-col>
      </a-row>
    </div>
    
    <!-- I2L 模式 -->
    <div v-if="currentMode === 'i2l'">
      <!-- 类似布局，右侧显示位置信息 -->
    </div>
    
    <!-- L2I 模式 -->
    <div v-if="currentMode === 'l2i'">
      <!-- 左侧坐标输入，右侧图片结果 -->
    </div>
  </div>
</template>
```

## 四、路由配置

```typescript
// router/index.ts
const routes: Array<RouteRecordRaw> = [
  {
    path: '/retrieval',
    name: 'Retrieval',
    component: Retrieval
  },
  {
    path: '/admin/data-upload',
    name: 'AdminDataUpload',
    component: AdminDataUpload,
    meta: { loginRequire: true }
  }
];
```

## 五、导航菜单更新

```vue
<!-- the-header.vue -->
<a-menu>
  <a-menu-item key="/">
    <router-link to="/">首页</router-link>
  </a-menu-item>
  <a-menu-item key="/retrieval">
    <router-link to="/retrieval">多模态检索</router-link>
  </a-menu-item>
  <a-menu-item key="/admin/data-upload">
    <router-link to="/admin/data-upload">数据上传</router-link>
  </a-menu-item>
</a-menu>
```

## 六、样式设计要点

### 6.1 颜色方案
- **主色调**：#1890ff（Ant Design 蓝色）
- **背景色**：渐变背景 `linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%)`
- **成功色**：#52c41a
- **卡片背景**：#ffffff

### 6.2 布局特点
- 使用 `a-row` + `a-col` 实现响应式栅格布局
- 左侧输入区域占 8/24，右侧结果区域占 16/24
- 卡片圆角 12px，阴影 `0 4px 12px rgba(0, 0, 0, 0.08)`

### 6.3 交互细节
- 图片悬停上浮效果 `transform: translateY(-4px)`
- 相似度标签颜色根据数值变化（绿→青→蓝→橙）
- 上传区域拖拽状态反馈

## 七、文件清单

| 文件路径 | 说明 |
|---------|------|
| `src/views/admin/admin-data-upload.vue` | 图4.8 数据上传页面 |
| `src/views/retrieval.vue` | 图4.9 检索页面 |
| `src/router/index.ts` | 路由配置（已更新） |
| `src/components/the-header.vue` | 导航头部（已更新） |
| `design-doc.md` | 本文档 |

## 八、运行方式

```bash
cd web
npm install
npm run serve
```

访问：
- 检索页面：`http://localhost:8080/retrieval`
- 数据上传页面：`http://localhost:8080/admin/data-upload`（需登录）
