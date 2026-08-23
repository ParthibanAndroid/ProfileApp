package com.learning.profile

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class ProfileImageFileProviderImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : ProfileImageFileProvider {
        override suspend fun createFileFromUri(uri: Uri): File =
            withContext(Dispatchers.IO) {
                val mimeType = context.contentResolver.getType(uri)

                val extension =
                    when (mimeType) {
                        "image/jpeg" -> {
                            ".jpg"
                        }

                        "image/png" -> {
                            ".png"
                        }

                        "image/webp" -> {
                            ".webp"
                        }

                        else -> {
                            throw IllegalArgumentException(
                                "Only JPEG, PNG and WebP images are supported",
                            )
                        }
                    }

                val file =
                    File.createTempFile(
                        "profile_image_",
                        extension,
                        context.cacheDir,
                    )

                context.contentResolver.openInputStream(uri).use { input ->
                    requireNotNull(input) {
                        "Unable to open image URI"
                    }

                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                file
            }
    }
