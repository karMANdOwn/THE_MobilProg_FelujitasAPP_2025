package com.example.felujitas.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class TaskStatus {
    OPEN, DOING, DONE
}

//szobákhoz kapcsolt feladatok tábla
@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Room::class,
            parentColumns = ["id"],
            childColumns = ["roomId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("roomId")]
)

// feladatok adatainak tárolása
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val roomId: Long,
    val dueDate: String? = null,
    val notes: String? = null,
    val status: TaskStatus = TaskStatus.OPEN
)