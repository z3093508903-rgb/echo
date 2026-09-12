# 回声 Echo — 多 Agent 实时交接

> 本文件是 `z3093508903-rgb/echo` 的唯一实时项目交接状态。所有 Agent 在开始、转向、验证、提交和交接时都必须更新这里。

## 当前仓库快照

| 字段 | 当前值 |
| --- | --- |
| 项目 | 回声 Echo |
| 仓库 | `z3093508903-rgb/echo` |
| 默认分支 | `main` |
| main HEAD | `007eeba03c816fa64338844e40f1b4294d932b09` |
| 当前阶段 | v1 日志泄露已修复并轮换到 v2 测试签名；Preview #20 已发布，下一步是真机迁移后验证 Echo → Phone Link → Windows |
| 更新时间 | `2026-09-12T23:50:00+08:00` |
| Android 业务代码改动 | 本轮未改通知业务逻辑，只改测试签名发布链与交接状态 |
| 真机构建与运行 | #16 → #18 未卸载覆盖成功；#18 `MainActivity` `Status: ok`；Preview #20 为首个 v2 签名包，尚未真机安装 |

## 产品定位

Echo 是一个 **本地优先的 Android 注意力防火墙**。当前阶段只有单人真机测试，因此发布链优先低维护成本：PR 保持 Debug CI，main Preview 使用 Actions Cache 维护的测试专用签名并发布 Release APK。

## 进行中的工作

当前无代码 work 占用。下一步由真机验证接手：从 #18 一次性迁移到 v2 Preview，然后测试权限、后台通知和 `Echo → Phone Link → Windows`。

## 当前发布链

- PR / 非 main：`compileDebugKotlin` → `testDebugUnitTest` → `assembleDebug` → Debug artifact；不发布 GitHub Release。
- main：恢复 `echo-preview-test-signing-v2` cache；cache miss 时生成新的 v2 测试 key/cert 与随机密码；执行 `assembleRelease` → GitHub prerelease。
- v2 随机密码不写入 `GITHUB_ENV`；只在签名 step 内读取并 `add-mask` 后临时 export。
- `versionName = 3.12`。
- `versionCode = 3_120_000 + GITHUB_RUN_NUMBER`；Preview #20 为 `3120020`。
- 测试签名仍只是单人测试便利方案，不是正式生产密钥管理。

## 最近完成

| work_id | 结果 | 提交 / PR | 验证 | `[UNRUN]` / 下一步 |
| --- | --- | --- | --- | --- |
| `signing-log-hardening-20260912-webgpt` | 废弃已暴露 v1 测试签名身份；新建 v2 cache + 新 key/cert + 随机密码；签名密码不再进入跨 step 环境；公开日志不再显示密码值 | PR #11；merge `007eeba03c816fa64338844e40f1b4294d932b09`；Preview #20 | PR CI #19：compile/unit/assembleDebug ✅；main CI #20：compile/unit ✅、v2 cache miss 后生成 key ✅、`assembleRelease` ✅、artifact ✅、prerelease ✅、v2 cache saved ✅；#20 完整公开 job log 已检查，未再出现 `MESSAGE_RELAY_KEYSTORE_PASSWORD` / `MESSAGE_RELAY_KEY_PASSWORD` 的实际值 | #18 → #20 因主动安全轮换需完整卸载一次 `[UNRUN]`；#20 → 后续 v2 Preview 覆盖升级 `[UNRUN]` |
| `fixed-release-signing-v3.12-20260912-webgpt` | 固定测试签名覆盖升级机制已真机闭环 | PR #9；Preview #16/#18 | #16 安装成功；未卸载 #16 直接覆盖 #18 返回 `Success`；#16/#18 证书一致；覆盖前后数据目录标识一致；#18 `MainActivity` `Status: ok` | v1 已因日志泄露被安全废弃 |
| `pc-relay-v1-20260912-webgpt` | Echo 本机通知 fallback + Android 13+ 通知权限门 + Phone Link 最小出口 | PR #8；merge `2fa6be55d286089a1d6acf60357aa213ecccabb8` | PR CI + main CI ✅ | 权限实际授权、页面视觉、后台通知、Echo→Phone Link→Windows 端到端仍 `[UNRUN]` |

