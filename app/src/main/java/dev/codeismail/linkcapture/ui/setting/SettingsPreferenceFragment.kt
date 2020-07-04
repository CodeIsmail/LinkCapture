package dev.codeismail.linkcapture.ui.setting

import android.content.SharedPreferences
import android.os.Bundle
import androidx.core.content.edit
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import dev.codeismail.linkcapture.R
import dev.codeismail.linkcapture.utils.ThemeManager

class SettingsPreferenceFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.app_setting, rootKey)

    }
    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key.equals(getString(R.string.key_dark_theme_label), true)) {
            val switchValue = (findPreference(key) as SwitchPreference?)!!.isChecked
            sharedPreferences.edit(true) {
                putBoolean(key, switchValue)
            }
            setAppToDarkTheme(switchValue)
        } else if (key.equals(getString(R.string.key_save_label), true)) {
            val switchValue = (findPreference(key) as SwitchPreference?)!!.isChecked
            sharedPreferences.edit(true) {
                putBoolean(key, switchValue)
            }
        }
    }

    private fun setAppToDarkTheme(enableDarkTheme : Boolean) {
        ThemeManager.applyTheme(enableDarkTheme)
    }

}
