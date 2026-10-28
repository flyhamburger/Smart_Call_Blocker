package com.example.smartcallblocker

import com.example.smartcallblocker.data.local.CountryCodeHelper
import com.example.smartcallblocker.data.local.CountryCodeHelper.toRuleEntity
import com.example.smartcallblocker.data.local.getFlagEmoji
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CountryCodeHelperTest {

    @Test
    fun `standard countries list is non empty and contains primary countries`() {
        val countries = CountryCodeHelper.standardCountries
        assertTrue(countries.size > 20)

        val us = countries.find { it.isoCode == "US" }
        assertNotNull(us)
        assertEquals("+1", us?.code)
        assertEquals("United States", us?.name)

        val uk = countries.find { it.isoCode == "GB" }
        assertNotNull(uk)
        assertEquals("+44", uk?.code)

        val india = countries.find { it.isoCode == "IN" }
        assertNotNull(india)
        assertEquals("+91", india?.code)

        val germany = countries.find { it.isoCode == "DE" }
        assertNotNull(germany)
        assertEquals("+49", germany?.code)

        val japan = countries.find { it.isoCode == "JP" }
        assertNotNull(japan)
        assertEquals("+81", japan?.code)

        val australia = countries.find { it.isoCode == "AU" }
        assertNotNull(australia)
        assertEquals("+61", australia?.code)
    }

    @Test
    fun `search countries filters by name code or iso`() {
        val searchIndia = CountryCodeHelper.searchCountries("India")
        assertTrue(searchIndia.any { it.isoCode == "IN" })

        val searchCode = CountryCodeHelper.searchCountries("+44")
        assertTrue(searchCode.any { it.isoCode == "GB" })

        val searchIso = CountryCodeHelper.searchCountries("de")
        assertTrue(searchIso.any { it.isoCode == "DE" })
    }

    @Test
    fun `toRuleEntity creates valid CountryCodeRuleEntity`() {
        val country = CountryCodeHelper.findByIso("US")
        assertNotNull(country)
        val entity = country!!.toRuleEntity(isEnabled = true)
        assertEquals("+1", entity.countryCode)
        assertEquals("United States", entity.countryName)
        assertTrue(entity.isEnabled)
    }

    @Test
    fun `getFlagEmoji creates emoji flag string`() {
        val usFlag = getFlagEmoji("US")
        assertTrue(usFlag.isNotEmpty())
    }
}
