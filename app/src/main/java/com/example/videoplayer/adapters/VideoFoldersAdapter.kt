package com.example.videoplayer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.videoplayer.R
import com.example.videoplayer.models.VideoFolder

class VideoFoldersAdapter(
    private val videoFolderClickListener: (VideoFolder) -> Unit
) : ListAdapter<VideoFolder, VideoFoldersAdapter.VideoFolderVH>(VideoFolderDiffUtilItemCallback()) {

    inner class VideoFolderVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val folderNameTv: TextView = itemView.findViewById(R.id.folder_name)
        private val folderPathTv: TextView = itemView.findViewById(R.id.folder_path)
        private val numberOfFilesTv: TextView = itemView.findViewById(R.id.number_of_files)

        fun bind(videoFolder: VideoFolder) {
            val folderName = videoFolder.folderPath.substringAfterLast('/')
            folderNameTv.text = folderName
            folderPathTv.text = videoFolder.folderPath
            numberOfFilesTv.text = "${videoFolder.items.size} videos"
            itemView.setOnClickListener {
                videoFolderClickListener(videoFolder)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoFolderVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_video_folder, parent, false)
        return VideoFolderVH(view)
    }

    override fun onBindViewHolder(holder: VideoFolderVH, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int = currentList.size

}