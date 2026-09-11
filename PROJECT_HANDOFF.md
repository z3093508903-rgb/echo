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
| 当前阶段 | 建立手机测试用 APK 自动发布链；随后进入 Echo onboarding v1 |
| 更新时间 | `2026-09-12T02:27:00+08:00` |
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

```text
work_id: release-pipeline-20260912-webgpt
agent: 网页 GPT
branch@base: gpt/apk-release-pipeline-20260912@a5c2c969f23ba554c8ef22fe8b0954112deb9b39
goal: 建立 GitHub Actions 自动编译、保存并发布可手机下载的 Echo debug APK 测试包
owns: PROJECT_HANDOFF.md; .github/workflows/android-ci.yml
status: editing
updated_at: 2026-09-12T02:27:00+08:00
notes: 不修改 Android 业务代码；Release 仅用于测试，使用 debug 签名并标记 prerelease
```

> 若这里存在 `claimed / editing / verifying` 工作，新 Agent 必须先检查自己的修改范围是否重叠。只读审查不取得文件所有权，但不得覆盖另一 Agent 正在写的文件。

## 最近完成

| work_id | 结果 | 提交 / PR | 验证 | `[UNRUN]` / 下一步 |
| --- | --- | --- | --- | --- |
| `baseline-ux-observation-20260912-user` | 用户确认 UI 观感差、白底白字/低对比度；原版 onboarding 强制第三方渠道，Echo 用户无法无渠道进入主界面 | 用户真机观察；PR #3 已合入 main | 源码复核确认 step 2/3 的渠道门槛与用户现象一致 | 白底白字具体触发控件仍需新版复测；下一步 Echo onboarding v1 |
| `bootstrap-20260911-webgpt` | 建立 Echo 产品定位、唯一实时交接表和多 Agent 强制规则 | PR #1 已合入 main | 远端复核文档变化 | Android 业务构建与真机链后续逐项验证 |

## 已冻结边界

- 保留上游 GPL-3.0-only 许可证、原作者归属与 Git 历史。
- 初始阶段不大规模重写，不因为产品定位变化就先重做全部 UI。
- Echo 不要求用户必须拥有 Bark、飞书或钉钉；第三方转发渠道只能是可选高级能力，不得阻塞核心首次配置。
- Echo 首次配置的最小主路径：通知访问权限 → 选择需要观察/管理的 App → 进入主界面。
- 白底白字/低对比度属于阻塞可读性的 bug，优先于整体视觉重设计修复。
- 测试 APK 通过 GitHub Actions 构建；GitHub Release 明确标记 Preview/Prerelease，不视为正式稳定版本。
- 用户真实通知正文、数据库、备份、Token、Webhook、验证码、电话号码等敏感数据不得进入源码仓库或 Release。
- Room Entity 变化必须有 Migration；DataStore 新字段必须有默认值。
- 未经真机验证的行为必须标记 `[UNRUN]` 或 `[UNVERIFIED]`。
- `main` 只接收已交接、可回滚、验证状态明确的改动；Agent 日常开发使用 feature 分支。

## 下一条开发链

1. 建立 GitHub Actions APK 自动发布链，让用户可直接从 Releases 下载测试包。
2. Echo onboarding v1：去除 Bark / 飞书 / 钉钉强制依赖，首次配置只保留“通知访问 → 选择来源 App → 进入主界面”。
3. 同轮修复 onboarding 的文字/背景对比度。
4. 自动发布新 Preview APK，用户手机复测无需渠道即可进入主界面。
5. 建立“通知观察模式”最小方案并观察微信 / 抖音 / 小红书实际系统通知格式。
6. 基于真实样本冻结 P0/P1/P2/P3 与 Echo 第一版 UI。
7. 核心指标仍是主动打开平台检查反馈的次数是否下降。

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
7. 完成时必须留下唯一下一步，保证另一 Agent 不依赖聊天记录也能接手。
