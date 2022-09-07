package com.example.videoplayer.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.videoplayer.models.VideoFile

class VideoFileDiffUtilItemCallback: DiffUtil.ItemCallback<VideoFile>() {
    override fun areItemsTheSame(oldItem: VideoFile, newItem: VideoFile): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: VideoFile, newItem: VideoFile): Boolean {
        return oldItem == newItem
    }
}