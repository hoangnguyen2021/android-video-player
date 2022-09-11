package com.example.videoplayer.fragments

import android.app.Activity
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.videoplayer.adapters.VideoFilesAdapter
import com.example.videoplayer.data.VideoDataManager
import com.example.videoplayer.models.VideoFile

class VideoDeleteDialogFragment(
    private val videoFilesAdapter: VideoFilesAdapter,
    private val videoFile: VideoFile
) : DialogFragment() {

    private val deleteLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val folderPath = videoFile.path.substringBeforeLast('/')
            // update videoFilesAdapter
            val videoFolder = VideoDataManager.getVideoFolder(requireContext(), folderPath)
            if (videoFolder.items.isEmpty()) {
                requireActivity().finish()
            } else {
                videoFilesAdapter.submitList(videoFolder.items)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("Delete video")
            .setMessage("Do you want to delete this video? This action cannot be undone!")
            .setPositiveButton("Delete") { _, _ ->
                // request delete access to media files on Android 11+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val resolver = requireContext().applicationContext.contentResolver
                    val pendingIntent = MediaStore
                        .createDeleteRequest(resolver, listOf(videoFile.contentUri))
                    val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent).build()
                    deleteLauncher.launch(intentSenderRequest)
                }
            }
            .setNegativeButton("Cancel") { _, _ -> }
            .create()
    }

    companion object {
        const val TAG = "VideoDeleteDialogFragment"
    }

}