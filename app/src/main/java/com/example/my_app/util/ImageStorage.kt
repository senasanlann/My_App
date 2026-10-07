package com.example.my_app.util

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

/**
 * Galeriden seçilen bir resmi (content:// Uri) uygulamanın kendi özel
 * depolama alanına (filesDir/images) kopyalar. Sistem galerisinin verdiği
 * Uri her zaman kalıcı olmayabilir; dosyayı kendi alanımıza kopyalayınca
 * yolunu (String) Room'da güvenle saklayabiliriz.
 */
object ImageStorage {
    suspend fun save(context: Context, uri: Uri, prefix: String): String {
        return withContext(Dispatchers.IO) {
            val dir = File(context.filesDir, "images").apply { mkdirs() }
            val file = File(dir, "${prefix}_${UUID.randomUUID()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output -> input.copyTo(output) }
            }
            file.absolutePath
        }
    }
}
