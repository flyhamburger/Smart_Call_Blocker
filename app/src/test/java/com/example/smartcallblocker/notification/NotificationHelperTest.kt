package com.example.smartcallblocker.notification

import com.example.smartcallblocker.data.model.NotificationPreference
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationHelperTest {

    @Test
    fun `ALL preference posts notification for any block reason`() {
        val pref = NotificationPreference.ALL

        assertTrue(NotificationHelper.shouldPostNotification(pref, "Private Caller"))
        assertTrue(NotificationHelper.shouldPostNotification(pref, "All International Calls (+)"))
        assertTrue(NotificationHelper.shouldPostNotification(pref, "Country Code Rule matched: USA (+1)"))
        assertTrue(NotificationHelper.shouldPostNotification(pref, "Prefix Rule matched: 1800"))
        assertTrue(NotificationHelper.shouldPostNotification(pref, "Suffix Rule matched: 9999"))
    }

    @Test
    fun `INTERNATIONAL_ONLY preference posts notification only for international calls`() {
        val pref = NotificationPreference.INTERNATIONAL_ONLY

        assertTrue(NotificationHelper.shouldPostNotification(pref, "All International Calls (+)"))
        assertTrue(NotificationHelper.shouldPostNotification(pref, "Country Code Rule matched: Japan (+81)"))

        assertFalse(NotificationHelper.shouldPostNotification(pref, "Private Caller"))
        assertFalse(NotificationHelper.shouldPostNotification(pref, "Prefix Rule matched: 1800"))
        assertFalse(NotificationHelper.shouldPostNotification(pref, "Suffix Rule matched: 9999"))
    }

    @Test
    fun `PATTERNS_ONLY preference posts notification only for prefix or suffix patterns`() {
        val pref = NotificationPreference.PATTERNS_ONLY

        assertTrue(NotificationHelper.shouldPostNotification(pref, "Prefix Rule matched: 1800"))
        assertTrue(NotificationHelper.shouldPostNotification(pref, "Suffix Rule matched: 9999"))

        assertFalse(NotificationHelper.shouldPostNotification(pref, "Private Caller"))
        assertFalse(NotificationHelper.shouldPostNotification(pref, "All International Calls (+)"))
        assertFalse(NotificationHelper.shouldPostNotification(pref, "Country Code Rule matched: UK (+44)"))
    }

    @Test
    fun `NONE preference never posts notification`() {
        val pref = NotificationPreference.NONE

        assertFalse(NotificationHelper.shouldPostNotification(pref, "Private Caller"))
        assertFalse(NotificationHelper.shouldPostNotification(pref, "All International Calls (+)"))
        assertFalse(NotificationHelper.shouldPostNotification(pref, "Country Code Rule matched: UK (+44)"))
        assertFalse(NotificationHelper.shouldPostNotification(pref, "Prefix Rule matched: 1800"))
        assertFalse(NotificationHelper.shouldPostNotification(pref, "Suffix Rule matched: 9999"))
    }
}
