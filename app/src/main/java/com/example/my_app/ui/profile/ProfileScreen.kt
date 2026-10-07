package com.example.my_app.ui.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.HourglassTop
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.my_app.GorevlerimApp
import com.example.my_app.model.TaskStatus
import com.example.my_app.ui.components.ErrorView
import com.example.my_app.ui.components.rememberImagePickerLauncher
import com.example.my_app.ui.profile.components.StatCard
import com.example.my_app.ui.theme.EyebrowRed
import com.example.my_app.ui.theme.EyebrowRedDark
import com.example.my_app.util.ImageStorage
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onStatClick: (TaskStatus?) -> Unit = {},
    viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(
            sessionManager = (LocalContext.current.applicationContext as GorevlerimApp).container.sessionManager,
            taskRepository = (LocalContext.current.applicationContext as GorevlerimApp).container.taskRepository,
            userRepository = (LocalContext.current.applicationContext as GorevlerimApp).container.userRepository
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val initials = uiState.name.take(1).uppercase().ifBlank { "D" }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    val pickAvatar = rememberImagePickerLauncher { uri ->
        scope.launch {
            val path = ImageStorage.save(context, uri, "avatar")
            viewModel.updateAvatar(path)
            Toast.makeText(context, "Profil fotoğrafı güncellendi", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            onLogout()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            uiState.errorMessage != null -> {
                ErrorView(
                    message = uiState.errorMessage ?: "Bir hata oluştu",
                    onRetry = viewModel::retry,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 22.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Üst Başlık: Profilim
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Profilim",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontSize = 28.sp,
                                lineHeight = 34.sp
                            ),
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Profil Avatarı
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .clickable(onClick = pickAvatar),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(84.dp)
                                .clip(CircleShape)
                                .background(color = MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            if (uiState.avatarPath != null) {
                                AsyncImage(
                                    model = File(uiState.avatarPath!!),
                                    contentDescription = "Profil fotoğrafı",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Text(
                                    text = initials,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        // Kamera Rozeti
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CameraAlt,
                                contentDescription = "Fotoğrafı değiştir",
                                tint = if (isDark) MaterialTheme.colorScheme.onPrimary else Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Kullanıcı İsmi ve E-postası
                    Text(
                        text = uiState.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = uiState.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (uiState.memberSinceLabel.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = uiState.memberSinceLabel,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // "Bir bakışta senin düzenin"
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Bir bakışta senin düzenin",
                            style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2x2 İstatistik Kartları Izgarası
                    val card1Bg = if (isDark) Color(0xFF292C42) else Color(0xFFDFE2F7)
                    val card2Bg = if (isDark) Color(0xFF2E2822) else Color(0xFFEEE7DC)
                    val card3Bg = if (isDark) Color(0xFF37321F) else Color(0xFFF6EBCE)
                    val card4Bg = if (isDark) Color(0xFF213527) else Color(0xFFDFEFE3)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            count = uiState.totalCount,
                            label = "Toplam görev",
                            icon = Icons.Outlined.Layers,
                            containerColor = card1Bg,
                            modifier = Modifier.weight(1f),
                            onClick = { onStatClick(null) }
                        )
                        StatCard(
                            count = uiState.todoCount,
                            label = "Yapılacak",
                            icon = Icons.Outlined.Schedule,
                            containerColor = card2Bg,
                            modifier = Modifier.weight(1f),
                            onClick = { onStatClick(TaskStatus.TODO) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            count = uiState.inProgressCount,
                            label = "Devam ediyor",
                            icon = Icons.Outlined.HourglassTop,
                            containerColor = card3Bg,
                            modifier = Modifier.weight(1f),
                            onClick = { onStatClick(TaskStatus.IN_PROGRESS) }
                        )
                        StatCard(
                            count = uiState.doneCount,
                            label = "Tamamlandı",
                            icon = Icons.Outlined.CheckCircle,
                            containerColor = card4Bg,
                            modifier = Modifier.weight(1f),
                            onClick = { onStatClick(TaskStatus.DONE) }
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Çıkış Yap Butonu
                    val logoutBg = if (isDark) Color(0xFF3B242A) else Color(0xFFFCEBEB)
                    val logoutText = if (isDark) EyebrowRedDark else EyebrowRed

                    Button(
                        onClick = { showLogoutDialog = true },
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = logoutBg,
                            contentColor = logoutText
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Logout,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = logoutText
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Çıkış yap",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = logoutText
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Çıkış Yap") },
            text = { Text("Çıkış yapmak istiyor musun?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    viewModel.logout()
                }) {
                    Text("Çıkış Yap", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Vazgeç")
                }
            }
        )
    }
}
