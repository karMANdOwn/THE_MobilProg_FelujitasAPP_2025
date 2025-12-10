package com.example.felujitas.data.dao

import androidx.room.*
import com.example.felujitas.data.entity.Task
import com.example.felujitas.data.entity.TaskStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY dueDate ASC, title ASC")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE roomId = :roomId ORDER BY dueDate ASC, title ASC")
    fun getTasksByRoom(roomId: Long): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE status != 'DONE' ORDER BY dueDate ASC, title ASC")
    fun getOpenTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE roomId = :roomId AND status != 'DONE' ORDER BY dueDate ASC")
    fun getOpenTasksByRoom(roomId: Long): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): Task?

    @Query("SELECT COUNT(*) FROM tasks WHERE status != 'DONE'")
    fun getActiveTaskCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE status = 'DONE'")
    fun getCompletedTaskCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE roomId = :roomId AND status = 'DONE'")
    fun getCompletedTaskCountByRoom(roomId: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE roomId = :roomId")
    fun getTotalTaskCountByRoom(roomId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)
}