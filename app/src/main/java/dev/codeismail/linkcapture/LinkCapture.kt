package dev.codeismail.linkcapture

import android.app.Application
import android.content.SharedPreferences
import dagger.hilt.android.HiltAndroidApp
import dev.codeismail.linkcapture.utils.ThemeManager
import javax.inject.Inject

@HiltAndroidApp
class LinkCapture : Application() {

    @Inject lateinit var sharedPreference: SharedPreferences
    override fun onCreate() {
        super.onCreate()
        initTheme()
    }

    private fun initTheme(){
        ThemeManager.applyTheme(sharedPreference.getBoolean(getString(R.string.key_dark_theme_label),false))
    }
}