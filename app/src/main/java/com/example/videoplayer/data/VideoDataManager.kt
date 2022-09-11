package com.example.videoplayer.data

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.example.videoplayer.models.VideoFile
import com.example.videoplayer.models.VideoFolder

object VideoDataManager {

    fun getVideoFolders(context: Context): List<VideoFolder> {
        val videoFolders: MutableList<VideoFolder> = mutableListOf()

        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.WIDTH,
            MediaStore.Video.Media.HEIGHT
        )
        context.contentResolver
            .query(uri, projection, null, null, null)
            ?.use { cursor ->
                while (cursor.moveToNext()) {
                    val id =
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID))
                    val title =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE))
                    val displayName =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME))
                    val size =
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE))
                    val duration =
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))
                    val path =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))
                    val dateAdded =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED))
                    val mimeType =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE))
                    val width =
                        cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH))
                    val height =
                        cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT))
                    val contentUri = ContentUris
                        .withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)

                    val videoFile =
                        VideoFile(id, title, displayName, size, duration, path, dateAdded, mimeType, width, height, contentUri)

                    val folderPath = path.substringBeforeLast('/')
                    val foundFolder = videoFolders.find { it.folderPath == folderPath }
                    // add video file to video folder if it the folder exists in video folder list
                    // if not, add video folder with the video file to video folder list
                    if (foundFolder != null) {
                        foundFolder.items.add(videoFile)
                    } else {
                        videoFolders.add(VideoFolder(folderPath, mutableListOf(videoFile)))
                    }
                }
            }
        return videoFolders
    }

    fun getVideoFolder(context: Context, folderPath: String): VideoFolder {
        val videoFiles: MutableList<VideoFile> = mutableListOf()

        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.WIDTH,
            MediaStore.Video.Media.HEIGHT
        )
        val selection = "${MediaStore.Video.Media.DATA} LIKE ?"
        val selectionArgs = arrayOf("$folderPath%")
        context.contentResolver
            .query(uri, projection, selection, selectionArgs, null)
            ?.use { cursor ->
                while (cursor.moveToNext()) {
                    val id =
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID))
                    val title =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE))
                    val displayName =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME))
                    val size =
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE))
                    val duration =
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))
                    val path =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))
                    val dateAdded =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED))
                    val mimeType =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE))
                    val width =
                        cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH))
                    val height =
                        cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT))
                    val contentUri = ContentUris
                        .withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)

                    videoFiles.add(
                        VideoFile(id, title, displayName, size, duration, path, dateAdded, mimeType, width, height, contentUri)
                    )
                }
            }

        return VideoFolder(folderPath, videoFiles)
    }

}