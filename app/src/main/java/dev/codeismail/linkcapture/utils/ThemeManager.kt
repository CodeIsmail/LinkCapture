package dev.codeismail.linkcapture.utils

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {
    private const val LIGHT_MODE = "Light"
    private const val DARK_MODE = "Dark"
    private const val AUTO_BATTERY_MODE = "Auto-battery"
    private const val FOLLOW_SYSTEM_MODE = "System"

    fun applyTheme(themePreference: Boolean) {
        when (themePreference) {
            false -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            true -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        }
    }
}
