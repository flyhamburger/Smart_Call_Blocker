package com.example.smartcallblocker.data.repository

import com.example.smartcallblocker.data.local.entity.BlockedCallLogEntity
import com.example.smartcallblocker.data.local.entity.CountryCodeRuleEntity
import com.example.smartcallblocker.data.local.entity.PatternRuleEntity
import kotlinx.coroutines.flow.Flow

interface CallBlockerRepository {
    // Country Code Rules
    fun getAllCountryCodeRules(): Flow<List<CountryCodeRuleEntity>>
    suspend fun getEnabledCountryCodeRules(): List<CountryCodeRuleEntity>
    suspend fun addCountryCodeRule(rule: CountryCodeRuleEntity): Long
    suspend fun updateCountryCodeRule(rule: CountryCodeRuleEntity)
    suspend fun deleteCountryCodeRule(rule: CountryCodeRuleEntity)
    suspend fun deleteCountryCodeRuleById(id: Long)

    // Pattern Rules
    fun getAllPatternRules(): Flow<List<PatternRuleEntity>>
    suspend fun getEnabledPatternRules(): List<PatternRuleEntity>
    suspend fun addPatternRule(rule: PatternRuleEntity): Long
    suspend fun updatePatternRule(rule: PatternRuleEntity)
    suspend fun deletePatternRule(rule: PatternRuleEntity)
    suspend fun deletePatternRuleById(id: Long)

    // Blocked Call Logs
    fun getAllBlockedCallLogs(): Flow<List<BlockedCallLogEntity>>
    fun getRecentBlockedCallLogs(limit: Int): Flow<List<BlockedCallLogEntity>>
    suspend fun addBlockedCallLog(log: BlockedCallLogEntity): Long
    suspend fun deleteBlockedCallLog(log: BlockedCallLogEntity)
    suspend fun clearAllBlockedCallLogs()
}
