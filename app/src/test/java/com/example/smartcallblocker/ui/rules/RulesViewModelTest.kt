package com.example.smartcallblocker.ui.rules

import com.example.smartcallblocker.data.local.CountryCodeHelper
import com.example.smartcallblocker.data.local.entity.RuleType
import com.example.smartcallblocker.data.repository.FakeCallBlockerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RulesViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeCallBlockerRepository
    private lateinit var viewModel: RulesViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeCallBlockerRepository()
        viewModel = RulesViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun subscribeToViewModelFlows(scope: kotlinx.coroutines.CoroutineScope) {
        scope.launch { viewModel.countryCodeRules.collect {} }
        scope.launch { viewModel.patternRules.collect {} }
        scope.launch { viewModel.filteredPatternRules.collect {} }
    }

    @Test
    fun `addCountryCodeRule adds country rule to repository and closes picker`() = runTest {
        subscribeToViewModelFlows(backgroundScope)

        viewModel.openCountryPicker()
        assertTrue(viewModel.isCountryPickerOpen.value)

        val usCountry = CountryCodeHelper.findByIso("US")!!
        viewModel.addCountryCodeRule(usCountry)

        assertFalse(viewModel.isCountryPickerOpen.value)
        val rules = viewModel.countryCodeRules.value
        assertEquals(1, rules.size)
        assertEquals("+1", rules[0].countryCode)
        assertEquals("United States", rules[0].countryName)
        assertTrue(rules[0].isEnabled)
    }

    @Test
    fun `toggleCountryCodeRule toggles rule enabled state in repository`() = runTest {
        subscribeToViewModelFlows(backgroundScope)

        val usCountry = CountryCodeHelper.findByIso("US")!!
        viewModel.addCountryCodeRule(usCountry)

        val ruleBefore = viewModel.countryCodeRules.value.first()
        assertTrue(ruleBefore.isEnabled)

        viewModel.toggleCountryCodeRule(ruleBefore)
        val ruleAfter = viewModel.countryCodeRules.value.first()
        assertFalse(ruleAfter.isEnabled)

        viewModel.toggleCountryCodeRule(ruleAfter)
        val ruleToggledBack = viewModel.countryCodeRules.value.first()
        assertTrue(ruleToggledBack.isEnabled)
    }

    @Test
    fun `deleteCountryCodeRule removes rule from repository`() = runTest {
        subscribeToViewModelFlows(backgroundScope)

        val usCountry = CountryCodeHelper.findByIso("US")!!
        val ukCountry = CountryCodeHelper.findByIso("GB")!!
        viewModel.addCountryCodeRule(usCountry)
        viewModel.addCountryCodeRule(ukCountry)

        assertEquals(2, viewModel.countryCodeRules.value.size)

        val ruleToDelete = viewModel.countryCodeRules.value.first { it.countryCode == "+1" }
        viewModel.deleteCountryCodeRule(ruleToDelete)

        val remainingRules = viewModel.countryCodeRules.value
        assertEquals(1, remainingRules.size)
        assertEquals("+44", remainingRules[0].countryCode)
    }

    @Test
    fun `submitAddPatternRule with blank pattern fails validation and sets error`() = runTest {
        subscribeToViewModelFlows(backgroundScope)

        viewModel.openAddPatternDialog()
        viewModel.updatePatternInput("   ")

        val result = viewModel.submitAddPatternRule()
        assertFalse(result)
        assertNotNull(viewModel.patternInputError.value)
        assertEquals("Pattern cannot be empty", viewModel.patternInputError.value)
        assertTrue(viewModel.isAddPatternDialogOpen.value)
    }

    @Test
    fun `submitAddPatternRule with valid pattern adds rule and closes dialog`() = runTest {
        subscribeToViewModelFlows(backgroundScope)

        viewModel.openAddPatternDialog(RuleType.PREFIX)
        viewModel.updatePatternInput("1800")
        viewModel.updateLabelInput("Toll Free")

        val result = viewModel.submitAddPatternRule()
        assertTrue(result)
        assertFalse(viewModel.isAddPatternDialogOpen.value)
        assertNull(viewModel.patternInputError.value)

        val rules = viewModel.patternRules.value
        assertEquals(1, rules.size)
        assertEquals("1800", rules[0].pattern)
        assertEquals(RuleType.PREFIX, rules[0].type)
        assertEquals("Toll Free", rules[0].label)
        assertTrue(rules[0].isEnabled)
    }

    @Test
    fun `togglePatternRule toggles enabled state`() = runTest {
        subscribeToViewModelFlows(backgroundScope)

        viewModel.openAddPatternDialog()
        viewModel.updatePatternInput("9999")
        viewModel.updateRuleTypeInput(RuleType.SUFFIX)
        viewModel.submitAddPatternRule()

        val rule = viewModel.patternRules.value.first()
        assertTrue(rule.isEnabled)

        viewModel.togglePatternRule(rule)
        assertFalse(viewModel.patternRules.value.first().isEnabled)
    }

    @Test
    fun `deletePatternRule removes pattern rule from repository`() = runTest {
        subscribeToViewModelFlows(backgroundScope)

        viewModel.openAddPatternDialog()
        viewModel.updatePatternInput("1800")
        viewModel.submitAddPatternRule()

        viewModel.openAddPatternDialog()
        viewModel.updatePatternInput("9999")
        viewModel.updateRuleTypeInput(RuleType.SUFFIX)
        viewModel.submitAddPatternRule()

        assertEquals(2, viewModel.patternRules.value.size)

        val suffixRule = viewModel.patternRules.value.first { it.type == RuleType.SUFFIX }
        viewModel.deletePatternRule(suffixRule)

        val remaining = viewModel.patternRules.value
        assertEquals(1, remaining.size)
        assertEquals(RuleType.PREFIX, remaining[0].type)
    }

    @Test
    fun `patternFilter correctly filters pattern rules`() = runTest {
        subscribeToViewModelFlows(backgroundScope)

        viewModel.openAddPatternDialog()
        viewModel.updatePatternInput("1800")
        viewModel.updateRuleTypeInput(RuleType.PREFIX)
        viewModel.submitAddPatternRule()

        viewModel.openAddPatternDialog()
        viewModel.updatePatternInput("9999")
        viewModel.updateRuleTypeInput(RuleType.SUFFIX)
        viewModel.submitAddPatternRule()

        // ALL filter
        viewModel.setPatternFilter(PatternFilter.ALL)
        assertEquals(2, viewModel.filteredPatternRules.value.size)

        // PREFIX filter
        viewModel.setPatternFilter(PatternFilter.PREFIX)
        assertEquals(1, viewModel.filteredPatternRules.value.size)
        assertEquals(RuleType.PREFIX, viewModel.filteredPatternRules.value[0].type)

        // SUFFIX filter
        viewModel.setPatternFilter(PatternFilter.SUFFIX)
        assertEquals(1, viewModel.filteredPatternRules.value.size)
        assertEquals(RuleType.SUFFIX, viewModel.filteredPatternRules.value[0].type)
    }
}
