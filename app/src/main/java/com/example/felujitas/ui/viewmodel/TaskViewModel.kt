package com.example.felujitas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.felujitas.data.entity.Room
import com.example.felujitas.data.entity.Task
import com.example.felujitas.data.entity.TaskStatus
import com.example.felujitas.data.repository.RenovationRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class TaskUiState(
    val tasks: List<Task> = emptyList(),
    val rooms: List<Room> = emptyList(),
    val selectedRoomId: Long? = null,
    val showOnlyOpen: Boolean = false
)

//szobák feladatainak kezelés,CRUD műveleteket,státusz ciklikus váltás
class TaskViewModel(
    private val repository: RenovationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.allRooms.collect { rooms ->
                _uiState.value = _uiState.value.copy(rooms = rooms)
                loadTasks()
            }
        }
    }

    private fun loadTasks() {
        viewModelScope.launch {
            val roomId = _uiState.value.selectedRoomId
            val showOnlyOpen = _uiState.value.showOnlyOpen

            val taskFlow = when {
                roomId != null && showOnlyOpen -> repository.getOpenTasksByRoom(roomId)
                roomId != null -> repository.getTasksByRoom(roomId)
                showOnlyOpen -> repository.openTasks
                else -> repository.allTasks
            }

            taskFlow.collect { tasks ->
                _uiState.value = _uiState.value.copy(tasks = tasks)
            }
        }
    }

    fun setRoomFilter(roomId: Long?) {
        _uiState.value = _uiState.value.copy(selectedRoomId = roomId)
        loadTasks()
    }

    fun toggleShowOnlyOpen() {
        _uiState.value = _uiState.value.copy(showOnlyOpen = !_uiState.value.showOnlyOpen)
        loadTasks()
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            repository.insertTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun cycleTaskStatus(task: Task) {
        val newStatus = when (task.status) {
            TaskStatus.OPEN -> TaskStatus.DOING
            TaskStatus.DOING -> TaskStatus.DONE
            TaskStatus.DONE -> TaskStatus.OPEN
        }
        updateTask(task.copy(status = newStatus))
    }
}