# 回声 Echo — 多 Agent 实时交接

> 本文件是 `z3093508903-rgb/echo` 的唯一实时项目交接状态。所有 Agent 在开始、转向、验证、提交和交接时都必须更新这里。

## 当前仓库快照

| 字段 | 当前值 |
| --- | --- |
| 项目 | 回声 Echo |
| 仓库 | `z3093508903-rgb/echo` |
| 默认分支 | `main` |
| main HEAD | `2fa6be55d286089a1d6acf60357aa213ecccabb8` |
| 当前阶段 | Preview 发布链切换为测试专用固定签名 + Release APK |
| 更新时间 | `2026-09-12T23:08:00+08:00` |
| Android 业务代码改动 | 本 work 不改通知业务逻辑，只改构建/发布配置与版本信息 |
| 真机构建与运行 | Preview #8 仍是旧随机 Debug 签名；首个测试固定签名 Preview 尚未发布 |

## 进行中的工作

```text
work_id: fixed-release-signing-v3.12-20260912-webgpt
agent: 网页 GPT
branch@base: gpt/fixed-release-signing-v3.12-20260912@2fa6be55d286089a1d6acf60357aa213ecccabb8
goal: PR 保持 Debug CI；main Preview 使用测试专用固定签名 assembleRelease；versionCode 每次发布单调递增；versionName=3.12
owns: PROJECT_HANDOFF.md; .github/workflows/android-ci.yml; app/build.gradle.kts; docs/RELEASE_SIGNING.md
status: verifying
updated_at: 2026-09-12T23:08:00+08:00
notes: 用户明确当前仅单人测试，不希望维护 keystore/密码/Secrets。方案已从 Repository Secrets 收缩为 GitHub Actions Cache 保存测试签名 keystore：首次 main 自动生成，后续 main 复用同一 cache key。PR 仍只构建 Debug artifact；main 执行 assembleRelease。该方案不是正式生产签名体系；若 cache 将来丢失，可能再次需要完整卸载迁移。
```

## 本轮发布约束

- PR / 非 main：`compileDebugKotlin` → `testDebugUnitTest` → `assembleDebug` → Debug artifact；不发布 GitHub Release。
- main：恢复 `echo-preview-test-signing-v1` Actions cache；若首次没有 keystore，则在 Runner 内自动生成测试签名。
- main 使用现有 `MESSAGE_RELAY_KEYSTORE_PATH` / `PASSWORD` / `ALIAS` / `KEY_PASSWORD` 环境变量注入 Gradle，执行 `assembleRelease`。
- keystore 不提交 Git、不进入 APK artifact 之外的任何发布物，也不要求用户配置 Secrets。
- `versionName = 3.12`。
- `versionCode = 3_120_000 + GITHUB_RUN_NUMBER`，发布单调递增。
- 旧 Preview #1/#4/#8 随机 Debug 签名用户，第一次迁移仍需完整卸载一次。
- Actions Cache 不是正式密钥保管；cache 丢失可能导致测试签名轮换。正式公开稳定发行前必须迁移到长期 Release keystore + Secrets。
- 不改 Room/DataStore schema。

## 最近完成

| work_id | 结果 | 提交 / PR | 验证 | `[UNRUN]` / 下一步 |
| --- | --- | --- | --- | --- |
| `fixed-release-signing-v3.12-20260912-webgpt` | PR Debug / main Release 拆分；3.12；单调 versionCode；测试签名改为 Actions cache 自动维护 | PR #9；workflow simplification `ce40adb9a0798ea7ea6915fcf1464e296b6e25dc` | 旧 secret 方案 PR CI run #12 已通过 compile/unit/assembleDebug；简化后的最新 head 等待 CI | main `assembleRelease`、首次 cache 签名生成、Preview 发布、覆盖升级仍待验证 |
| `pc-relay-v1-20260912-webgpt` | Echo 本机通知 fallback + Android 13+ 通知权限门 + Phone Link 最小出口 | PR #8；merge `2fa6be55d286089a1d6acf60357aa213ecccabb8`；Preview #8 | PR CI + main CI ✅ | Echo→Phone Link→Windows 最终真机链仍由用户验证 |

## 已冻结边界

- 当前阶段只有单人测试，优先低维护成本；测试签名不等同正式生产签名。
- Preview Release 必须来自 `assembleRelease`，PR Debug artifact 不作为可持续安装包。
- 首次从历史随机 Debug 签名迁移必须完整卸载一次。
- Echo 不要求 Bark、飞书或钉钉；第三方转发只是可选能力。
- **PC-first**：现阶段优先复用 Windows Phone Link。
- Room Entity 变化必须有 Migration；DataStore 新字段必须有默认值。

## 下一条开发链

1. 等待 PR #9 最新 head 的 Debug CI 通过。
2. 合并 PR #9 到 main。
3. main Actions 首次生成/缓存测试签名，执行 `assembleRelease` 并发布 Echo 3.12 Preview。
4. 用户完整卸载 Preview #8 一次，安装首个测试固定签名 Preview。
5. 再发布一个更高 versionCode 的 Preview，真机确认可以直接覆盖升级。
6. 覆盖升级闭环成立后，回到 Echo → Phone Link → Windows 通知链验证。

## 完成 / 交接

```text
changed: workflow 拆分 PR Debug 与 main Release；main 测试签名由 Actions Cache 自动维护；Gradle 支持 MESSAGE_RELAY_VERSION_CODE/NAME；versionName=3.12；versionCode=3_120_000+run_number；更新测试签名文档
remote: gpt/fixed-release-signing-v3.12-20260912 / PR #9
checks: 旧方案 PR CI run #12 PASS；最新 cache-signing head CI 待跑
data_or_schema: 无 Room/DataStore schema 变化
unrun: main assembleRelease；首次 test keystore cache；Release publish；旧 Debug→测试签名一次卸载；下一测试签名 Preview 覆盖升级
rollback: 回退 PR #9 即恢复旧 Debug Preview 发布链（不建议）
next: 最新 PR CI 通过后 merge，验证 main Release
```

## 协作规则摘要

1. 开工先读 `AGENTS.md` 与本文件。
2. 修改前先登记 `work_id`、feature 分支和精确文件范围。
3. 不直接在 `main` 开发；不抢占另一 Agent 已声明的文件。
4. 方向变化、阻塞或扩大文件范围时，先更新本文件。
5. “测试通过”只能写真实运行过的测试；没跑就是 `[UNRUN]`。
6. 涉及数据/schema 必须写清迁移和回滚。
7. 完成时必须留下唯一下一步。
