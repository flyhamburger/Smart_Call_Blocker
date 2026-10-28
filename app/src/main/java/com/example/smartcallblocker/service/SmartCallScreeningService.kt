package com.example.smartcallblocker.service

import android.net.Uri
import android.telecom.Call
import android.telecom.CallScreeningService
import com.example.smartcallblocker.data.local.AppDatabase
import com.example.smartcallblocker.data.local.entity.BlockedCallLogEntity
import com.example.smartcallblocker.data.repository.CallBlockerRepository
import com.example.smartcallblocker.data.repository.CallBlockerRepositoryImpl
import com.example.smartcallblocker.engine.CallScreeningEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SmartCallScreeningService : CallScreeningService() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    private var repository: CallBlockerRepository? = null
    private val engine = CallScreeningEngine()

    override fun onCreate() {
        super.onCreate()
        if (repository == null) {
            val db = AppDatabase.getInstance(applicationContext)
            repository = CallBlockerRepositoryImpl(
                countryCodeRuleDao = db.countryCodeRuleDao(),
                patternRuleDao = db.patternRuleDao(),
                blockedCallLogDao = db.blockedCallLogDao(),
            )
        }
    }

    fun setRepositoryForTesting(repo: CallBlockerRepository) {
        this.repository = repo
    }

    override fun onScreenCall(callDetails: Call.Details) {
        val handle: Uri? = callDetails.handle
        val rawPhoneNumber: String = handle?.schemeSpecificPart ?: ""

        if (rawPhoneNumber.isBlank()) {
            val response = CallResponse.Builder().build()
            respondToCall(callDetails, response)
            return
        }

        val currentRepo = repository ?: run {
            val db = AppDatabase.getInstance(applicationContext)
            CallBlockerRepositoryImpl(
                countryCodeRuleDao = db.countryCodeRuleDao(),
                patternRuleDao = db.patternRuleDao(),
                blockedCallLogDao = db.blockedCallLogDao()
            ).also { repository = it }
        }

        serviceScope.launch {
            try {
                val countryCodeRules = currentRepo.getEnabledCountryCodeRules()
                val patternRules = currentRepo.getEnabledPatternRules()

                val result = engine.evaluate(
                    phoneNumber = rawPhoneNumber,
                    countryCodeRules = countryCodeRules,
                    patternRules = patternRules
                )

                if (result.shouldBlock) {
                    val response = CallResponse.Builder()
                        .setDisallowCall(true)
                        .setRejectCall(true)
                        .setSkipCallLog(false)
                        .setSkipNotification(false)
                        .build()

                    respondToCall(callDetails, response)

                    val log = BlockedCallLogEntity(
                        phoneNumber = rawPhoneNumber,
                        timestamp = System.currentTimeMillis(),
                        blockReason = result.blockReason ?: "Blocked by rule"
                    )
                    currentRepo.addBlockedCallLog(log)
                } else {
                    val response = CallResponse.Builder().build()
                    respondToCall(callDetails, response)
                }
            } catch (_: Exception) {
                val response = CallResponse.Builder().build()
                respondToCall(callDetails, response)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }
}
