package com.example.videoplayer.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoFolder(
    val folderPath: String,
    val items: MutableList<VideoFile>
): Parcelable