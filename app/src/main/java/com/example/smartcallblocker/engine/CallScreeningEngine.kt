package com.example.smartcallblocker.engine

import com.example.smartcallblocker.data.local.entity.CountryCodeRuleEntity
import com.example.smartcallblocker.data.local.entity.PatternRuleEntity
import com.example.smartcallblocker.data.local.entity.RuleType
import com.example.smartcallblocker.data.model.AppSettings
import com.example.smartcallblocker.data.model.BlockAllCallsMode
import java.time.LocalTime

enum class MatchedRuleType {
    ALL_CALLS,
    PRIVATE_CALLER,
    ALL_INTERNATIONAL,
    COUNTRY_CODE,
    PREFIX,
    SUFFIX
}

data class EvaluationResult(
    val shouldBlock: Boolean,
    val matchedRuleType: MatchedRuleType? = null,
    val blockReason: String? = null,
)

class CallScreeningEngine {

    /**
     * Evaluates an incoming phone number against app settings, country code rules, and pattern rules.
     * Order of precedence:
     * 1. Block All Calls check (ALWAYS or SCHEDULED within active time range)
     * 2. Private/Unknown Caller check
     * 3. Block All International check
     * 4. Specific Country Code rules
     * 5. Local Prefix rules
     * 6. Local Suffix rules
     */
    fun evaluate(
        phoneNumber: String?,
        countryCodeRules: List<CountryCodeRuleEntity> = emptyList(),
        patternRules: List<PatternRuleEntity> = emptyList(),
        appSettings: AppSettings = AppSettings(),
        currentTime: LocalTime = LocalTime.now()
    ): EvaluationResult {
        // 0. High priority check: Block All Incoming Calls
        when (appSettings.blockAllCallsMode) {
            BlockAllCallsMode.ALWAYS -> {
                return EvaluationResult(
                    shouldBlock = true,
                    matchedRuleType = MatchedRuleType.ALL_CALLS,
                    blockReason = "All Calls Blocked"
                )
            }
            BlockAllCallsMode.SCHEDULED -> {
                if (isTimeInScheduledRange(
                        currentTime = currentTime,
                        startTimeStr = appSettings.blockAllCallsStartTime,
                        endTimeStr = appSettings.blockAllCallsEndTime
                    )
                ) {
                    return EvaluationResult(
                        shouldBlock = true,
                        matchedRuleType = MatchedRuleType.ALL_CALLS,
                        blockReason = "All Calls Blocked (Scheduled)"
                    )
                }
            }
            BlockAllCallsMode.DISABLED -> { /* proceed */ }
        }

        // 1. Private / Unknown Caller Check
        if (isPrivateOrUnknown(phoneNumber)) {
            return if (appSettings.blockPrivateCallers) {
                EvaluationResult(
                    shouldBlock = true,
                    matchedRuleType = MatchedRuleType.PRIVATE_CALLER,
                    blockReason = "Private Caller"
                )
            } else {
                EvaluationResult(shouldBlock = false)
            }
        }

        val cleanNumber = phoneNumber!!.trim()
        val digitsOnly = cleanNumber.filter { it.isDigit() }

        // 2. Block All International Calls Check (+ or 00) with Whitelisted Country Codes Exemption
        if (appSettings.blockAllInternational && (cleanNumber.startsWith("+") || cleanNumber.startsWith("00"))) {
            val enabledCcRules = countryCodeRules.filter { it.isEnabled }
            val isWhitelisted = enabledCcRules.any { ccRule ->
                val ccDigits = ccRule.countryCode.filter { it.isDigit() }
                ccDigits.isNotEmpty() && matchesCountryCode(cleanNumber, ccRule.countryCode, ccDigits)
            }

            if (isWhitelisted) {
                return EvaluationResult(shouldBlock = false)
            } else {
                return EvaluationResult(
                    shouldBlock = true,
                    matchedRuleType = MatchedRuleType.ALL_INTERNATIONAL,
                    blockReason = "All International Calls (+)"
                )
            }
        }

        // Filter active pattern rules
        val enabledPatterns = patternRules.filter { it.isEnabled }

        // 4. Evaluate Local Prefix Rules
        val prefixRules = enabledPatterns.filter { it.type == RuleType.PREFIX }
        for (prefixRule in prefixRules) {
            val prefixDigits = prefixRule.pattern.filter { it.isDigit() }
            if (prefixDigits.isEmpty()) continue

            if (matchesPrefix(cleanNumber, digitsOnly, prefixDigits)) {
                val label = prefixRule.label?.takeIf { it.isNotBlank() }?.let { " ($it)" } ?: ""
                return EvaluationResult(
                    shouldBlock = true,
                    matchedRuleType = MatchedRuleType.PREFIX,
                    blockReason = "Prefix Rule matched: ${prefixRule.pattern}$label"
                )
            }
        }

        // 5. Evaluate Local Suffix Rules
        val suffixRules = enabledPatterns.filter { it.type == RuleType.SUFFIX }
        for (suffixRule in suffixRules) {
            val suffixDigits = suffixRule.pattern.filter { it.isDigit() }
            if (suffixDigits.isEmpty()) continue

            if (matchesSuffix(digitsOnly, suffixDigits)) {
                val label = suffixRule.label?.takeIf { it.isNotBlank() }?.let { " ($it)" } ?: ""
                return EvaluationResult(
                    shouldBlock = true,
                    matchedRuleType = MatchedRuleType.SUFFIX,
                    blockReason = "Suffix Rule matched: ${suffixRule.pattern}$label"
                )
            }
        }

        return EvaluationResult(shouldBlock = false)
    }

