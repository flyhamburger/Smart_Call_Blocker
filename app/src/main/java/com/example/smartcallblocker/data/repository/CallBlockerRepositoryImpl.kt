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
import com.example.smartcallblocker.data.model.BlockAllCallsMode
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
        val blockAllCallsModeName = prefs.getString(KEY_BLOCK_ALL_CALLS_MODE, BlockAllCallsMode.DISABLED.name)
        val blockAllCallsMode = try {
            BlockAllCallsMode.valueOf(blockAllCallsModeName ?: BlockAllCallsMode.DISABLED.name)
        } catch (_: Exception) {
            BlockAllCallsMode.DISABLED
        }
        val blockAllCallsStartTime = prefs.getString(KEY_BLOCK_ALL_CALLS_START_TIME, "22:00") ?: "22:00"
        val blockAllCallsEndTime = prefs.getString(KEY_BLOCK_ALL_CALLS_END_TIME, "07:00") ?: "07:00"

        return AppSettings(
            blockAllInternational = blockAllInt,
            blockPrivateCallers = blockPrivate,
            notificationPreference = notifPref,
            blockAllCallsMode = blockAllCallsMode,
            blockAllCallsStartTime = blockAllCallsStartTime,
            blockAllCallsEndTime = blockAllCallsEndTime
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

    override suspend fun updateBlockAllCallsMode(mode: BlockAllCallsMode) {
        _appSettings.value = _appSettings.value.copy(blockAllCallsMode = mode)
        prefs?.edit()?.putString(KEY_BLOCK_ALL_CALLS_MODE, mode.name)?.apply()
    }

    override suspend fun updateBlockAllCallsSchedule(startTime: String, endTime: String) {
        _appSettings.value = _appSettings.value.copy(
            blockAllCallsStartTime = startTime,
            blockAllCallsEndTime = endTime
        )
        prefs?.edit()
            ?.putString(KEY_BLOCK_ALL_CALLS_START_TIME, startTime)
            ?.putString(KEY_BLOCK_ALL_CALLS_END_TIME, endTime)
            ?.apply()
    }

    override suspend fun updateAppSettings(settings: AppSettings) {
        _appSettings.value = settings
        prefs?.edit()
            ?.putBoolean(KEY_BLOCK_ALL_INT, settings.blockAllInternational)
            ?.putBoolean(KEY_BLOCK_PRIVATE, settings.blockPrivateCallers)
            ?.putString(KEY_NOTIF_PREF, settings.notificationPreference.name)
            ?.putString(KEY_BLOCK_ALL_CALLS_MODE, settings.blockAllCallsMode.name)
            ?.putString(KEY_BLOCK_ALL_CALLS_START_TIME, settings.blockAllCallsStartTime)
            ?.putString(KEY_BLOCK_ALL_CALLS_END_TIME, settings.blockAllCallsEndTime)
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
        private const val KEY_BLOCK_ALL_CALLS_MODE = "block_all_calls_mode"
        private const val KEY_BLOCK_ALL_CALLS_START_TIME = "block_all_calls_start_time"
        private const val KEY_BLOCK_ALL_CALLS_END_TIME = "block_all_calls_end_time"
    }
}
