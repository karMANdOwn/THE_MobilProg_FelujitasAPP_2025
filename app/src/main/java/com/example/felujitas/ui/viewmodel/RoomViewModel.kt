package com.example.felujitas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.felujitas.data.entity.Room
import com.example.felujitas.data.repository.RenovationRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

//szoba listák
data class RoomUiState(
    val rooms: List<Room> = emptyList()
)

//szobák listájának kezelése , CRUD műveletek
class RoomViewModel(
    private val repository: RenovationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoomUiState())
    val uiState: StateFlow<RoomUiState> = _uiState.asStateFlow()

    init {
        loadRooms()
    }

    private fun loadRooms() {
        viewModelScope.launch {
            repository.allRooms.collect { rooms ->
                _uiState.value = _uiState.value.copy(rooms = rooms)
            }
        }
    }

    fun addRoom(name: String) {
        viewModelScope.launch {
            repository.insertRoom(Room(name = name))
        }
    }

    fun updateRoom(room: Room) {
        viewModelScope.launch {
            repository.updateRoom(room)
        }
    }

    fun deleteRoom(room: Room) {
        viewModelScope.launch {
            repository.deleteRoom(room)
        }
    }
}