package com.example.felujitas.data.dao

import androidx.room.*
import com.example.felujitas.data.entity.Material
import kotlinx.coroutines.flow.Flow

@Dao
interface MaterialDao {
    @Query("SELECT * FROM materials ORDER BY name ASC")
    fun getAllMaterials(): Flow<List<Material>>

    @Query("SELECT * FROM materials WHERE roomId = :roomId ORDER BY name ASC")
    fun getMaterialsByRoom(roomId: Long): Flow<List<Material>>

    @Query("SELECT * FROM materials WHERE id = :materialId")
    suspend fun getMaterialById(materialId: Long): Material?

    @Query("SELECT COUNT(*) FROM materials WHERE acquired = 1")
    fun getAcquiredMaterialCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM materials")
    fun getTotalMaterialCount(): Flow<Int>

    @Query("SELECT SUM(price) FROM materials")
    fun getTotalCost(): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: Material): Long

    @Update
    suspend fun updateMaterial(material: Material)

    @Delete
    suspend fun deleteMaterial(material: Material)
}