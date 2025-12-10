package com.example.felujitas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.felujitas.data.repository.RenovationRepository

//a ViewModel példányok létrehozása Repository függőséggel
class ViewModelFactory(
    private val repository: RenovationRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                DashboardViewModel(repository) as T
            }
            modelClass.isAssignableFrom(TaskViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                TaskViewModel(repository) as T
            }
            modelClass.isAssignableFrom(MaterialViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                MaterialViewModel(repository) as T
            }
            modelClass.isAssignableFrom(RoomViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                RoomViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}