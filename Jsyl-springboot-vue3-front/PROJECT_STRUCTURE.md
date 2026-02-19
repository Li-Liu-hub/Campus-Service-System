# 项目结构优化文档

## 概述
本文档描述了项目的优化后目录结构，遵循软件工程最佳实践和模块化设计原则。

## 目录结构

```
src/
├── api/                          # API层 - 数据访问接口
│   ├── index.ts                  # API统一导出
│   ├── auth/                     # 认证模块API
│   │   ├── types.ts              # 认证相关类型定义
│   │   └── index.ts              # 认证API函数
│   ├── user/                     # 用户模块API
│   │   ├── types.ts              # 用户相关类型定义
│   │   └── index.ts              # 用户API函数
│   └── order/                    # 订单模块API
│       ├── types.ts              # 订单相关类型定义
│       └── index.ts              # 订单API函数
│
├── assets/                       # 静态资源
│   ├── images/                   # 图片资源
│   ├── icons/                    # 图标资源
│   └── styles/                   # 全局样式
│       └── variables.scss        # 样式变量
│
├── components/                   # 公共组件
│   ├── common/                   # 通用组件
│   │   ├── Button/               # 按钮组件
│   │   ├── Input/                # 输入框组件
│   │   ├── Card/                 # 卡片组件
│   │   ├── Table/                # 表格组件
│   │   ├── Modal/                # 弹窗组件
│   │   └── Loading/              # 加载组件
│   ├── layout/                   # 布局组件
│   │   ├── Sidebar/              # 侧边栏组件
│   │   ├── Header/               # 顶部导航组件
│   │   └── Footer/               # 底部组件
│   └── business/                 # 业务组件
│       ├── Main/                 # 主页控制台组件
│       ├── OrderCenter/          # 订单中心组件
│       └── Profile/              # 个人中心组件
│
├── composables/                  # 组合式函数 (Vue 3)
│   ├── index.ts                  # 统一导出
│   ├── useAuth.ts                # 认证相关逻辑
│   ├── useUser.ts                # 用户相关逻辑
│   ├── useTable.ts               # 表格相关逻辑
│   └── useToast.ts               # 消息提示相关逻辑
│
├── constants/                    # 常量定义
│   ├── index.ts                  # 统一导出
│   ├── api.ts                    # API路径常量
│   ├── routes.ts                 # 路由常量
│   └── config.ts                 # 配置常量
│
├── hooks/                        # 自定义Hooks (兼容性)
│   └── index.ts
│
├── layouts/                      # 页面布局
│   ├── AuthLayout.vue            # 认证页面布局
│   ├── DefaultLayout.vue         # 默认页面布局
│   └── index.ts                  # 布局统一导出
│
├── router/                       # 路由配置
│   ├── index.ts                  # 路由入口
│   ├── modules/                  # 路由模块
│   │   ├── auth.ts               # 认证路由
│   │   └── main.ts               # 主要路由
│   ├── guard.ts                  # 路由守卫
│   └── types.ts                  # 路由类型定义
│
├── stores/                       # Pinia状态管理
│   ├── index.ts                  # Store统一导出
│   ├── auth.ts                   # 认证状态
│   ├── user.ts                   # 用户状态
│   └── app.ts                    # 应用状态
│
├── types/                        # 类型定义
│   ├── index.ts                  # 统一导出
│   ├── api.ts                    # API相关类型
│   ├── global.ts                 # 全局类型
│   └── common.ts                 # 通用类型
│
├── utils/                        # 工具函数
│   ├── index.ts                  # 统一导出
│   ├── request.ts                # 请求封装
│   ├── validate.ts               # 验证规则
│   ├── storage.ts                # 本地存储
│   ├── logger.ts                 # 日志工具
│   ├── format.ts                 # 格式化工具
│   └── helpers.ts                # 通用辅助函数
│
├── views/                        # 页面视图
│   ├── auth/                     # 认证页面
│   │   ├── Login.vue             # 登录页面
│   │   └── Register.vue          # 注册页面 (新增)
│   ├── home/                     # 首页模块
│   │   ├── index.vue             # 首页容器
│   │   └── Main.vue              # 控制台
│   ├── order/                    # 订单模块
│   │   └── OrderCenter.vue       # 订单中心
│   └── user/                     # 用户模块
│       └── Profile.vue           # 个人中心
│
├── App.vue                       # 根组件
├── main.ts                       # 应用入口
└── style.css                     # 全局样式
```

## 设计原则

### 1. 分层架构
- **API层**：负责数据访问和网络请求
- **业务逻辑层**：通过Composables和Stores实现
- **表示层**：Views和Components负责UI展示
- **公共层**：Utils、Constants、Types提供基础支持

### 2. 模块划分
- 按功能域划分模块（auth、user、order等）
- 每个模块内部保持高内聚
- 模块间通过明确的接口进行低耦合通信

### 3. 可扩展性
- 新增功能只需在对应模块添加
- 公共组件可复用
- 支持配置化和插件化

### 4. 可维护性
- 统一的代码风格和命名规范
- 清晰的目录结构便于定位
- 充分的类型定义和文档注释

## 文件命名规范

### 组件文件
- 大写驼峰命名：`UserProfile.vue`
- 单个组件一个文件
- 相关组件放在同一目录

### 工具/类型文件
- 小写连字符命名：`user-utils.ts`
- 功能单一明确
- 提供充分的文档注释

### 常量/配置文件
- 大写蛇形命名常量：`API_BASE_URL`
- 相关常量组织在同一文件

## 依赖关系

```
views → components → composables → stores
                                ↓
views → composables → stores → api → utils
                          ↓
                      constants/types
```

## 迁移指南

### 1. API模块
- 将原 `api/user.ts` 移至 `api/user/`
- 将原 `api/orderCenter.ts` 移至 `api/order/`
- 新增 `api/auth/` 模块

### 2. 组件
- 将原 `components/` 中的组件分类
- 布局组件移至 `components/layout/`
- 业务组件移至 `components/business/`

### 3. 页面
- 认证页面移至 `views/auth/`
- 首页相关移至 `views/home/`
- 订单相关移至 `views/order/`
- 用户相关移至 `views/user/`

## 新增功能说明

### 用户注册功能
- 新增 `views/auth/Register.vue` 注册页面
- 新增 `api/auth/index.ts` 注册API
- 新增表单验证规则
- 完善错误处理和成功反馈

## 测试策略

### 单元测试
- Utils函数需覆盖核心逻辑
- Composables需测试主要功能
- API模块需mock测试

### 集成测试
- 页面流程测试
- 路由跳转测试
- 状态管理测试
