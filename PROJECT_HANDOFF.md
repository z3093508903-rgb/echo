# 回声 Echo — 多 Agent 实时交接

> 本文件是 `z3093508903-rgb/echo` 的唯一实时项目交接状态。所有 Agent 在开始、转向、验证、提交和交接时都必须更新这里。长期产品方向写进稳定文档，但“现在做到哪、谁在改什么、下一步是什么”只认本文件。

## 当前仓库快照

| 字段 | 当前值 |
| --- | --- |
| 项目 | 回声 Echo |
| 仓库 | `z3093508903-rgb/echo` |
| 默认分支 | `main` |
| 当前阶段 | Echo onboarding v1 已完成源码修改并通过 PR CI，准备合入 main 后发布 Preview 复测 |
| 更新时间 | `2026-09-12T11:54:00+08:00` |
| Android 业务代码改动 | feature 分支已修改 onboarding / 首页 / 设置的首次配置语义和主题对比度 |
| 真机构建与运行 | 新版真机 `[UNRUN]`；原版首次配置阻塞已由用户真机确认 |

## 进行中的工作

```text
work_id: onboarding-v1-20260912-webgpt
agent: 网页 GPT
branch@base: gpt/echo-onboarding-v1-20260912@aa485f7ba599b85c4446fbcd4902557fc1452af7
goal: 首次配置收敛为“通知访问 → 选择来源 App → 进入主界面”，移除 Bark/飞书/钉钉强制门槛，并修复 onboarding 白底白字/低对比度
owns: PROJECT_HANDOFF.md; app/src/main/java/io/github/messagerelay/MainActivity.kt
status: verifying
updated_at: 2026-09-12T11:54:00+08:00
notes: PR #7 的 Android CI run #2 已真实通过 compileDebugKotlin、testDebugUnitTest、assembleDebug，并成功生成 PR artifact；lintDebug 当前 workflow 未覆盖，继续 [UNRUN]。下一步标记 PR ready 并合入 main，等待自动生成 Preview，再由用户真机复测两步 onboarding 与深浅色对比度。
```

## 本轮关键事实

- 原 onboarding 根页面未显式铺 `colors.page` 背景，系统深色模式下可能出现浅色文字叠白底；本轮已给 `PageScaffold` 显式铺背景，并补齐 light/dark `onPrimary/onSurface/onSurfaceVariant/outline`。
- 原 onboarding 把 Bark / 飞书 / 钉钉配置与发送测试作为第 3、4 步硬门槛；本轮已改为 2 步：通知访问 → 选择来源 App → 进入 Echo。
- 原首页把外部渠道算作 ready 必填；本轮已改为仅来源 App 决定基础配置是否完成，外部转发显示为可选能力。
- 正式“无渠道本地观察”仍未实现；底层通知引擎语义不在本 work 修改，留给 Notification Observatory。

## 最近完成

| work_id | 结果 | 提交 / PR | 验证 | `[UNRUN]` / 下一步 |
| --- | --- | --- | --- | --- |
| `onboarding-v1-20260912-webgpt` | 四步 onboarding → 两步；移除渠道/发送测试门槛；首页只把来源 App 作为首次设置条件；外部渠道改为可选入口；修复主题对比度 | code `1f9ccf14f72709fadcbb2a9c4802e2b8b06cc940`；PR #7 | Android CI run #2：compileDebugKotlin ✅、testDebugUnitTest ✅、assembleDebug ✅、artifact ✅ | `lintDebug`、新版 APK 真机、深浅色真机、两步 onboarding 真机仍 `[UNRUN]` |
| `release-pipeline-20260912-webgpt` | 自动 APK artifact + main prerelease 发布链 | PR #4；merge `99dd15f` | Android CI #1 已真实成功，并发布 Echo Preview #1 | 后续 Preview 继续由 main push 自动生成 |
| `baseline-ux-observation-20260912-user` | 用户确认 UI 低对比度与第三方渠道阻塞 | 用户真机观察；PR #3 已合入 main | 源码复核与现象一致 | 当前 onboarding v1 已处理，待新版真机复测 |

## 已冻结边界

- Echo 不要求用户必须拥有 Bark、飞书或钉钉；第三方转发渠道只是可选高级能力。
- Echo 首次配置最小主路径：通知访问权限 → 选择需要观察/管理的 App → 进入主界面。
- 白底白字/低对比度是阻塞可读性的 bug，优先修复。
- “允许无渠道进入 Echo”不等于底层已具备正式无渠道本地观察能力；后者必须单独实现并验证。
- 测试 APK 通过 GitHub Actions 产出并发布到 GitHub Preview/Prerelease。
- 用户真实通知正文、数据库、备份、Token、Webhook、验证码、电话号码等敏感数据不得进入源码仓库或 Release。
- Room Entity 变化必须有 Migration；DataStore 新字段必须有默认值。
- 未经真机验证的行为必须标记 `[UNRUN]` 或 `[UNVERIFIED]`。

## 下一条开发链

1. 将 PR #7 标记 ready 并合入 main。
2. 等 main 自动 Actions 通过并生成新的 Echo Preview APK / GitHub prerelease。
3. 用户真机验证：首次配置只有 2 步、无需 Bark/飞书/钉钉即可进入、浅色/深色都可读、已选来源 App 正确保存。
4. 开独立 work 实现 Notification Observatory：无外部渠道时将符合来源规则的通知作为本地观察记录，而不是“发送失败”。
5. 用真机观察微信 / 抖音 / 小红书实际系统通知格式，再冻结 P0/P1/P2/P3。

## 协作规则摘要

1. 开工先读 `AGENTS.md` 与本文件。
2. 修改前先登记 `work_id`、feature 分支和精确文件范围。
3. 不直接在 `main` 开发；不抢占另一 Agent 已声明的文件。
4. 方向变化、阻塞或扩大文件范围时，先更新本文件。
5. “测试通过”只能写真实运行过的测试；没跑就是 `[UNRUN]`。
6. 涉及数据/schema 必须写清迁移和回滚。
7. 完成时必须留下唯一下一步。
