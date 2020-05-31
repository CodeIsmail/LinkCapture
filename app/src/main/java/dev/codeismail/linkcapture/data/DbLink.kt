package dev.codeismail.linkcapture.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "link")
data class DbLink (@PrimaryKey val id: String = UUID.randomUUID().toString(),
                   val linkString: String, var lastVisit: String)