# 回声 Echo — 多 Agent 实时交接

> 本文件是 `z3093508903-rgb/echo` 的唯一实时项目交接状态。所有 Agent 在开始、转向、验证、提交和交接时都必须更新这里。长期产品方向写进稳定文档，但“现在做到哪、谁在改什么、下一步是什么”只认本文件。

## 当前仓库快照

| 字段 | 当前值 |
| --- | --- |
| 项目 | 回声 Echo |
| 仓库 | `z3093508903-rgb/echo` |
| 默认分支 | `main` |
| main HEAD | `2fa6be55d286089a1d6acf60357aa213ecccabb8` |
| 当前阶段 | 固定 Release 签名发布链代码已完成并通过 PR Debug CI，等待 Repository Secrets 一次性配置后验证 main signed Release |
| 更新时间 | `2026-09-12T22:15:00+08:00` |
| Android 业务代码改动 | 本 work 不改通知业务逻辑，只改构建/发布配置与版本信息 |
| 真机构建与运行 | Preview #8 仍是旧随机 Debug 签名；首个固定签名 Preview 尚未发布 |

## 产品定位

Echo 是一个 **本地优先的 Android 注意力防火墙**。当前冻结第一性原则：**手机负责捕获，电脑优先负责消费信息**。发布链必须支持连续真机迭代，因此 Preview 必须使用稳定签名和单调递增 `versionCode`。

## 进行中的工作

```text
work_id: fixed-release-signing-v3.12-20260912-webgpt
agent: 网页 GPT
branch@base: gpt/fixed-release-signing-v3.12-20260912@2fa6be55d286089a1d6acf60357aa213ecccabb8
goal: PR 保持 Debug CI；main Preview 改为固定签名 assembleRelease；缺签名 Secrets 时禁止发布；versionCode 每次 main 发布单调递增；目标 versionName 同步到 3.12
owns: PROJECT_HANDOFF.md; .github/workflows/android-ci.yml; app/build.gradle.kts; docs/RELEASE_SIGNING.md
status: blocked-on-secret-setup
updated_at: 2026-09-12T22:15:00+08:00
notes: PR #9 已创建。workflow 首版因 Release notes 跨行字符串破坏 YAML 缩进，invalid run #10 无 job；已在 `1dda146088453010d5c99983e64e4c435013f145` 修复。PR CI run #11 已真实通过 compileDebugKotlin、testDebugUnitTest、assembleDebug、Debug artifact；main Release 步骤在 PR 中按设计全部 skipped。当前 GitHub connector 不提供 Repository Secrets 写接口，因此下一步必须由仓库管理员一次性创建固定签名 Secrets，之后才能合并并验证 signed Release。不得提交 keystore 或密码。
```

## 本轮发布约束

- Pull Request / 非 main 手动运行：执行 Debug Kotlin 编译、Debug 单元测试、`assembleDebug`，上传 Debug CI artifact；不创建 GitHub Release。
- `main` / main 手动运行：发布物必须来自 `assembleRelease`，不得再发布 `app-debug.apk`。
- 固定签名通过 GitHub Actions Secrets 注入：workflow 使用 `MESSAGE_RELAY_KEYSTORE_BASE64` 在 Runner 临时目录还原 keystore，再映射现有 `MESSAGE_RELAY_KEYSTORE_PATH`、`MESSAGE_RELAY_KEYSTORE_PASSWORD`、`MESSAGE_RELAY_KEY_ALIAS`、`MESSAGE_RELAY_KEY_PASSWORD`。
- Repository Secrets 共 4 个：`MESSAGE_RELAY_KEYSTORE_BASE64`、`MESSAGE_RELAY_KEYSTORE_PASSWORD`、`MESSAGE_RELAY_KEY_ALIAS`、`MESSAGE_RELAY_KEY_PASSWORD`。
- Secrets 缺任意一项时，main workflow 在 Release 构建前明确失败，后续 Preview artifact / GitHub Release 不得执行。
- keystore、密码、base64 私钥内容不得进入源码、日志、artifact 或 Release。
- `app/build.gradle.kts` 当前默认/目标 `versionName = 3.12`；main workflow 注入 `versionCode = 3_120_000 + GITHUB_RUN_NUMBER`，新发布单调递增，失败 run 可跳号但不得倒退。
- 旧随机 Debug 签名 Preview 无法被新固定 Release 签名原地覆盖；用户需完整卸载旧版一次。卸载会清除当前 App 本地数据，Release 说明必须明确提示。
- 不改 Room/DataStore schema。

## 最近完成

