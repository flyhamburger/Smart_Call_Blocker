package com.example.smartcallblocker.data.repository

import com.example.smartcallblocker.data.local.entity.BlockedCallLogEntity
import com.example.smartcallblocker.data.local.entity.CountryCodeRuleEntity
import com.example.smartcallblocker.data.local.entity.PatternRuleEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeCallBlockerRepository : CallBlockerRepository {

    private val countryCodeRulesFlow = MutableStateFlow<List<CountryCodeRuleEntity>>(emptyList())
    private val patternRulesFlow = MutableStateFlow<List<PatternRuleEntity>>(emptyList())
    private val blockedCallLogsFlow = MutableStateFlow<List<BlockedCallLogEntity>>(emptyList())

    private var nextCountryRuleId = 1L
    private var nextPatternRuleId = 1L
    private var nextLogId = 1L

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
        patternRuleFlow.value = patternRulesFlow.value.filter { it.id != id }
    }

    private val patternRuleFlow: MutableStateFlow<List<PatternRuleEntity>>
        get() = patternRulesFlow

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
