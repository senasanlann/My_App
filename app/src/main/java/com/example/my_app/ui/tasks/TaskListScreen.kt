package com.example.my_app.ui.tasks

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import com.example.my_app.model.FilterState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.my_app.GorevlerimApp
import com.example.my_app.ui.components.ConfirmDialog
import com.example.my_app.ui.components.EmojiPickerDialog
import com.example.my_app.ui.components.EmptyView
import com.example.my_app.ui.components.ErrorView
import com.example.my_app.ui.components.rememberImagePickerLauncher
import com.example.my_app.ui.tasks.components.FilterSheet
import com.example.my_app.ui.tasks.components.SortMenu
import com.example.my_app.ui.tasks.components.TaskItem
import com.example.my_app.ui.tasks.components.TaskSearchBar
import com.example.my_app.ui.theme.EyebrowRed
import com.example.my_app.util.ImageStorage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    onNavigateBack: () -> Unit = {},
    onTaskClick: (Long) -> Unit = {},
    onAddClick: () -> Unit = {},
    showDeletedMessage: Boolean = false,
    onDeletedMessageShown: () -> Unit = {},
    initialFilter: FilterState? = null,
    onInitialFilterConsumed: () -> Unit = {},
    viewModel: TaskListViewModel = viewModel(
        factory = TaskListViewModelFactory(
            sessionManager = (LocalContext.current.applicationContext as GorevlerimApp).container.sessionManager,
            taskRepository = (LocalContext.current.applicationContext as GorevlerimApp).container.taskRepository,
            categoryRepository = (LocalContext.current.applicationContext as GorevlerimApp).container.categoryRepository,
            settingsManager = (LocalContext.current.applicationContext as GorevlerimApp).container.settingsManager
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showFilterSheet by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var showVisualMenu by remember { mutableStateOf(false) }
    var showEmojiPicker by remember { mutableStateOf(false) }
    var showDeleteCategoryDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<TaskListItem?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    val activeCategory = uiState.filterState.categoryId
        ?.let { id -> uiState.categories.firstOrNull { it.id == id } }

    val pickCategoryImage = rememberImagePickerLauncher { uri ->
        val category = activeCategory ?: return@rememberImagePickerLauncher
        scope.launch {
            val path = ImageStorage.save(context, uri, "category_${category.id}")
            viewModel.updateCategoryImage(category.id, path)
        }
    }

    LaunchedEffect(showDeletedMessage) {
        if (showDeletedMessage) {
            snackbarHostState.showSnackbar("Görev silindi")
            onDeletedMessageShown()
        }
    }

    LaunchedEffect(initialFilter) {
        if (initialFilter != null) {
            viewModel.onFilterStateChange(initialFilter)
            onInitialFilterConsumed()
        }
    }

    val currentCategoryTitle = activeCategory?.name ?: "Tüm Görevler"
    val categoryDescription = when (currentCategoryTitle) {
        "Ev & Yaşam" -> "Evin için küçük, iyi gelen adımlar."
        "İş" -> "Odaklan, üret ve adım adım ilerle."
        "Kişisel" -> "Kendine ayırdığın en değerli zamanlar."
        "Sağlık" -> "Bedenine ve ruhuna iyi gelen anlar."
        "Alışveriş" -> "Eksikler listesi, ihtiyaçlar ve planlar."
        else -> "Düzenli bir gün, dingin bir zihin."
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                shape = RoundedCornerShape(18.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = if (isDark) MaterialTheme.colorScheme.onPrimary else Color.White,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 2.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Yeni görev", modifier = Modifier.size(24.dp))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Özel Üst Başlık Çubuğu: ← Kategoriler ve •••
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onNavigateBack)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Geri",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Kategoriler",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                if (activeCategory != null) {
                    Box {
                        IconButton(onClick = { showVisualMenu = true }) {
                            Icon(
                                imageVector = Icons.Outlined.MoreHoriz,
                                contentDescription = "Daha fazla",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        DropdownMenu(
                            expanded = showVisualMenu,
                            onDismissRequest = { showVisualMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Emoji seç") },
                                leadingIcon = { Text("😀") },
                                onClick = {
                                    showVisualMenu = false
                                    showEmojiPicker = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Fotoğraf seç") },
                                leadingIcon = { Icon(Icons.Outlined.CameraAlt, contentDescription = null) },
                                onClick = {
                                    showVisualMenu = false
                                    pickCategoryImage()
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Kategoriyi sil", color = MaterialTheme.colorScheme.error) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Delete,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = {
                                    showVisualMenu = false
                                    showDeleteCategoryDialog = true
                                }
                            )
                        }
                    }
                }
            }

            // Kategori Başlık ve Alt Başlığı
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 4.dp)
            ) {
                Text(
                    text = currentCategoryTitle,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 28.sp,
                        lineHeight = 34.sp
                    ),
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = categoryDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Arama ve Filtre Butonu Satırı
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TaskSearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = viewModel::onSearchQueryChange,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(10.dp))

                // Filtreleme Butonu
                BadgedBox(
                    badge = {
                        if (uiState.filterState.activeCount > 0) {
                            Badge(
                                containerColor = EyebrowRed,
                                contentColor = Color.White
                            ) {
                                Text("${uiState.filterState.activeCount}")
                            }
                        }
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f))
                            .clickable { showFilterSheet = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FilterList,
                            contentDescription = "Filtrele",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Aktif Filtre Hapları
            if (uiState.filterState.activeCount > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.filterState.categoryId?.let { categoryId ->
                        val name = uiState.categories.firstOrNull { it.id == categoryId }?.name ?: ""
                        FilterChipPill(label = name, onRemove = {
                            viewModel.onFilterStateChange(uiState.filterState.copy(categoryId = null))
                        })
                    }
                    uiState.filterState.status?.let { status ->
                        FilterChipPill(label = status.displayName, onRemove = {
                            viewModel.onFilterStateChange(uiState.filterState.copy(status = null))
                        })
                    }
                    uiState.filterState.priority?.let { priority ->
                        FilterChipPill(label = priority.displayName, onRemove = {
                            viewModel.onFilterStateChange(uiState.filterState.copy(priority = null))
                        })
                    }
                }
            }

            val hasActiveSearchOrFilter = uiState.searchQuery.isNotBlank() || uiState.filterState.activeCount > 0

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
                        onRetry = viewModel::retry
                    )
                }
                uiState.tasks.isEmpty() && hasActiveSearchOrFilter -> {
                    EmptyView(
                        icon = Icons.Outlined.SearchOff,
                        title = "Sonuç bulunamadı",
                        message = "Arama veya filtre kriterlerine uyan görev yok",
                        actionLabel = "Filtreleri temizle",
                        onAction = viewModel::clearAllFilters
                    )
                }
                uiState.tasks.isEmpty() -> {
                    EmptyView(
                        icon = Icons.Outlined.Checklist,
                        title = "Henüz görev yok",
                        message = "Yeni bir görev eklemek için sağ alttaki + butonuna dokun"
                    )
                }
                else -> {
                    // Görev Sayacı ve Sıralama
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 22.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val resultLabel = if (hasActiveSearchOrFilter && uiState.tasks.size < uiState.totalTaskCount) {
                            "${uiState.totalTaskCount} görevden ${uiState.tasks.size}'i gösteriliyor"
                        } else {
                            "${uiState.tasks.size} görev gösteriliyor"
                        }
                        Text(
                            text = resultLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        SortMenu(selected = uiState.sortOption, onSelect = viewModel::onSortOptionChange)
                    }

                    // Görevler Listesi
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 110.dp)
                    ) {
                        items(uiState.tasks, key = { it.id }) { task ->
                            TaskItem(
                                item = task,
                                onClick = { onTaskClick(task.id) },
                                onLongClick = { taskToDelete = task }
                            )
                        }
                        item {
                            Text(
                                text = "Her küçük adım, evini biraz daha sen yapar.",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        FilterSheet(
            categories = uiState.categories,
            currentFilters = uiState.filterState,
            onApply = { filters ->
                viewModel.onFilterStateChange(filters)
                showFilterSheet = false
            },
            onDismiss = { showFilterSheet = false }
        )
    }

    if (showEmojiPicker) {
        val category = activeCategory
        EmojiPickerDialog(
            onDismiss = { showEmojiPicker = false },
            onEmojiSelected = { emoji ->
                category?.let { viewModel.updateCategoryEmoji(it.id, emoji) }
            }
        )
    }

    taskToDelete?.let { task ->
        ConfirmDialog(
            title = "Görevi Sil",
            message = "\"${task.title}\" görevini silmek istediğine emin misin?",
            confirmText = "Sil",
            onConfirm = {
                viewModel.deleteTask(task.id)
                taskToDelete = null
            },
            onDismiss = { taskToDelete = null }
        )
    }

    if (showDeleteCategoryDialog && activeCategory != null) {
        ConfirmDialog(
            title = "Kategoriyi Sil",
            message = "\"${activeCategory.name}\" kategorisini silmek istediğine emin misin? Bu kategorideki görevler \"Kişisel\" kategorisine taşınacaktır.",
            confirmText = "Sil",
            onConfirm = {
                viewModel.deleteCategory(activeCategory.id)
                showDeleteCategoryDialog = false
                onNavigateBack()
            },
            onDismiss = { showDeleteCategoryDialog = false }
        )
    }
}

@Composable
private fun FilterChipPill(label: String, onRemove: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f))
            .clickable(onClick = onRemove)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.Close,
            contentDescription = "Kaldır",
            modifier = Modifier.size(13.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
