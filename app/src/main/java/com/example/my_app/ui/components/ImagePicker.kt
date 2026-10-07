package com.example.my_app.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

/**
 * Android'in yerleşik Foto Seçici'sini (Photo Picker) açan, seçilen Uri'yi
 * geri döndüren yeniden kullanılabilir bir yardımcı. Galeri izni istemeye
 * gerek yok — sistem, seçilen tek dosya için geçici erişim veriyor.
 *
 * Kullanımı:
 *   val pickImage = rememberImagePickerLauncher { uri -> ... }
 *   Button(onClick = pickImage) { Text("Resim seç") }
 */
@Composable
fun rememberImagePickerLauncher(onImagePicked: (Uri) -> Unit): () -> Unit {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) onImagePicked(uri) }

    return {
        launcher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }
}
