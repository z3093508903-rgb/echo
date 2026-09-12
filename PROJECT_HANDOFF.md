# 回声 Echo — 多 Agent 实时交接

> 本文件是 `z3093508903-rgb/echo` 的唯一实时项目交接状态。所有 Agent 在开始、转向、验证、提交和交接时都必须更新这里。长期产品方向写进稳定文档，但“现在做到哪、谁在改什么、下一步是什么”只认本文件。

## 当前仓库快照

| 字段 | 当前值 |
| --- | --- |
| 项目 | 回声 Echo |
| 仓库 | `z3093508903-rgb/echo` |
| 默认分支 | `main` |
| 当前阶段 | PC-first 最小闭环源码已完成并通过 PR CI，准备合入 main 发布 Preview 真机验证 |
| 更新时间 | `2026-09-12T16:04:00+08:00` |
| Android 业务代码改动 | 无外部渠道时新增 Echo 本机通知 fallback；新增 Android 13+ 通知权限门；应用显示名改为回声 Echo |
| 真机构建与运行 | 新版 `[UNRUN]`；Windows Phone Link 已成功连接并保持代理开启可用 |

## 产品定位

Echo 是一个 **本地优先的 Android 注意力防火墙**。当前冻结一条第一性原理：**手机负责捕获，电脑优先负责消费信息**。目标不是在手机里再造一个信息中心，而是减少“拿起手机检查 → 顺手打开原平台”的机会。

当前最小链路：

```text
来源 App 系统通知 → Echo 规则处理 → Echo 自己生成 Android 通知 → Windows Phone Link 同步 Echo 通知到电脑
```

本阶段不新增服务器、ntfy、Gotify、GitHub 消息仓库、GPT 实时中转或 Echo Desktop。先验证微软现成通路是否足以降低主动拿手机次数。

## 进行中的工作

```text
work_id: pc-relay-v1-20260912-webgpt
agent: 网页 GPT
branch@base: gpt/echo-pc-relay-v1-20260912@34f856acf9f00314de2210338a7e3f7c5cea9b4f
goal: 当来源通知命中 Echo 现有规则且没有外部 Bark/飞书/钉钉渠道时，生成一条 Echo 自身 Android 通知，供 Windows Phone Link 只同步 Echo；Android 13+ 首次启动时请求 Echo 通知权限
owns: PROJECT_HANDOFF.md; app/src/main/java/io/github/messagerelay/RelayEngine.kt; app/src/main/java/io/github/messagerelay/RelayNotificationService.kt; app/src/main/java/io/github/messagerelay/EchoNotificationPublisher.kt; app/src/main/java/io/github/messagerelay/EchoLauncherActivity.kt; app/src/main/AndroidManifest.xml
status: handoff-ready
updated_at: 2026-09-12T16:04:00+08:00
notes: PR #8 CI run #6 已真实通过 compileDebugKotlin、testDebugUnitTest、assembleDebug、artifact。首次 run #5 因权限回调参数写成 Array<out String> 编译失败，已改为 Android 当前签名要求的 Array<String> 并重新验证通过。lintDebug workflow 未覆盖，继续 [UNRUN]。下一步合入 main 并等待自动 Preview，再由用户验证 Echo 通知 → Phone Link → Windows。
```

## 本轮设计边界

- **无外部渠道时**：Echo 本机通知成为默认输出，不再把“未配置渠道”视为发送失败。
- **有外部渠道时**：保持现有 Bark / 飞书 / 钉钉行为，本轮不叠加 Echo 本机通知，避免双重提醒。
- Echo 本机通知点击只打开 Echo，不跳回原社交 App。
- Android 13+ 首次启动通过 `EchoLauncherActivity` 请求 `POST_NOTIFICATIONS`；该 Activity 不承载业务状态，授权或拒绝后立即进入现有 `MainActivity`。
- Echo 运行状态通知标记 `localOnly`，避免把“Echo 正在运行”当成业务消息桥接到其他设备。
- 记录层只有本机 `NotificationManager.notify()` 成功后才写发送成功；这不代表 Windows 已收到。
- Phone Link 端由用户设置为只同步 Echo；来源 App 原通知是否在 PC 显示由 Windows Phone Link 控制。
- 不上传真实通知正文、Token、数据库或日志到 GitHub。

## 最近完成

