# 回声 Echo — 多 Agent 实时交接

> 本文件是 `z3093508903-rgb/echo` 的唯一实时项目交接状态。所有 Agent 在开始、转向、验证、提交和交接时都必须更新这里。长期产品方向写进稳定文档，但“现在做到哪、谁在改什么、下一步是什么”只认本文件。

## 当前仓库快照

| 字段 | 当前值 |
| --- | --- |
| 项目 | 回声 Echo |
| 仓库 | `z3093508903-rgb/echo` |
| 默认分支 | `main` |
| 当前阶段 | Echo onboarding v1 已真机通过；进入 PC-first 最小闭环：Echo 本机通知 → Windows Phone Link |
| 更新时间 | `2026-09-12T15:30:00+08:00` |
| Android 业务代码改动 | 新 work 已登记，尚未开始业务代码修改 |
| 真机构建与运行 | Preview #4 已由用户安装并确认可正常进入；Windows Phone Link 已成功连接并保持代理开启可用 |

## 产品定位

Echo 是一个 **本地优先的 Android 注意力防火墙**。当前进一步冻结一条第一性原理：**手机负责捕获，电脑优先负责消费信息**。目标不是在手机里再造一个信息中心，而是减少“拿起手机检查 → 顺手打开原平台”的机会。

当前最小链路：

```text
来源 App 系统通知 → Echo 规则处理 → Echo 自己生成 Android 通知 → Windows Phone Link 同步 Echo 通知到电脑
```

本阶段不新增服务器、ntfy、Gotify、GitHub 消息仓库、GPT 实时中转或 Echo Desktop。先验证微软现成通路是否足以降低主动拿手机次数。

## 进行中的工作

```text
work_id: pc-relay-v1-20260912-webgpt
agent: 网页 GPT
branch@base: gpt/echo-pc-relay-v1-20260912@main
goal: 当来源通知命中 Echo 现有规则且没有外部 Bark/飞书/钉钉渠道时，生成一条 Echo 自身 Android 通知，供 Windows Phone Link 只同步 Echo；同时保证 Android 13+ 会请求 Echo 通知权限
owns: PROJECT_HANDOFF.md; app/src/main/java/io/github/messagerelay/RelayEngine.kt; app/src/main/java/io/github/messagerelay/RelayNotificationService.kt; app/src/main/java/io/github/messagerelay/EchoNotificationPublisher.kt; app/src/main/java/io/github/messagerelay/MainActivity.kt; app/src/main/AndroidManifest.xml
status: claimed
updated_at: 2026-09-12T15:30:00+08:00
notes: 不改 Room/DataStore schema；不做 P0/P1/P2/P3 智能分类；不取消来源 App 原通知；不宣称 Windows 已收到，只能确认 Echo 本机通知已生成。外部渠道已配置时继续走原有外部发送逻辑，避免重复提醒。
```

## 本轮设计边界

- **无外部渠道时**：Echo 本机通知成为默认输出，不再把“未配置渠道”视为发送失败。
- **有外部渠道时**：保持现有 Bark / 飞书 / 钉钉行为，本轮不叠加 Echo 本机通知，避免双重提醒。
- Echo 本机通知的点击入口只打开 Echo，不跳回原社交 App，避免形成新的“顺手刷一下”入口。
- Android 13+ 若未授予 `POST_NOTIFICATIONS`，必须请求系统通知权限；未授权时记录明确失败原因，不伪装成功。
- 记录层只能写“Echo 本机通知已生成”，不能写“电脑已收到”，因为应用无法观测 Phone Link 最终同步结果。
- Phone Link 端由用户设置为只同步 Echo；微信/抖音/小红书原通知是否在 PC 显示由 Windows Phone Link 控制，不在本轮 Android 代码中强制取消来源通知。
- 不上传真实通知正文、Token、数据库或日志到 GitHub。

## 最近完成

| work_id | 结果 | 提交 / PR | 验证 | `[UNRUN]` / 下一步 |
| --- | --- | --- | --- | --- |
| `onboarding-v1-20260912-webgpt` | 四步 onboarding → 两步；移除 Bark/飞书/钉钉强制门槛；修复主题对比度 | PR #7；merge `34f856acf9f00314de2210338a7e3f7c5cea9b4f`；Preview #4 | PR CI compile/unit/assemble ✅；main CI + Release ✅；用户已安装并确认可正常进入 | 深浅色完整矩阵未系统回归，但阻塞问题已解除 |
| `phone-link-setup-20260912-user` | Windows Phone Link / Android Link to Windows 配对成功；首次微软登录因代理报 `0x80190001`，退出代理完成认证后重新开启代理仍保持正常连接 | 用户现场验证 | Windows 与手机当前正常连接 | 下一步验证 Echo 自身通知能否稳定出现在 Windows |
| `release-pipeline-20260912-webgpt` | 自动 APK artifact + main prerelease 发布链 | PR #4 | Preview #1 / #4 已真实发布 | 后续 main push 自动生成 Preview |

## 已冻结边界

- Echo 不要求 Bark、飞书或钉钉；第三方转发只是可选能力。
- Echo 首次配置最小主路径：通知访问 → 选择来源 App → 进入主界面。
- **PC-first**：现阶段优先复用 Windows Phone Link，不新增自建 PC 通信实体。
- “Echo 生成了本机通知”与“Windows 已收到”必须严格区分；后者需要用户真机实测。
- 第一阶段仍不凭想象硬编码微信、抖音、小红书的 P0/P1/P2/P3 分类；先让观察与出口链路跑通。
- 测试 APK 通过 GitHub Actions 产出并发布 GitHub Preview/Prerelease。
- Room Entity 变化必须有 Migration；DataStore 新字段必须有默认值。
- 未经真机验证的行为必须标记 `[UNRUN]` / `[UNVERIFIED]`。

## 下一条开发链

1. 实现 `EchoNotificationPublisher` 与无渠道 fallback，并补 Android 13+ 通知权限请求。
2. GitHub Actions 执行 `compileDebugKotlin`、`testDebugUnitTest`、`assembleDebug`；`lintDebug` 若 workflow 未覆盖继续标 `[UNRUN]`。
3. CI 通过后合入 main，自动发布新 Preview APK。
4. 用户手机安装后，在 Phone Link 中关闭微信/抖音/小红书 PC 通知，仅保留 Echo。
5. 触发一个已选来源 App 的真实通知，验证：手机 Echo 产生一条通知 → Windows 出现同一条 Echo 通知。
6. 闭环成立后再做 Notification Observatory 和真实通知样本分类。

## 协作规则摘要

1. 开工先读 `AGENTS.md` 与本文件。
2. 修改前先登记 `work_id`、feature 分支和精确文件范围。
3. 不直接在 `main` 开发；不抢占另一 Agent 已声明的文件。
4. 方向变化、阻塞或扩大文件范围时，先更新本文件。
5. “测试通过”只能写真实运行过的测试；没跑就是 `[UNRUN]`。
6. 涉及数据/schema 必须写清迁移和回滚。
7. 完成时必须留下唯一下一步。
