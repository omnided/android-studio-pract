package ua.edu.mobile.smartlife.ui.records

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ua.edu.mobile.smartlife.data.model.RecordType

/** Діалог «Новий запис»: вибір типу, поле значення з перевіркою, нотатка. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddRecordDialog(
    initialType: RecordType,
    onDismiss: () -> Unit,
    onConfirm: (RecordType, Double, String) -> Unit,
) {
    // rememberSaveable: введений текст не зникає після повороту екрана
    var type by rememberSaveable { mutableStateOf(initialType) }
    var valueText by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }
    val value = valueText.replace(',', '.').toDoubleOrNull()
    val isValueValid = value != null && value > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новий запис") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    RecordType.entries.forEach { option ->
                        FilterChip(
                            selected = type == option,
                            onClick = { type = option },
                            label = { Text(option.title) },
                        )
                    }
                }
                OutlinedTextField(
                    value = valueText,
                    onValueChange = { valueText = it },
                    label = { Text("Значення, ${type.unit}") },
                    singleLine = true,
                    isError = valueText.isNotEmpty() && !isValueValid,
                    supportingText = {
                        if (valueText.isNotEmpty() && !isValueValid) Text("Введіть додатне число")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Нотатка") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(type, value!!, note.trim()) },
                enabled = isValueValid,
            ) { Text("Зберегти") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Скасувати") } },
    )
}
