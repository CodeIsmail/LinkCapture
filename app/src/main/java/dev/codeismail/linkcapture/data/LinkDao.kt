package dev.codeismail.linkcapture.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.codeismail.linkcapture.adapter.Link
import kotlinx.coroutines.flow.Flow

@Dao
interface LinkDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(links: List<DbLink>)

    @Query("SELECT * FROM link")
    fun getLinks(): Flow<List<DbLink>>

}