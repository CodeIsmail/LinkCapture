package dev.codeismail.linkcapture.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DbLink::class],
    version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun linkDao(): LinkDao
}

