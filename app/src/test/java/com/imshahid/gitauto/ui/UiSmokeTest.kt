package com.imshahid.gitauto.ui

import org.junit.Assert.assertTrue
import org.junit.Test

class UiSmokeTest {

    @Test
    fun criticalScreensAreRegistered() {
        val screens = Screen.entries.map { it.name }.toSet()

        val required = setOf(
            "Splash", "Onboarding1", "Onboarding2", "Onboarding3",
            "SignIn", "SignUp", "ForgotPassword",
            "Home", "Transactions", "TransactionDetails", "AddTransaction",
            "Budgets", "BudgetEditor", "Reports", "Ai", "ReceiptScanner",
            "Goals", "GoalEditor", "Profile", "EditProfile",
            "NotificationSettings", "BackupSync", "ConnectedAccounts",
            "HelpSupport", "About"
        )

        assertTrue(
            "Missing screens: " + (required - screens).joinToString(),
            screens.containsAll(required)
        )
    }
}
