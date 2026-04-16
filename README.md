# 文化遗产交流平台（Heritage Exchange）

这是一个面向“文化遗产图文交流平台”场景的 Spring Boot 项目。当前版本已经从基础后端，增量演进为包含**文章审核流、管理员后台、用户角色管理、贡献者申请流**的可联调版本。

## 技术栈

- Java 21
- Spring Boot 3.5.13
- Spring Web
- Spring Data JPA
- H2 / MySQL
- Maven Wrapper
- 静态前端页面（`src/main/resources/static`）

## 当前已实现能力

### 1. 基础内容能力

- 用户注册
- 用户登录
- 分类管理
- 图片上传到本地 `uploads`
- Public 文章列表与详情
- 评论发布

### 2. 文章草稿与审核流

文章状态机已经升级为：

- `DRAFT`
- `PENDING_REVIEW`
- `PUBLISHED`
- `ARCHIVED`
- `REJECTED`

当前规则：

- 新建文章默认保存为 `DRAFT`
- 用户可编辑自己的 `DRAFT / REJECTED`
- 用户可将 `DRAFT / REJECTED` 提交为 `PENDING_REVIEW`
- 管理员可将 `PENDING_REVIEW` 审核为 `PUBLISHED / REJECTED`
- 管理员可将 `PUBLISHED` 归档为 `ARCHIVED`
- 管理员可将 `ARCHIVED` 恢复为 `PUBLISHED`
- Public 端仅 `PUBLISHED` 可见
- 评论仅允许对 `PUBLISHED` 文章发布

文章审核相关字段已补充：

- `submittedAt`
- `reviewedAt`
- `reviewedBy`
- `rejectReason`

### 3. 用户侧内容工作台

已实现用户自己的文章工作流：

- 创建草稿
- 更新草稿 / 驳回稿
- 提交审核
- 查看我的文章列表
- 查看我的文章详情（覆盖所有状态）
- 从真实接口重新打开旧草稿继续编辑

### 4. 管理员后台：文章管理

管理员侧已实现：

- 查看所有文章
- 按状态筛选文章
- 按标题关键词搜索文章
- 按更新时间 / 提交时间 / 审核时间排序
- 查看文章详情
- 审核通过 / 驳回
- 归档 / 恢复发布

### 5. 管理员后台：Pending Review Queue

已实现独立待审核队列：

- 只显示 `PENDING_REVIEW` 文章
- 按 `submittedAt` 倒序展示
- 显示待审核数量
- 支持快速通过（Quick approve）
- 可跳转到文章详情进行完整审核

### 6. 管理员后台：用户管理

已实现真实 User Management：

- 查看用户列表
- 按用户名搜索
- 按角色筛选
- 查看用户角色、活跃状态、贡献数
- 管理 `USER <-> CONTRIBUTOR`
- 阻止将任何用户改成 `ADMIN`
- 阻止修改现有 `ADMIN` 的角色

其中 contributionCount 当前按“已发布文章数”计算。

### 7. 贡献者申请流

已实现最小可用闭环：

- `USER` 可提交成为 `CONTRIBUTOR` 的申请
- 用户可查看自己的申请记录
- 同一用户不能存在多个并行 `PENDING` 申请
- `CONTRIBUTOR / ADMIN` 不能再次申请
- 管理员可查看申请列表并按状态筛选
- 管理员可 approve / reject 申请
- approve 后自动将用户角色提升为 `CONTRIBUTOR`

申请状态包括：

- `PENDING`
- `APPROVED`
- `REJECTED`

## 当前鉴权方案

本轮仍采用**最小改动鉴权方案**，尚未切换到 JWT：

- 通过 `X-User-Id` 请求头识别当前用户
- `AuthContextService` 统一处理当前用户解析与 `ADMIN` 校验
- 已为后续切换 JWT 预留解析入口，避免重写文章审核与后台业务

## 本轮开发完成的 PBI 记录

### P0：文章审核主链路与稳定性

- 完成文章审核状态机升级
- 完成管理员审核、归档、恢复发布接口
- 完成用户草稿 / 驳回稿编辑与送审接口
- 增加接口级测试 `PostReviewFlowApiTests`
- 新增“我的文章详情”接口，支持真实重开旧草稿
- 前端改为优先通过真实接口回填草稿
- 修复本地 H2 历史枚举约束导致 `PENDING_REVIEW` 无法写入的问题

### P1：管理员文章管理增强

- 管理员文章标题搜索
- 状态筛选与标题搜索叠加
- Pending Review Queue 独立数据流
- 修复文章管理与待审队列搜索状态串扰问题
- 支持排序：
  - `UPDATED_DESC`
  - `SUBMITTED_DESC`
  - `REVIEWED_DESC`
- 增加结果数量、筛选摘要、排序摘要反馈

### P3：管理员用户与权限管理

- User Management 接真实后端
- 支持用户名搜索、角色筛选、角色调整
- 完成贡献者申请流最小闭环
- 用户侧申请区和管理员审批区都已接真实接口

## 主要接口（当前版本）

### 公共接口

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/categories`
- `GET /api/posts`
- `GET /api/posts/{id}`
- `POST /api/posts/{id}/comments`
- `POST /api/uploads/images`

### 用户侧接口

- `POST /api/posts` 创建草稿
- `PUT /api/posts/{id}` 更新草稿 / 驳回稿
- `POST /api/posts/{id}/submit-review`
- `GET /api/my/posts`
- `GET /api/my/posts/{id}`
- `GET /api/my/contributor-applications`
- `POST /api/my/contributor-applications`

### 管理员接口

- `GET /api/admin/posts`
- `GET /api/admin/posts/{id}`
- `POST /api/admin/posts/{id}/review`
- `POST /api/admin/posts/{id}/archive`
- `POST /api/admin/posts/{id}/restore`
- `GET /api/admin/users`
- `POST /api/admin/users/{id}/role`
- `GET /api/admin/contributor-applications`
- `POST /api/admin/contributor-applications/{id}/approve`
- `POST /api/admin/contributor-applications/{id}/reject`

## 运行方式

### 1. IntelliJ IDEA 直接运行

运行主类：

`com.heritage.platform.HeritagePlatformApplication`

默认使用 H2 文件数据库。当前项目已补充 `schema-h2.sql`，用于修正旧本地 H2 库在文章状态枚举升级后的兼容问题。

### 2. 命令行运行

```bash
./mvnw spring-boot:run
```

Windows：

```bash
.\mvnw.cmd spring-boot:run
```

### 3. 测试

```bash
./mvnw test
```

Windows：

```bash
.\mvnw.cmd test
```

## 当前已知限制

- 仍未切换到 JWT，当前鉴权仍依赖 `X-User-Id`
- 管理员文章管理尚未做分页
- 权限申请流暂未增加驳回原因与更完整的统计反馈
- 远程数据库配置不纳入默认仓库提交

## 建议下一步

- 切换到 JWT 鉴权
- 为管理员文章管理增加分页
- 增强权限申请流反馈（如驳回原因、审批结果展示）
- 继续补充接口级测试与联调说明
