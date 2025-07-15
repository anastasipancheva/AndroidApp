package ru.chantreck.myapplication.ui.components

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.Dialog
import ru.chantreck.myapplication.R
import ru.chantreck.myapplication.model.VariableData
import ru.chantreck.myapplication.ui.theme.Dimens

@Composable
fun SortDialog(
    variables: Map<String, VariableData>,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val arrayVariables = variables.filter { it.value.isArray() }
    var selectedArray by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    if (arrayVariables.isEmpty()) {
        LaunchedEffect(Unit) {
            onDismiss()
        }
        return
    }

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
                    text = stringResource(R.string.dialog_title_bubble_sort),
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = stringResource(R.string.label_select_array_to_sort),
                    style = MaterialTheme.typography.bodyMedium
                )

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
                            text = if (selectedArray.isEmpty()) stringResource(R.string.label_select_array) else selectedArray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    }
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    arrayVariables.keys.forEach { arrayName ->
                        DropdownMenuItem(
                            text = {
                                Text("$arrayName [${arrayVariables[arrayName]?.getArraySize()}]")
                            },
                            onClick = {
                                selectedArray = arrayName
                                expanded = false
                            }
                        )
                    }
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
                            if (selectedArray.isNotEmpty()) {
                                onConfirm(selectedArray)
                            }
                        },
                        enabled = selectedArray.isNotEmpty()
                    ) {
                        Text(stringResource(R.string.label_sort))
                    }
                }
            }
        }
    }
}

@Composable
fun ArrSetDialog(
    variables: Map<String, VariableData>,
    onConfirm: (String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val arrayVariables = variables.filter { it.value.isArray() }
    var selectedArray by remember { mutableStateOf("") }
    var index by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    if (arrayVariables.isEmpty()) {
        LaunchedEffect(Unit) {
            onDismiss()
        }
        return
    }

    val errorSelectArray = stringResource(R.string.error_select_array)
    val errorEnterIndex = stringResource(R.string.error_enter_index)
    val errorEnterValue = stringResource(R.string.error_enter_value)

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
                    text = stringResource(R.string.dialog_title_set_array_element),
                    style = MaterialTheme.typography.titleLarge
                )

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
                            text = if (selectedArray.isEmpty()) stringResource(R.string.label_select_array) else selectedArray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    }
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    arrayVariables.keys.forEach { arrayName ->
                        DropdownMenuItem(
                            text = {
                                Text("$arrayName [${arrayVariables[arrayName]?.getArraySize()}]")
                            },
                            onClick = {
                                selectedArray = arrayName
                                expanded = false
                                errorMessage = null
                            }
                        )
                    }
                }

                OutlinedTextField(
                    value = index,
                    onValueChange = {
                        index = it
                        errorMessage = null
                    },
                    label = { Text(stringResource(R.string.label_index)) },
                    placeholder = { Text("0, 1, 2...") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = value,
                    onValueChange = {
                        value = it
                        errorMessage = null
                    },
                    label = { Text(stringResource(R.string.label_value)) },
                    placeholder = { Text("42, x+1, ...") },
                    modifier = Modifier.fillMaxWidth()
                )

                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
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
                            if (selectedArray.isEmpty()) {
                                errorMessage = errorSelectArray
                                return@Button
                            }
                            if (index.isEmpty()) {
                                errorMessage = errorEnterIndex
                                return@Button
                            }
                            if (value.isEmpty()) {
                                errorMessage = errorEnterValue
                                return@Button
                            }
                            onConfirm(selectedArray, index, value)
                        },
                        enabled = selectedArray.isNotEmpty() && index.isNotEmpty() && value.isNotEmpty()
                    ) {
                        Text(stringResource(R.string.label_create))
                    }
                }
            }
        }
    }
}

@Composable
fun ArrGetDialog(
    variables: Map<String, VariableData>,
    onConfirm: (String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedArray by remember { mutableStateOf("") }
    var index by remember { mutableStateOf("") }
    var variableName by remember { mutableStateOf("") }
    val arrayVariables = variables.filter { it.value.isArray() }

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
                    text = stringResource(R.string.dialog_title_get_array_element),
                    style = MaterialTheme.typography.titleLarge
                )

                OutlinedTextField(
                    value = selectedArray,
                    onValueChange = { selectedArray = it },
                    label = { Text(stringResource(R.string.label_array_name)) },
                    placeholder = { Text("myArray") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = index,
                    onValueChange = { index = it },
                    label = { Text(stringResource(R.string.label_index)) },
                    placeholder = { Text("0, 1, 2??") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = variableName,
                    onValueChange = { variableName = it },
                    label = { Text(stringResource(R.string.label_store_in_variable)) },
                    placeholder = { Text("x, result??)") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (arrayVariables.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.label_available_arrays),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    arrayVariables.keys.forEach { arrayName ->
                        Text(
                            text = arrayName,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
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
                            if (selectedArray.isNotEmpty() && index.isNotEmpty() && variableName.isNotEmpty()) {
                                onConfirm(selectedArray, index, variableName)
                            }
                        }
                    ) {
                        Text(stringResource(R.string.label_ok))
                    }
                }
            }
        }
    }
}

@Composable
fun ArrDialog(
    onConfirm: (String, List<Int>) -> Unit,
    onDismiss: () -> Unit
) {
    var arrayName by remember { mutableStateOf("") }
    var elements by remember { mutableStateOf(listOf("0")) }

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
                    .padding(Dimens.PaddingMedium)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.label_create_array),
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.padding(Dimens.PaddingMedium))

                OutlinedTextField(
                    value = arrayName,
                    onValueChange = { arrayName = it },
                    label = { Text(stringResource(R.string.label_array_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.padding(Dimens.PaddingMedium))

                Text(
                    text = stringResource(R.string.label_array_elements),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(Dimens.PaddingSmall))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .heightIn(max = Dimens.Max200)
                ) {
                    itemsIndexed(elements) { index, element ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = Dimens.PaddingSmallest),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = element,
                                onValueChange = { newValue ->
                                    elements = elements.toMutableList().apply {
                                        this[index] = newValue
                                    }
                                },
                                label = { Text(stringResource(R.string.label_element, index + 1)) },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.padding(Dimens.PaddingSmall))

                            IconButton(
                                onClick = {
                                    if (elements.size > 1) {
                                        elements = elements.toMutableList().apply {
                                            removeAt(index)
                                        }
                                    }
                                },
                                enabled = elements.size > 1
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(R.string.label_remove_element)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.PaddingSmall))

                Button(
                    onClick = {
                        elements = elements + "0"
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.label_add_element)
                    )
                    Spacer(modifier = Modifier.width(Dimens.PaddingSmallest))
                    Text(stringResource(R.string.label_add_element))
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
                            if (arrayName.isNotBlank()) {
                                val intElements = elements.map { it.toIntOrNull() ?: 0 }
                                onConfirm(arrayName, intElements)
                            }
                        },
                        enabled = arrayName.isNotBlank()
                    ) {
                        Text(stringResource(R.string.label_confirm))
                    }
                }
            }
        }
    }
}
