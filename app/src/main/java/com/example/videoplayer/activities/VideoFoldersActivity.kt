package com.example.videoplayer.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.videoplayer.R
import com.example.videoplayer.adapters.VideoFoldersAdapter
import com.example.videoplayer.data.VideoDataManager

class VideoFoldersActivity : AppCompatActivity() {

    private lateinit var foldersSwipeRefresh: SwipeRefreshLayout
    private lateinit var videoFoldersRv: RecyclerView
    private lateinit var videoFoldersAdapter: VideoFoldersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_folders)

        foldersSwipeRefresh = findViewById(R.id.swipe_refresh_folders)
        videoFoldersRv = findViewById(R.id.rv_video_folders)

        initVideoFoldersRv()

        foldersSwipeRefresh.setOnRefreshListener {
            loadVideoFolders()
            foldersSwipeRefresh.isRefreshing = false
        }
    }

    override fun onResume() {
        super.onResume()

        loadVideoFolders()
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
                Intent(Intent.ACTION_SEND)
                    .apply {
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
                    putExtra(VideoFilesActivity.FOLDER_PATH, videoFolder.folderPath)
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
        val videoFolders = VideoDataManager.getVideoFolders(this)
        videoFoldersAdapter.submitList(videoFolders)
    }

}