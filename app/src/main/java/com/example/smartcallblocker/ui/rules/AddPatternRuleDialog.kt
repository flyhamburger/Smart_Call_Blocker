package com.example.smartcallblocker.ui.rules

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smartcallblocker.data.local.entity.RuleType
import com.example.smartcallblocker.ui.theme.SmartCallBlockerTheme

@Composable
fun AddPatternRuleDialog(
    patternInput: String,
    ruleTypeInput: RuleType,
    labelInput: String,
    errorMessage: String?,
    onPatternChange: (String) -> Unit,
    onRuleTypeChange: (RuleType) -> Unit,
    onLabelChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = "Add Pattern Rule",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Rule Type",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterChip(
                        selected = ruleTypeInput == RuleType.PREFIX,
                        onClick = { onRuleTypeChange(RuleType.PREFIX) },
                        label = { Text("Prefix (Starts with)") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )

                    FilterChip(
                        selected = ruleTypeInput == RuleType.SUFFIX,
                        onClick = { onRuleTypeChange(RuleType.SUFFIX) },
                        label = { Text("Suffix (Ends with)") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = patternInput,
                    onValueChange = onPatternChange,
                    label = { Text("Pattern String") },
                    placeholder = { Text(if (ruleTypeInput == RuleType.PREFIX) "e.g. 1800, 0900" else "e.g. 9999, 0000") },
                    isError = errorMessage != null,
                    supportingText = {
                        if (errorMessage != null) {
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            Text("Digits or characters to match at ${ruleTypeInput.name.lowercase()}")
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = labelInput,
                    onValueChange = onLabelChange,
                    label = { Text("Label (Optional)") },
                    placeholder = { Text("e.g. Toll Free, Robocalls") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "Add Rule",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun AddPatternRuleDialogPreview() {
    SmartCallBlockerTheme {
        AddPatternRuleDialog(
            patternInput = "1800",
            ruleTypeInput = RuleType.PREFIX,
            labelInput = "Toll Free Spam",
            errorMessage = null,
            onPatternChange = {},
            onRuleTypeChange = {},
            onLabelChange = {},
            onConfirm = {},
            onDismissRequest = {}
        )
    }
}