## 安全事件 / 发布链注意事项

- #16 Actions：`https://github.com/z3093508903-rgb/echo/actions/runs/34701623661`
- #18 Actions：`https://github.com/z3093508903-rgb/echo/actions/runs/34701899520`
- 上述两个 v1 公开日志出现过未遮蔽的签名密码环境变量，且 v1 密码可由公开仓库名确定性派生，因此 v1 测试签名身份已废弃。
- #20 Actions：`https://github.com/z3093508903-rgb/echo/actions/runs/34703130469`
- #20 使用全新 v2 测试 key/cert 和随机密码；公开 job log 已人工检查：脚本源码会显示变量名和 `openssl rand` / `cat` 命令，但不会显示随机密码实际值；后续 env block 也不再携带两个密码变量。
- v2 keystore 与其随机密码文件仍一起放在 Actions Cache 中，只用于当前单人测试便利；正式公开发行前必须迁移到长期 Release keystore + 正式 Secrets/受控密钥存储。

## 已冻结边界

- #16 → #18 覆盖升级问题已真机关闭，不再把 `INSTALL_FAILED_UPDATE_INCOMPATIBLE` 作为当前 blocker。
- v1 → v2 因安全轮换需要一次卸载，不能归类为覆盖升级失败。
- v2 后续应保持相同证书并通过更高 versionCode 直接覆盖，但必须再经一次真机覆盖验证后才能宣称 v2 闭环。
- Echo 不要求 Bark、飞书或钉钉；第三方转发只是可选能力。
- **PC-first**：现阶段优先复用 Windows Phone Link。
- Room Entity 变化必须有 Migration；DataStore 新字段必须有默认值。

## 下一条开发链

1. 用户完整卸载当前 Preview #18 一次，并安装首个 v2 包 `Echo 3.12 Preview #20 (3120020)`。
2. 安装后直接开始验证：通知权限实际授权、Echo 本机通知、后台通知，以及 `Echo → Phone Link → Windows`。
3. 下一次任意功能迭代产生更高 versionCode 的 v2 Preview 时，直接覆盖 #20；成功后关闭 v2 覆盖验证项。
4. 页面视觉问题继续记录，但不阻塞上述功能链测试。

## 完成 / 交接

```text
changed: 修复公开 Actions 签名密码泄露；废弃 v1；引入 v2 cache-backed 随机密码测试签名；#20 已成功构建并发布
commit: merge 007eeba03c816fa64338844e40f1b4294d932b09
remote: PR #11 merged
checks: PR CI #19 PASS；main CI #20 PASS；assembleRelease PASS；Preview #20 published；v2 cache saved；#20 public log inspected and no actual signing password value exposed
data_or_schema: 无 Room/DataStore schema 变化
unrun: #18→#20 一次卸载迁移；#20 真机启动/权限/页面视觉/后台通知；#20→后续 v2 直接覆盖；Echo→Phone Link→Windows
rollback: 不恢复 v1 已暴露签名；若 v2 workflow 故障仅回退实现方式，并继续使用新的未暴露签名身份
next: 用户卸载 #18 一次、安装 Preview #20，然后立即测试 Echo→Phone Link→Windows
```

## 协作规则摘要

1. 开工先读 `AGENTS.md` 与本文件。
2. 修改前先登记 `work_id`、feature 分支和精确文件范围。
3. 不直接在 `main` 开发；不抢占另一 Agent 已声明的文件。
4. 方向变化、阻塞或扩大文件范围时，先更新本文件。
5. “测试通过”只能写真实运行过的测试；没跑就是 `[UNRUN]`。
6. 涉及数据/schema 必须写清迁移和回滚。
7. 完成时必须留下唯一下一步。