| work_id | 结果 | 提交 / PR | 验证 | `[UNRUN]` / 下一步 |
| --- | --- | --- | --- | --- |
| `pc-relay-v1-20260912-webgpt` | 新增 `EchoNotificationPublisher`；无外部渠道走 Echo 本机通知；一次性 Android 13+ 通知权限门；应用名改为 `回声 Echo`；运行状态通知 `localOnly` | PR #8；feature head `5756dbc19fd9a55751d441237836a4cf88d83fb8` + 本交接 commit | PR CI run #6：compileDebugKotlin ✅、testDebugUnitTest ✅、assembleDebug ✅、artifact ✅ | `lintDebug`、APK 真机、Echo→Phone Link→Windows 均 `[UNRUN]`；唯一下一步：merge + Preview 真机验证 |
| `onboarding-v1-20260912-webgpt` | 四步 onboarding → 两步；移除 Bark/飞书/钉钉强制门槛；修复主题对比度 | PR #7；merge `34f856acf9f00314de2210338a7e3f7c5cea9b4f`；Preview #4 | PR CI compile/unit/assemble ✅；main CI + Release ✅；用户已安装并确认可正常进入 | 阻塞问题已解除 |
| `phone-link-setup-20260912-user` | Windows Phone Link / Android Link to Windows 配对成功；首次微软登录因代理报 `0x80190001`，退出代理完成认证后重新开启代理仍保持正常连接 | 用户现场验证 | Windows 与手机当前正常连接 | 下一步验证 Echo 自身通知能否稳定出现在 Windows |
| `release-pipeline-20260912-webgpt` | 自动 APK artifact + main prerelease 发布链 | PR #4 | Preview #1 / #4 已真实发布 | 后续 main push 自动生成 Preview |

## 已冻结边界

- Echo 不要求 Bark、飞书或钉钉；第三方转发只是可选能力。
- **PC-first**：现阶段优先复用 Windows Phone Link，不新增自建 PC 通信实体。
- “Echo 生成了本机通知”与“Windows 已收到”必须严格区分；后者需要用户真机实测。
- 第一阶段仍不凭想象硬编码微信、抖音、小红书的 P0/P1/P2/P3 分类；先让观察与出口链路跑通。
- 测试 APK 通过 GitHub Actions 产出并发布 GitHub Preview/Prerelease。
- Room Entity 变化必须有 Migration；DataStore 新字段必须有默认值。
- 未经真机验证的行为必须标记 `[UNRUN]` / `[UNVERIFIED]`。

## 下一条开发链

1. 合入 PR #8，等待 main 自动 Actions 通过并发布新的 Preview APK。
2. 用户安装 Preview，允许“回声 Echo”发送通知。
3. Windows Phone Link 中只保留 Echo 的 PC 通知，关闭微信/抖音/小红书等来源 App 的 PC 通知。
4. 触发一个已选来源 App 的真实通知，验证：手机 Echo 产生一条通知 → Windows 出现同一条 Echo 通知。
5. 若闭环成立，下一独立 work 实现 Notification Observatory；若不成立，先定位 Android 本机通知、Phone Link App 选择或桥接层哪一段失败。

## 完成 / 交接

```text
changed: 无外部渠道时使用 Echo 自身 Android 通知作为默认输出；新增 Android 13+ 一次性通知权限门；应用显示名切换为回声 Echo；运行状态通知 localOnly
commit: feature latest before handoff doc = 5756dbc19fd9a55751d441237836a4cf88d83fb8
remote: gpt/echo-pc-relay-v1-20260912 / PR #8
checks: Android CI run #6 -> compileDebugKotlin PASS; testDebugUnitTest PASS; assembleDebug PASS; artifact PASS
data_or_schema: 无 Room/DataStore schema 变化；新增 app-private SharedPreferences 仅记录 POST_NOTIFICATIONS 是否已询问一次
unrun: lintDebug；Android 真机权限弹窗；Echo 本机通知；Phone Link 最终同步；Windows toast
rollback: 回退 PR #8 merge commit 即可；无数据库迁移
next: merge PR #8，安装 main 自动发布的 Preview 后验证 Echo → Phone Link → Windows
```

## 协作规则摘要

1. 开工先读 `AGENTS.md` 与本文件。
2. 修改前先登记 `work_id`、feature 分支和精确文件范围。
3. 不直接在 `main` 开发；不抢占另一 Agent 已声明的文件。
4. 方向变化、阻塞或扩大文件范围时，先更新本文件。
5. “测试通过”只能写真实运行过的测试；没跑就是 `[UNRUN]`。
6. 涉及数据/schema 必须写清迁移和回滚。
7. 完成时必须留下唯一下一步。
