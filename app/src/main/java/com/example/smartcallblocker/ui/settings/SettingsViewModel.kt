package com.example.smartcallblocker.ui.settings

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartcallblocker.data.model.AppSettings
import com.example.smartcallblocker.data.model.NotificationPreference
import com.example.smartcallblocker.data.repository.CallBlockerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: CallBlockerRepository
) : ViewModel() {

    val appSettings: StateFlow<AppSettings> = repository.appSettings

    private val _isNotificationPermissionGranted = MutableStateFlow(true)
    val isNotificationPermissionGranted: StateFlow<Boolean> = _isNotificationPermissionGranted.asStateFlow()

    fun checkNotificationPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            _isNotificationPermissionGranted.value = granted
        } else {
            _isNotificationPermissionGranted.value = true
        }
    }

    fun updateNotificationPermission(granted: Boolean) {
        _isNotificationPermissionGranted.value = granted
    }

    fun setNotificationPreference(preference: NotificationPreference) {
        viewModelScope.launch {
            repository.updateNotificationPreference(preference)
        }
    }

    fun toggleBlockPrivateCallers() {
        viewModelScope.launch {
            val current = repository.appSettings.value.blockPrivateCallers
            repository.updateBlockPrivateCallers(!current)
        }
    }

    fun setBlockPrivateCallers(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateBlockPrivateCallers(enabled)
        }
    }

    fun toggleBlockAllInternational() {
        viewModelScope.launch {
            val current = repository.appSettings.value.blockAllInternational
            repository.updateBlockAllInternational(!current)
        }
    }

    fun setBlockAllInternational(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateBlockAllInternational(enabled)
        }
    }
}

class SettingsViewModelFactory(
    private val repository: CallBlockerRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
