package com.example.smartcallblocker.ui.main

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class MainNavigationTest {

    @Test
    fun `MainTab entries have correct titles`() {
        val tabs = MainTab.entries
        assertEquals(3, tabs.size)

        assertEquals("Country Codes", MainTab.COUNTRY_CODES.title)
        assertEquals("Local Patterns", MainTab.LOCAL_PATTERNS.title)
        assertEquals("Call History", MainTab.CALL_HISTORY.title)
    }

    @Test
    fun `MainTab entries have valid icons`() {
        MainTab.entries.forEach { tab ->
            assertNotNull(tab.unselectedIcon)
            assertNotNull(tab.selectedIcon)
        }
    }
}
