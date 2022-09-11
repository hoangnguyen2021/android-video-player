package com.example.videoplayer.fragments

import android.app.Dialog
import android.os.Bundle
import android.text.format.Formatter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.videoplayer.models.VideoFile
import com.example.videoplayer.utils.Utils

class VideoPropertiesDialogFragment(
    private val videoFile: VideoFile
): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val message = StringBuilder()
            .append("Display name: ${videoFile.displayName}")
            .append("\n\n")
            .append("Path: ${videoFile.path.substringBeforeLast('/')}")
            .append("\n\n")
            .append("Size: ${Formatter.formatFileSize(requireContext(), videoFile.size)}")
            .append("\n\n")
            .append("Length: ${Utils.convertMillisToTime(videoFile.duration)}")
            .append("\n\n")
            .append("Format: ${videoFile.mimeType}")
            .append("\n\n")
            .append("Resolution: ${videoFile.width} x ${videoFile.height}")
            .append("\n\n")

        return AlertDialog.Builder(requireContext())
            .setTitle("Video properties")
            .setMessage(message)
            .setPositiveButton("OK") { _, _ -> }
            .create()
    }

    companion object {
        const val TAG = "VideoPropertiesDialogFragment"
    }

}