package dev.codeismail.linkcapture.ui.setting

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.edit
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
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
        when {
            key.equals(getString(R.string.key_dark_theme_label), true) -> {
                val switchValue = (findPreference(key) as SwitchPreference?)!!.isChecked
                sharedPreferences.edit(true) {
                    putBoolean(key, switchValue)
                }
                setAppToDarkTheme(switchValue)
            }
            key.equals(getString(R.string.key_save_label), true) -> {
                val switchValue = (findPreference(key) as SwitchPreference?)!!.isChecked
                sharedPreferences.edit(true) {
                    putBoolean(key, switchValue)
                }
            }
            key.equals(getString(R.string.key_url_cleanup_label), true) -> {
                val urlCleanUpHour = (findPreference(key) as SeekBarPreference?)!!.value
                if (urlCleanUpHour< DEFAULT_CLEANUP_HOUR){
                    Toast.makeText(context,
                        "You should consider $DEFAULT_CLEANUP_HOUR hours as default for routine cleanup to reduce resource usage", Toast.LENGTH_LONG).show()
                }
                sharedPreferences.edit(true) {
                    putInt(key, urlCleanUpHour)
                }

            }
            key.equals(getString(R.string.key_image_cleanup_label), true) -> {
                val imageCleanUpHour = (findPreference(key) as SeekBarPreference?)!!.value
                if (imageCleanUpHour< DEFAULT_CLEANUP_HOUR){
                    Toast.makeText(context,
                        "You should consider $DEFAULT_CLEANUP_HOUR hours as default for routine cleanup to reduce resource usage", Toast.LENGTH_LONG).show()
                }
                sharedPreferences.edit(true) {
                    putInt(key, imageCleanUpHour)
                }

            }
        }
    }

    private fun setAppToDarkTheme(enableDarkTheme : Boolean) {
        ThemeManager.applyTheme(enableDarkTheme)
    }
    companion object{
        private const val DEFAULT_CLEANUP_HOUR = 9
    }

}
