# 文化遗产交流平台（Heritage Exchange）

这是一个面向**文化遗产内容展示、投稿、审核与社区交流**场景的 Spring Boot 项目。当前仓库版本基于 **JWT 鉴权链**，并已在此前基础上，增量完成了管理员后台、文章审核流、用户管理、贡献者申请流，以及一轮围绕后台效率和权限边界的稳定性增强。

本 README 重点记录**这一轮增量开发后的系统状态**，便于作为阶段性提交说明、联调依据与后续前端整理 sprint 的基线文档。

---

## 1. 当前版本定位

当前版本不是早期的 `X-User-Id` 轻量鉴权版本，而是已经切换到 **JWT + Spring Security** 的真实工作区版本，目标是提供一套可运行、可联调、可继续演进的文化资源平台后台与基础前台能力。

当前版本的核心方向：

- 完成 JWT 登录态闭环
- 完成文章草稿、送审、审核、归档、恢复发布全链路
- 完成管理员文章管理、待审队列、用户管理
- 完成贡献者申请与审批闭环
- 完成 active/inactive 用户启停控制
- 在不大改架构的前提下，逐步收紧权限边界、补足回归测试与前端最小可用体验

---

## 2. 技术栈

### 后端

- Java 21
- Spring Boot 3.5.13
- Spring Web
- Spring Data JPA
- Spring Security
- JWT（jjwt）
- H2 / MySQL
- Maven Wrapper

### 前端

- 静态前端页面
- `src/main/resources/static/index.html`
- `src/main/resources/static/app.js`

### 测试

- Spring Boot Test
- MockMvc
- 接口级回归测试
- JWT 登录与 Bearer 鉴权回归测试

---

## 3. 当前系统模块总览

当前系统可以分为以下几个核心模块：

1. **认证与权限模块**
2. **Public 内容浏览与评论模块**
3. **投稿草稿与文章审核模块**
4. **管理员文章管理模块**
5. **Pending Review Queue（待审核队列）模块**
6. **管理员用户管理模块**
7. **Contributor Application（贡献者申请）模块**
8. **用户侧 Permission Requests（权限申请记录）模块**

---

## 4. 当前鉴权与权限模型

### 4.1 JWT 鉴权链

当前版本已接通真实 JWT 主链：

- 用户登录成功后，后端返回 JWT token
- 前端将当前用户对象与 token 存入本地存储
- 前端受保护请求统一携带：
  - `Authorization: Bearer <token>`
- 后端通过 `JwtFilter` 解析 Bearer token
- Spring Security 负责安全过滤链与权限边界控制
- 业务层继续通过统一的当前用户解析方式完成用户/管理员校验

### 4.2 当前角色模型

系统当前使用以下角色：

- `USER`
- `CONTRIBUTOR`
- `ADMIN`

语义约定：

- `USER`：普通注册用户，可浏览、评论、申请成为贡献者
- `CONTRIBUTOR`：可创建草稿、编辑、送审、上传内容
- `ADMIN`：可审核文章、管理用户、处理贡献者申请

### 4.3 用户 active / inactive 状态

当前用户还具有 `active` 状态：

- `active = true`：正常登录与访问受保护操作
- `active = false`：
  - 登录被拒绝
  - 关键受保护操作被拒绝

管理员目前可以对 **USER / CONTRIBUTOR** 执行启用与停用：

- `POST /api/admin/users/{id}/activate`
- `POST /api/admin/users/{id}/deactivate`

并有最小保护：

- 不能操作 `ADMIN`
- 不能停用当前登录管理员自己

---

## 5. 文章模块与审核状态机

### 5.1 文章状态机

当前文章状态机为：

- `DRAFT`
- `PENDING_REVIEW`
- `PUBLISHED`
- `ARCHIVED`
- `REJECTED`

### 5.2 状态流转规则

