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
| main 立项基线 | `9411233acedb72e73892719d753066d8e74e269d`（PR #1） |
| 当前阶段 | 原版首次真机体验已暴露产品阻塞，下一步进入 Echo onboarding v1 |
| 更新时间 | `2026-09-12T02:22:00+08:00` |
| Android 业务代码改动 | 尚无 Echo 业务改动 |
| 真机构建与运行 | `[OBSERVED]` 用户已进入原版首次配置流程；具体 APK commit / 构建链仍 `[UNVERIFIED]` |

## 产品定位

Echo 不是“把更多通知集中到另一个信息流”的工具，而是一个 **本地优先的 Android 注意力防火墙**。

当前核心问题：用户会因为期待作品反馈而反复主动打开微信、抖音、小红书等平台检查消息，并容易被平台信息流继续吸走注意力。Echo 的目标是降低这种主动轮询：让真正需要行动的消息可靠地来找用户，把普通社会反馈延迟或集中处理，把平台诱导与低价值提醒尽量隔离。

当前设计方向：

- P0：需要行动或及时回应的信息 → 允许立即提醒；
- P1：普通社会反馈 → 静默收集、延迟或批量查看；
- P2：点赞、收藏、关注等数字反馈 → 尽量汇总而非逐条刺激；
- P3：平台诱导、推荐、直播、好友分享视频等 → 默认过滤或隐藏；
- 默认本地处理、本地存储，不把私人通知内容发送到陌生服务；
- 第一阶段先观察真实通知数据，不凭想象硬编码规则。

## 进行中的工作

暂无。

> 若这里存在 `claimed / editing / verifying` 工作，新 Agent 必须先检查自己的修改范围是否重叠。只读审查不取得文件所有权，但不得覆盖另一 Agent 正在写的文件。

## 最近完成

| work_id | 结果 | 提交 / PR | 验证 | `[UNRUN]` / 下一步 |
| --- | --- | --- | --- | --- |
| `baseline-ux-observation-20260912-user` | 用户在原版首次配置流程中确认两个高优先级问题：界面整体观感差且出现白底白字/低对比度；第 3 步强制 Bark/飞书/钉钉，第 4 步又要求渠道测试成功，Echo 用户无法无渠道进入主界面 | 用户真机观察，无代码提交 | 源码复核确认 `Onboarding` 第 3 步要求 `selectedChannelConfig(...).isNotEmpty()`，第 4 步要求 `testPassed`；与用户现象一致 | 白底白字具体控件与设备主题组合仍需截图/新版复测；下一步先做 Echo onboarding v1，移除第三方渠道强依赖 |
| `bootstrap-20260911-webgpt` | 建立 Echo 产品定位、唯一实时交接表和多 Agent 强制规则；保留上游工程安全约束；已合入 `main` | PR `#1`；merge `9411233` | 远端重新读取 `AGENTS.md` / `PROJECT_HANDOFF.md`；合并前 compare 仅 2 个 Markdown 文件变化 | Android 编译、单测、lint、APK、真机均 `[UNRUN]`；后续由真实测试逐项替换 |

### `baseline-ux-observation-20260912-user` 交接详情

```text
changed: 无代码变化；记录用户第一次真机体验与源码复核结果
commit: none
remote: 用户反馈待同步到 main
tests: 用户可进入原版 onboarding；具体 APK 来源/commit 未复核
checks: 已复核 MainActivity.kt 中 Onboarding：step 2 继续条件要求至少一个第三方渠道；step 3 完成条件要求至少一次渠道发送测试成功
data_or_schema: 无
unrun: 白底白字的具体控件/主题组合、后台通知监听、微信/抖音/小红书通知捕获仍未验证
rollback: 仅文档状态，无运行时影响
next: 新建 feature 分支实现 Echo onboarding v1：通知权限 → 选择来源 App → 进入 Echo；Bark/飞书/钉钉改为可选高级能力，并同时修复首次配置页面的文字/背景对比度
```

### `bootstrap-20260911-webgpt` 交接详情

```text
changed: 新增 PROJECT_HANDOFF.md；将 AGENTS.md 改为 Echo 多 Agent 协作与工程约束入口；未修改 Android 业务代码
commit: 7d2fd57c445dfa80fff707352f42a42d67fa0ae5 + c637ec54f2704cbb9635369bf761f93e5bbdb669 + 238fcba3706d5400734b75881e15c6bfbf7e0902
remote: PR #1 已 merge 到 main，merge commit 9411233acedb72e73892719d753066d8e74e269d
checks: 远端逐文件重新读取；合并前 GitHub compare 仅 AGENTS.md 与 PROJECT_HANDOFF.md 变化
data_or_schema: 无
unrun: Gradle compile/test/lint/assemble、APK 安装、通知权限、后台行为、微信/抖音/小红书真机通知全部 [UNRUN]
rollback: revert PR #1 / merge commit 9411233；未触碰用户数据
next: 已被 2026-09-12 真机体验结果取代，转入 Echo onboarding v1
```

## 已冻结边界

- 保留上游 GPL-3.0-only 许可证、原作者归属与 Git 历史。
- 初始阶段不大规模重写，不因为产品定位变化就先重做全部 UI。
- Echo 不要求用户必须拥有 Bark、飞书或钉钉；第三方转发渠道只能是可选高级能力，不得阻塞核心首次配置。
- Echo 首次配置的最小主路径应收敛为：通知访问权限 → 选择需要观察/管理的 App → 进入主界面。
- 白底白字/低对比度属于阻塞可读性的 bug，优先于整体视觉重设计修复。
- 在确认真实通知数据之前，不承诺微信 / 抖音 / 小红书所有 App 内消息都可被 Echo 捕获。
- 用户真实通知正文、数据库、备份、Token、Webhook、验证码、电话号码等敏感数据不得进入源码仓库。
- Room Entity 变化必须有 Migration；DataStore 新字段必须有默认值。
- 未经真机验证的行为必须标记 `[UNRUN]` 或 `[UNVERIFIED]`。
- `main` 只接收已交接、可回滚、验证状态明确的改动；Agent 日常开发使用 feature 分支。

## 下一条开发链

1. Echo onboarding v1：去除 Bark / 飞书 / 钉钉强制依赖，首次配置只保留“通知访问 → 选择来源 App → 进入主界面”。
2. 同一轮修复 onboarding 的文字/背景对比度；若无法从源码静态确定白底白字触发条件，则显式定义 light/dark 的 `onSurface` / `onPrimary` / `onSurfaceVariant` 等颜色并真机复测。
3. 构建并安装新 APK，用户重新走一遍首次配置，确认无需渠道即可进入主界面。
4. 建立“通知观察模式”最小方案：仅采集并脱敏展示通知元数据，不先做复杂分类。
5. 用真机观察微信 / 抖音 / 小红书实际产生的系统通知格式。
6. 基于真实样本再冻结 P0/P1/P2/P3 规则与 Echo 第一版 UI。
7. 以“主动打开平台检查反馈的次数是否下降”作为核心产品指标，而不是功能数量。

## Agent 开工模板

任何修改前，在“进行中的工作”登记：

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
4. 中途发生方向变化、阻塞、扩大文件范围时，先更新本文件再继续。
5. “测试通过”只能写真实运行过的测试；没跑就是 `[UNRUN]`。
6. 涉及数据/schema 必须写清迁移和回滚。
7. 完成时必须留下唯一下一步，保证另一 Agent 不依赖聊天记录也能接手。
