package com.example.videoplayer.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.videoplayer.R
import com.example.videoplayer.models.VideoFile
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class VideoBottomSheetDialogFragment(
    private val videoFiles: List<VideoFile>,
    private val videoFile: VideoFile,
    private val position: Int,
    private val videoFileClickListener: (List<VideoFile>, VideoFile, Int) -> Unit
): BottomSheetDialogFragment() {

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
                videoFileClickListener(videoFiles, videoFile, position)
                dismiss()
            }
    }

    companion object {
        const val TAG = "VideoBottomSheetDialogFragment"
    }

}