    private fun isTimeInScheduledRange(
        currentTime: LocalTime,
        startTimeStr: String,
        endTimeStr: String
    ): Boolean {
        return try {
            val start = LocalTime.parse(startTimeStr)
            val end = LocalTime.parse(endTimeStr)

            if (start == end) {
                true
            } else if (start.isBefore(end)) {
                !currentTime.isBefore(start) && currentTime.isBefore(end)
            } else {
                !currentTime.isBefore(start) || currentTime.isBefore(end)
            }
        } catch (_: Exception) {
            false
        }
    }

    private fun isPrivateOrUnknown(phoneNumber: String?): Boolean {
        if (phoneNumber == null || phoneNumber.isBlank()) return true
        val trimmed = phoneNumber.trim().lowercase()
        return trimmed in PRIVATE_CALLER_KEYWORDS
    }

    private fun matchesCountryCode(
        cleanNumber: String,
        rawCountryCode: String,
        ccDigits: String,
    ): Boolean {
        if (cleanNumber.startsWith("+$ccDigits") || cleanNumber.startsWith("00$ccDigits")) {
            return true
        }

        val formattedCode = if (rawCountryCode.startsWith("+")) rawCountryCode else "+$rawCountryCode"
        return cleanNumber.startsWith(formattedCode) ||
                (cleanNumber.startsWith(ccDigits) && (cleanNumber.startsWith("+") || cleanNumber.startsWith("00")))
    }

    private fun matchesPrefix(
        cleanNumber: String,
        digitsOnly: String,
        prefixDigits: String,
    ): Boolean {
        if (digitsOnly.startsWith(prefixDigits)) return true

        val strippedLeading = when {
            cleanNumber.startsWith("+") -> cleanNumber.substring(1)
            cleanNumber.startsWith("00") -> cleanNumber.substring(2)
            else -> cleanNumber
        }
        return strippedLeading.startsWith(prefixDigits)
    }

    private fun matchesSuffix(
        digitsOnly: String,
        suffixDigits: String,
    ): Boolean {
        return digitsOnly.endsWith(suffixDigits)
    }

    companion object {
        private val PRIVATE_CALLER_KEYWORDS = setOf(
            "private",
            "restricted",
            "unknown",
            "withheld",
            "anonymous",
            "blocked",
            "unavailable",
            "private number",
            "unknown caller"
        )
    }
}
