package com.example.smartcallblocker.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smartcallblocker.data.local.entity.CountryCodeRuleEntity
import com.example.smartcallblocker.data.local.entity.PatternRuleEntity
import com.example.smartcallblocker.data.local.entity.RuleType
import com.example.smartcallblocker.ui.components.CallScreeningStatusBanner
import com.example.smartcallblocker.ui.history.CallHistoryScreenContent
import com.example.smartcallblocker.ui.history.CallHistoryViewModel
import com.example.smartcallblocker.ui.rules.AddPatternRuleDialog
import com.example.smartcallblocker.ui.rules.CountryCodeRulesTab
import com.example.smartcallblocker.ui.rules.CountryPickerDialog
import com.example.smartcallblocker.ui.rules.PatternFilter
import com.example.smartcallblocker.ui.rules.PatternRulesTab
import com.example.smartcallblocker.ui.rules.RulesViewModel

enum class MainTab(
    val title: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
) {
    COUNTRY_CODES("Country Codes", Icons.Outlined.Public, Icons.Filled.Public),
    LOCAL_PATTERNS("Local Patterns", Icons.Outlined.Tag, Icons.Filled.Tag),
    CALL_HISTORY("Call History", Icons.Outlined.History, Icons.Filled.History)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    rulesViewModel: RulesViewModel,
    callHistoryViewModel: CallHistoryViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.COUNTRY_CODES) }

    val countryCodeRules by rulesViewModel.countryCodeRules.collectAsStateWithLifecycle()
    val patternRules by rulesViewModel.filteredPatternRules.collectAsStateWithLifecycle()
    val allPatternRules by rulesViewModel.patternRules.collectAsStateWithLifecycle()
    val patternFilter by rulesViewModel.patternFilter.collectAsStateWithLifecycle()

    val isCountryPickerOpen by rulesViewModel.isCountryPickerOpen.collectAsStateWithLifecycle()
    val countrySearchQuery by rulesViewModel.countrySearchQuery.collectAsStateWithLifecycle()

    val isAddPatternDialogOpen by rulesViewModel.isAddPatternDialogOpen.collectAsStateWithLifecycle()
    val patternInput by rulesViewModel.patternInput.collectAsStateWithLifecycle()
    val ruleTypeInput by rulesViewModel.ruleTypeInput.collectAsStateWithLifecycle()
    val labelInput by rulesViewModel.labelInput.collectAsStateWithLifecycle()
    val patternInputError by rulesViewModel.patternInputError.collectAsStateWithLifecycle()

    val blockedCallLogs by callHistoryViewModel.blockedCallLogs.collectAsStateWithLifecycle()
    val showClearDialog by callHistoryViewModel.showClearConfirmDialog.collectAsStateWithLifecycle()

    MainScreenContent(
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it },
        countryCodeRules = countryCodeRules,
        patternRules = patternRules,
        allPatternRulesCount = allPatternRules.size,
        blockedCallCount = blockedCallLogs.size,
        patternFilter = patternFilter,
        onPatternFilterSelected = rulesViewModel::setPatternFilter,
        onToggleCountryCodeRule = rulesViewModel::toggleCountryCodeRule,
        onDeleteCountryCodeRule = rulesViewModel::deleteCountryCodeRule,
        onAddCountryCodeClick = rulesViewModel::openCountryPicker,
        onTogglePatternRule = rulesViewModel::togglePatternRule,
        onDeletePatternRule = rulesViewModel::deletePatternRule,
        onAddPatternRuleClick = {
            val initialType = when (patternFilter) {
                PatternFilter.SUFFIX -> RuleType.SUFFIX
                else -> RuleType.PREFIX
            }
            rulesViewModel.openAddPatternDialog(initialType)
        },
        blockedCallLogs = blockedCallLogs,
        showClearDialog = showClearDialog,
        onClearHistoryClick = callHistoryViewModel::showClearConfirmationDialog,
        onConfirmClearHistory = callHistoryViewModel::clearAllLogs,
        onDismissClearDialog = callHistoryViewModel::dismissClearConfirmationDialog,
        onDeleteLog = callHistoryViewModel::deleteLog,
        modifier = modifier
    )

    if (isCountryPickerOpen) {
        CountryPickerDialog(
            searchQuery = countrySearchQuery,
            onSearchQueryChange = rulesViewModel::updateCountrySearchQuery,
            onCountrySelected = rulesViewModel::addCountryCodeRule,
            onDismissRequest = rulesViewModel::closeCountryPicker
        )
    }

    if (isAddPatternDialogOpen) {
        AddPatternRuleDialog(
            patternInput = patternInput,
            ruleTypeInput = ruleTypeInput,
            labelInput = labelInput,
            errorMessage = patternInputError,
            onPatternChange = rulesViewModel::updatePatternInput,
            onRuleTypeChange = rulesViewModel::updateRuleTypeInput,
            onLabelChange = rulesViewModel::updateLabelInput,
            onConfirm = { rulesViewModel.submitAddPatternRule() },
            onDismissRequest = rulesViewModel::closeAddPatternDialog
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContent(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    countryCodeRules: List<CountryCodeRuleEntity>,
    patternRules: List<PatternRuleEntity>,
    allPatternRulesCount: Int,
    blockedCallCount: Int,
    patternFilter: PatternFilter,
    onPatternFilterSelected: (PatternFilter) -> Unit,
    onToggleCountryCodeRule: (CountryCodeRuleEntity) -> Unit,
    onDeleteCountryCodeRule: (CountryCodeRuleEntity) -> Unit,
    onAddCountryCodeClick: () -> Unit,
    onTogglePatternRule: (PatternRuleEntity) -> Unit,
    onDeletePatternRule: (PatternRuleEntity) -> Unit,
    onAddPatternRuleClick: () -> Unit,
    blockedCallLogs: List<com.example.smartcallblocker.data.local.entity.BlockedCallLogEntity>,
    showClearDialog: Boolean,
    onClearHistoryClick: () -> Unit,
    onConfirmClearHistory: () -> Unit,
    onDismissClearDialog: () -> Unit,
    onDeleteLog: (com.example.smartcallblocker.data.local.entity.BlockedCallLogEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isExpanded = maxWidth >= 600.dp

        if (isExpanded) {
            // Expanded Layout: Permanent Navigation Rail on left + Main Content on right
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail(
                    modifier = Modifier.fillMaxHeight(),
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Smart Blocker",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp)
                    )

                    MainTab.entries.forEach { tab ->
                        val badgeCount = when (tab) {
                            MainTab.COUNTRY_CODES -> countryCodeRules.size
                            MainTab.LOCAL_PATTERNS -> allPatternRulesCount
                            MainTab.CALL_HISTORY -> blockedCallCount
                        }

                        NavigationRailItem(
                            selected = selectedTab == tab,
                            onClick = { onTabSelected(tab) },
                            icon = {
                                TabIconWithBadge(
                                    tab = tab,
                                    isSelected = selectedTab == tab,
                                    badgeCount = badgeCount
                                )
                            },
                            label = { Text(tab.title) }
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    TopAppBar(
                        title = {
                            Text(
                                text = selectedTab.title,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        actions = {
                            if (selectedTab == MainTab.CALL_HISTORY && blockedCallLogs.isNotEmpty()) {
                                IconButton(onClick = onClearHistoryClick) {
                                    Icon(
                                        imageVector = Icons.Outlined.DeleteSweep,
                                        contentDescription = "Clear Call History",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    CallScreeningStatusBanner()

                    Box(modifier = Modifier.weight(1f)) {
                        TabContent(
                            selectedTab = selectedTab,
                            countryCodeRules = countryCodeRules,
                            patternRules = patternRules,
                            patternFilter = patternFilter,
                            onPatternFilterSelected = onPatternFilterSelected,
                            onToggleCountryCodeRule = onToggleCountryCodeRule,
                            onDeleteCountryCodeRule = onDeleteCountryCodeRule,
                            onAddCountryCodeClick = onAddCountryCodeClick,
                            onTogglePatternRule = onTogglePatternRule,
                            onDeletePatternRule = onDeletePatternRule,
                            onAddPatternRuleClick = onAddPatternRuleClick,
                            blockedCallLogs = blockedCallLogs,
                            showClearDialog = showClearDialog,
                            onClearHistoryClick = onClearHistoryClick,
                            onConfirmClearHistory = onConfirmClearHistory,
                            onDismissClearDialog = onDismissClearDialog,
                            onDeleteLog = onDeleteLog
                        )
                    }
                }
            }
        } else {
            // Compact Layout (Phones): Scaffold with Bottom Navigation Bar
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = selectedTab.title,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        actions = {
                            if (selectedTab == MainTab.CALL_HISTORY && blockedCallLogs.isNotEmpty()) {
                                IconButton(onClick = onClearHistoryClick) {
                                    Icon(
                                        imageVector = Icons.Outlined.DeleteSweep,
                                        contentDescription = "Clear Call History",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ) {
                        MainTab.entries.forEach { tab ->
                            val badgeCount = when (tab) {
                                MainTab.COUNTRY_CODES -> countryCodeRules.size
                                MainTab.LOCAL_PATTERNS -> allPatternRulesCount
                                MainTab.CALL_HISTORY -> blockedCallCount
                            }

                            NavigationBarItem(
                                selected = selectedTab == tab,
                                onClick = { onTabSelected(tab) },
                                icon = {
                                    TabIconWithBadge(
                                        tab = tab,
                                        isSelected = selectedTab == tab,
                                        badgeCount = badgeCount
                                    )
                                },
                                label = { Text(tab.title) }
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    CallScreeningStatusBanner()

                    Box(modifier = Modifier.weight(1f)) {
                        TabContent(
                            selectedTab = selectedTab,
                            countryCodeRules = countryCodeRules,
                            patternRules = patternRules,
                            patternFilter = patternFilter,
                            onPatternFilterSelected = onPatternFilterSelected,
                            onToggleCountryCodeRule = onToggleCountryCodeRule,
                            onDeleteCountryCodeRule = onDeleteCountryCodeRule,
                            onAddCountryCodeClick = onAddCountryCodeClick,
                            onTogglePatternRule = onTogglePatternRule,
                            onDeletePatternRule = onDeletePatternRule,
                            onAddPatternRuleClick = onAddPatternRuleClick,
                            blockedCallLogs = blockedCallLogs,
                            showClearDialog = showClearDialog,
                            onClearHistoryClick = onClearHistoryClick,
                            onConfirmClearHistory = onConfirmClearHistory,
                            onDismissClearDialog = onDismissClearDialog,
                            onDeleteLog = onDeleteLog
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TabIconWithBadge(
    tab: MainTab,
    isSelected: Boolean,
    badgeCount: Int
) {
    BadgedBox(
        badge = {
            if (badgeCount > 0) {
                Badge(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text("$badgeCount")
                }
            }
        }
    ) {
        Icon(
            imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
            contentDescription = tab.title
        )
    }
}

@Composable
private fun TabContent(
    selectedTab: MainTab,
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
    blockedCallLogs: List<com.example.smartcallblocker.data.local.entity.BlockedCallLogEntity>,
    showClearDialog: Boolean,
    onClearHistoryClick: () -> Unit,
    onConfirmClearHistory: () -> Unit,
    onDismissClearDialog: () -> Unit,
    onDeleteLog: (com.example.smartcallblocker.data.local.entity.BlockedCallLogEntity) -> Unit
) {
    when (selectedTab) {
        MainTab.COUNTRY_CODES -> {
            CountryCodeRulesTab(
                rules = countryCodeRules,
                onToggleRule = onToggleCountryCodeRule,
                onDeleteRule = onDeleteCountryCodeRule,
                onAddCountryClick = onAddCountryCodeClick
            )
        }

        MainTab.LOCAL_PATTERNS -> {
            PatternRulesTab(
                rules = patternRules,
                selectedFilter = patternFilter,
                onFilterSelected = onPatternFilterSelected,
                onToggleRule = onTogglePatternRule,
                onDeleteRule = onDeletePatternRule,
                onAddRuleClick = onAddPatternRuleClick
            )
        }

        MainTab.CALL_HISTORY -> {
            CallHistoryScreenContent(
                logs = blockedCallLogs,
                showClearDialog = showClearDialog,
                onClearHistoryClick = onClearHistoryClick,
                onConfirmClearHistory = onConfirmClearHistory,
                onDismissClearDialog = onDismissClearDialog,
                onDeleteLog = onDeleteLog
            )
        }
    }
}
