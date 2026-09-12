# 回声 Echo — 多 Agent 实时交接

> 本文件是 `z3093508903-rgb/echo` 的唯一实时项目交接状态。所有 Agent 在开始、转向、验证、提交和交接时都必须更新这里。

## 当前仓库快照

| 字段 | 当前值 |
| --- | --- |
| 项目 | 回声 Echo |
| 仓库 | `z3093508903-rgb/echo` |
| 默认分支 | `main` |
| main HEAD | `bea65d0989042619d2c749978c3918572c0d5b18` |
| 当前阶段 | 真机反馈确认：当前实现仍是“监听后转发”，原通知未被 Echo 接管；下一步实现来源通知取消 + 无延迟本机 fast path |
| 更新时间 | `2026-09-13T00:58:00+08:00` |
| Android 业务代码改动 | 本轮正在修改通知接管与本机即时重发路径；不改 Room/DataStore schema |
| 真机构建与运行 | #16 → #18 覆盖链已通过；当前用户反馈：Echo 有延时、原 App 弹窗未被拦截、关闭源 App 通知后 Echo 无法捕获、Phone Link 未收到 Echo 通知 |

## 产品定位

Echo 是一个 **本地优先的 Android 注意力防火墙**。当前阶段优先证明：来源 App 保持系统通知权限作为“数据源”，但原通知由 Echo 接管；只有 Echo 判断允许的消息才重新显示。PC-first 仍保留，但 Phone Link 暂不作为本轮 blocker。

## 进行中的工作

### `notification-takeover-fastpath-20260913-webgpt`

- agent: Web GPT
- branch: `gpt/notification-takeover-fastpath-20260913` @ `bea65d0989042619d2c749978c3918572c0d5b18`
- goal: 对已选择的来源 App 真正接管系统通知；尽快取消原通知；当无延迟、无合并、无外部渠道时绕过 WorkManager，直接发布 Echo 本机通知，降低可感知延时
- owns:
  - `PROJECT_HANDOFF.md`
  - `app/src/main/java/io/github/messagerelay/RelayNotificationService.kt`
  - `app/src/main/java/io/github/messagerelay/RelayEngine.kt`
  - `app/src/main/java/io/github/messagerelay/NotificationTakeoverPolicy.kt`（如需新增）
  - `app/src/test/java/io/github/messagerelay/NotificationTakeoverPolicyTest.kt`（如需新增）
- status: `in_progress`
- updated_at: `2026-09-13T00:58:00+08:00`
- constraints:
  - 只接管已启用的来源 App；Echo 自己的通知永不递归处理
  - 用户把来源 App 的系统通知总开关关闭后，Android 不产生可监听通知，此边界无法由 Echo 绕过
  - 本轮不新增 Windows 协议、不依赖 Phone Link 成功
  - 不修改 Room Entity / DataStore schema

## 当前发布链

- PR / 非 main：`compileDebugKotlin` → `testDebugUnitTest` → `assembleDebug` → Debug artifact；不发布 GitHub Release。
- main：恢复 `echo-preview-test-signing-v2` cache；cache miss 时生成新的 v2 测试 key/cert 与随机密码；执行 `assembleRelease` → GitHub prerelease。
- v2 随机密码不写入 `GITHUB_ENV`；只在签名 step 内读取并 `add-mask` 后临时 export。
- `versionName = 3.12`。
- `versionCode = 3_120_000 + GITHUB_RUN_NUMBER`。
- 测试签名仍只是单人测试便利方案，不是正式生产密钥管理。

## 最近完成

| work_id | 结果 | 提交 / PR | 验证 | `[UNRUN]` / 下一步 |
| --- | --- | --- | --- | --- |
| `signing-log-hardening-20260912-webgpt` | 废弃已暴露 v1 测试签名身份；新建 v2 cache + 新 key/cert + 随机密码；签名密码不再进入跨 step 环境 | PR #11；main 后续 Preview #20/#22 使用 v2 | PR CI、main Release CI ✅；#22 命中 v2 signing cache | v2→v2 下一次覆盖升级可随功能包顺手验证 |
| `fixed-release-signing-v3.12-20260912-webgpt` | 固定测试签名覆盖升级机制已真机闭环 | PR #9；Preview #16/#18 | #16 安装成功；未卸载 #16 直接覆盖 #18 返回 `Success`；证书一致；数据目录未重建；#18 启动正常 | v1 已因日志泄露被安全废弃 |
| `pc-relay-v1-20260912-webgpt` | Echo 本机通知 fallback + Android 13+ 通知权限门 + Phone Link 最小出口 | PR #8；merge `2fa6be55d286089a1d6acf60357aa213ecccabb8` | PR CI + main CI ✅ | 真机反馈：存在延时；原通知未拦截；Phone Link 未收到，因此进入 takeover/fast-path 修复 |

## 真机反馈：2026-09-13

- Echo 能监听来源通知，但存在可感知延时。
- 原 App 的系统横幅/弹窗仍正常出现，说明 Echo 当前没有调用取消原通知的能力。
- 若在 ColorOS 中直接关闭来源 App 的通知显示/总通知能力，Echo 也捕获不到该通知；因此正确配置应是**保留来源通知总开关，仅关闭横幅/声音/震动/锁屏展示**。
- Windows Phone Link 当前没有收到 Echo 重发通知；本轮不把 Phone Link 当作核心 blocker，先完成 Android 端真正接管。

## 已冻结边界

- #16 → #18 覆盖升级问题已真机关闭，不再把 `INSTALL_FAILED_UPDATE_INCOMPATIBLE` 作为当前 blocker。
- Echo 不要求 Bark、飞书或钉钉；第三方转发只是可选能力。
- **PC-first** 方向保留，但 Android 端“接管 → 分类 → 重发”必须先独立成立。
- 来源 App 的系统通知总开关不能关闭，否则 `NotificationListenerService` 没有事件可消费。
- Room Entity 变化必须有 Migration；DataStore 新字段必须有默认值。

## 下一条开发链

1. `notification-takeover-fastpath-20260913-webgpt`：只对已启用来源 App 取消原通知，并建立本机即时重发 fast path。
2. PR CI 通过后合并，main 自动发布新的 v2 Preview。
3. 真机验证：来源 App 通知总开关保持开启、横幅/声音关闭；检查原通知是否被快速清除、Echo 是否立即重发、后台是否稳定。
4. Android 接管成立后，再单独判断 Phone Link 是否值得继续；若仍不可靠，再讨论局域网 Echo Bridge，而不是现在并行增加新实体。

## 完成 / 交接

```text
changed: [IN PROGRESS] 通知接管 + 本机 fast path
commit: [IN PROGRESS]
remote: gpt/notification-takeover-fastpath-20260913
checks: [UNRUN] compileDebugKotlin / testDebugUnitTest / assembleDebug / real device
data_or_schema: 计划无 Room/DataStore schema 变化
unrun: 当前全部待实现与验证
rollback: 回退本 feature PR 即可恢复现有“监听后 WorkManager 转发”逻辑
next: 完成实现后跑 PR CI，再交付真机测试
```

## 协作规则摘要

1. 开工先读 `AGENTS.md` 与本文件。
2. 修改前先登记 `work_id`、feature 分支和精确文件范围。
3. 不直接在 `main` 开发；不抢占另一 Agent 已声明的文件。
4. 方向变化、阻塞或扩大文件范围时，先更新本文件。
5. “测试通过”只能写真实运行过的测试；没跑就是 `[UNRUN]`。
6. 涉及数据/schema 必须写清迁移和回滚。
7. 完成时必须留下唯一下一步。
