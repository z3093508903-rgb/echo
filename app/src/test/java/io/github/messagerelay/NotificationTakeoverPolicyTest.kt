package io.github.messagerelay

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationTakeoverPolicyTest {
    @Test fun `selected readable notification is cancelled only when Echo can safely replace it`() {
        assertTrue(
            NotificationTakeoverPolicy.shouldCancelSource(
                hasEnabledRule = true,
                paused = false,
                canPostEchoNotification = true,
                hasReadableContent = true,
                ongoing = false
            )
        )
        assertFalse(
            NotificationTakeoverPolicy.shouldCancelSource(
                hasEnabledRule = true,
                paused = true,
                canPostEchoNotification = true,
                hasReadableContent = true,
                ongoing = false
            )
        )
        assertFalse(
            NotificationTakeoverPolicy.shouldCancelSource(
                hasEnabledRule = true,
                paused = false,
                canPostEchoNotification = false,
                hasReadableContent = true,
                ongoing = false
            )
        )
        assertFalse(
            NotificationTakeoverPolicy.shouldCancelSource(
                hasEnabledRule = true,
                paused = false,
                canPostEchoNotification = true,
                hasReadableContent = false,
                ongoing = false
            )
        )
        assertFalse(
            NotificationTakeoverPolicy.shouldCancelSource(
                hasEnabledRule = true,
                paused = false,
                canPostEchoNotification = true,
                hasReadableContent = true,
                ongoing = true
            )
        )
    }

    @Test fun `immediate local delivery requires zero delay no merge and no external channel`() {
        assertTrue(
            NotificationTakeoverPolicy.shouldUseImmediateLocalDelivery(
                deliveryDelaySeconds = 0,
                mergeNotifications = false,
                hasExternalChannels = false
            )
        )
        assertFalse(
            NotificationTakeoverPolicy.shouldUseImmediateLocalDelivery(
                deliveryDelaySeconds = 1,
                mergeNotifications = false,
                hasExternalChannels = false
            )
        )
        assertFalse(
            NotificationTakeoverPolicy.shouldUseImmediateLocalDelivery(
                deliveryDelaySeconds = 0,
                mergeNotifications = true,
                hasExternalChannels = false
            )
        )
        assertFalse(
            NotificationTakeoverPolicy.shouldUseImmediateLocalDelivery(
                deliveryDelaySeconds = 0,
                mergeNotifications = false,
                hasExternalChannels = true
            )
        )
    }
}
