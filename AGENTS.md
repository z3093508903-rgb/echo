# AGENTS.md

本文供所有参与 **回声 Echo** 的 Codex、网页版 GPT、其他 Agent 与维护者共同遵守。

## 开工必读

任何修改前必须依次读取：

1. `AGENTS.md`
2. `PROJECT_HANDOFF.md`

然后在 `PROJECT_HANDOFF.md` 的“进行中的工作”登记 `work_id`、feature 分支、精确文件范围、目标和状态。未登记不得开始写代码。

如果已有 Agent 声明了相同文件或目录，优先避免并行修改；只读审查可以继续，但建议交给当前 owner，不得直接覆盖。

## 项目定位

Echo 基于 `MSCNUAN/message-relay-android` 演进。上游是 Kotlin + Jetpack Compose + Material 3 编写的 Android 通知转发工具；Echo 将其逐步重塑为 **本地优先的注意力防火墙**。

Echo 的目标不是制造第四个信息流，而是降低用户因为期待社会反馈而反复主动打开微信、抖音、小红书等平台检查消息的频率：

- 真正需要行动的关键消息可以及时提醒；
- 普通社会反馈静默、延迟或批量查看；
- 点赞、收藏等数字反馈避免逐条刺激；
- 推荐、直播、低价值分享等平台诱导尽量隔离；
- 优先本机判断与本机存储，私人通知内容默认不离开设备。

当前事实与实时进度以 `PROJECT_HANDOFF.md` 为唯一权威来源，不从聊天记录猜测项目状态。

## 多 Agent 协作规则

1. 不直接在 `main` 上开发；每项工作使用独立 feature 分支。
2. 修改前必须登记精确 `owns` 范围，不得写“整个项目”。
3. 扩大修改范围、遇到阻塞、改变方案或准备交接时，先更新 `PROJECT_HANDOFF.md`。
4. 完成时必须记录：`changed`、`commit`、`remote`、`checks`、`data_or_schema`、`unrun`、`rollback`、`next`。
5. 测试只能记录真实执行过的命令和结果；未运行统一标记 `[UNRUN]`。
6. 未经真机验证的行为不得描述成稳定能力。
7. 一个 Agent 不应重写另一 Agent 正在维护的交接状态；发现冲突先重新读取远端再处理。
8. 产品长期方向可以进入稳定文档，但实时“谁在做什么、做到哪里”只维护一份 `PROJECT_HANDOFF.md`。

## 上游工程维护规则

以下约束继承自 Message Relay，继续有效：

1. 不要把全部代码继续堆进 `MainActivity.kt`；新增复杂逻辑应拆到独立文件或状态层。
2. UI 不应直接执行数据库和网络操作，优先通过 Repository、Worker 或业务对象完成。
3. 修改 Room Entity 时必须提供 Migration，并兼容旧用户数据。
4. 新增 DataStore 字段必须有默认值。
5. 不得清空用户现有配置，不得在升级时静默丢弃渠道、规则和模板。
6. 不得在日志中输出 Token、Webhook、完整电话号码、验证码或完整私人通知内容。
7. 未经真机验证的电话、SIM、AOD/Doze、通知行为和厂商后台能力不能写成稳定。
8. 所有用户提示使用简体中文，除非产品后续明确加入多语言。
9. README、CHANGELOG、交接文档和应用内更新日志必须保持事实一致。
10. 源码仓库不提交 APK、数据库、备份、日志、签名文件、本机私有配置或真实通知样本。
11. 保留 GPL-3.0-only 许可证、原作者归属与上游 Git 历史。

## Echo 当前开发原则

- 先验证上游原版可构建、可运行，再做个性化。
- 第一阶段优先建立“通知观察模式”，看真实 Android 系统通知长什么样。
- 在真实样本出现前，不凭想象硬编码微信、抖音、小红书规则。
- 观察数据优先做脱敏；如果必须保留正文用于本地分类测试，不得提交到 GitHub。
- 初期避免大规模 UI 重写和 AI 分类器；先证明“减少主动检查”这个核心假设。
- 产品核心指标优先看用户主动打开平台检查反馈的次数是否下降，而不是功能数量。

## 常用验证命令

```powershell
.\gradlew.bat :app:compileDebugKotlin --no-daemon --console=plain
.\gradlew.bat :app:testDebugUnitTest --no-daemon --console=plain
.\gradlew.bat :app:lintDebug --no-daemon --console=plain
.\gradlew.bat :app:assembleDebug --no-daemon --console=plain
.\gradlew.bat :app:installDebug connectedDebugAndroidTest --no-daemon --console=plain
```

`connectedDebugAndroidTest` 需要可用 Android 设备或模拟器。若没有设备，不得把它写成已验证。
