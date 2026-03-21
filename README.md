# Campus Service System

一个以校园交易为主、附带社区互动能力的综合服务平台，主要包含商品交易、论坛交流、私信沟通和后台管理等模块，面向校园场景下的信息流转与交易协作需求。

## 项目亮点

- 以校园交易为核心业务，形成"浏览商品/社区交流/私信沟通/后台治理"的完整业务闭环
- 基于 Spring Boot + Vue3 实现前后端分离架构
- 使用 Redis 对热点商品、帖子等高频访问数据进行缓存优化
- 使用 RabbitMQ 处理异步业务，提升系统解耦能力
- 基于 WebSocket 实现用户之间的实时私信通信
- 集成后台管理能力，支持用户、帖子、商品等内容治理

## 核心模块

- 交易模块：商品发布、商品浏览、订单相关业务
- 论坛模块：帖子发布、评论互动、社区交流
- 私信模块：买卖双方实时沟通
- 后台管理模块：内容审核、用户管理、基础运营治理

## 技术栈

### 后端

Spring Boot、Spring MVC、MyBatis、MySQL、Redis、RabbitMQ、WebSocket

### 前端

Vue3、TypeScript、Pinia、Axios、Element Plus

### 工程化

Maven、Git、阿里云 OSS

## 项目结构

```
Campus-Service-System
├── Jsyl-springboot-background/      # 后端服务
├── Jsyl-springboot-vue3-front/     # 前端项目
├── docs/                           # 项目文档
└── jsyl_ms.sql                     # 数据库初始化脚本
```

## 系统架构

- 前端：负责页面展示、状态管理与接口调用
- 后端：负责业务逻辑处理、数据访问与权限控制
- MySQL：负责业务数据持久化存储
- Redis：负责热点数据缓存
- RabbitMQ：负责异步消息处理
- WebSocket：负责私信实时通信

## 快速启动

### 环境要求

- JDK 17+
- MySQL 8.x
- Redis 6.x
- RabbitMQ 3.x
- Node.js 18+
- Maven 3.9+

### 后端启动

1. 导入 `jsyl_ms.sql` 初始化数据库
2. 修改后端配置文件中的 MySQL、Redis、RabbitMQ 连接信息
3. 进入 `Jsyl-springboot-background/` 目录
4. 执行 `mvn clean install`
5. 启动后端主服务

### 前端启动

1. 进入 `Jsyl-springboot-vue3-front/` 目录
2. 执行 `npm install` 安装依赖
3. 执行 `npm run dev` 启动前端

## 项目截图

![5408f322-1dd4-4a3c-9747-76d0a49480b4](file:///C:/Users/20886/Pictures/Typedown/5408f322-1dd4-4a3c-9747-76d0a49480b4.png)

![b924f895-9e08-4613-8ea1-3623701365ba](file:///C:/Users/20886/Pictures/Typedown/b924f895-9e08-4613-8ea1-3623701365ba.png)

![c80eefcd-a871-46b6-ab94-46ff9757e880](file:///C:/Users/20886/Pictures/Typedown/c80eefcd-a871-46b6-ab94-46ff9757e880.png)

![99822c6d-b3c3-439b-a909-1c3e9ae8627e](file:///C:/Users/20886/Pictures/Typedown/99822c6d-b3c3-439b-a909-1c3e9ae8627e.png)

## 文档导航

- [用户认证模块](./docs/01_用户认证模块.md)
- [订单交易模块](./docs/02_订单模块.md)
- [帖子论坛模块](./docs/03_帖子模块.md)
- [评论互动模块](./docs/04_评论模块.md)
- [通知消息模块](./docs/05_通知消息模块.md)
- [签到系统模块](./docs/06_签到模块.md)
- [秒杀活动模块](./docs/07_秒杀模块.md)
- [私信聊天模块](./docs/08_私信聊天模块.md)
- [管理后台模块](./docs/09_管理后台模块.md)
