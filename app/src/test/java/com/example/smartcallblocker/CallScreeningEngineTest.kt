package com.example.smartcallblocker

import com.example.smartcallblocker.data.local.entity.CountryCodeRuleEntity
import com.example.smartcallblocker.data.local.entity.PatternRuleEntity
import com.example.smartcallblocker.data.local.entity.RuleType
import com.example.smartcallblocker.engine.CallScreeningEngine
import com.example.smartcallblocker.engine.MatchedRuleType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CallScreeningEngineTest {

    private lateinit var engine: CallScreeningEngine

    @Before
    fun setUp() {
        engine = CallScreeningEngine()
    }

    @Test
    fun `country code rule blocks matching caller with plus or double zero`() {
        val ccRules = listOf(
            CountryCodeRuleEntity(id = 1, countryCode = "+1", countryName = "United States", isEnabled = true),
            CountryCodeRuleEntity(id = 2, countryCode = "+44", countryName = "United Kingdom", isEnabled = true)
        )

        // Caller starting with +1
        val result1 = engine.evaluate("+18005550199", ccRules, emptyList())
        assertTrue(result1.shouldBlock)
        assertEquals(MatchedRuleType.COUNTRY_CODE, result1.matchedRuleType)
        assertTrue(result1.blockReason?.contains("United States") == true)

        // Caller starting with 001
        val result2 = engine.evaluate("0018005550199", ccRules, emptyList())
        assertTrue(result2.shouldBlock)
        assertEquals(MatchedRuleType.COUNTRY_CODE, result2.matchedRuleType)

        // Caller starting with +44
        val result3 = engine.evaluate("+442079460991", ccRules, emptyList())
        assertTrue(result3.shouldBlock)
        assertEquals(MatchedRuleType.COUNTRY_CODE, result3.matchedRuleType)
        assertTrue(result3.blockReason?.contains("United Kingdom") == true)
    }

    @Test
    fun `prefix rule blocks matching caller number`() {
        val patternRules = listOf(
            PatternRuleEntity(id = 1, pattern = "1800", type = RuleType.PREFIX, isEnabled = true, label = "Toll Free Spam"),
            PatternRuleEntity(id = 2, pattern = "09", type = RuleType.PREFIX, isEnabled = true, label = "Promo Line")
        )

        val result1 = engine.evaluate("18005550199", emptyList(), patternRules)
        assertTrue(result1.shouldBlock)
        assertEquals(MatchedRuleType.PREFIX, result1.matchedRuleType)
        assertTrue(result1.blockReason?.contains("1800") == true)
        assertTrue(result1.blockReason?.contains("Toll Free Spam") == true)

        val result2 = engine.evaluate("0912345678", emptyList(), patternRules)
        assertTrue(result2.shouldBlock)
        assertEquals(MatchedRuleType.PREFIX, result2.matchedRuleType)
    }

    @Test
    fun `suffix rule blocks matching caller number`() {
        val patternRules = listOf(
            PatternRuleEntity(id = 1, pattern = "9999", type = RuleType.SUFFIX, isEnabled = true, label = "Robocall End")
        )

        val result1 = engine.evaluate("0912349999", emptyList(), patternRules)
        assertTrue(result1.shouldBlock)
        assertEquals(MatchedRuleType.SUFFIX, result1.matchedRuleType)
        assertTrue(result1.blockReason?.contains("9999") == true)

        val result2 = engine.evaluate("+18005559999", emptyList(), patternRules)
        assertTrue(result2.shouldBlock)
        assertEquals(MatchedRuleType.SUFFIX, result2.matchedRuleType)
    }

    @Test
    fun `disabled rules are ignored`() {
        val ccRules = listOf(
            CountryCodeRuleEntity(id = 1, countryCode = "+1", countryName = "United States", isEnabled = false)
        )
        val patternRules = listOf(
            PatternRuleEntity(id = 1, pattern = "1800", type = RuleType.PREFIX, isEnabled = false)
        )

        val result = engine.evaluate("+18005550199", ccRules, patternRules)
        assertFalse(result.shouldBlock)
    }

    @Test
    fun `country code rule takes precedence over prefix and suffix`() {
        val ccRules = listOf(
            CountryCodeRuleEntity(id = 1, countryCode = "+91", countryName = "India", isEnabled = true)
        )
        val patternRules = listOf(
            PatternRuleEntity(id = 1, pattern = "987", type = RuleType.PREFIX, isEnabled = true),
            PatternRuleEntity(id = 2, pattern = "010", type = RuleType.SUFFIX, isEnabled = true)
        )

        val result = engine.evaluate("+919876543010", ccRules, patternRules)
        assertTrue(result.shouldBlock)
        assertEquals(MatchedRuleType.COUNTRY_CODE, result.matchedRuleType)
        assertTrue(result.blockReason?.contains("India") == true)
    }

    @Test
    fun `unmatched phone number is allowed`() {
        val ccRules = listOf(
            CountryCodeRuleEntity(id = 1, countryCode = "+44", countryName = "United Kingdom", isEnabled = true)
        )
        val patternRules = listOf(
            PatternRuleEntity(id = 1, pattern = "1800", type = RuleType.PREFIX, isEnabled = true),
            PatternRuleEntity(id = 2, pattern = "9999", type = RuleType.SUFFIX, isEnabled = true)
        )

        val result = engine.evaluate("+12025550123", ccRules, patternRules)
        assertFalse(result.shouldBlock)
    }
}
