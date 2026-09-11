# 回声 Echo — 多 Agent 实时交接

> 本文件是 `z3093508903-rgb/echo` 的唯一实时项目交接状态。所有 Agent 在开始、转向、验证、提交和交接时都必须更新这里。长期产品方向写进稳定文档，但“现在做到哪、谁在改什么、下一步是什么”只认本文件。

## 当前仓库快照

| 字段 | 当前值 |
| --- | --- |
| 项目 | 回声 Echo |
| 仓库 | `z3093508903-rgb/echo` |
| 上游 | `MSCNUAN/message-relay-android` |
| 上游基线 | `4ef8947e92f91a5b05c762752c04fecbc9d8a7e9` |
| 默认分支 | `main` |
| 当前阶段 | Echo onboarding v1 源码已完成，等待 Android 构建验证与真机复测 |
| 更新时间 | `2026-09-12T03:22:15+08:00` |
| Android 业务代码改动 | feature 分支已修改 onboarding / 首页 /设置的首次配置语义和主题对比度 |
| 真机构建与运行 | 新版 `[UNRUN]`；原版首次配置阻塞已由用户真机确认 |

## 产品定位

Echo 是一个 **本地优先的 Android 注意力防火墙**。目标不是制造第四个信息流，而是降低因为期待社会反馈而反复主动打开微信、抖音、小红书等平台检查消息的频率。

当前方向：P0 需要行动 → 及时提醒；P1 普通社会反馈 → 延迟/批量；P2 点赞收藏关注 → 汇总；P3 平台诱导/推荐/低价值分享 → 默认过滤。第一阶段先观察真实系统通知，不凭想象硬编码规则。

## 进行中的工作

```text
work_id: onboarding-v1-20260912-webgpt
agent: 网页 GPT
branch@base: gpt/echo-onboarding-v1-20260912@aa485f7ba599b85c4446fbcd4902557fc1452af7
goal: 将首次配置收敛为“通知访问 → 选择来源 App → 进入主界面”，移除 Bark/飞书/钉钉强制门槛，并修复 onboarding 白底白字/低对比度
owns: PROJECT_HANDOFF.md; app/src/main/java/io/github/messagerelay/MainActivity.kt
status: blocked
updated_at: 2026-09-12T03:22:15+08:00
notes: 源码改动与静态复核已完成；阻塞仅为当前无可用 Android SDK/Gradle 构建环境且 Fork Actions 尚无 run。不得在 compile/test/lint/assemble 实际执行前合入 main。
```

## 本轮关键发现

- 原 onboarding 的根 `PageScaffold` 没有显式铺 `colors.page` 背景；系统深色模式下文字使用浅色，而底层窗口可能仍为白色，可直接解释用户看到的白底白字/低对比度。
- 原 onboarding 将 Bark / 飞书 / 钉钉配置与发送测试设为第 3、4 步硬门槛，因此无第三方渠道用户无法完成首次配置。
- 原首页还把“推送渠道”纳入 `ready` 必填条件，因此仅删除 onboarding 门槛仍会在首页继续显示“配置缺失”。
- 底层 `RelayWorker` 当前在无渠道时仍会写入本地 `DeliveryRecord`，但状态为“未配置渠道”并返回失败；因此“无渠道本地观察”还不是正式能力，留给下一轮 Notification Observatory，不在本 work 偷改引擎语义。

## 最近完成

| work_id | 结果 | 提交 / PR | 验证 | `[UNRUN]` / 下一步 |
| --- | --- | --- | --- | --- |
| `onboarding-v1-20260912-webgpt` | 四步 onboarding → 两步；移除渠道/发送测试门槛；首页只把来源 App 作为首次设置条件；外部渠道改为可选入口；显式补齐 light/dark `onPrimary/onSurface/onSurfaceVariant/outline` 并给 `PageScaffold` 铺背景；按钮显式定义内容色 | code `1f9ccf14f72709fadcbb2a9c4802e2b8b06cc940`；feature `gpt/echo-onboarding-v1-20260912` | 已复核 commit diff、关键源码区间和 branch compare；仅 `MainActivity.kt` + 本交接文件变化 | Gradle compile/unit/lint/assemble、APK、深浅色真机、两步 onboarding 真机均 `[UNRUN]`；唯一下一步：在可用 Android 构建环境执行验证，成功后再 merge / 发布 Preview |
| `release-policy-20260912-user` | Echo 测试包不要求 SHA-256/哈希校验；以版本/commit/Release 追踪 | PR #6 已合入 main | 无运行时影响 | 后续不把哈希作为发布门槛 |
| `release-pipeline-20260912-webgpt` | CI 增加 APK artifact、`workflow_dispatch` 与 `main` 自动 GitHub prerelease；测试包固定名 `Echo-preview.apk` | PR #4；merge `99dd15f` | workflow 文件已远端复核 | Actions 实际 run / APK / Release `[UNRUN]` |
| `baseline-ux-observation-20260912-user` | 用户确认 UI 观感差、白底白字/低对比度；原版 onboarding 强制第三方渠道 | 用户真机观察；PR #3 已合入 main | 源码复核与现象一致 | 当前 onboarding v1 已在 feature 分支处理 |

