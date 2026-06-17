package com.harold.audivix.wear.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.harold.audivix.wear.data.repository.WearMediaRepository

class WearViewModelFactory(private val repository: WearMediaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(WearHomeViewModel::class.java) -> WearHomeViewModel(repository) as T
            modelClass.isAssignableFrom(WearSettingsViewModel::class.java) -> WearSettingsViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel")
        }
    }
}
