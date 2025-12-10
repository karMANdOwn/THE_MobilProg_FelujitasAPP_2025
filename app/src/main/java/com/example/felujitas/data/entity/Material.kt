package com.example.felujitas.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class ReceiptType {
    //a számlán egy anyag
    ITEM,
    //teljes számla
    FULL_RECEIPT
}

enum class Currency {
    HUF, EUR
}

//szobákhoz kapcsolt anyag tábla
@Entity(
    tableName = "materials",
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
//anyag adatok tárolása
data class Material(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val roomId: Long? = null,
    val quantity: String? = null,
    val price: Double = 0.0,
    val currency: Currency = Currency.HUF,
    val receiptType: ReceiptType = ReceiptType.ITEM,
    val store: String? = null,
    val receiptPhotoUri: String? = null,
    val acquired: Boolean = false
)