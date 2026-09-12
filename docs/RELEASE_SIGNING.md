# Echo Preview 测试签名发布

当前 Echo 只有单人真机测试，因此 Preview 使用**测试专用固定签名**，目标是解决历史 GitHub Runner 随机 Debug 签名导致的：

```text
INSTALL_FAILED_UPDATE_INCOMPATIBLE
```

这不是正式生产签名方案，而是为了让 Preview 之间尽量可以直接覆盖安装。

## 当前方案

- PR / 非 main 运行：继续 `assembleDebug`，只上传 Debug CI artifact，不发布 GitHub Release。
- main：使用 GitHub Actions Cache 保存一份测试签名 keystore。
- 第一次 main 运行如果缓存中没有 keystore，workflow 自动用 JDK `keytool` 生成。
- 之后 main 运行优先从同一 cache key 恢复该 keystore，再执行 `assembleRelease`。
- keystore 不提交到 Git、不上传到 Preview artifact / Release，也不需要用户手动配置 GitHub Secrets。
- `versionName` 当前为 `3.12`。
- main Preview 的 `versionCode = 3_120_000 + GITHUB_RUN_NUMBER`，因此新的 main Preview 单调递增；失败 run 可以跳号但不会倒退。

Gradle 仍使用现有签名环境变量：

- `MESSAGE_RELAY_KEYSTORE_PATH`
- `MESSAGE_RELAY_KEYSTORE_PASSWORD`
- `MESSAGE_RELAY_KEY_ALIAS`
- `MESSAGE_RELAY_KEY_PASSWORD`

这些值由 workflow 在 Runner 内部临时注入，不要求测试者维护。

## 重要边界

该方案只适合当前单人 Preview 测试：

- GitHub Actions Cache 不是永久密钥保管系统。
- 如果签名 cache 将来被 GitHub 清除，workflow 可能生成新的测试签名；那时已安装旧测试签名版的设备需要再次完整卸载一次。
- 如果 Echo 未来准备公开稳定发行，应迁移到真正长期保存的 Release keystore + GitHub Actions Secrets，并冻结签名证书。

## 从历史随机 Debug 签名迁移

安装过 Preview #1 / #4 / #8 等旧随机 Debug 签名版本的设备，第一次安装当前测试签名 Preview 仍需：

1. 如有需要先备份 Echo 内的重要本地配置。
2. 完整卸载旧 Echo。
3. 安装首个测试固定签名 Preview。
4. 后续在测试签名 cache 未变化且 `versionCode` 更高时，可以直接覆盖升级。

完整卸载会清除 App 本地数据，这是本次从历史随机 Debug 签名迁移到新的测试签名链的一次性成本。
