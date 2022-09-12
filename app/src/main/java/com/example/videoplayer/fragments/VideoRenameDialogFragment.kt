package com.example.videoplayer.fragments

import android.app.Dialog
import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.videoplayer.adapters.VideoFilesAdapter
import com.example.videoplayer.data.VideoDataManager
import com.example.videoplayer.models.VideoFile

class VideoRenameDialogFragment(
    private val videoFilesAdapter: VideoFilesAdapter,
    private val videoFile: VideoFile
) : DialogFragment() {

    private lateinit var editText: EditText
    private var positiveButton: Button? = null
    private val originalName: String =
        videoFile.path.substringAfterLast('/').substringBeforeLast('.')

    private val textWatcher = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable) {
            positiveButton?.isEnabled = s.isNotEmpty() && s.toString() != originalName
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        editText = EditText(requireContext()).apply {
            setText(originalName)
            requestFocus()
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Rename to")
            .setView(editText)
            .setPositiveButton("OK") { _, _ ->
                // rename video file
                val folderPath = videoFile.path.substringBeforeLast('/')
                val extension = videoFile.path.substringAfterLast('.')
                val resolver = requireContext().contentResolver
                val contentValues = ContentValues()
                contentValues.apply {
                    put(MediaStore.Video.Media.DISPLAY_NAME, "${editText.text}.$extension")
                    // required to update MediaStore.Video.Media.TITLE
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.Video.Media.IS_PENDING, 1)
                    }
                }
                resolver.update(
                    videoFile.contentUri, contentValues,
                    "${MediaStore.Video.Media._ID} = ?", arrayOf(videoFile.id.toString())
                )
                contentValues.apply {
                    clear()
                    put(MediaStore.Video.Media.TITLE, "${editText.text}")
                    // required to update MediaStore.Video.Media.TITLE
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.Video.Media.IS_PENDING, 0)
                    }
                }
                resolver.update(
                    videoFile.contentUri, contentValues,
                    "${MediaStore.Video.Media._ID} = ?", arrayOf(videoFile.id.toString())
                )
                // update videoFilesAdapter
                val videoFolder = VideoDataManager.getVideoFolder(requireContext(), folderPath)
                videoFilesAdapter.submitList(videoFolder.items)
            }
            .setNegativeButton("Cancel") { _, _ -> }
            .create()
            .apply {
                // force the soft keyboard to appear
                window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            }
    }

    override fun onStart() {
        super.onStart()

        positiveButton = (dialog as? AlertDialog)?.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton?.apply {
            isEnabled = false
            editText.addTextChangedListener(textWatcher)
        }
    }

    override fun onStop() {
        super.onStop()

        editText.removeTextChangedListener(textWatcher)
    }

    companion object {
        const val TAG = "VideoRenameDialogFragment"
    }

}