package com.example.videoplayer.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.videoplayer.models.VideoFolder

class VideoFolderDiffUtilItemCallback: DiffUtil.ItemCallback<VideoFolder>() {
    override fun areItemsTheSame(oldItem: VideoFolder, newItem: VideoFolder): Boolean {
        return oldItem.folderPath == newItem.folderPath
    }

    override fun areContentsTheSame(oldItem: VideoFolder, newItem: VideoFolder): Boolean {
        return oldItem == newItem
    }
}