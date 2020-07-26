package dev.codeismail.linkcapture

import android.app.Application
import androidx.preference.PreferenceManager
import dagger.hilt.android.HiltAndroidApp
import dev.codeismail.linkcapture.utils.ThemeManager

@HiltAndroidApp
class LinkCapture : Application() {
    override fun onCreate() {
        super.onCreate()
        initTheme()
        //val urlWorkerRequest = PeriodicWorkRequest
    }

    private fun initTheme(){
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        ThemeManager.applyTheme(preferences.getBoolean(getString(R.string.key_dark_theme_label),false)!!)
    }
}