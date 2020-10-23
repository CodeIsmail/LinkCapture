package dev.codeismail.linkcapture.utils

import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {

    fun applyTheme(themePreference: Boolean) {
        when (themePreference) {
            false -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            true -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        }
    }
}
