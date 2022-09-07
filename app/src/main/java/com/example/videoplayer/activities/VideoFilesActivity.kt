package com.example.videoplayer.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.videoplayer.R
import com.example.videoplayer.adapters.VideoFilesAdapter
import com.example.videoplayer.models.VideoFile
import com.example.videoplayer.models.VideoFolder

class VideoFilesActivity : AppCompatActivity() {

    private lateinit var videoFilesRv: RecyclerView
    private lateinit var videoFilesAdapter: VideoFilesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_video_files)

        val videoFolder: VideoFolder? = intent.getParcelableExtra(VIDEO_FOLDER)
        supportActionBar?.title = videoFolder?.folderPath?.substringAfterLast('/')

        videoFilesRv = findViewById(R.id.rv_video_files)

        initVideoFilesRv()
        videoFolder?.let { loadVideoFiles(it) }
    }

    private fun loadVideoFiles(videoFolder: VideoFolder) {
        val videoFiles: MutableList<VideoFile> = mutableListOf()
        videoFiles.addAll(videoFolder.items)
        videoFilesAdapter.submitList(videoFiles)
    }

    private fun initVideoFilesRv() {
        videoFilesAdapter = VideoFilesAdapter { videoFiles, videoFile, position ->
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
        const val VIDEO_FOLDER = "com.example.videoplayer.activities.extras.FOLDER_NAME"
    }

}