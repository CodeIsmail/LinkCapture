package dev.codeismail.linkcapture.ui.setting

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import androidx.core.content.edit
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreference
import dev.codeismail.linkcapture.R
import dev.codeismail.linkcapture.service.AlarmReceiver
import dev.codeismail.linkcapture.utils.ThemeManager
import dev.codeismail.linkcapture.utils.toMilliSecs

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
                setProcessTime(URL_MERGE_CODE, urlCleanUpHour)

            }
            key.equals(getString(R.string.key_image_cleanup_label), true) -> {
                val imageCleanUpHour = (findPreference(key) as SeekBarPreference?)!!.value
                if (imageCleanUpHour < DEFAULT_CLEANUP_HOUR){
                    Toast.makeText(context,
                        "You should consider $DEFAULT_CLEANUP_HOUR hours as default for routine cleanup to reduce resource usage", Toast.LENGTH_LONG).show()
                }
                sharedPreferences.edit(true) {
                    putInt(key, imageCleanUpHour)
                }
                setProcessTime(IMAGE_CLEANUP_CODE, imageCleanUpHour)

            }
        }
    }

    private fun setProcessTime(requestCode: Int, time : Int){
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(requireContext(), AlarmReceiver::class.java)
        val alarmPendingIntent = PendingIntent.getBroadcast(requireContext(), requestCode,
            alarmIntent, 0
        )
        val fireTime = SystemClock.elapsedRealtime() + time.toMilliSecs()
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
            fireTime, time.toMilliSecs(), alarmPendingIntent
        )
    }

    private fun setAppToDarkTheme(enableDarkTheme : Boolean) {
        ThemeManager.applyTheme(enableDarkTheme)
    }
    companion object{
        private const val DEFAULT_CLEANUP_HOUR = 9
        private const val URL_MERGE_CODE = 1001
        private const val IMAGE_CLEANUP_CODE = 2002
    }

}
