package com.example.my_app.ui.tasks.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.my_app.data.local.CategoryEntity
import com.example.my_app.model.FilterState
import com.example.my_app.model.TaskPriority
import com.example.my_app.model.TaskStatus

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterSheet(
    categories: List<CategoryEntity>,
    currentFilters: FilterState,
    onApply: (FilterState) -> Unit,
    onDismiss: () -> Unit
) {
    var draftCategoryId by remember { mutableStateOf(currentFilters.categoryId) }
    var draftStatus by remember { mutableStateOf(currentFilters.status) }
    var draftPriority by remember { mutableStateOf(currentFilters.priority) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Filtrele", style = MaterialTheme.typography.titleLarge)

            Text("Kategori", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = draftCategoryId == null,
                    onClick = { draftCategoryId = null },
                    label = { Text("Tümü") }
                )
                categories.forEach { category ->
                    FilterChip(
                        selected = draftCategoryId == category.id,
                        onClick = { draftCategoryId = category.id },
                        label = { Text(category.name) }
                    )
                }
            }

            Text("Durum", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = draftStatus == null,
                    onClick = { draftStatus = null },
                    label = { Text("Tümü") }
                )
                TaskStatus.entries.forEach { status ->
                    FilterChip(
                        selected = draftStatus == status,
                        onClick = { draftStatus = status },
                        label = { Text(status.displayName) }
                    )
                }
            }

            Text("Önem Derecesi", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = draftPriority == null,
                    onClick = { draftPriority = null },
                    label = { Text("Tümü") }
                )
                TaskPriority.entries.forEach { priority ->
                    FilterChip(
                        selected = draftPriority == priority,
                        onClick = { draftPriority = priority },
                        label = { Text(priority.displayName) }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        draftCategoryId = null
                        draftStatus = null
                        draftPriority = null
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Temizle")
                }
                Button(
                    onClick = {
                        onApply(FilterState(draftCategoryId, draftStatus, draftPriority))
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Uygula")
                }
            }
        }
    }
}
