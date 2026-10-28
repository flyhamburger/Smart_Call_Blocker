package com.example.smartcallblocker.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.smartcallblocker.data.local.dao.BlockedCallLogDao
import com.example.smartcallblocker.data.local.dao.CountryCodeRuleDao
import com.example.smartcallblocker.data.local.dao.PatternRuleDao
import com.example.smartcallblocker.data.local.entity.BlockedCallLogEntity
import com.example.smartcallblocker.data.local.entity.CountryCodeRuleEntity
import com.example.smartcallblocker.data.local.entity.PatternRuleEntity
import com.example.smartcallblocker.data.model.AppSettings
import com.example.smartcallblocker.data.model.NotificationPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CallBlockerRepositoryImpl(
    private val countryCodeRuleDao: CountryCodeRuleDao,
    private val patternRuleDao: PatternRuleDao,
    private val blockedCallLogDao: BlockedCallLogDao,
    sharedPreferences: SharedPreferences? = null,
    context: Context? = null
) : CallBlockerRepository {

    private val prefs: SharedPreferences? = sharedPreferences
        ?: context?.applicationContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val initialSettings: AppSettings = loadAppSettings()
    private val _appSettings = MutableStateFlow(initialSettings)
    override val appSettings: StateFlow<AppSettings> = _appSettings.asStateFlow()

    private fun loadAppSettings(): AppSettings {
        if (prefs == null) return AppSettings()
        val blockAllInt = prefs.getBoolean(KEY_BLOCK_ALL_INT, false)
        val blockPrivate = prefs.getBoolean(KEY_BLOCK_PRIVATE, false)
        val notifPrefName = prefs.getString(KEY_NOTIF_PREF, NotificationPreference.ALL.name)
        val notifPref = try {
            NotificationPreference.valueOf(notifPrefName ?: NotificationPreference.ALL.name)
        } catch (_: Exception) {
            NotificationPreference.ALL
        }
        return AppSettings(
            blockAllInternational = blockAllInt,
            blockPrivateCallers = blockPrivate,
            notificationPreference = notifPref
        )
    }

    override suspend fun getAppSettings(): AppSettings = _appSettings.value

    override suspend fun updateBlockAllInternational(enabled: Boolean) {
        _appSettings.value = _appSettings.value.copy(blockAllInternational = enabled)
        prefs?.edit()?.putBoolean(KEY_BLOCK_ALL_INT, enabled)?.apply()
    }

    override suspend fun updateBlockPrivateCallers(enabled: Boolean) {
        _appSettings.value = _appSettings.value.copy(blockPrivateCallers = enabled)
        prefs?.edit()?.putBoolean(KEY_BLOCK_PRIVATE, enabled)?.apply()
    }

    override suspend fun updateNotificationPreference(preference: NotificationPreference) {
        _appSettings.value = _appSettings.value.copy(notificationPreference = preference)
        prefs?.edit()?.putString(KEY_NOTIF_PREF, preference.name)?.apply()
    }

    override suspend fun updateAppSettings(settings: AppSettings) {
        _appSettings.value = settings
        prefs?.edit()
            ?.putBoolean(KEY_BLOCK_ALL_INT, settings.blockAllInternational)
            ?.putBoolean(KEY_BLOCK_PRIVATE, settings.blockPrivateCallers)
            ?.putString(KEY_NOTIF_PREF, settings.notificationPreference.name)
            ?.apply()
    }

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

    companion object {
        private const val PREFS_NAME = "smart_call_blocker_prefs"
        private const val KEY_BLOCK_ALL_INT = "block_all_international"
        private const val KEY_BLOCK_PRIVATE = "block_private_callers"
        private const val KEY_NOTIF_PREF = "notification_preference"
    }
}
