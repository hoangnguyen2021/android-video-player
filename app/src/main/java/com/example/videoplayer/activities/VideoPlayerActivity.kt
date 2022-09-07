package com.example.videoplayer.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.videoplayer.R
import com.example.videoplayer.models.VideoFile
import com.example.videoplayer.utils.IntentUtil
import com.example.videoplayer.utils.Utils
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DataSource
import kotlin.math.max

class VideoPlayerActivity : AppCompatActivity() {

    private lateinit var playerView: StyledPlayerView
    private lateinit var videoTitleTv: TextView

    private var exoPlayer: ExoPlayer? = null
    private lateinit var mediaItems: List<MediaItem>
    private lateinit var dataSourceFactory: DataSource.Factory

    private var startAutoPlay = true
    private var startItemIndex = C.INDEX_UNSET
    private var startPosition = C.TIME_UNSET

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataSourceFactory = Utils.getDataSourceFactory(this)

        setContentView(R.layout.activity_video_player)
        supportActionBar?.hide()

        playerView = findViewById(R.id.player_view)
        videoTitleTv = findViewById(R.id.video_title)

        playerView.requestFocus()

        if (savedInstanceState != null) {
            startAutoPlay = savedInstanceState.getBoolean(KEY_AUTO_PLAY)
            startItemIndex = savedInstanceState.getInt(KEY_ITEM_INDEX)
            startPosition = savedInstanceState.getLong(KEY_POSITION)
        } else {
            clearStartPosition()
        }
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT > 23) {
            initializePlayer()
            playerView.onResume()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT <= 23 || exoPlayer == null) {
            initializePlayer()
            playerView.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT <= 23) {
            playerView.onPause()
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT > 23) {
            playerView.onPause()
            releasePlayer()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        updateStartPosition()
        outState.putBoolean(KEY_AUTO_PLAY, startAutoPlay)
        outState.putInt(KEY_ITEM_INDEX, startItemIndex)
        outState.putLong(KEY_POSITION, startPosition)
    }

    private fun initializePlayer(): Boolean {
        if (exoPlayer == null) {
            mediaItems = createMediaItems(intent)

            if (mediaItems.isEmpty()) {
                return false
            }

            val videoFile: VideoFile = intent.getParcelableExtra(VIDEO_FILE)!!
            val videoTitle: String = videoFile.title
            videoTitleTv.text = videoTitle

            val exoPlayerBuilder = ExoPlayer.Builder(this)
                .setMediaSourceFactory(createMediaSourceFactory())
                .setSeekBackIncrementMs(10000)
                .setSeekForwardIncrementMs(10000)
            setRenderersFactory(exoPlayerBuilder, false)
            exoPlayer = exoPlayerBuilder.build().apply {
                addListener(object : Player.Listener {
                    override fun onPlayerError(error: PlaybackException) {
                        super.onPlayerError(error)

                        Toast.makeText(
                            this@VideoPlayerActivity,
                            "Error playing video",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
                setAudioAttributes(AudioAttributes.DEFAULT, true)
                playWhenReady = startAutoPlay
            }

            playerView.apply {
                player = exoPlayer
                keepScreenOn = true
            }
        }

        exoPlayer!!.apply {
            val haveStartPosition = startItemIndex != C.INDEX_UNSET
            if (haveStartPosition) {
                seekTo(startItemIndex, startPosition)
            }
            setMediaItems(mediaItems, !haveStartPosition)
            prepare()
        }
        return true
    }

    private fun releasePlayer() {
        if (exoPlayer != null) {
            updateStartPosition()
            exoPlayer?.release()
            exoPlayer = null
            playerView.player = null
            mediaItems = emptyList()
        }
    }

    private fun createMediaItems(intent: Intent): List<MediaItem> {
        return IntentUtil.createMediaItemsFromIntent(intent)
    }

    private fun createMediaSourceFactory(): MediaSource.Factory {
        return DefaultMediaSourceFactory(this)
            .setDataSourceFactory(dataSourceFactory)
    }

    private fun setRenderersFactory(
        playerBuilder: ExoPlayer.Builder, preferExtensionDecoders: Boolean
    ) {
        val renderersFactory =
            Utils.buildRenderersFactory(this, preferExtensionDecoders)
        playerBuilder.setRenderersFactory(renderersFactory)
    }

    private fun updateStartPosition() {
        exoPlayer?.let {
            startAutoPlay = it.playWhenReady
            startItemIndex = it.currentMediaItemIndex
            startPosition = max(0, it.contentPosition)
        }
    }

    private fun clearStartPosition() {
        startAutoPlay = true
        startItemIndex = intent.getIntExtra(START_ITEM_INDEX, 0)
        startPosition = C.TIME_UNSET
    }

    companion object {
        const val VIDEO_FILE = "com.example.videoplayer.activities.extras.VIDEO_FILE"
        const val START_ITEM_INDEX = "com.example.videoplayer.activities.extras.POSITION"
        const val VIDEO_FILES = "com.example.videoplayer.activities.extras.bundle.VIDEO_FILES"

        private const val KEY_ITEM_INDEX = "item_index"
        private const val KEY_POSITION = "position"
        private const val KEY_AUTO_PLAY = "auto_play"
    }

}