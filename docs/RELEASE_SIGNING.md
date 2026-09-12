# Echo Preview 固定签名发布

Echo 的 `main` Preview 必须使用固定 Release 签名。PR 仍只构建 Debug APK，Debug artifact 不作为可持续覆盖升级的 Preview 发布物。

## 为什么必须固定签名

Android 只允许使用同一 applicationId 且同一签名证书的 APK 覆盖安装。GitHub Hosted Runner 的 Debug keystore 不是 Echo 的长期发布身份，因此历史 Preview 之间可能出现：

```text
INSTALL_FAILED_UPDATE_INCOMPATIBLE
```

从固定 Release 签名链开始，后续 Preview 只要继续使用同一 keystore 且 `versionCode` 更高，即可正常覆盖升级。

## 一次性生成 Release keystore

在可信本机上执行，示例使用 JDK 自带 `keytool`：

```powershell
keytool -genkeypair -v `
  -keystore echo-release.jks `
  -alias echo-release `
  -keyalg RSA `
  -keysize 4096 `
  -validity 36500
```

密码由仓库管理员自行设置。**不要把 keystore、密码或导出的 base64 内容提交到 Git。** `.gitignore` 已忽略 `*.jks` / `*.keystore` / secrets 文件。

生成后应把 keystore 与密码做独立离线备份。丢失固定签名密钥后，已经安装固定签名版 Echo 的设备将无法通过新签名 APK 原地升级。

## GitHub Actions Secrets

进入：

`Repository → Settings → Secrets and variables → Actions → New repository secret`

需要配置 4 个 Secret：

| Secret | 内容 |
| --- | --- |
| `MESSAGE_RELAY_KEYSTORE_BASE64` | `echo-release.jks` 的 Base64 文本 |
| `MESSAGE_RELAY_KEYSTORE_PASSWORD` | keystore 密码 |
| `MESSAGE_RELAY_KEY_ALIAS` | key alias，例如 `echo-release` |
| `MESSAGE_RELAY_KEY_PASSWORD` | key 密码 |

Windows PowerShell 可生成 keystore 的 Base64 文本：

```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes(".\echo-release.jks")) | Set-Clipboard
```

然后把剪贴板内容保存到 `MESSAGE_RELAY_KEYSTORE_BASE64` Secret。不要把 Base64 输出到 issue、PR、Actions 日志或仓库文件。

## CI 行为

- PR / 非 main 手动运行：`compileDebugKotlin` → `testDebugUnitTest` → `assembleDebug` → Debug artifact。
- main：先检查固定签名 Secrets；缺任意一项即失败，禁止发布。
- main Secrets 齐全：Runner 临时还原 keystore → 注入现有 `MESSAGE_RELAY_*` 签名环境变量 → `assembleRelease` → 上传 `Echo-preview.apk` → 创建 GitHub prerelease。
- keystore 只存在于 Runner 临时目录，不进入 artifact / Release。
- `versionName` 当前为 `3.12`。
- main Preview 的 `versionCode = 3_120_000 + GITHUB_RUN_NUMBER`，因此每次新的 main workflow 发布单调递增；失败的 run 可能造成跳号，但不会倒退。

## 从旧随机 Debug 签名迁移

安装过 Preview #1 / #4 / #8 等旧随机 Debug 签名版本的设备，第一次迁移到固定 Release 签名时仍需：

1. 如有需要先手动备份 Echo 内的重要本地配置。
2. 完整卸载旧 Echo。
3. 安装首个固定 Release 签名 Preview。
4. 之后保持同一固定签名，即可通过更高 `versionCode` 的 Preview 覆盖升级。

完整卸载会清除 App 本地数据，这是 Android 签名身份切换的一次性迁移成本。
