package com.example.videoplayer.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.videoplayer.R
import com.example.videoplayer.adapters.VideoFilesAdapter
import com.example.videoplayer.data.VideoDataManager

class VideoFilesActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var foldersSwipeRefresh: SwipeRefreshLayout
    private lateinit var videoFilesRv: RecyclerView
    private lateinit var videoFilesAdapter: VideoFilesAdapter

    private var folderPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_video_files)

        folderPath = intent.getStringExtra(FOLDER_PATH)
        supportActionBar?.title = folderPath?.substringAfterLast('/')

        foldersSwipeRefresh = findViewById(R.id.swipe_refresh_files)
        videoFilesRv = findViewById(R.id.rv_video_files)

        initVideoFilesRv()

        foldersSwipeRefresh.setOnRefreshListener {
            loadVideoFiles(folderPath)
            foldersSwipeRefresh.isRefreshing = false
        }
    }

    override fun onResume() {
        super.onResume()

        loadVideoFiles(folderPath)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.video_file_menu, menu)
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.setOnQueryTextListener(this)

        return super.onCreateOptionsMenu(menu)
    }

    private fun loadVideoFiles(folderPath: String?, query: String? = null) {
        if (folderPath == null) {
            videoFilesAdapter.submitList(emptyList())
        } else {
            val videoFolder = VideoDataManager.getVideoFolder(this, folderPath)
            if (query == null) {
                videoFilesAdapter.submitList(videoFolder.items)
            } else {
                videoFilesAdapter.submitList(
                    videoFolder.items.filter { it.title.contains(query, true) }
                )
            }
        }
    }

    private fun initVideoFilesRv() {
        videoFilesAdapter = VideoFilesAdapter(this) { videoFiles, videoFile, position ->
            Intent(this, VideoPlayerActivity::class.java)
                .apply {
                    val bundle = Bundle()
                        .apply {
                            putParcelableArrayList(
                                VideoPlayerActivity.VIDEO_FILES,
                                ArrayList(videoFiles)
                            )
                        }
                    putExtras(bundle)
                    putExtra(VideoPlayerActivity.VIDEO_FILE, videoFile)
                    putExtra(VideoPlayerActivity.START_ITEM_INDEX, position)
                }
                .also { startActivity(it) }
        }
        videoFilesRv.apply {
            adapter = videoFilesAdapter
            layoutManager = LinearLayoutManager(
                this@VideoFilesActivity, RecyclerView.VERTICAL, false
            )
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        loadVideoFiles(folderPath, newText)

        return true
    }

    companion object {
        const val FOLDER_PATH = "com.example.videoplayer.activities.extras.FOLDER_PATH"
    }

}