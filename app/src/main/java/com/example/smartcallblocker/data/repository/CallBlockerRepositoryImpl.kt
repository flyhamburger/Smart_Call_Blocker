package com.example.smartcallblocker.data.repository

import com.example.smartcallblocker.data.local.dao.BlockedCallLogDao
import com.example.smartcallblocker.data.local.dao.CountryCodeRuleDao
import com.example.smartcallblocker.data.local.dao.PatternRuleDao
import com.example.smartcallblocker.data.local.entity.BlockedCallLogEntity
import com.example.smartcallblocker.data.local.entity.CountryCodeRuleEntity
import com.example.smartcallblocker.data.local.entity.PatternRuleEntity
import kotlinx.coroutines.flow.Flow

class CallBlockerRepositoryImpl(
    private val countryCodeRuleDao: CountryCodeRuleDao,
    private val patternRuleDao: PatternRuleDao,
    private val blockedCallLogDao: BlockedCallLogDao,
) : CallBlockerRepository {

    override fun getAllCountryCodeRules(): Flow<List<CountryCodeRuleEntity>> =
        countryCodeRuleDao.getAllRules()

    override suspend fun getEnabledCountryCodeRules(): List<CountryCodeRuleEntity> =
        countryCodeRuleDao.getEnabledRules()

    override suspend fun addCountryCodeRule(rule: CountryCodeRuleEntity): Long =
        countryCodeRuleDao.insertRule(rule)

    override suspend fun updateCountryCodeRule(rule: CountryCodeRuleEntity) =
        countryCodeRuleDao.updateRule(rule)

    override suspend fun deleteCountryCodeRule(rule: CountryCodeRuleEntity) =
        countryCodeRuleDao.deleteRule(rule)

    override suspend fun deleteCountryCodeRuleById(id: Long) =
        countryCodeRuleDao.deleteRuleById(id)

    override fun getAllPatternRules(): Flow<List<PatternRuleEntity>> =
        patternRuleDao.getAllRules()

    override suspend fun getEnabledPatternRules(): List<PatternRuleEntity> =
        patternRuleDao.getEnabledRules()

    override suspend fun addPatternRule(rule: PatternRuleEntity): Long =
        patternRuleDao.insertRule(rule)

    override suspend fun updatePatternRule(rule: PatternRuleEntity) =
        patternRuleDao.updateRule(rule)

    override suspend fun deletePatternRule(rule: PatternRuleEntity) =
        patternRuleDao.deleteRule(rule)

    override suspend fun deletePatternRuleById(id: Long) =
        patternRuleDao.deleteRuleById(id)

    override fun getAllBlockedCallLogs(): Flow<List<BlockedCallLogEntity>> =
        blockedCallLogDao.getAllLogs()

    override fun getRecentBlockedCallLogs(limit: Int): Flow<List<BlockedCallLogEntity>> =
        blockedCallLogDao.getRecentLogs(limit)

    override suspend fun addBlockedCallLog(log: BlockedCallLogEntity): Long =
        blockedCallLogDao.insertLog(log)

    override suspend fun deleteBlockedCallLog(log: BlockedCallLogEntity) =
        blockedCallLogDao.deleteLog(log)

    override suspend fun clearAllBlockedCallLogs() =
        blockedCallLogDao.clearAllLogs()
}
