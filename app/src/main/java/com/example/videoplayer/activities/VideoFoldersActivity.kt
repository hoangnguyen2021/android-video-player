package com.example.videoplayer.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.videoplayer.R
import com.example.videoplayer.adapters.VideoFoldersAdapter
import com.example.videoplayer.models.VideoFolder
import com.example.videoplayer.models.VideoFile

class VideoFoldersActivity : AppCompatActivity() {

    private lateinit var foldersSwipeRefresh: SwipeRefreshLayout
    private lateinit var videoFoldersRv: RecyclerView
    private lateinit var videoFoldersAdapter: VideoFoldersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        foldersSwipeRefresh = findViewById(R.id.swipe_refresh_folders)
        videoFoldersRv = findViewById(R.id.rv_video_folders)
        initVideoFoldersRv()
        loadVideoFolders()

        foldersSwipeRefresh.setOnRefreshListener {
            loadVideoFolders()
            foldersSwipeRefresh.isRefreshing = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.folder_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val appUrl =
            "https://play.google.com/store/apps/details?id=${applicationContext.packageName}"
        when (item.itemId) {
            R.id.rate_us -> {
                val uri = Uri.parse(appUrl)
                Intent(Intent.ACTION_VIEW, uri).also { startActivity(it) }
            }
            R.id.refresh_folders -> {
                loadVideoFolders()
            }
            R.id.share_app -> {
                Intent()
                    .apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, appUrl)
                        type = "text/plain"
                    }
                    .also {
                        startActivity(Intent.createChooser(it, "Share app via"))
                    }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initVideoFoldersRv() {
        videoFoldersAdapter = VideoFoldersAdapter { videoFolder ->
            Intent(this, VideoFilesActivity::class.java)
                .apply {
                    putExtra(VideoFilesActivity.VIDEO_FOLDER, videoFolder)
                }
                .also { startActivity(it) }
        }
        videoFoldersRv.apply {
            adapter = videoFoldersAdapter
            layoutManager = LinearLayoutManager(
                this@VideoFoldersActivity, RecyclerView.VERTICAL, false
            )
        }
    }

    private fun loadVideoFolders() {
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

        )
        contentResolver
            .query(uri, projection, null, null, null)
            ?.use { cursor ->
                while (cursor.moveToNext()) {
                    val id =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID))
                    val title =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE))
                    val displayName =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME))
                    val size =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE))
                    val duration =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))
                    val path =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))
                    val dateAdded =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED))
                    val mimeType =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE))
                    val videoFile =
                        VideoFile(id, title, displayName, size, duration, path, dateAdded, mimeType)

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
        videoFoldersAdapter.submitList(videoFolders)
    }

}