| work_id | 结果 | 提交 / PR | 验证 | `[UNRUN]` / 下一步 |
| --- | --- | --- | --- | --- |
| `fixed-release-signing-v3.12-20260912-webgpt` | main Preview 改为固定 Release 签名设计；PR 保持 Debug；3.12 + 单调 versionCode；缺 Secret 禁止发布；新增签名运维文档 | PR #9；workflow fix head `1dda146088453010d5c99983e64e4c435013f145` | PR CI run #11：compileDebugKotlin ✅、testDebugUnitTest ✅、assembleDebug ✅、artifact `Echo-debug-11` ✅ | fixed signed `assembleRelease`、main prerelease、覆盖升级仍 `[UNRUN]`；等待 Secrets |
| `pc-relay-v1-20260912-webgpt` | Echo 本机通知 fallback + Android 13+ 通知权限门 + Phone Link 最小出口 | PR #8；merge `2fa6be55d286089a1d6acf60357aa213ecccabb8`；Preview #8 | PR CI + main CI ✅ | Echo→Phone Link→Windows 最终真机链仍由用户验证 |
| `onboarding-v1-20260912-webgpt` | 四步 onboarding → 两步；移除渠道强制门槛；修复主题对比度 | PR #7；Preview #4 | CI ✅；用户真机可进入 | 阻塞已解除 |

## 已冻结边界

- Preview 从本 work 起必须采用固定 Release 签名；CI Debug artifact 与可安装 Preview Release 是两类不同产物。
- 固定签名密钥只允许存在于可信本机离线备份与 GitHub Actions Secrets；不得进入 Git 历史。
- Secrets 尚未配置、main `assembleRelease` 尚未真实通过前，不得声称固定签名发布链已完成上线。
- 首次从随机 Debug 签名迁移到固定 Release 签名必须完整卸载一次；后续固定签名 + 更高 versionCode 才可覆盖升级。
- Echo 不要求 Bark、飞书或钉钉；第三方转发只是可选能力。
- **PC-first**：现阶段优先复用 Windows Phone Link，不新增自建 PC 通信实体。
- Room Entity 变化必须有 Migration；DataStore 新字段必须有默认值。

## 下一条开发链

1. 仓库管理员在可信本机生成并离线备份固定 Release keystore。
2. GitHub `Settings → Secrets and variables → Actions` 创建 4 个 Repository Secrets；真实 secret 不发送到聊天、不进入仓库。
3. Secrets 配置完成后合并 PR #9。
4. main Actions 应通过 Secret 预检 → 临时还原 keystore → `assembleRelease` → `Echo-preview.apk` → GitHub prerelease；验证 versionName/versionCode 与签名。
5. 旧 Preview 用户完整卸载一次并安装首个固定签名 Preview。
6. 再发布一个更高 versionCode 的固定签名 Preview，真机验证可直接覆盖安装，正式关闭 `INSTALL_FAILED_UPDATE_INCOMPATIBLE` 问题。

## 完成 / 交接

```text
changed: workflow 已拆分 PR Debug 与 main signed Release 行为；main 缺 Secrets 明确失败且禁止发布；Gradle 支持 MESSAGE_RELAY_VERSION_CODE/NAME；目标 versionName=3.12；main versionCode=3_120_000+run_number；新增无秘密签名文档
commit: workflow fix = 1dda146088453010d5c99983e64e4c435013f145；本交接 commit 待写入
remote: gpt/fixed-release-signing-v3.12-20260912 / PR #9
checks: PR CI run #11 -> compileDebugKotlin PASS; testDebugUnitTest PASS; assembleDebug PASS; Echo-debug-11 artifact PASS
data_or_schema: 无 Room/DataStore schema 变化；只变更 APK 构建元数据和 CI
unrun: fixed signed assembleRelease；main Preview publish；APK signer identity；旧 Debug→fixed Release 一次卸载迁移；fixed Release→next fixed Release 覆盖升级
rollback: 回退 PR #9 即恢复旧 Debug Preview 发布链（不建议继续使用）
next: 仓库管理员配置 4 个固定签名 Repository Secrets，然后合并 PR #9 验证 main signed Release
```

## 协作规则摘要

1. 开工先读 `AGENTS.md` 与本文件。
2. 修改前先登记 `work_id`、feature 分支和精确文件范围。
3. 不直接在 `main` 开发；不抢占另一 Agent 已声明的文件。
4. 方向变化、阻塞或扩大文件范围时，先更新本文件。
5. “测试通过”只能写真实运行过的测试；没跑就是 `[UNRUN]`。
6. 涉及数据/schema 必须写清迁移和回滚。
7. 完成时必须留下唯一下一步。
