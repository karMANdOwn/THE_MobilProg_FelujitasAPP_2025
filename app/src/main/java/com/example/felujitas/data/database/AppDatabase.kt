package com.example.felujitas.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.felujitas.data.dao.MaterialDao
import com.example.felujitas.data.dao.RoomDao
import com.example.felujitas.data.dao.TaskDao
import com.example.felujitas.data.entity.Material
import com.example.felujitas.data.entity.Task


//táblák megadása db-ben
@Database(
    entities = [
        com.example.felujitas.data.entity.Room::class,
        Task::class,
        Material::class
    ],
    version = 2,
    exportSchema = false
)

//tipusok konvertálása stringre és vissza
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    //táblaműveletek elérése
    abstract fun roomDao(): RoomDao
    abstract fun taskDao(): TaskDao
    abstract fun materialDao(): MaterialDao

    companion object {
        @Volatile
        //thread legfrissebb értek
        private var INSTANCE: AppDatabase? = null

        //adatbázist létrehozó getter amit használni fog az alkalmazás
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "renovation_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}