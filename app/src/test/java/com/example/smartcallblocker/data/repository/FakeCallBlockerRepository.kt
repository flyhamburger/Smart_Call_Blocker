package com.example.smartcallblocker.data.repository

import com.example.smartcallblocker.data.local.entity.BlockedCallLogEntity
import com.example.smartcallblocker.data.local.entity.CountryCodeRuleEntity
import com.example.smartcallblocker.data.local.entity.PatternRuleEntity
import com.example.smartcallblocker.data.model.AppSettings
import com.example.smartcallblocker.data.model.BlockAllCallsMode
import com.example.smartcallblocker.data.model.NotificationPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeCallBlockerRepository : CallBlockerRepository {

    private val appSettingsFlow = MutableStateFlow(AppSettings())
    override val appSettings: StateFlow<AppSettings> = appSettingsFlow.asStateFlow()

    private val countryCodeRulesFlow = MutableStateFlow<List<CountryCodeRuleEntity>>(emptyList())
    private val patternRulesFlow = MutableStateFlow<List<PatternRuleEntity>>(emptyList())
    private val blockedCallLogsFlow = MutableStateFlow<List<BlockedCallLogEntity>>(emptyList())

    private var nextCountryRuleId = 1L
    private var nextPatternRuleId = 1L
    private var nextLogId = 1L

    override suspend fun getAppSettings(): AppSettings = appSettingsFlow.value

    override suspend fun updateBlockAllInternational(enabled: Boolean) {
        appSettingsFlow.value = appSettingsFlow.value.copy(blockAllInternational = enabled)
    }

    override suspend fun updateBlockPrivateCallers(enabled: Boolean) {
        appSettingsFlow.value = appSettingsFlow.value.copy(blockPrivateCallers = enabled)
    }

    override suspend fun updateNotificationPreference(preference: NotificationPreference) {
        appSettingsFlow.value = appSettingsFlow.value.copy(notificationPreference = preference)
    }

    override suspend fun updateBlockAllCallsMode(mode: BlockAllCallsMode) {
        appSettingsFlow.value = appSettingsFlow.value.copy(blockAllCallsMode = mode)
    }

    override suspend fun updateBlockAllCallsSchedule(startTime: String, endTime: String) {
        appSettingsFlow.value = appSettingsFlow.value.copy(
            blockAllCallsStartTime = startTime,
            blockAllCallsEndTime = endTime
        )
    }

    override suspend fun updateAppSettings(settings: AppSettings) {
        appSettingsFlow.value = settings
    }

    override fun getAllCountryCodeRules(): Flow<List<CountryCodeRuleEntity>> = countryCodeRulesFlow

    override suspend fun getEnabledCountryCodeRules(): List<CountryCodeRuleEntity> {
        return countryCodeRulesFlow.value.filter { it.isEnabled }
    }

    override suspend fun addCountryCodeRule(rule: CountryCodeRuleEntity): Long {
        val id = if (rule.id == 0L) nextCountryRuleId++ else rule.id
        val newRule = rule.copy(id = id)
        countryCodeRulesFlow.value = countryCodeRulesFlow.value + newRule
        return id
    }

    override suspend fun updateCountryCodeRule(rule: CountryCodeRuleEntity) {
        countryCodeRulesFlow.value = countryCodeRulesFlow.value.map {
            if (it.id == rule.id) rule else it
        }
    }

    override suspend fun deleteCountryCodeRule(rule: CountryCodeRuleEntity) {
        countryCodeRulesFlow.value = countryCodeRulesFlow.value.filter { it.id != rule.id }
    }

    override suspend fun deleteCountryCodeRuleById(id: Long) {
        countryCodeRulesFlow.value = countryCodeRulesFlow.value.filter { it.id != id }
    }

    override fun getAllPatternRules(): Flow<List<PatternRuleEntity>> = patternRulesFlow

    override suspend fun getEnabledPatternRules(): List<PatternRuleEntity> {
        return patternRulesFlow.value.filter { it.isEnabled }
    }

    override suspend fun addPatternRule(rule: PatternRuleEntity): Long {
        val id = if (rule.id == 0L) nextPatternRuleId++ else rule.id
        val newRule = rule.copy(id = id)
        patternRulesFlow.value = patternRulesFlow.value + newRule
        return id
    }

    override suspend fun updatePatternRule(rule: PatternRuleEntity) {
        patternRulesFlow.value = patternRulesFlow.value.map {
            if (it.id == rule.id) rule else it
        }
    }

    override suspend fun deletePatternRule(rule: PatternRuleEntity) {
        patternRulesFlow.value = patternRulesFlow.value.filter { it.id != rule.id }
    }

    override suspend fun deletePatternRuleById(id: Long) {
        patternRulesFlow.value = patternRulesFlow.value.filter { it.id != id }
    }

    override fun getAllBlockedCallLogs(): Flow<List<BlockedCallLogEntity>> = blockedCallLogsFlow

    override fun getRecentBlockedCallLogs(limit: Int): Flow<List<BlockedCallLogEntity>> {
        return blockedCallLogsFlow.map { logs -> logs.take(limit) }
    }

    override suspend fun addBlockedCallLog(log: BlockedCallLogEntity): Long {
        val id = if (log.id == 0L) nextLogId++ else log.id
        val newLog = log.copy(id = id)
        blockedCallLogsFlow.value = blockedCallLogsFlow.value + newLog
        return id
    }

    override suspend fun deleteBlockedCallLog(log: BlockedCallLogEntity) {
        blockedCallLogsFlow.value = blockedCallLogsFlow.value.filter { it.id != log.id }
    }

    override suspend fun clearAllBlockedCallLogs() {
        blockedCallLogsFlow.value = emptyList()
    }
}
