package com.example.smartcallblocker.ui.rules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartcallblocker.data.local.CountryInfo
import com.example.smartcallblocker.data.local.entity.CountryCodeRuleEntity
import com.example.smartcallblocker.data.local.entity.PatternRuleEntity
import com.example.smartcallblocker.data.local.entity.RuleType
import com.example.smartcallblocker.data.model.AppSettings
import com.example.smartcallblocker.data.model.NotificationPreference
import com.example.smartcallblocker.data.repository.CallBlockerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class PatternFilter {
    ALL,
    PREFIX,
    SUFFIX
}

class RulesViewModel(
    private val repository: CallBlockerRepository
) : ViewModel() {

    // App Settings
    val appSettings: StateFlow<AppSettings> = repository.appSettings

    // Country Code Rules from DB
    val countryCodeRules: StateFlow<List<CountryCodeRuleEntity>> = repository.getAllCountryCodeRules()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    // Pattern Rules from DB
    val patternRules: StateFlow<List<PatternRuleEntity>> = repository.getAllPatternRules()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    // Pattern filter selection
    private val _patternFilter = MutableStateFlow(PatternFilter.ALL)
    val patternFilter: StateFlow<PatternFilter> = _patternFilter.asStateFlow()

    // Filtered pattern rules based on filter selection
    val filteredPatternRules: StateFlow<List<PatternRuleEntity>> = combine(
        patternRules,
        _patternFilter
    ) { rules, filter ->
        when (filter) {
            PatternFilter.ALL -> rules
            PatternFilter.PREFIX -> rules.filter { it.type == RuleType.PREFIX }
            PatternFilter.SUFFIX -> rules.filter { it.type == RuleType.SUFFIX }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    // Country Picker Dialog State
    private val _isCountryPickerOpen = MutableStateFlow(false)
    val isCountryPickerOpen: StateFlow<Boolean> = _isCountryPickerOpen.asStateFlow()

    private val _countrySearchQuery = MutableStateFlow("")
    val countrySearchQuery: StateFlow<String> = _countrySearchQuery.asStateFlow()

    // Add Pattern Rule Dialog State
    private val _isAddPatternDialogOpen = MutableStateFlow(false)
    val isAddPatternDialogOpen: StateFlow<Boolean> = _isAddPatternDialogOpen.asStateFlow()

    private val _patternInput = MutableStateFlow("")
    val patternInput: StateFlow<String> = _patternInput.asStateFlow()

    private val _ruleTypeInput = MutableStateFlow(RuleType.PREFIX)
    val ruleTypeInput: StateFlow<RuleType> = _ruleTypeInput.asStateFlow()

    private val _labelInput = MutableStateFlow("")
    val labelInput: StateFlow<String> = _labelInput.asStateFlow()

    private val _patternInputError = MutableStateFlow<String?>(null)
    val patternInputError: StateFlow<String?> = _patternInputError.asStateFlow()

    // Actions: Settings
    fun toggleBlockAllInternational() {
        viewModelScope.launch {
            val current = repository.appSettings.value.blockAllInternational
            repository.updateBlockAllInternational(!current)
        }
    }

    fun toggleBlockPrivateCallers() {
        viewModelScope.launch {
            val current = repository.appSettings.value.blockPrivateCallers
            repository.updateBlockPrivateCallers(!current)
        }
    }

    fun setNotificationPreference(preference: NotificationPreference) {
        viewModelScope.launch {
            repository.updateNotificationPreference(preference)
        }
    }

    // Actions: Country Code Rules
    fun toggleCountryCodeRule(rule: CountryCodeRuleEntity) {
        viewModelScope.launch {
            repository.updateCountryCodeRule(rule.copy(isEnabled = !rule.isEnabled))
        }
    }

    fun deleteCountryCodeRule(rule: CountryCodeRuleEntity) {
        viewModelScope.launch {
            repository.deleteCountryCodeRule(rule)
        }
    }

    fun addCountryCodeRule(countryInfo: CountryInfo) {
        viewModelScope.launch {
            val entity = CountryCodeRuleEntity(
                countryCode = countryInfo.code,
                countryName = countryInfo.name,
                isEnabled = true
            )
            repository.addCountryCodeRule(entity)
            closeCountryPicker()
        }
    }

    fun openCountryPicker() {
        _countrySearchQuery.value = ""
        _isCountryPickerOpen.value = true
    }

    fun closeCountryPicker() {
        _isCountryPickerOpen.value = false
        _countrySearchQuery.value = ""
    }

    fun updateCountrySearchQuery(query: String) {
        _countrySearchQuery.value = query
    }

    // Actions: Pattern Rules
    fun setPatternFilter(filter: PatternFilter) {
        _patternFilter.value = filter
    }

    fun togglePatternRule(rule: PatternRuleEntity) {
        viewModelScope.launch {
            repository.updatePatternRule(rule.copy(isEnabled = !rule.isEnabled))
        }
    }

    fun deletePatternRule(rule: PatternRuleEntity) {
        viewModelScope.launch {
            repository.deletePatternRule(rule)
        }
    }

    fun openAddPatternDialog(initialType: RuleType = RuleType.PREFIX) {
        _patternInput.value = ""
        _ruleTypeInput.value = initialType
        _labelInput.value = ""
        _patternInputError.value = null
        _isAddPatternDialogOpen.value = true
    }

    fun closeAddPatternDialog() {
        _isAddPatternDialogOpen.value = false
        _patternInput.value = ""
        _labelInput.value = ""
        _patternInputError.value = null
    }

    fun updatePatternInput(input: String) {
        _patternInput.value = input
        if (input.isNotBlank()) {
            _patternInputError.value = null
        }
    }

    fun updateRuleTypeInput(type: RuleType) {
        _ruleTypeInput.value = type
    }

    fun updateLabelInput(label: String) {
        _labelInput.value = label
    }

    fun submitAddPatternRule(): Boolean {
        val pattern = _patternInput.value.trim()
        if (pattern.isBlank()) {
            _patternInputError.value = "Pattern cannot be empty"
            return false
        }

        viewModelScope.launch {
            val entity = PatternRuleEntity(
                pattern = pattern,
                type = _ruleTypeInput.value,
                isEnabled = true,
                label = _labelInput.value.trim().ifBlank { null }
            )
            repository.addPatternRule(entity)
            closeAddPatternDialog()
        }
        return true
    }
}

class RulesViewModelFactory(
    private val repository: CallBlockerRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RulesViewModel::class.java)) {
            return RulesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
