package com.example.smartcallblocker.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartcallblocker.data.local.entity.BlockedCallLogEntity
import com.example.smartcallblocker.data.repository.CallBlockerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CallHistoryViewModel(
    private val repository: CallBlockerRepository
) : ViewModel() {

    val blockedCallLogs: StateFlow<List<BlockedCallLogEntity>> = repository.getAllBlockedCallLogs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    private val _showClearConfirmDialog = MutableStateFlow(false)
    val showClearConfirmDialog: StateFlow<Boolean> = _showClearConfirmDialog.asStateFlow()

    fun showClearConfirmationDialog() {
        _showClearConfirmDialog.value = true
    }

    fun dismissClearConfirmationDialog() {
        _showClearConfirmDialog.value = false
    }

    fun clearAllLogs() {
        viewModelScope.launch {
            repository.clearAllBlockedCallLogs()
            _showClearConfirmDialog.value = false
        }
    }

    fun deleteLog(log: BlockedCallLogEntity) {
        viewModelScope.launch {
            repository.deleteBlockedCallLog(log)
        }
    }
}

class CallHistoryViewModelFactory(
    private val repository: CallBlockerRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CallHistoryViewModel::class.java)) {
            return CallHistoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
