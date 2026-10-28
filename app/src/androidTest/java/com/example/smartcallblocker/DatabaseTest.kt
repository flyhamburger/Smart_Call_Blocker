package com.example.smartcallblocker

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.smartcallblocker.data.local.AppDatabase
import com.example.smartcallblocker.data.local.entity.BlockedCallLogEntity
import com.example.smartcallblocker.data.local.entity.CountryCodeRuleEntity
import com.example.smartcallblocker.data.local.entity.PatternRuleEntity
import com.example.smartcallblocker.data.local.entity.RuleType
import com.example.smartcallblocker.data.repository.CallBlockerRepository
import com.example.smartcallblocker.data.repository.CallBlockerRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: CallBlockerRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        repository = CallBlockerRepositoryImpl(
            countryCodeRuleDao = db.countryCodeRuleDao(),
            patternRuleDao = db.patternRuleDao(),
            blockedCallLogDao = db.blockedCallLogDao()
        )
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndRetrieveCountryCodeRule() = runBlocking {
        val rule = CountryCodeRuleEntity(countryCode = "+1", countryName = "United States", isEnabled = true)
        val id = repository.addCountryCodeRule(rule)
        assertTrue(id > 0)

        val enabledRules = repository.getEnabledCountryCodeRules()
        assertEquals(1, enabledRules.size)
        assertEquals("+1", enabledRules[0].countryCode)
        assertEquals("United States", enabledRules[0].countryName)
    }

    @Test
    fun insertAndRetrievePatternRule() = runBlocking {
        val rule = PatternRuleEntity(pattern = "1800", type = RuleType.PREFIX, isEnabled = true, label = "Toll free")
        val id = repository.addPatternRule(rule)
        assertTrue(id > 0)

        val enabledRules = repository.getEnabledPatternRules()
        assertEquals(1, enabledRules.size)
        assertEquals("1800", enabledRules[0].pattern)
        assertEquals(RuleType.PREFIX, enabledRules[0].type)
        assertEquals("Toll free", enabledRules[0].label)
    }

    @Test
    fun insertAndRetrieveBlockedCallLog() = runBlocking {
        val log = BlockedCallLogEntity(phoneNumber = "+18005550199", blockReason = "Blocked by rule")
        val id = repository.addBlockedCallLog(log)
        assertTrue(id > 0)
    }
}
