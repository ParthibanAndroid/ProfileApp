package com.learning.profile

import android.net.Uri
import java.io.File

interface ProfileImageFileProvider {
    suspend fun createFileFromUri(uri: Uri): File
}
