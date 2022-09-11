package com.example.videoplayer.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.videoplayer.R
import com.example.videoplayer.adapters.VideoFilesAdapter
import com.example.videoplayer.models.VideoFile
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class VideoBottomSheetDialogFragment(
    private val videoFilesAdapter: VideoFilesAdapter,
    private val videoFile: VideoFile,
    private val videoFileView: View
) : BottomSheetDialogFragment() {

    private val renameLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            VideoRenameDialogFragment(videoFilesAdapter, videoFile)
                .show(parentFragmentManager, VideoRenameDialogFragment.TAG)
        }
        dismiss()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.video_bottom_sheet_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<LinearLayout>(R.id.bottom_sheet_play)
            .setOnClickListener {
                videoFileView.performClick()
                dismiss()
            }
        view.findViewById<LinearLayout>(R.id.bottom_sheet_rename)
            .setOnClickListener {
                // request write access to media files on Android 11+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val resolver = requireContext().applicationContext.contentResolver
                    val pendingIntent = MediaStore
                        .createWriteRequest(resolver, listOf(videoFile.contentUri))
                    val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent).build()
                    renameLauncher.launch(intentSenderRequest)
                }
            }
        view.findViewById<LinearLayout>(R.id.bottom_sheet_share)
            .setOnClickListener {
                Intent(Intent.ACTION_SEND)
                    .apply {
                        putExtra(Intent.EXTRA_STREAM, videoFile.contentUri)
                        type = "video/*"
                    }
                    .also {
                        startActivity(Intent.createChooser(it, "Share video via"))
                    }
                dismiss()
            }
        view.findViewById<LinearLayout>(R.id.bottom_sheet_delete)
            .setOnClickListener {
                VideoDeleteDialogFragment(videoFilesAdapter, videoFile)
                    .show(parentFragmentManager, VideoDeleteDialogFragment.TAG)
                dismiss()
            }
    }

    companion object {
        const val TAG = "VideoBottomSheetDialogFragment"
    }

}