package com.example.my_app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.example.my_app.util.Validators

@Composable
fun NewCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<Int?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yeni Kategori") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        if (it.length <= 20) {
                            name = it
                            error = null
                        }
                    },
                    label = { Text("Kategori adı") },
                    supportingText = {
                        Text(error?.let { stringResource(it) } ?: "${name.length}/20")
                    },
                    isError = error != null,
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val validationError = Validators.validateCategoryName(name)
                if (validationError != null) {
                    error = validationError
                } else {
                    onConfirm(name)
                }
            }) {
                Text("Ekle")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("İptal") }
        }
    )
}
