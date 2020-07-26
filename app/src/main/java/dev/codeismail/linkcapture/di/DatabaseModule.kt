package dev.codeismail.linkcapture.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.codeismail.linkcapture.data.AppDatabase
import dev.codeismail.linkcapture.data.LinkDao
import dev.codeismail.linkcapture.data.Repository
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase{
        return Room.databaseBuilder(appContext, AppDatabase::class.java, DATABASE_NAME).build()
    }

    @Provides
    fun provideLinkDao(appDatabase: AppDatabase): LinkDao{
        return appDatabase.linkDao()
    }

    @Provides
    fun provideRepository(linkDao: LinkDao): Repository{
        return Repository(linkDao)
    }

}

private const val DATABASE_NAME = "links-db"