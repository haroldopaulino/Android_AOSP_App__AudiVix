package com.harold.audivix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.harold.audivix.data.repository.MediaRepository
import com.harold.audivix.data.repository.SettingsRepository

class HomeViewModelFactory(private val repository: MediaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = HomeViewModel(repository) as T
}

class SettingsViewModelFactory(private val settingsRepository: SettingsRepository, private val mediaRepository: MediaRepository? = null) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = SettingsViewModel(settingsRepository, mediaRepository) as T
}

class PlaylistViewModelFactory(private val repository: MediaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = PlaylistViewModel(repository) as T
}
