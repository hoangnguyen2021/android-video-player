package com.example.videoplayer.utils

import android.content.Intent
import android.net.Uri
import com.example.videoplayer.activities.VideoPlayerActivity
import com.example.videoplayer.models.VideoFile
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata

object IntentUtil {

    fun createMediaItemsFromIntent(intent: Intent): List<MediaItem> {
        val mediaItems = mutableListOf<MediaItem>()
        val videoFiles = intent.extras?.getParcelableArrayList<VideoFile>(
            VideoPlayerActivity.VIDEO_FILES
        )?.toList()
        videoFiles?.forEach { videoFile ->
            val mediaItem = createMediaItemFromVideoFile(videoFile)
            mediaItems.add(mediaItem)
        }

        return mediaItems
    }

    private fun createMediaItemFromVideoFile(videoFile: VideoFile): MediaItem {
        val uri = Uri.parse(videoFile.path)
        val mediaMetaData = MediaMetadata.Builder()
            .setTitle(videoFile.title)
            .build()
        val builder = MediaItem.Builder()
            .setUri(uri)
            .setMimeType(videoFile.mimeType)
            .setMediaMetadata(mediaMetaData)

        return builder.build()
    }

}