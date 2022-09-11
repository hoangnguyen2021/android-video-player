package com.example.videoplayer.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.videoplayer.R
import com.example.videoplayer.adapters.VideoFilesAdapter
import com.example.videoplayer.data.VideoDataManager

class VideoFilesActivity : AppCompatActivity() {

    private lateinit var videoFilesRv: RecyclerView
    private lateinit var videoFilesAdapter: VideoFilesAdapter

    private var folderPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_video_files)

        folderPath = intent.getStringExtra(FOLDER_PATH)
        supportActionBar?.title = folderPath?.substringAfterLast('/')

        videoFilesRv = findViewById(R.id.rv_video_files)

        initVideoFilesRv()
    }

    override fun onResume() {
        super.onResume()

        folderPath?.let { loadVideoFiles(it) }
    }

    private fun loadVideoFiles(folderPath: String) {
        val videoFolder = VideoDataManager.getVideoFolder(this, folderPath)
        videoFilesAdapter.submitList(videoFolder.items)
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

    companion object {
        const val FOLDER_PATH = "com.example.videoplayer.activities.extras.FOLDER_PATH"
    }

}