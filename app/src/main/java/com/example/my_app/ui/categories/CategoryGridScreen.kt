package com.example.my_app.ui.categories

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.my_app.GorevlerimApp
import com.example.my_app.ui.categories.components.AddCategoryCard
import com.example.my_app.ui.categories.components.CategoryCard
import com.example.my_app.ui.components.ConfirmDialog
import com.example.my_app.ui.components.EmptyView
import com.example.my_app.ui.components.ErrorView
import com.example.my_app.ui.components.NewCategoryDialog
import com.example.my_app.ui.theme.EyebrowRed
import com.example.my_app.ui.theme.EyebrowRedDark
import com.example.my_app.util.CategoryVisuals
import com.example.my_app.util.DateUtils

@Composable
fun CategoryGridScreen(
    onCategoryClick: (categoryId: Long, categoryName: String) -> Unit,
    onAddClick: () -> Unit = {},
    viewModel: CategoryGridViewModel = viewModel(
        factory = CategoryGridViewModelFactory(
            sessionManager = (LocalContext.current.applicationContext as GorevlerimApp).container.sessionManager,
            categoryRepository = (LocalContext.current.applicationContext as GorevlerimApp).container.categoryRepository,
            taskRepository = (LocalContext.current.applicationContext as GorevlerimApp).container.taskRepository
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showNewCategoryDialog by remember { mutableStateOf(false) }
    var categoryToDelete by remember { mutableStateOf<CategoryGridItem?>(null) }
    val todayLabel = remember { DateUtils.formatTodayHeader() }
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                shape = RoundedCornerShape(18.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = if (isDark) MaterialTheme.colorScheme.onPrimary else Color.White,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Yeni görev",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GÖREVLERİM",
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 1.6.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) EyebrowRedDark else EyebrowRed
                    )
                    Text(
                        text = todayLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (uiState.userName.isNotBlank()) "Merhaba, ${uiState.userName}." else "Merhaba.",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 28.sp,
                        lineHeight = 34.sp
                    ),
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${uiState.totalTaskCount} görev, ${uiState.categories.size} kategori. Her şey yerli yerinde.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                uiState.errorMessage != null -> {
                    ErrorView(
                        message = uiState.errorMessage ?: "Bir hata oluştu",
                        onRetry = {}
                    )
                }
                uiState.categories.isEmpty() -> {
                    EmptyView(
                        icon = Icons.Filled.Category,
                        title = "Henüz kategori yok",
                        message = "İlk kategorini oluşturarak başla",
                        actionLabel = "Kategori Oluştur",
                        onAction = { showNewCategoryDialog = true }
                    )
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .padding(bottom = 68.dp),
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        itemsIndexed(uiState.categories) { index, category ->
                            val visual = CategoryVisuals.of(category.name, fallbackIndex = index)
                            CategoryCard(
                                name = category.name,
                                taskCount = category.taskCount,
                                icon = visual.icon,
                                backgroundIndex = index,
                                imagePath = category.imagePath,
                                emoji = category.emoji,
                                onClick = { onCategoryClick(category.id, category.name) },
                                onLongClick = { categoryToDelete = category }
                            )
                        }
                        item {
                            AddCategoryCard(onClick = { showNewCategoryDialog = true })
                        }
                    }
                }
            }
        }
    }

    if (showNewCategoryDialog) {
        NewCategoryDialog(
            onDismiss = { showNewCategoryDialog = false },
            onConfirm = { name ->
                viewModel.addCategory(name)
                showNewCategoryDialog = false
            }
        )
    }

    categoryToDelete?.let { category ->
        ConfirmDialog(
            title = "Kategoriyi Sil",
            message = "\"${category.name}\" kategorisini silmek istediğine emin misin? Bu kategorideki görevler \"Kişisel\" kategorisine taşınacaktır.",
            confirmText = "Sil",
            onConfirm = {
                viewModel.deleteCategory(category.id)
                categoryToDelete = null
            },
            onDismiss = { categoryToDelete = null }
        )
    }
}
