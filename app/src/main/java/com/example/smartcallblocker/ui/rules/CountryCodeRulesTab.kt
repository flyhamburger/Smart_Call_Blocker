package com.example.smartcallblocker.ui.rules

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.ui.unit.sp
import com.example.smartcallblocker.data.local.CountryCodeHelper
import com.example.smartcallblocker.data.local.entity.CountryCodeRuleEntity
import com.example.smartcallblocker.ui.theme.SmartCallBlockerTheme

@Composable
fun CountryCodeRulesTab(
    rules: List<CountryCodeRuleEntity>,
    onToggleRule: (CountryCodeRuleEntity) -> Unit,
    onDeleteRule: (CountryCodeRuleEntity) -> Unit,
    onAddCountryClick: () -> Unit,
    isBlockAllInternationalActive: Boolean = false,
    onToggleBlockAllInternational: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = 16.dp,
                end = 16.dp,
                bottom = 88.dp // Space for ExtendedFAB
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Featured Card: Block All International Calls (+)
            item {
                FeaturedBlockAllInternationalCard(
                    isActive = isBlockAllInternationalActive,
                    onToggle = { onToggleBlockAllInternational?.invoke() }
                )
            }

            if (rules.isEmpty()) {
                item {
                    EmptyCountryCodeRulesView(
                        onAddCountryClick = onAddCountryClick,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            } else {
                items(
                    items = rules,
                    key = { it.id }
                ) { rule ->
                    CountryCodeRuleItemCard(
                        rule = rule,
                        onToggle = { onToggleRule(rule) },
                        onDelete = { onDeleteRule(rule) }
                    )
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = onAddCountryClick,
            icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
            text = { Text("Add Country Code") },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}

@Composable
fun FeaturedBlockAllInternationalCard(
    isActive: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isActive) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainerHigh
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🌐",
                fontSize = 36.sp,
                modifier = Modifier.padding(end = 12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Block All International Calls (+)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Automatically block any caller starting with '+' or '00'",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isActive) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outlineVariant
                    }
                ) {
                    Text(
                        text = if (isActive) "Active (Blocking All + / 00)" else "Disabled",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isActive) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Switch(
                checked = isActive,
                onCheckedChange = { onToggle() },
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
fun CountryCodeRuleItemCard(
    rule: CountryCodeRuleEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val countryInfo = CountryCodeHelper.findByCode(rule.countryCode).firstOrNull {
        it.name.equals(rule.countryName, ignoreCase = true)
    } ?: CountryCodeHelper.findByCode(rule.countryCode).firstOrNull()

    val flagEmoji = countryInfo?.flagEmoji ?: "🌐"

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
            Text(
                text = flagEmoji,
                fontSize = 32.sp,
                modifier = Modifier.padding(end = 12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = rule.countryName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = rule.countryCode,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
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
                    contentDescription = "Delete country rule",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun EmptyCountryCodeRulesView(
    onAddCountryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier
            .padding(vertical = 12.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Public,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "No Specific Country Rules",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Add specific country codes (e.g. +1, +44) to block calls from individual countries.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onAddCountryClick,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Add Country Code")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CountryCodeRulesTabPreview() {
    SmartCallBlockerTheme {
        CountryCodeRulesTab(
            rules = listOf(
                CountryCodeRuleEntity(id = 1, countryCode = "+1", countryName = "United States", isEnabled = true),
                CountryCodeRuleEntity(id = 2, countryCode = "+44", countryName = "United Kingdom", isEnabled = false)
            ),
            onToggleRule = {},
            onDeleteRule = {},
            onAddCountryClick = {},
            isBlockAllInternationalActive = true,
            onToggleBlockAllInternational = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyCountryCodeRulesViewPreview() {
    SmartCallBlockerTheme {
        EmptyCountryCodeRulesView(onAddCountryClick = {})
    }
}
