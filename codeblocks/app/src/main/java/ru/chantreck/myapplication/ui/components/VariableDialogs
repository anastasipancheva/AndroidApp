package ru.chantreck.myapplication.ui.components
// настя надо разделить этот диалогс на 2/3/4 разных мб??
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import ru.chantreck.myapplication.model.VariableData
import ru.chantreck.myapplication.model.*
import ru.chantreck.myapplication.ui.theme.Dimens

@Composable
fun SetVarDialog(
    variables: Map<String, VariableData>,
    onConfirm: (String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var varName by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("int") }
    var isNewVariable by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }

    val types = listOf("int", "float", "boolean", "string")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(Dimens.CornerRadius16),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = Dimens.Max)
        ) {
            Column(
                modifier = Modifier
                    .padding(Dimens.PaddingMedium)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmallMedium)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isNewVariable,
                        onCheckedChange = {
                            isNewVariable = it
                            if (!it) varName = ""
                        }
                    )
                    Text(
                        text = stringResource(R.string.label_create_new_variable),
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                if (isNewVariable) {
                    OutlinedTextField(
                        value = varName,
                        onValueChange = { varName = it },
                        label = { Text(stringResource(R.string.label_new_variable_name)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { typeExpanded = !typeExpanded }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Dimens.PaddingMedium),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.label_type, selectedType),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }

                    DropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        types.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    selectedType = type
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { expanded = !expanded }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Dimens.PaddingMedium),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (varName.isEmpty()) stringResource(R.string.label_select_variable) else varName,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        variables.keys.forEach { key ->
                            DropdownMenuItem(
                                text = { Text("$key (${variables[key]?.type})") },
                                onClick = {
                                    varName = key
                                    selectedType = variables[key]?.type ?: "int"
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text(stringResource(R.string.label_value_or_expression)) },
                    placeholder = { Text("111, mb x+1, \"hello\", true, everythink you want") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (varName.isNotEmpty() && value.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.result_preview, "$varName = $value"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.label_cancel))
                    }
                    Spacer(modifier = Modifier.width(Dimens.PaddingSmall))
                    Button(
                        onClick = {
                            if (varName.isNotEmpty() && value.isNotEmpty()) {
                                onConfirm(varName, value, selectedType)
                            }
                        },
                        enabled = varName.isNotEmpty() && value.isNotEmpty()
                    ) {
                        Text(stringResource(R.string.label_create))
                    }
                }
            }
        }
    }
}

@Composable
fun MultiVarDialog(
    onConfirm: (List<VariableDefinition>) -> Unit,
    onDismiss: () -> Unit
) {
    var variables by remember { mutableStateOf(listOf(VariableDefinition("", "int", "0"))) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(Dimens.CornerRadius16),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = Dimens.Max)
        ) {
            Column(
                modifier = Modifier
                    .padding(Dimens.PaddingMedium)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.label_define_multiple_variables),
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.padding(Dimens.PaddingMedium))

                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    itemsIndexed(variables) { index, variable ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = Dimens.PaddingSmallest),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = variable.type,
                                onValueChange = { newType ->
                                    variables = variables.toMutableList().apply {
                                        this[index] = variable.copy(type = newType)
                                    }
                                },
                                label = { Text(stringResource(R.string.label_type)) },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.padding(Dimens.PaddingSmall))

                            OutlinedTextField(
                                value = variable.name,
                                onValueChange = { newName ->
                                    variables = variables.toMutableList().apply {
                                        this[index] = variable.copy(name = newName)
                                    }
                                },
                                label = { Text(stringResource(R.string.label_name)) },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.padding(Dimens.PaddingSmall))

                            OutlinedTextField(
                                value = variable.value,
                                onValueChange = { newValue ->
                                    variables = variables.toMutableList().apply {
                                        this[index] = variable.copy(value = newValue)
                                    }
                                },
                                label = { Text(stringResource(R.string.label_value)) },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.padding(Dimens.PaddingSmall))

                            IconButton(
                                onClick = {
                                    if (variables.size > 1) {
                                        variables = variables.toMutableList().apply {
                                            removeAt(index)
                                        }
                                    }
                                },
                                enabled = variables.size > 1
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(R.string.label_remove_variable)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.PaddingSmall))

                Button(
                    onClick = {
                        variables = variables + VariableDefinition("", "int", "0")
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.label_add_variable)
                    )
                    Spacer(modifier = Modifier.width(Dimens.PaddingSmallest))
                    Text(stringResource(R.string.label_add_variable))
                }

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
                            val validVariables = variables.filter {
                                it.name.isNotBlank()
                            }
                            if (validVariables.isNotEmpty()) {
                                onConfirm(validVariables)
                            }
                        },
                        enabled = variables.any { it.name.isNotBlank() }
                    ) {
                        Text(stringResource(R.string.label_confirm))
                    }
                }
            }
        }
    }
}
