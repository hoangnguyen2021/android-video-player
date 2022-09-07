package com.example.videoplayer.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoFile(
    val id: String,
    val title: String,
    val displayName: String,
    val size: String,
    val duration: String,
    val path: String,
    val dateAdded: String,
    val mimeType: String
): Parcelable