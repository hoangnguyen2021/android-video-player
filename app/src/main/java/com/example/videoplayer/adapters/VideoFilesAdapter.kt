package com.example.videoplayer.adapters

import android.content.Context
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videoplayer.R
import com.example.videoplayer.fragments.VideoBottomSheetDialogFragment
import com.example.videoplayer.models.VideoFile
import com.example.videoplayer.utils.Utils
import java.io.File

class VideoFilesAdapter(
    private val context: Context,
    private val videoFileClickListener: (List<VideoFile>, VideoFile, Int) -> Unit
) : ListAdapter<VideoFile, VideoFilesAdapter.VideoFileVH>(VideoFileDiffUtilItemCallback()) {

    inner class VideoFileVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val thumbnailIv: ImageView = itemView.findViewById(R.id.thumbnail)
        private val menuMoreIv: ImageView = itemView.findViewById(R.id.video_menu_more)
        private val videoNameTv: TextView = itemView.findViewById(R.id.video_name)
        private val videoSizeTv: TextView = itemView.findViewById(R.id.video_size)
        private val videoDurationTv: TextView = itemView.findViewById(R.id.video_duration)

        fun bind(videoFile: VideoFile, position: Int) {
            videoNameTv.text = videoFile.displayName
            videoSizeTv.text = Formatter.formatFileSize(itemView.context, videoFile.size)
            videoDurationTv.text = Utils.convertMillisToTime(videoFile.duration)
            Glide.with(itemView.context)
                .load(File(videoFile.path))
                .into(thumbnailIv)
            menuMoreIv.setOnClickListener {
                VideoBottomSheetDialogFragment(
                    this@VideoFilesAdapter, videoFile, itemView
                )
                    .show(
                        (context as AppCompatActivity).supportFragmentManager,
                        VideoBottomSheetDialogFragment.TAG
                    )
            }
            itemView.setOnClickListener {
                videoFileClickListener(currentList, videoFile, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoFileVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_video_file, parent, false)
        return VideoFileVH(view)
    }

    override fun onBindViewHolder(holder: VideoFileVH, position: Int) {
        holder.bind(getItem(position), position)
    }

    override fun getItemCount(): Int = currentList.size

}