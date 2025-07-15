package ru.chantreck.myapplication.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import ru.chantreck.myapplication.R
import ru.chantreck.myapplication.model.CodeBlockData
import ru.chantreck.myapplication.model.VariableData
import ru.chantreck.myapplication.ui.theme.Dimens

@Composable
fun EditBlockDialog(
    currentText: String,
    block: CodeBlockData,
    onConfirm: (String, CodeBlockData) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(currentText) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(Dimens.CornerRadius16),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.PaddingMedium)
        ) {
            Column(
                modifier = Modifier
                    .padding(Dimens.PaddingMedium)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
            ) {
                Text(
                    text = stringResource(R.string.dialog_title_edit_block),
                    style = MaterialTheme.typography.titleLarge
                )

                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text(stringResource(R.string.label_block_text)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.label_cancel))
                    }
                    Spacer(modifier = Modifier.padding(Dimens.PaddingSmall))
                    Button(onClick = onDelete) {
                        Text(stringResource(R.string.label_delete))
                    }
                    Spacer(modifier = Modifier.padding(Dimens.PaddingSmall))
                    Button(onClick = { onConfirm(text, block) }) {
                        Text(stringResource(R.string.label_save))
                    }
                }
            }
        }
    }
}

@Composable
fun IfDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var leftValue by remember { mutableStateOf("") }
    var rightValue by remember { mutableStateOf("") }
    var selectedOperator by remember { mutableStateOf("==") }
    var operatorExpanded by remember { mutableStateOf(false) }

    val operators = listOf("==", "!=", "<", ">", "<=", ">=")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(Dimens.CornerRadius16),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.PaddingMedium)
        ) {
            Column(
                modifier = Modifier
                    .padding(Dimens.PaddingMedium)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
            ) {
                Text(
                    text = stringResource(R.string.dialog_title_create_condition),
                    style = MaterialTheme.typography.titleLarge
                )

                OutlinedTextField(
                    value = leftValue,
                    onValueChange = { leftValue = it },
                    label = { Text(stringResource(R.string.label_value_or_variable)) },
                    placeholder = { Text("x, 5, counter...") },
                    modifier = Modifier.fillMaxWidth()
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { operatorExpanded = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimens.PaddingMedium),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.label_operator, selectedOperator),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    }
                }

                DropdownMenu(
                    expanded = operatorExpanded,
                    onDismissRequest = { operatorExpanded = false }
                ) {
                    operators.forEach { operator ->
                        DropdownMenuItem(
                            text = { Text(operator) },
                            onClick = {
                                selectedOperator = operator
                                operatorExpanded = false
                            }
                        )
                    }
                }

                OutlinedTextField(
                    value = rightValue,
                    onValueChange = { rightValue = it },
                    label = { Text(stringResource(R.string.label_compare_with)) },
                    placeholder = { Text("y, 10, sum...") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (leftValue.isNotEmpty() && rightValue.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.label_result, leftValue, selectedOperator, rightValue),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.label_cancel))
                    }
                    Spacer(modifier = Modifier.padding(Dimens.PaddingSmall))
                    Button(
                        onClick = {
                            if (leftValue.isNotEmpty() && rightValue.isNotEmpty()) {
                                onConfirm("$leftValue $selectedOperator $rightValue")
                            }
                        },
                        enabled = leftValue.isNotEmpty() && rightValue.isNotEmpty()
                    ) {
                        Text(stringResource(R.string.label_create))
                    }
                }
            }
        }
    }
}

@Composable
fun PrintDialog(
    variables: Map<String, VariableData>,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var outputExpr by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(Dimens.CornerRadius16),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.PaddingMedium)
        ) {
            Column(
                modifier = Modifier
                    .padding(Dimens.PaddingMedium)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
            ) {
                Text(
                    text = stringResource(R.string.dialog_title_print_output),
                    style = MaterialTheme.typography.titleLarge
                )

                OutlinedTextField(
                    value = outputExpr,
                    onValueChange = { outputExpr = it },
                    label = { Text(stringResource(R.string.label_expression_or_variable)) },
                    placeholder = { Text("\"Hello World\", x, a+b*2") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (variables.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.label_available_variables, variables.keys.joinToString(", ")),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.label_cancel))
                    }
                    Spacer(modifier = Modifier.padding(Dimens.PaddingSmall))
                    Button(
                        onClick = {
                            if (outputExpr.isNotBlank()) {
                                onConfirm(outputExpr)
                                onDismiss()
                            }
                        },
                        enabled = outputExpr.isNotBlank()
                    ) {
                        Text(stringResource(R.string.label_create))
                    }
                }
            }
        }
    }
}

@Composable
fun WhileDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var condition by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(Dimens.CornerRadius16),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(Dimens.PaddingMedium)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.label_while_condition),
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.padding(Dimens.PaddingMedium))

                OutlinedTextField(
                    value = condition,
                    onValueChange = { condition = it },
                    label = { Text(stringResource(R.string.label_condition)) },
                    placeholder = { Text("e.g. i < 10") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.padding(Dimens.PaddingMedium))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.label_cancel))
                    }

                    Spacer(modifier = Modifier.padding(Dimens.PaddingSmall))

                    Button(
                        onClick = {
                            if (condition.isNotBlank()) {
                                onConfirm(condition)
                            }
                        },
                        enabled = condition.isNotBlank()
                    ) {
                        Text(stringResource(R.string.label_confirm))
                    }
                }
            }
        }
    }
}
