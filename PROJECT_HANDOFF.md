# 回声 Echo — 多 Agent 实时交接

> 本文件是 `z3093508903-rgb/echo` 的唯一实时项目交接状态。所有 Agent 在开始、转向、验证、提交和交接时都必须更新这里。

## 当前仓库快照

| 字段 | 当前值 |
| --- | --- |
| 项目 | 回声 Echo |
| 仓库 | `z3093508903-rgb/echo` |
| 默认分支 | `main` |
| main HEAD | `21a5ea918014915725325127ec1ed5916ff2bb2b` |
| 当前阶段 | #16 → #18 固定测试签名覆盖升级已真机闭环；正在修复公开 Actions 日志中的签名密码泄露并轮换测试签名 |
| 更新时间 | `2026-09-12T23:34:00+08:00` |
| Android 业务代码改动 | 本 work 不改通知业务逻辑，仅处理测试签名发布链日志与缓存轮换 |
| 真机构建与运行 | Preview #16 → #18 未卸载直接覆盖成功；#18 `MainActivity` 启动 `Status: ok`，当前前台 |

## 产品定位

Echo 是一个 **本地优先的 Android 注意力防火墙**。当前阶段只有单人真机测试，因此发布链优先低维护成本：PR 保持 Debug CI，main Preview 使用 Actions Cache 维护的测试专用签名并发布 Release APK。

## 进行中的工作

```text
work_id: signing-log-hardening-20260912-webgpt
agent: 网页 GPT
branch@base: gpt/signing-log-hardening-20260912@main
goal: 修复 #16/#18 Actions 公开日志暴露 keystore/key 密码；废弃已暴露的 v1 测试签名缓存，生成随机密码保护的新 v2 测试签名，并停止把密码写入跨 step GITHUB_ENV
owns: PROJECT_HANDOFF.md; .github/workflows/android-ci.yml
status: implementing
updated_at: 2026-09-12T23:34:00+08:00
notes: 用户明确建议隐藏日志值并轮换测试签名缓存。由于 v1 密码既已出现在公开日志，又由公开仓库名确定性派生，本轮不继续复用 v1 key/cert，而是使用独立 v2 cache + 新测试 key/cert + 随机密码。代价：从当前 #18 迁移到首个 v2 Preview 需要再完整卸载一次；这是安全轮换，不是覆盖升级 bug 回归。之后 v2 Preview 之间继续可直接覆盖。
```

## 当前发布链

- PR / 非 main：`compileDebugKotlin` → `testDebugUnitTest` → `assembleDebug` → Debug artifact；不发布 GitHub Release。
- main：固定测试签名 + `assembleRelease` → GitHub prerelease。
- `versionName = 3.12`。
- `versionCode = 3_120_000 + GITHUB_RUN_NUMBER`。
- 测试签名不是正式生产签名体系。

## 最近完成

| work_id | 结果 | 提交 / PR | 验证 | `[UNRUN]` / 下一步 |
| --- | --- | --- | --- | --- |
| `fixed-release-signing-v3.12-20260912-webgpt` | 固定测试签名发布链已闭环 | PR #9；merge `98e07522b4e89cd59f8eaffdf0258933799b7d09`；Preview #16/#18 | #16 安装成功；未卸载 #16 直接覆盖 #18 返回 `Success`；#16/#18 证书一致；覆盖前后数据目录标识一致；#18 `MainActivity` `Status: ok` | 签名覆盖问题已关闭；后续只需处理日志暴露问题 |
| `pc-relay-v1-20260912-webgpt` | Echo 本机通知 fallback + Android 13+ 通知权限门 + Phone Link 最小出口 | PR #8；merge `2fa6be55d286089a1d6acf60357aa213ecccabb8`；Preview #8 | PR CI + main CI ✅ | 权限实际授权、页面视觉、后台通知、Echo→Phone Link→Windows 端到端仍 `[UNRUN]` |

## 安全事件 / 发布链注意事项

- #16 Actions：`https://github.com/z3093508903-rgb/echo/actions/runs/34701623661`
- #18 Actions：`https://github.com/z3093508903-rgb/echo/actions/runs/34701899520`
- 两个公开日志都出现未遮蔽的 `MESSAGE_RELAY_KEYSTORE_PASSWORD` / `MESSAGE_RELAY_KEY_PASSWORD`。
- 旧 v1 密码由公开 `GITHUB_REPOSITORY` 可确定性计算得到，因此 v1 测试签名视为已暴露并废弃。
- 当前 keystore 本体没有被发布到 Release，但不再继续信任 v1 签名身份。
- v2 方案：新的 cache key、全新测试 key/cert、随机密码文件随测试 keystore 一起缓存；运行时先 `add-mask`，敏感变量只在 `assembleRelease` step 内临时 export，不进入跨 step `GITHUB_ENV`。
- 该方案仍只是单人测试便利方案，不是正式生产密钥管理。

## 已冻结边界

- #16 → #18 覆盖升级问题已真机关闭，不再把 `INSTALL_FAILED_UPDATE_INCOMPATIBLE` 作为当前 blocker。
- v1 → v2 因安全轮换需要一次卸载，不能归类为覆盖升级失败。
- 当前单人 Preview 测试阶段允许 cache-backed 测试签名；它不是正式生产身份。
- Preview Release 必须来自 `assembleRelease`；PR Debug artifact 不作为持续安装包。
- Echo 不要求 Bark、飞书或钉钉；第三方转发只是可选能力。
- **PC-first**：现阶段优先复用 Windows Phone Link。
- Room Entity 变化必须有 Migration；DataStore 新字段必须有默认值。

## 下一条开发链

1. 修复 Actions 日志泄露：新 v2 cache + 新随机密码测试签名；密码不进入 `GITHUB_ENV`。
2. PR CI 通过后合并；main 自动发布首个 v2 Preview。
3. 用户完整卸载当前 #18 一次，再安装首个 v2 Preview。
4. 后续再发布一个更高 versionCode 的 v2 Preview，直接覆盖验证新签名链稳定。
5. 然后进入 `Echo → Phone Link → Windows` 端到端功能验证。

## 完成 / 交接

```text
changed: #16→#18 覆盖升级真机验证通过；发现公开 Actions 日志泄露测试签名密码；已登记并转向 v2 测试签名轮换
checks: #16 install PASS；#16→#18 direct overlay PASS；same cert PASS；same data dir PASS；#18 MainActivity Status: ok
data_or_schema: 无 Room/DataStore schema 变化
unrun: v2 签名构建/发布；#18→首个 v2 一次卸载迁移；v2→v2 覆盖升级；权限实际授权；页面视觉；后台通知；Echo→Phone Link→Windows
rollback: 若 v2 发布链异常可回退 workflow，但不得继续以 v1 已暴露签名作为可信测试身份
next: 完成 signing-log-hardening PR 并发布首个 v2 Preview
```

## 协作规则摘要

1. 开工先读 `AGENTS.md` 与本文件。
2. 修改前先登记 `work_id`、feature 分支和精确文件范围。
3. 不直接在 `main` 开发；不抢占另一 Agent 已声明的文件。
4. 方向变化、阻塞或扩大文件范围时，先更新本文件。
5. “测试通过”只能写真实运行过的测试；没跑就是 `[UNRUN]`。
6. 涉及数据/schema 必须写清迁移和回滚。
7. 完成时必须留下唯一下一步。
