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
| 当前阶段 | APK 自动发布链已合入；等待仓库首次启用 GitHub Actions，然后进入 Echo onboarding v1 |
| 更新时间 | `2026-09-12T02:31:00+08:00` |
| Android 业务代码改动 | 尚无 Echo 业务改动 |
| 真机构建与运行 | `[OBSERVED]` 用户已进入原版首次配置流程；新版自动发布链尚未实际运行 |

## 产品定位

Echo 是一个 **本地优先的 Android 注意力防火墙**。目标不是制造第四个信息流，而是降低因为期待社会反馈而反复主动打开微信、抖音、小红书等平台检查消息的频率。

当前方向：P0 需要行动 → 及时提醒；P1 普通社会反馈 → 延迟/批量；P2 点赞收藏关注 → 汇总；P3 平台诱导/推荐/低价值分享 → 默认过滤。第一阶段先观察真实系统通知，不凭想象硬编码规则。

## 进行中的工作

暂无代码工作。当前存在一个仓库级外部阻塞：Fork 后 GitHub Actions 尚未启用，API 查询 `actions/runs` 返回 0 条，因此自动构建/Release 未实际执行。

## 最近完成

| work_id | 结果 | 提交 / PR | 验证 | `[UNRUN]` / 下一步 |
| --- | --- | --- | --- | --- |
| `release-pipeline-20260912-webgpt` | CI 增加 APK artifact、`workflow_dispatch` 与 `main` 自动 GitHub prerelease；测试包固定名 `Echo-preview.apk` | PR #4；merge `99dd15f` | workflow 文件已远端复核；PR 可合并并已合入 | Actions 实际 run / APK / Release `[UNRUN]`，因为 Fork 当前未触发任何 Actions；用户需首次启用 workflows 后手动 Run workflow |
| `baseline-ux-observation-20260912-user` | 用户确认 UI 观感差、白底白字/低对比度；原版 onboarding 强制第三方渠道 | 用户真机观察；PR #3 已合入 main | 源码复核与现象一致 | 下一步 Echo onboarding v1 |
| `bootstrap-20260911-webgpt` | 建立 Echo 产品定位、唯一实时交接表和多 Agent 强制规则 | PR #1 已合入 main | 远端复核 | 后续逐项验证 |

### `release-pipeline-20260912-webgpt` 交接详情

```text
changed: .github/workflows/android-ci.yml 增加 APK artifact、手动触发、main 自动 prerelease；未改 Android 业务代码
commit: 93493fa + df05100 + 9a5987e；merge 99dd15f
remote: PR #4 已 merge 到 main
checks: workflow 内容与 PR 状态已远端复核；main merge 成功；actions/runs 查询为 0
 data_or_schema: 无
unrun: GitHub Actions compile/test/assemble、artifact、Release、APK 安装全部 [UNRUN]，原因是 Fork workflows 尚未首次启用
rollback: revert PR #4 / merge 99dd15f；不影响用户数据
next: 用户在 GitHub Actions 页启用 workflows，选择 Android CI → Run workflow(main)；然后网页 GPT 检查 run、artifact 和 Release
```

## 已冻结边界

- 保留上游 GPL-3.0-only 许可证、原作者归属与 Git 历史。
- Echo 不要求用户必须拥有 Bark、飞书或钉钉；第三方转发渠道只是可选高级能力。
- Echo 首次配置最小主路径：通知访问权限 → 选择需要观察/管理的 App → 进入主界面。
- 白底白字/低对比度是阻塞可读性的 bug，优先修复。
- 测试 APK 通过 GitHub Actions 构建；GitHub Release 必须标记 Preview/Prerelease，不视为正式稳定版本。
- 当前 Preview 使用 debug 构建；跨构建覆盖安装签名稳定性尚未保证，必要时可能需要卸载旧测试版后安装。
- 用户真实通知正文、数据库、备份、Token、Webhook、验证码、电话号码等敏感数据不得进入源码仓库或 Release。
- Room Entity 变化必须有 Migration；DataStore 新字段必须有默认值。
- 未经真机验证的行为必须标记 `[UNRUN]` 或 `[UNVERIFIED]`。
- `main` 只接收已交接、可回滚、验证状态明确的改动；Agent 日常开发使用 feature 分支。

## 下一条开发链

1. 用户首次启用 GitHub Actions，并运行 `Android CI` 的 `main` workflow；确认产生 `Echo-preview.apk` 与 prerelease。
2. Echo onboarding v1：去除 Bark / 飞书 / 钉钉强制依赖，首次配置只保留“通知访问 → 选择来源 App → 进入主界面”。
3. 同轮修复 onboarding 文字/背景对比度。
4. 自动发布新 Preview APK，用户手机复测。
5. 建立通知观察模式并观察微信 / 抖音 / 小红书实际系统通知格式。
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
7. 完成时必须留下唯一下一步。
