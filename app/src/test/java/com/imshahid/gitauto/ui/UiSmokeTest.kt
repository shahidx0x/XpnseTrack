package com.imshahid.gitauto.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UiSmokeTest {

    @Test
    fun allPrimaryScreensAreRegistered() {
        val screens = Screen.entries.map { it.name }
        assertEquals(8, screens.size)
        assertTrue(screens.containsAll(listOf(
            "Home", "Transactions", "Add", "Budgets",
            "Reports", "Ai", "Goals", "Profile"
        )))
    }
}
