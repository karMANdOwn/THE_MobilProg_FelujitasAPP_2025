package com.example.felujitas.data.repository

import com.example.felujitas.data.dao.MaterialDao
import com.example.felujitas.data.dao.RoomDao
import com.example.felujitas.data.dao.TaskDao
import com.example.felujitas.data.entity.Material
import com.example.felujitas.data.entity.Room
import com.example.felujitas.data.entity.Task
import kotlinx.coroutines.flow.Flow


//parancsok listája amit a ViewModel használ
class RenovationRepository(
    private val roomDao: RoomDao,
    private val taskDao: TaskDao,
    private val materialDao: MaterialDao
) {
    //szobák
    val allRooms: Flow<List<Room>> = roomDao.getAllRooms()

    suspend fun insertRoom(room: Room): Long = roomDao.insertRoom(room)
    suspend fun updateRoom(room: Room) = roomDao.updateRoom(room)
    suspend fun deleteRoom(room: Room) = roomDao.deleteRoom(room)
    suspend fun getRoomById(id: Long): Room? = roomDao.getRoomById(id)

    //feladatok
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()
    val openTasks: Flow<List<Task>> = taskDao.getOpenTasks()
    val activeTaskCount: Flow<Int> = taskDao.getActiveTaskCount()
    val completedTaskCount: Flow<Int> = taskDao.getCompletedTaskCount()

    fun getTasksByRoom(roomId: Long): Flow<List<Task>> = taskDao.getTasksByRoom(roomId)
    fun getOpenTasksByRoom(roomId: Long): Flow<List<Task>> = taskDao.getOpenTasksByRoom(roomId)
    fun getCompletedTaskCountByRoom(roomId: Long): Flow<Int> =
        taskDao.getCompletedTaskCountByRoom(roomId)
    fun getTotalTaskCountByRoom(roomId: Long): Flow<Int> =
        taskDao.getTotalTaskCountByRoom(roomId)

    suspend fun insertTask(task: Task): Long = taskDao.insertTask(task)
    suspend fun updateTask(task: Task) = taskDao.updateTask(task)
    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)
    suspend fun getTaskById(id: Long): Task? = taskDao.getTaskById(id)

    //anyagok
    val allMaterials: Flow<List<Material>> = materialDao.getAllMaterials()
    val acquiredMaterialCount: Flow<Int> = materialDao.getAcquiredMaterialCount()
    val totalMaterialCount: Flow<Int> = materialDao.getTotalMaterialCount()
    val totalCost: Flow<Double?> = materialDao.getTotalCost()

    fun getMaterialsByRoom(roomId: Long): Flow<List<Material>> =
        materialDao.getMaterialsByRoom(roomId)

    suspend fun insertMaterial(material: Material): Long = materialDao.insertMaterial(material)
    suspend fun updateMaterial(material: Material) = materialDao.updateMaterial(material)
    suspend fun deleteMaterial(material: Material) = materialDao.deleteMaterial(material)
    suspend fun getMaterialById(id: Long): Material? = materialDao.getMaterialById(id)
}