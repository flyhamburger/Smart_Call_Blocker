package com.example.smartcallblocker.engine

import com.example.smartcallblocker.data.local.entity.CountryCodeRuleEntity
import com.example.smartcallblocker.data.local.entity.PatternRuleEntity
import com.example.smartcallblocker.data.local.entity.RuleType

enum class MatchedRuleType {
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
     * Evaluates an incoming phone number against enabled country code rules and pattern rules.
     * Order of precedence:
     * 1. Country Code rules
     * 2. Local Prefix rules
     * 3. Local Suffix rules
     */
    fun evaluate(
        phoneNumber: String,
        countryCodeRules: List<CountryCodeRuleEntity>,
        patternRules: List<PatternRuleEntity>,
    ): EvaluationResult {
        if (phoneNumber.isBlank()) {
            return EvaluationResult(shouldBlock = false)
        }

        val cleanNumber = phoneNumber.trim()
        val digitsOnly = cleanNumber.filter { it.isDigit() }

        // 1. Evaluate Country Code Rules
        val enabledCcRules = countryCodeRules.filter { it.isEnabled }
        for (ccRule in enabledCcRules) {
            val ccDigits = ccRule.countryCode.filter { it.isDigit() }
            if (ccDigits.isEmpty()) continue

            if (matchesCountryCode(cleanNumber, ccRule.countryCode, ccDigits)) {
                return EvaluationResult(
                    shouldBlock = true,
                    matchedRuleType = MatchedRuleType.COUNTRY_CODE,
                    blockReason = "Country Code Rule matched: ${ccRule.countryName} (${ccRule.countryCode})"
                )
            }
        }

        // Filter active pattern rules
        val enabledPatterns = patternRules.filter { it.isEnabled }

        // 2. Evaluate Local Prefix Rules
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

        // 3. Evaluate Local Suffix Rules
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
}
