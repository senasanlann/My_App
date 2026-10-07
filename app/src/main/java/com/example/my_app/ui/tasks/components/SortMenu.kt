package com.example.my_app.ui.tasks.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.my_app.model.SortOption

@Composable
fun SortMenu(
    selected: SortOption,
    onSelect: (SortOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(Icons.Filled.Sort, contentDescription = "Sırala")
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        SortOption.entries.forEach { option ->
            DropdownMenuItem(
                text = { Text(option.displayName) },
                onClick = {
                    onSelect(option)
                    expanded = false
                },
                trailingIcon = {
                    if (option == selected) {
                        Icon(Icons.Filled.Check, contentDescription = null)
                    }
                }
            )
        }
    }
}
