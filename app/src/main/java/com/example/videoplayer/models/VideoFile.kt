package com.example.videoplayer.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoFile(
    val id: Long,
    val title: String,
    val displayName: String,
    val size: Long,
    val duration: Long,
    val path: String,
    val dateAdded: String,
    val mimeType: String,
    val width: Int,
    val height: Int,
    val contentUri: Uri
): Parcelable