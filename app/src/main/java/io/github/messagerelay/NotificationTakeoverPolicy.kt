package io.github.messagerelay

internal object NotificationTakeoverPolicy {
    fun shouldCancelSource(
        hasEnabledRule: Boolean,
        paused: Boolean,
        canPostEchoNotification: Boolean,
        hasReadableContent: Boolean,
        ongoing: Boolean
    ): Boolean =
        hasEnabledRule &&
            !paused &&
            canPostEchoNotification &&
            hasReadableContent &&
            !ongoing

    fun shouldUseImmediateLocalDelivery(
        deliveryDelaySeconds: Int,
        mergeNotifications: Boolean,
        hasExternalChannels: Boolean
    ): Boolean =
        deliveryDelaySeconds <= 0 &&
            !mergeNotifications &&
            !hasExternalChannels
}