- 新建文章：`DRAFT`
- 用户提交审核：`DRAFT / REJECTED -> PENDING_REVIEW`
- 管理员审核通过：`PENDING_REVIEW -> PUBLISHED`
- 管理员审核驳回：`PENDING_REVIEW -> REJECTED`
- 管理员归档：`PUBLISHED -> ARCHIVED`
- 管理员恢复发布：`ARCHIVED -> PUBLISHED`

### 5.3 文章审核字段

文章审核链已补齐这些关键字段：

- `submittedAt`
- `reviewedAt`
- `reviewedBy`
- `rejectReason`

### 5.4 Public 可见性规则

- Public 列表只返回 `PUBLISHED`
- Public 详情只允许读取 `PUBLISHED`
- 评论仅允许对 `PUBLISHED` 文章发布

---

## 6. 当前已实现能力

### 6.1 公共内容能力

- 用户注册
- 用户登录
- 分类列表
- Public 文章列表
- Public 文章详情
- 对已发布文章发表评论
- 图片上传

### 6.2 用户侧投稿能力

- 创建草稿
- 更新自己的 `DRAFT / REJECTED`
- 提交审核
- 查看我的文章列表
- 查看我的文章详情
- 通过真实接口重新打开草稿继续编辑

### 6.3 管理员文章管理

管理员 Article Management 当前支持：

- 查看所有文章
- 状态筛选
- 标题搜索
- 排序：
  - 最近更新
  - 最近提交
  - 最近审核
- 文章详情查看
- 审核通过 / 驳回
- 归档 / 恢复发布

### 6.4 管理员文章列表分页

Article Management 当前已支持**可选分页模式**：

- `/api/admin/posts` 在带 `page / size` 参数时进入分页模式
- 响应体仍保持项目原有 `ApiResponse<List<...>>` 风格
- 分页信息通过响应头返回，例如：
  - `X-Page`
  - `X-Size`
  - `X-Total-Elements`
  - `X-Total-Pages`
  - `X-Has-Previous`
  - `X-Has-Next`

前端已支持：

- 页大小切换
- 上一页 / 下一页
- 搜索 / 状态筛选 / 排序切换后自动回第一页
- 审核、归档、恢复后保留当前分页上下文
- 当前页被操作清空时自动回退到上一页

### 6.5 Pending Review Queue

Pending Review Queue 当前保持独立语义，不与 Article Management 混用主数据源。

当前支持：

- 只显示 `PENDING_REVIEW`
- 按 `submittedAt` 倒序展示
- Quick approve
- 打开详情进行完整审核
- 队列独立标题搜索（本地过滤）
- 队列结果反馈：
  - waiting count
  - 当前是否应用搜索
  - 不同空态提示
- 从队列进入详情审核后，Approve / Reject 返回时保持当前队列搜索上下文

### 6.6 管理员用户管理（User Management）

当前 User Management 已接真实后端，支持：

- 查看用户列表
- 按用户名搜索
- 按角色筛选
- 按 active 状态本地筛选：
  - all
  - active
  - inactive
- 结果数量反馈
- 当前筛选摘要展示
- `USER <-> CONTRIBUTOR` 角色切换
- `USER / CONTRIBUTOR` 的 activate / deactivate

并保留以下保护规则：

- 不能把任何用户改成 `ADMIN`
- 不能修改已有 `ADMIN` 的角色
- 不能停用 `ADMIN`
- 不能停用当前登录管理员自己

当前用户列表中的 `contributionCount` 按**已发布文章数**统计。

### 6.7 Contributor Application（贡献者申请）

当前已实现完整最小闭环：

#### 用户侧

- `USER` 可以提交成为 `CONTRIBUTOR` 的申请
- 用户可以查看自己的申请记录
- 若已有并行 `PENDING` 申请，则不能重复申请
- `CONTRIBUTOR / ADMIN` 不允许再次申请
- 用户侧已有申请入口，并已确认可达

#### 管理员侧

- 管理员可查看申请列表
- 可按状态筛选
- 可 approve / reject
- approve 后自动将申请人角色提升为 `CONTRIBUTOR`
- reject 不改角色，只记录审批结果

#### 申请状态

- `PENDING`
- `APPROVED`
- `REJECTED`

