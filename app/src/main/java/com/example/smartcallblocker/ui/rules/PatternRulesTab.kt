package com.example.smartcallblocker.ui.rules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smartcallblocker.data.local.entity.PatternRuleEntity
import com.example.smartcallblocker.data.local.entity.RuleType
import com.example.smartcallblocker.ui.theme.SmartCallBlockerTheme

@Composable
fun PatternRulesTab(
    rules: List<PatternRuleEntity>,
    selectedFilter: PatternFilter,
    onFilterSelected: (PatternFilter) -> Unit,
    onToggleRule: (PatternRuleEntity) -> Unit,
    onDeleteRule: (PatternRuleEntity) -> Unit,
    onAddRuleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == PatternFilter.ALL,
                    onClick = { onFilterSelected(PatternFilter.ALL) },
                    label = { Text("All Rules") }
                )
                FilterChip(
                    selected = selectedFilter == PatternFilter.PREFIX,
                    onClick = { onFilterSelected(PatternFilter.PREFIX) },
                    label = { Text("Prefix Rules") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
                FilterChip(
                    selected = selectedFilter == PatternFilter.SUFFIX,
                    onClick = { onFilterSelected(PatternFilter.SUFFIX) },
                    label = { Text("Suffix Rules") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                )
            }

            if (rules.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyPatternRulesView(
                        filter = selectedFilter,
                        onAddRuleClick = onAddRuleClick
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        top = 8.dp,
                        end = 16.dp,
                        bottom = 88.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = rules,
                        key = { it.id }
                    ) { rule ->
                        PatternRuleItemCard(
                            rule = rule,
                            onToggle = { onToggleRule(rule) },
                            onDelete = { onDeleteRule(rule) }
                        )
                    }
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = onAddRuleClick,
            icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
            text = { Text("Add Rule") },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}

@Composable
fun PatternRuleItemCard(
    rule: PatternRuleEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPrefix = rule.type == RuleType.PREFIX
    val typeBgColor = if (isPrefix) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.tertiaryContainer
    val typeTextColor = if (isPrefix) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onTertiaryContainer

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = rule.pattern,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = typeBgColor
                    ) {
                        Text(
                            text = if (isPrefix) "PREFIX" else "SUFFIX",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = typeTextColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    rule.label?.takeIf { it.isNotBlank() }?.let { label ->
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Text(
                        text = if (rule.isEnabled) "Active" else "Disabled",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (rule.isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Switch(
                checked = rule.isEnabled,
                onCheckedChange = { onToggle() },
                modifier = Modifier.padding(end = 4.dp)
            )

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete pattern rule",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun EmptyPatternRulesView(
    filter: PatternFilter,
    onAddRuleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier
            .padding(24.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer,
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Tag,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val filterText = when (filter) {
                PatternFilter.ALL -> "Pattern"
                PatternFilter.PREFIX -> "Prefix"
                PatternFilter.SUFFIX -> "Suffix"
            }

            Text(
                text = "No $filterText Rules",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Block calls matching specific starting numbers (Prefix) or ending digits (Suffix).",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            androidx.compose.material3.Button(
                onClick = onAddRuleClick,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Add Rule")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PatternRulesTabPreview() {
    SmartCallBlockerTheme {
        PatternRulesTab(
            rules = listOf(
                PatternRuleEntity(id = 1, pattern = "1800", type = RuleType.PREFIX, isEnabled = true, label = "Toll Free Spam"),
                PatternRuleEntity(id = 2, pattern = "9999", type = RuleType.SUFFIX, isEnabled = false, label = "Robocall Suffix")
            ),
            selectedFilter = PatternFilter.ALL,
            onFilterSelected = {},
            onToggleRule = {},
            onDeleteRule = {},
            onAddRuleClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyPatternRulesViewPreview() {
    SmartCallBlockerTheme {
        EmptyPatternRulesView(
            filter = PatternFilter.ALL,
            onAddRuleClick = {}
        )
    }
}
