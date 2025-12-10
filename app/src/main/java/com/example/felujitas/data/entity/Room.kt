package com.example.felujitas.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

//szoba lista ID-val és névvel
@Entity(tableName = "rooms")
data class Room(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String
)