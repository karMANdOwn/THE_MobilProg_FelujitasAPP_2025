package com.example.felujitas.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.felujitas.data.entity.Material
import com.example.felujitas.data.entity.Room
import com.example.felujitas.data.repository.RenovationRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class MaterialUiState(
    val materials: List<Material> = emptyList(),
    val rooms: List<Room> = emptyList(),
    val selectedRoomId: Long? = null
)

//anyag adatok kezelése szoba szerint,CRUD műveleteket, számla fotó kezelés
class MaterialViewModel(
    private val repository: RenovationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MaterialUiState())
    val uiState: StateFlow<MaterialUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.allRooms.collect { rooms ->
                _uiState.value = _uiState.value.copy(rooms = rooms)
                loadMaterials()
            }
        }
    }

    private fun loadMaterials() {
        viewModelScope.launch {
            val roomId = _uiState.value.selectedRoomId
            val materialFlow = if (roomId != null) {
                repository.getMaterialsByRoom(roomId)
            } else {
                repository.allMaterials
            }

            materialFlow.collect { materials ->
                _uiState.value = _uiState.value.copy(materials = materials)
            }
        }
    }

    fun setRoomFilter(roomId: Long?) {
        _uiState.value = _uiState.value.copy(selectedRoomId = roomId)
        loadMaterials()
    }

    fun addMaterial(material: Material) {
        viewModelScope.launch {
            repository.insertMaterial(material)
        }
    }

    fun updateMaterial(material: Material) {
        viewModelScope.launch {
            repository.updateMaterial(material)
        }
    }

    fun deleteMaterial(material: Material) {
        viewModelScope.launch {
            repository.deleteMaterial(material)
        }
    }

    fun toggleAcquired(material: Material) {
        updateMaterial(material.copy(acquired = !material.acquired))
    }

    fun updateReceiptPhoto(material: Material, uri: Uri?) {
        updateMaterial(material.copy(receiptPhotoUri = uri?.toString()))
    }
}