package dev.codeismail.linkcapture.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@InstallIn(ApplicationComponent::class)
@Module
class MainModule {

    @Provides
    fun provideSharedPreference(@ApplicationContext appContext: Context): SharedPreferences{
        return PreferenceManager.getDefaultSharedPreferences(appContext)
    }
}