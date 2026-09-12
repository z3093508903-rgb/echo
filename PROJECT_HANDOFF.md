# 回声 Echo — 多 Agent 实时交接

> 本文件是 `z3093508903-rgb/echo` 的唯一实时项目交接状态。所有 Agent 在开始、转向、验证、提交和交接时都必须更新这里。

## 当前仓库快照

| 字段 | 当前值 |
| --- | --- |
| 项目 | 回声 Echo |
| 仓库 | `z3093508903-rgb/echo` |
| 默认分支 | `main` |
| main HEAD | `98e07522b4e89cd59f8eaffdf0258933799b7d09` |
| 当前阶段 | Preview 测试固定签名 + Release APK 发布链已上线，等待真机一次卸载迁移与后续覆盖升级验证 |
| 更新时间 | `2026-09-12T23:19:00+08:00` |
| Android 业务代码改动 | 本 work 未改通知业务逻辑，仅改构建/发布配置与版本信息 |
| 真机构建与运行 | Echo 3.12 Preview #16 已发布；旧 Preview #8 → #16 仍需用户完整卸载一次 |

## 产品定位

Echo 是一个 **本地优先的 Android 注意力防火墙**。当前阶段只有单人真机测试，因此发布链优先低维护成本：PR 保持 Debug CI，main Preview 使用 Actions Cache 维护的测试专用签名并发布 Release APK。

## 进行中的工作

当前无代码 work 占用。下一步是真机验证 Preview #16 的一次性迁移与后续覆盖升级。

## 当前发布链

- PR / 非 main：`compileDebugKotlin` → `testDebugUnitTest` → `assembleDebug` → Debug artifact；不发布 GitHub Release。
- main：恢复 `echo-preview-test-signing-v1` Actions cache；首次 cache miss 时 Runner 自动生成测试 keystore。
- main 使用 `assembleRelease` 产出 `Echo-preview.apk`。
- keystore 不提交 Git，也不进入 Preview Release；测试者无需手动配置 keystore、密码或 Repository Secrets。
- `versionName = 3.12`。
- `versionCode = 3_120_000 + GITHUB_RUN_NUMBER`；Preview #16 为 `3120016`。
- 测试签名依赖 GitHub Actions Cache，不视为正式生产签名体系；若 cache 将来被清除，可能再次需要一次完整卸载迁移。

## 最近完成

| work_id | 结果 | 提交 / PR | 验证 | `[UNRUN]` / 下一步 |
| --- | --- | --- | --- | --- |
| `fixed-release-signing-v3.12-20260912-webgpt` | PR Debug / main Release 拆分；3.12；单调 versionCode；Actions Cache 自动维护测试签名；无需用户配置 Secrets | PR #9；merge `98e07522b4e89cd59f8eaffdf0258933799b7d09`；Preview #16 | PR workflow commit CI run #13：compile/unit/assembleDebug ✅；main CI run #16：compile/unit ✅、测试签名恢复/生成 ✅、`assembleRelease` ✅、signed Preview artifact ✅、GitHub prerelease ✅ | 旧 Debug → Preview #16 一次卸载迁移 `[UNRUN]`；Preview #16 → 下一更高 versionCode 覆盖升级 `[UNRUN]` |
| `pc-relay-v1-20260912-webgpt` | Echo 本机通知 fallback + Android 13+ 通知权限门 + Phone Link 最小出口 | PR #8；merge `2fa6be55d286089a1d6acf60357aa213ecccabb8`；Preview #8 | PR CI + main CI ✅ | Echo→Phone Link→Windows 最终真机链仍待用户验证 |

## 已冻结边界

- 当前单人 Preview 测试阶段允许使用 cache-backed 测试签名；它不是正式生产身份。
- Preview Release 必须来自 `assembleRelease`；PR Debug artifact 不作为持续安装包。
- 历史 Preview #1/#4/#8 使用随机 Debug 签名，迁移到 Preview #16 必须完整卸载一次。
- 后续若 cache 签名保持不变且 versionCode 更高，应可直接覆盖；必须由真机验证后才能宣称闭环完成。
- Echo 不要求 Bark、飞书或钉钉；第三方转发只是可选能力。
- **PC-first**：现阶段优先复用 Windows Phone Link。
- Room Entity 变化必须有 Migration；DataStore 新字段必须有默认值。

## 下一条开发链

1. 用户卸载当前旧随机 Debug 签名 Echo（如需保留配置先自行备份）。
2. 安装 `Echo 3.12 Preview #16`。
3. 验证 Echo 本机通知 → Windows Phone Link → Windows。
4. 下一次代码迭代发布更高 versionCode Preview 后，直接覆盖安装 #16；若成功，则关闭 `INSTALL_FAILED_UPDATE_INCOMPATIBLE` 发布链问题。

## 完成 / 交接

```text
changed: PR Debug/main Release 拆分；Actions Cache 测试签名；assembleRelease Preview；versionName 3.12；versionCode 3_120_000+run_number
commit: merge 98e07522b4e89cd59f8eaffdf0258933799b7d09
remote: PR #9 merged
checks: PR CI run #13 PASS；main CI run #16 PASS；Echo 3.12 Preview #16 published
data_or_schema: 无 Room/DataStore schema 变化
unrun: 旧 Debug→#16 真机卸载迁移；#16→下一 Preview 覆盖升级；Echo→Phone Link→Windows 业务链
rollback: 回退 PR #9 可恢复旧 Debug 发布链，但会重新引入随机签名问题，不建议
next: 用户安装 Preview #16 并继续真机测试
```

## 协作规则摘要

1. 开工先读 `AGENTS.md` 与本文件。
2. 修改前先登记 `work_id`、feature 分支和精确文件范围。
3. 不直接在 `main` 开发；不抢占另一 Agent 已声明的文件。
4. 方向变化、阻塞或扩大文件范围时，先更新本文件。
5. “测试通过”只能写真实运行过的测试；没跑就是 `[UNRUN]`。
6. 涉及数据/schema 必须写清迁移和回滚。
7. 完成时必须留下唯一下一步。
