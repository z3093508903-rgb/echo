# 回声 Echo — 多 Agent 实时交接

> 本文件是 `z3093508903-rgb/echo` 的唯一实时项目交接状态。所有 Agent 在开始、转向、验证、提交和交接时都必须更新这里。长期产品方向写进稳定文档，但“现在做到哪、谁在改什么、下一步是什么”只认本文件。

## 当前仓库快照

| 字段 | 当前值 |
| --- | --- |
| 项目 | 回声 Echo |
| 仓库 | `z3093508903-rgb/echo` |
| 默认分支 | `main` |
| main HEAD | `2fa6be55d286089a1d6acf60357aa213ecccabb8` |
| 当前阶段 | 修复 Preview 固定 Release 签名与单调 versionCode 发布链 |
| 更新时间 | `2026-09-12T22:07:00+08:00` |
| Android 业务代码改动 | 本 work 不改通知业务逻辑，只改构建/发布配置与版本信息 |
| 真机构建与运行 | Preview #8 已发布；旧 Preview 使用 GitHub Runner 随机 Debug 签名，跨 Preview 覆盖安装可能报 `INSTALL_FAILED_UPDATE_INCOMPATIBLE` |

## 产品定位

Echo 是一个 **本地优先的 Android 注意力防火墙**。当前冻结第一性原则：**手机负责捕获，电脑优先负责消费信息**。发布链必须支持连续真机迭代，因此 Preview 必须使用稳定签名和单调递增 `versionCode`。

## 进行中的工作

```text
work_id: fixed-release-signing-v3.12-20260912-webgpt
agent: 网页 GPT
branch@base: gpt/fixed-release-signing-v3.12-20260912@2fa6be55d286089a1d6acf60357aa213ecccabb8
goal: PR 保持 Debug CI；main Preview 改为固定签名 assembleRelease；缺签名 Secrets 时禁止发布；versionCode 每次 main 发布单调递增；目标 versionName 同步到 3.12
owns: PROJECT_HANDOFF.md; .github/workflows/android-ci.yml; app/build.gradle.kts; docs/RELEASE_SIGNING.md
status: editing
updated_at: 2026-09-12T22:07:00+08:00
notes: 当前 GitHub connector 不提供 Repository Secrets 写接口，因此代码会强制要求 Secrets 齐全后才允许 main Release 发布，但 keystore/password Secrets 需仓库管理员在 GitHub Settings 中一次性添加。不得提交 keystore 或密码。旧随机 Debug 签名用户迁移到固定 Release 签名时仍需完整卸载一次；之后相同固定签名 + 更高 versionCode 应可直接覆盖升级。
```

## 本轮发布约束

- Pull Request：继续执行 Debug Kotlin 编译、Debug 单元测试、`assembleDebug`，可上传 Debug CI artifact，但不发布 GitHub Release。
- `main` / main 手动运行：发布物必须来自 `assembleRelease`，不得再发布 `app-debug.apk`。
- 固定签名通过 GitHub Actions Secrets 注入：workflow 在 Runner 临时目录还原 keystore，并映射现有 `MESSAGE_RELAY_KEYSTORE_PATH`、`MESSAGE_RELAY_KEYSTORE_PASSWORD`、`MESSAGE_RELAY_KEY_ALIAS`、`MESSAGE_RELAY_KEY_PASSWORD`。
- Secrets 缺任意一项时，main Release job 必须明确失败，且后续 GitHub Release 步骤不得执行。
- keystore、密码、base64 私钥内容不得进入源码、日志、artifact 或 Release。
- `versionCode` 由 main workflow 注入单调递增值；`versionName` 本轮目标为 `3.12`。
- 旧随机 Debug 签名 Preview 无法被新固定 Release 签名原地覆盖；用户需完整卸载旧版一次。该迁移会清除当前 app 本地数据，需在 Release 说明中明确提示。
- 不改 Room/DataStore schema。

## 最近完成

| work_id | 结果 | 提交 / PR | 验证 | `[UNRUN]` / 下一步 |
| --- | --- | --- | --- | --- |
| `pc-relay-v1-20260912-webgpt` | Echo 本机通知 fallback + Android 13+ 通知权限门 + Phone Link 最小出口 | PR #8；merge `2fa6be55d286089a1d6acf60357aa213ecccabb8`；Preview #8 | PR CI compile/unit/assembleDebug ✅；main CI/Preview #8 ✅ | Echo→Phone Link→Windows 最终真机链仍由用户验证 |
| `onboarding-v1-20260912-webgpt` | 四步 onboarding → 两步；移除渠道强制门槛；修复主题对比度 | PR #7；merge `34f856acf9f00314de2210338a7e3f7c5cea9b4f`；Preview #4 | CI ✅；用户真机可进入 | 阻塞已解除 |
| `release-pipeline-20260912-webgpt` | 自动 APK artifact + prerelease | PR #4 | Preview #1/#4/#8 已发布 | 当前发现发布的是随机 Debug 签名 APK，本 work 修复 |

## 已冻结边界

- Echo 不要求 Bark、飞书或钉钉；第三方转发只是可选能力。
- **PC-first**：现阶段优先复用 Windows Phone Link，不新增自建 PC 通信实体。
- Preview 从本 work 起必须采用固定 Release 签名；CI Debug artifact 与可安装 Preview Release 是两类不同产物。
- 测试结果只记录真实执行值；Secrets 尚未配置时不得声称 signed Release 已发布成功。
- Room Entity 变化必须有 Migration；DataStore 新字段必须有默认值。

## 下一条开发链

1. 修改 `app/build.gradle.kts`：支持 workflow 注入单调 versionCode，默认/目标 versionName 设为 `3.12`。
2. 修改 Android CI：PR 走 Debug；main 预检签名 Secrets、临时还原 keystore、`assembleRelease`、上传/发布固定签名 APK。
3. 增加不含秘密的 `docs/RELEASE_SIGNING.md`，只记录 Secret 名称、一次性配置与迁移规则。
4. PR CI 验证 Debug 链不回归。
5. 仓库管理员添加固定签名 Secrets 后，合入 main 并验证 main Release；若 Secrets 未配置，预期 main workflow 应失败且不得产生新 Preview Release。
6. 首个固定签名 Preview：旧随机签名用户完整卸载一次再安装；后续版本验证可直接覆盖升级。

## 协作规则摘要

1. 开工先读 `AGENTS.md` 与本文件。
2. 修改前先登记 `work_id`、feature 分支和精确文件范围。
3. 不直接在 `main` 开发；不抢占另一 Agent 已声明的文件。
4. 方向变化、阻塞或扩大文件范围时，先更新本文件。
5. “测试通过”只能写真实运行过的测试；没跑就是 `[UNRUN]`。
6. 涉及数据/schema 必须写清迁移和回滚。
7. 完成时必须留下唯一下一步。