#### 审批结果信息

当前 rejectReason 已打通：

- 管理员 reject 时提交：`{ "reason": "..." }`
- 申请记录会保存并返回：
  - `reviewedAt`
  - `reviewerName`
  - `rejectReason`

### 6.8 用户侧 Permission Requests

用户侧 Permission Requests 区域当前已增强为：

- 查看自己的申请记录
- 本地按状态筛选：
  - All
  - Pending
  - Approved
  - Rejected
- 显示结果数量
- 显示当前筛选摘要
- 区分“没有任何申请记录”和“当前筛选无匹配结果”
- 继续显示审批结果信息：
  - reviewedAt
  - reviewerName
  - rejectReason

---

## 7. 这一轮增量开发完成的重点 PBI

下面按开发逻辑总结本轮已完成的关键增量。

### P0：JWT 迁移后的稳定性收口与回归护栏

本轮首先围绕“JWT 版本是否真实接通、原有功能是否仍然成立”完成了收口与验证：

- 确认当前仓库为真实 JWT 工作区，而非旧版轻量鉴权仓库
- 修复本地 H2 文件损坏导致的应用无法启动问题
- 清理文章创建与评论请求中的 `authorId` 旧鉴权残留
- 收紧 `SecurityConfig` 的 URL 级保护边界：
  - 登录与公共读接口保持匿名可访问
  - 草稿创建、更新、送审、评论、上传、`/api/my/**` 明确要求登录
  - `/api/admin/**` 明确保持管理员权限
- 补充 JWT 回归测试，验证：
  - `/api/auth/login` 返回结构与前端消费契约一致
  - 真实登录拿 token 后，Bearer token 可访问受保护评论写接口

### P1：管理员文章管理增强

围绕 Article Management 与 Pending Review Queue 做了多轮细化：

- 标题搜索
- 状态筛选
- 排序增强
- 结果数量与筛选摘要
- 管理员文章分页
- Pending Review Queue 独立数据流
- 队列搜索与结果反馈
- 审核后保留队列上下文

### P2：管理员用户管理增强

围绕 User Management 做了真实化与精细化增强：

- 用户列表接入真实后端
- 用户名搜索
- 角色筛选
- `USER <-> CONTRIBUTOR` 角色切换
- Active / Inactive 显示与启停操作
- Active 本地筛选与结果反馈
- 修复前端 Actions 区按钮渲染问题，保证非 ADMIN 用户能看到完整操作按钮

### P3：Contributor Application 闭环增强

围绕申请与审批链做了闭环补齐：

- 用户侧入口可达性确认
- 权限申请记录区状态筛选与结果反馈
- 管理员驳回原因 rejectReason 接通
- 管理员审批结果信息展示增强
- 用户侧审批结果信息展示增强
- 保持 approve 提升为 `CONTRIBUTOR`、reject 不改角色的既有规则不变

---

## 8. 主要接口清单（当前版本）

### 8.1 认证接口

- `POST /api/auth/register`
- `POST /api/auth/login`

### 8.2 公共接口

- `GET /api/categories`
- `GET /api/posts`
- `GET /api/posts/{id}`
- `POST /api/posts/{id}/comments`
- `POST /api/uploads/images`

### 8.3 用户侧文章接口

- `POST /api/posts` 创建草稿
- `PUT /api/posts/{id}` 更新草稿/驳回稿
- `POST /api/posts/{id}/submit-review`
- `GET /api/my/posts`
- `GET /api/my/posts/{id}`

### 8.4 管理员文章接口

- `GET /api/admin/posts`
- `GET /api/admin/posts/{id}`
- `POST /api/admin/posts/{id}/review`
- `POST /api/admin/posts/{id}/archive`
- `POST /api/admin/posts/{id}/restore`

### 8.5 管理员用户接口

- `GET /api/admin/users`
- `POST /api/admin/users/{id}/role`
- `POST /api/admin/users/{id}/activate`
- `POST /api/admin/users/{id}/deactivate`

