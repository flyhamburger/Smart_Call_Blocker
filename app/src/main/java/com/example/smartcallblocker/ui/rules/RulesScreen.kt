package com.example.smartcallblocker.ui.rules

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smartcallblocker.data.local.entity.CountryCodeRuleEntity
import com.example.smartcallblocker.data.local.entity.PatternRuleEntity
import com.example.smartcallblocker.data.local.entity.RuleType
import com.example.smartcallblocker.ui.theme.SmartCallBlockerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RulesScreen(
    viewModel: RulesViewModel,
    modifier: Modifier = Modifier
) {
    val countryCodeRules by viewModel.countryCodeRules.collectAsStateWithLifecycle()
    val patternRules by viewModel.filteredPatternRules.collectAsStateWithLifecycle()
    val patternFilter by viewModel.patternFilter.collectAsStateWithLifecycle()

    val isCountryPickerOpen by viewModel.isCountryPickerOpen.collectAsStateWithLifecycle()
    val countrySearchQuery by viewModel.countrySearchQuery.collectAsStateWithLifecycle()

    val isAddPatternDialogOpen by viewModel.isAddPatternDialogOpen.collectAsStateWithLifecycle()
    val patternInput by viewModel.patternInput.collectAsStateWithLifecycle()
    val ruleTypeInput by viewModel.ruleTypeInput.collectAsStateWithLifecycle()
    val labelInput by viewModel.labelInput.collectAsStateWithLifecycle()
    val patternInputError by viewModel.patternInputError.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    RulesScreenContent(
        selectedTabIndex = selectedTabIndex,
        onTabSelected = { selectedTabIndex = it },
        countryCodeRules = countryCodeRules,
        patternRules = patternRules,
        patternFilter = patternFilter,
        onPatternFilterSelected = viewModel::setPatternFilter,
        onToggleCountryCodeRule = viewModel::toggleCountryCodeRule,
        onDeleteCountryCodeRule = viewModel::deleteCountryCodeRule,
        onAddCountryCodeClick = viewModel::openCountryPicker,
        onTogglePatternRule = viewModel::togglePatternRule,
        onDeletePatternRule = viewModel::deletePatternRule,
        onAddPatternRuleClick = {
            val initialType = when (patternFilter) {
                PatternFilter.SUFFIX -> RuleType.SUFFIX
                else -> RuleType.PREFIX
            }
            viewModel.openAddPatternDialog(initialType)
        },
        modifier = modifier
    )

    if (isCountryPickerOpen) {
        CountryPickerDialog(
            searchQuery = countrySearchQuery,
            onSearchQueryChange = viewModel::updateCountrySearchQuery,
            onCountrySelected = viewModel::addCountryCodeRule,
            onDismissRequest = viewModel::closeCountryPicker
        )
    }

    if (isAddPatternDialogOpen) {
        AddPatternRuleDialog(
            patternInput = patternInput,
            ruleTypeInput = ruleTypeInput,
            labelInput = labelInput,
            errorMessage = patternInputError,
            onPatternChange = viewModel::updatePatternInput,
            onRuleTypeChange = viewModel::updateRuleTypeInput,
            onLabelChange = viewModel::updateLabelInput,
            onConfirm = { viewModel.submitAddPatternRule() },
            onDismissRequest = viewModel::closeAddPatternDialog
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RulesScreenContent(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    countryCodeRules: List<CountryCodeRuleEntity>,
    patternRules: List<PatternRuleEntity>,
    patternFilter: PatternFilter,
    onPatternFilterSelected: (PatternFilter) -> Unit,
    onToggleCountryCodeRule: (CountryCodeRuleEntity) -> Unit,
    onDeleteCountryCodeRule: (CountryCodeRuleEntity) -> Unit,
    onAddCountryCodeClick: () -> Unit,
    onTogglePatternRule: (PatternRuleEntity) -> Unit,
    onDeletePatternRule: (PatternRuleEntity) -> Unit,
    onAddPatternRuleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Call Blocking Rules",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { onTabSelected(0) },
                    text = {
                        BadgedBox(
                            badge = {
                                if (countryCodeRules.isNotEmpty()) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ) {
                                        Text("${countryCodeRules.size}")
                                    }
                                }
                            }
                        ) {
                            Text(
                                text = "Country Codes",
                                fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Public,
                            contentDescription = "Country Codes Tab"
                        )
                    }
                )

                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { onTabSelected(1) },
                    text = {
                        BadgedBox(
                            badge = {
                                if (patternRules.isNotEmpty()) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.tertiary,
                                        contentColor = MaterialTheme.colorScheme.onTertiary
                                    ) {
                                        Text("${patternRules.size}")
                                    }
                                }
                            }
                        ) {
                            Text(
                                text = "Prefix & Suffix",
                                fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Tag,
                            contentDescription = "Pattern Rules Tab"
                        )
                    }
                )
            }

            when (selectedTabIndex) {
                0 -> CountryCodeRulesTab(
                    rules = countryCodeRules,
                    onToggleRule = onToggleCountryCodeRule,
                    onDeleteRule = onDeleteCountryCodeRule,
                    onAddCountryClick = onAddCountryCodeClick
                )

                1 -> PatternRulesTab(
                    rules = patternRules,
                    selectedFilter = patternFilter,
                    onFilterSelected = onPatternFilterSelected,
                    onToggleRule = onTogglePatternRule,
                    onDeleteRule = onDeletePatternRule,
                    onAddRuleClick = onAddPatternRuleClick
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RulesScreenContentPreview() {
    SmartCallBlockerTheme {
        RulesScreenContent(
            selectedTabIndex = 0,
            onTabSelected = {},
            countryCodeRules = listOf(
                CountryCodeRuleEntity(id = 1, countryCode = "+1", countryName = "United States", isEnabled = true),
                CountryCodeRuleEntity(id = 2, countryCode = "+44", countryName = "United Kingdom", isEnabled = false)
            ),
            patternRules = listOf(
                PatternRuleEntity(id = 1, pattern = "1800", type = RuleType.PREFIX, isEnabled = true, label = "Toll Free Spam")
            ),
            patternFilter = PatternFilter.ALL,
            onPatternFilterSelected = {},
            onToggleCountryCodeRule = {},
            onDeleteCountryCodeRule = {},
            onAddCountryCodeClick = {},
            onTogglePatternRule = {},
            onDeletePatternRule = {},
            onAddPatternRuleClick = {}
        )
    }
}
