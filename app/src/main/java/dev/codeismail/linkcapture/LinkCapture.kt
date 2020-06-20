package dev.codeismail.linkcapture

import android.app.Application
import android.util.Log
import androidx.preference.PreferenceManager
import dev.codeismail.linkcapture.utils.ThemeManager

class LinkCapture : Application() {
    override fun onCreate() {
        super.onCreate()
        initTheme()
    }

    private fun initTheme(){
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        ThemeManager.applyTheme(preferences.getBoolean(getString(R.string.key_dark_theme_label),false)!!)
    }
}