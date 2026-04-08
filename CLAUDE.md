# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Campus Service System (校园服务平台) - A full-stack application with:
- **Backend**: Spring Boot 3.2.5 (Maven multi-module) + MyBatis + MySQL + Redis
- **Frontend**: Vue 3 + Vite + TypeScript + Element Plus + Pinia

## Build Commands

### Backend (Jsyl-springboot-background)
```bash
cd Jsyl-springboot-background
mvn clean compile -DskipTests          # Compile only
mvn clean package -DskipTests          # Build JAR
mvn spring-boot:run                    # Run application
```

### Frontend (Jsyl-springboot-vue3-front)
```bash
cd Jsyl-springboot-vue3-front
npm install                            # Install dependencies
npm run dev                            # Development server
npm run build                          # Production build
```

## Architecture

### Backend Module Structure
```
Jsyl-springboot-background/
├── jsyl/                    # Parent POM (dependency management)
├── jsyl-common/             # Shared utilities and config
│   ├── annotation/          # Custom annotations (@AutoFill, @OperationLog, @RateLimit)
│   ├── aspect/              # AOP aspects (AutoFill, OperationLog, RateLimit)
│   ├── config/              #Configurations (Security, Redis, RabbitMQ, WebMvc, OSS)
│   ├── constant/            # Constants (JwtClaims, Status, Role)
│   ├── context/             # ThreadLocal context (BaseContext)
│   ├── exception/           # Custom exceptions
│   ├── handler/            # Exception handler (GlobalExceptionHandler)
│   ├── interceptor/         # JWT interceptors (admin & user)
│   ├── properties/          # Configuration properties
│   ├── result/              # Response wrapper (Result, PageResult)
│   └── utils/              # Utilities (JwtUtil, AliOssUtil, SensitiveWordFilter)
├── jsyl-pojo/              # Data models
│   └── com.jsyl.model/
│       ├── admin/          # Admin-related (OperationLog, SensitiveWord)
│       ├── campus/         # Campus info
│       ├── chat/           # Chat (Conversation, PrivateMessage)
│       ├── common/         # Common (SystemSettings)
│       ├── forum/          # Forum (Post, Comment, PostType)
│       ├── notification/    # Notifications
│       ├── operation/      # Sign-in records
│       ├── trade/          # Orders
│       └── user/           # User, Address, Collect
├── jsyl-server/            # Main application
│   └── com.jsyl.module/    # Business modules
│       ├── admin/          # Admin controllers
│       ├── campus/         # Campus info
│       ├── chat/           # WebSocket chat
│       ├── forum/          # Posts & comments
│       ├── notification/   # Notifications
│       ├── operation/      # Sign-in
│       ├── storage/        # File upload
│       ├── trade/          # Orders
│       └── user/           # User profile
```

### Frontend Structure
```
Jsyl-springboot-vue3-front/
├── src/
│   ├── api/                # API layer (auth, user, order modules)
│   ├── components/         # Common + layout + business components
│   ├── composables/        # Vue 3 composables (useAuth, useUser, useTable)
│   ├── layouts/            # Page layouts (Auth, Default)
│   ├── router/             # Vue Router with guards
│   ├── stores/             # Pinia stores (auth, user, app)
│   ├── types/              # TypeScript types
│   ├── utils/              # Request wrapper, validators, helpers
│   └── views/              # Pages (auth, home, order, user)
```

### Key Technology Stack
| Component | Technology |
|-----------|------------|
| Backend Framework | Spring Boot 3.2.5 |
| Security | Spring Security 6 + JWT (jjwt 0.12.x) |
| ORM | MyBatis 3.0.4 + PageHelper 2.1.0 |
| Database | MySQL + Druid 1.2.21 |
| Cache | Redis (Spring Data Redis) |
| Queue | RabbitMQ |
| File Storage | Aliyun OSS |
| Frontend | Vue 3.5 + Vite 7 + TypeScript |
| UI Framework | Element Plus 2.13 |
| State | Pinia 3.0 |
| Real-time | WebSocket (STOMP over SockJS) |
| Charts | ECharts 6.0 |

### Backend Dependencies Flow
- `jsyl-server` depends on `jsyl-common` + `jsyl-pojo`
- `jsyl-common` depends on `jsyl-pojo` (for entity types)
- `jsyl-pojo` has no internal dependencies

### API Response Format
```java
// Success
Result.success(data)           // 200 OK with data
Result.success()               // 200 OK without data

// Error
Result.error(msg)              // 500 with message
throw new XxxException(msg)    // Handled by GlobalExceptionHandler
```

### JWT Authentication
- Two token types: admin (for admin endpoints) and user (for user endpoints)
- Admin interceptor: `JwtTokenAdminInterceptor`
- User interceptor: `JwtTokenUserInterceptor`
- Claims contain: userId, username, role

### Interceptor Registration (WebMvcConfiguration)
- `/user/**` and `/campus/**` require user JWT → `JwtTokenUserInterceptor`
- `/admin/**` requires admin JWT → `JwtTokenAdminInterceptor`

## Database
- SQL files: `jsyl_ms.sql` (main schema)
- MyBatis mappers: `jsyl-server/src/main/resources/mapper/*.xml`

## Configuration
- Main config: `jsyl-server/src/main/resources/application.yml`
- Environment profiles: `application-dev.yml`, `application-prod.yml`
- Custom properties prefix: `jsyl.` (jwt, datasource, redis, wechat, oss)
