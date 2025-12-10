package com.example.felujitas.data.database

import androidx.room.TypeConverter
import com.example.felujitas.data.entity.TaskStatus
import com.example.felujitas.data.entity.Currency
import com.example.felujitas.data.entity.ReceiptType

//enumok stringgé alakitása a db számára
class Converters {
    @TypeConverter
    fun fromTaskStatus(status: TaskStatus): String {
        return status.name
    }

    @TypeConverter
    fun toTaskStatus(value: String): TaskStatus {
        return TaskStatus.valueOf(value)
    }

    @TypeConverter
    fun fromCurrency(currency: Currency): String {
        return currency.name
    }

    @TypeConverter
    fun toCurrency(value: String): Currency {
        return try {
            Currency.valueOf(value)
        } catch (e: IllegalArgumentException) {
            Currency.HUF
        }
    }

    @TypeConverter
    fun fromReceiptType(type: ReceiptType): String {
        return type.name
    }

    @TypeConverter
    fun toReceiptType(value: String): ReceiptType {
        return try {
            ReceiptType.valueOf(value)
        } catch (e: IllegalArgumentException) {
            ReceiptType.ITEM
        }
    }
}