### 8.6 贡献者申请接口

#### 用户侧

- `GET /api/my/contributor-applications`
- `POST /api/my/contributor-applications`

#### 管理员侧

- `GET /api/admin/contributor-applications`
- `POST /api/admin/contributor-applications/{id}/approve`
- `POST /api/admin/contributor-applications/{id}/reject`

---

## 9. 运行方式

### 9.1 直接运行主类

运行：

`com.heritage.platform.HeritagePlatformApplication`

### 9.2 命令行运行

Windows：

```bash
.\mvnw.cmd spring-boot:run
```

macOS / Linux：

```bash
./mvnw spring-boot:run
```

### 9.3 H2 控制台

启动成功后，可访问 H2 Console。默认本地开发环境使用文件型 H2 数据库。

### 9.4 测试

Windows：

```bash
.\mvnw.cmd test
```

macOS / Linux：

```bash
./mvnw test
```

---

## 10. 当前测试状态

当前回归测试已覆盖这几类关键能力：

### 10.1 文章审核与后台主链

`PostReviewFlowApiTests`

覆盖内容包括：

- Public 文章列表/详情仅对 `PUBLISHED` 开放
- 评论仅对 `PUBLISHED` 生效
- 用户只能编辑自己的 `DRAFT / REJECTED`
- 提交审核状态流转
- 管理员审核、归档、恢复发布
- 管理员文章搜索、筛选、排序、分页
- Pending Review Queue 行为
- 用户管理角色切换
- Active / Inactive 启停逻辑
- Contributor Application 审批与 rejectReason

### 10.2 JWT 登录与 Bearer 链路

`AuthJwtRegressionTests`

覆盖内容包括：

- 登录返回结构契约
- 真实登录 -> 获取 token -> 带 Bearer 访问受保护接口
- inactive 用户登录被拒绝

---

## 11. 当前已知约束与刻意延后的内容

为了控制范围，本轮有意识地没有继续扩到以下内容：

### 前端层面

- 整体 UI/布局统一整理
- 更大规模组件化重构
- 更复杂的交互动画与视觉优化

### 管理员文章管理

- 更复杂分页器
- 首页/尾页跳转
- 批量审核
- 批量归档
- 导出能力

### Pending Review Queue

- 分页
- 批量审核
- 更复杂排序

### User Management

- 分页
- 批量启停
- active 审计日志
- 封禁原因与历史
- 更复杂 RBAC

### Contributor Application

- 分页
- 更复杂排序
- 批量审批
- 附件审核增强
- 邮件通知 / 站内通知

### 认证与安全

- refresh token 体系
- 更复杂的 token 失效策略
- 自定义认证失败返回体统一包装

---

## 12. 下一阶段建议

当前版本已经适合作为**阶段性增量提交**。在继续扩功能之前，最合理的下一阶段应当是：

### 前端整理 sprint

建议下一阶段先做一轮前端整理，而不是继续增加后端功能。原因是：

- 当前后端主链已较完整
- 管理端与用户侧已有多个模块进入“可用但风格略分散”的状态
- 继续叠加功能，会放大前端信息层级与布局不统一的问题

建议的前端整理方向：

1. 管理端布局统一
2. 表格/卡片/操作按钮样式统一
3. 筛选区与结果反馈区视觉统一
4. 文章详情、申请详情、用户管理信息呈现统一
5. 用户侧 Draft / Permission Requests / Profile 入口关系收口

---

## 13. 阶段性结论

到当前这一轮为止，项目已经从“基础内容平台”演进为一套具备以下能力的**可运行、可联调、可继续演进的 JWT 版本文化遗产交流平台**：

- 真实 JWT 登录态
- 完整文章审核流
- 管理员文章管理与待审队列
- 真实用户管理与角色/启停控制
- 贡献者申请与审批闭环
- 基础回归测试护栏

这意味着：

- 后端主链已经具备继续开发的稳定基础
- 管理员相关核心模块已形成闭环
- 下一步更适合进入前端整理和体验优化，而不是继续无边界扩功能