## 已冻结边界

- 保留上游 GPL-3.0-only 许可证、原作者归属与 Git 历史。
- Echo 不要求用户必须拥有 Bark、飞书或钉钉；第三方转发渠道只是可选高级能力。
- Echo 首次配置最小主路径：通知访问权限 → 选择需要观察/管理的 App → 进入主界面。
- 白底白字/低对比度是阻塞可读性的 bug，优先修复。
- “允许无渠道进入 Echo”与“底层已经具备正式无渠道本地观察能力”是两件事；后者必须由 Notification Observatory 单独实现并验证。
- 测试 APK 通过可验证构建产出并发布到 GitHub Release；Release 必须标记 Preview/Prerelease，不视为正式稳定版本。
- **Echo 测试包不要求 SHA-256/哈希校验，也不把哈希生成/核验作为发布门槛。** 版本、commit 与 GitHub Release 即为测试阶段的追踪依据。
- 当前 Preview 使用 debug 构建；跨构建覆盖安装签名稳定性尚未保证，必要时可能需要卸载旧测试版后安装。
- 用户真实通知正文、数据库、备份、Token、Webhook、验证码、电话号码等敏感数据不得进入源码仓库或 Release。
- Room Entity 变化必须有 Migration；DataStore 新字段必须有默认值。
- 未经真机验证的行为必须标记 `[UNRUN]` 或 `[UNVERIFIED]`。
- `main` 只接收已交接、可回滚、验证状态明确的改动；Agent 日常开发使用 feature 分支。

## 下一条开发链

1. 对 `gpt/echo-onboarding-v1-20260912` 执行 `compileDebugKotlin`、`testDebugUnitTest`、`lintDebug`、`assembleDebug`；任何失败先修 feature 分支，不碰 main。
2. 构建通过后合入 main，使用既有发布链生成 `Echo-preview.apk` / GitHub prerelease。
3. 用户真机验证：首次配置只有 2 步、无渠道可进入、浅色/深色都可读、已选择来源 App 被正确保存。
4. 开独立 work 实现 Notification Observatory：无外部渠道时把符合来源规则的系统通知视为“本地观察记录”而非“发送失败”，并明确脱敏/保留策略。
5. 用真机观察微信 / 抖音 / 小红书实际系统通知格式，再冻结 P0/P1/P2/P3。
6. 核心指标仍是主动打开平台检查反馈的次数是否下降。

## Agent 开工模板

```text
work_id: <feature>-<YYYYMMDD>-<short-id>
agent: <网页 GPT / Codex task / 其他 Agent / 人工>
branch@base: <feature branch>@<base commit>
goal: <一句可验收目标>
owns: <精确文件/目录；不得写“整个项目”>
status: claimed | editing | verifying | blocked | handoff-ready
updated_at: <ISO 8601 + timezone>
notes: <依赖、冲突、风险或已冻结决定>
```

## Agent 完成 / 交接模板

```text
changed: <实际改变了什么>
commit: <提交 SHA 或 none>
remote: <branch+commit / PR URL / 未推送>
checks: <真实执行的命令及覆盖范围>
data_or_schema: <无 / migration / 导入导出影响>
unrun: <真机、视觉、网络、厂商后台等未验证项>
rollback: <可回退的分支 / 提交 / 数据保护动作>
next: <唯一下一步>
```

## 协作规则摘要

1. 开工先读 `AGENTS.md` 与本文件。
2. 修改前先登记 `work_id`、feature 分支和精确文件范围。
3. 不直接在 `main` 开发；不抢占另一 Agent 已声明的文件。
4. 方向变化、阻塞或扩大文件范围时，先更新本文件。
5. “测试通过”只能写真实运行过的测试；没跑就是 `[UNRUN]`。
6. 涉及数据/schema 必须写清迁移和回滚。
7. 完成时必须留下唯一下一步。
