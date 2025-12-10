package com.example.felujitas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.felujitas.data.entity.Room
import com.example.felujitas.data.repository.RenovationRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

//statisztikák
data class DashboardUiState(
    val activeTaskCount: Int = 0,
    val completedTaskCount: Int = 0,
    val acquiredMaterialCount: Int = 0,
    val totalMaterialCount: Int = 0,
    val totalCost: Double = 0.0,
    val rooms: List<RoomProgress> = emptyList()
)

//szobák aktuális állása
data class RoomProgress(
    val room: Room,
    val completedTasks: Int,
    val totalTasks: Int,
    val progressPercentage: Int
)

//főoldal adatainak kezelesée/frissitése
class DashboardViewModel(
    private val repository: RenovationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    //dashboard összes adatainak betöltése
    private fun loadDashboardData() {
        viewModelScope.launch {
            launch {
                repository.activeTaskCount.collect { count ->
                    _uiState.value = _uiState.value.copy(activeTaskCount = count)
                }
            }

            launch {
                repository.completedTaskCount.collect { count ->
                    _uiState.value = _uiState.value.copy(completedTaskCount = count)
                }
            }

            launch {
                repository.acquiredMaterialCount.collect { count ->
                    _uiState.value = _uiState.value.copy(acquiredMaterialCount = count)
                }
            }

            launch {
                repository.totalMaterialCount.collect { count ->
                    _uiState.value = _uiState.value.copy(totalMaterialCount = count)
                }
            }

            launch {
                repository.totalCost.collect { cost ->
                    _uiState.value = _uiState.value.copy(totalCost = cost ?: 0.0)
                }
            }

            launch {
                loadRoomProgress()
            }
        }
    }

    //szabák allapo adatainak betöltése
    private suspend fun loadRoomProgress() {
        repository.allRooms.collect { rooms ->
            val roomProgressList = mutableListOf<RoomProgress>()

            for (room in rooms) {
                val completed = repository.getCompletedTaskCountByRoom(room.id).first()
                val total = repository.getTotalTaskCountByRoom(room.id).first()
                val percentage = if (total > 0) (completed * 100 / total) else 0

                roomProgressList.add(
                    RoomProgress(
                        room = room,
                        completedTasks = completed,
                        totalTasks = total,
                        progressPercentage = percentage
                    )
                )
            }

            _uiState.value = _uiState.value.copy(rooms = roomProgressList)
        }
    }
}