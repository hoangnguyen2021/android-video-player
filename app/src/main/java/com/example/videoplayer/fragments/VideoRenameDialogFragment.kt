package com.example.videoplayer.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.videoplayer.adapters.VideoFilesAdapter
import com.example.videoplayer.data.VideoDataManager
import com.example.videoplayer.models.VideoFile
import java.io.File

class VideoRenameDialogFragment(
    private val videoFilesAdapter: VideoFilesAdapter,
    private val videoFile: VideoFile
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val editText = EditText(requireContext()).apply {
            setText(videoFile.path.substringAfterLast('/').substringBeforeLast('.'))
            requestFocus()
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Rename to")
            .setView(editText)
            .setPositiveButton("OK") { _, _ ->
                val parentPath = videoFile.path.substringBeforeLast('/')
                val extension = videoFile.path.substringAfterLast('.')
                val newPath = "$parentPath/${editText.text}.$extension"
                // rename video file
                if (File(videoFile.path).renameTo(File(newPath))) {
                    // update videoFilesAdapter
                    val videoFolder = VideoDataManager.getVideoFolders(requireContext())
                        .find { it.folderPath == parentPath }
                    videoFolder?.let {
                        videoFilesAdapter.submitList(it.items)
                    }
                }
            }
            .setNegativeButton("Cancel") { _, _ -> }
            .create()
            .also {
                // force the soft keyboard to appear
                it.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            }
    }

    companion object {
        const val TAG = "VideoRenameDialogFragment"
    }

}