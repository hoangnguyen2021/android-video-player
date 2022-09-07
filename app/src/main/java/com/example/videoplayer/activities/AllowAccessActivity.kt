package com.example.videoplayer.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.videoplayer.R

class AllowAccessActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                startActivity(Intent(this, VideoFoldersActivity::class.java))
                finish()
            }
        }
    private lateinit var allowButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_allow_access)

        val allowAccessPreferences = getSharedPreferences("AllowAccess", MODE_PRIVATE)
        if (allowAccessPreferences.getBoolean(ALLOW, false)) {
            startActivity(Intent(this, VideoFoldersActivity::class.java))
            finish()
        }

        allowButton = findViewById(R.id.allow_access)
        allowButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                startMainActivity()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    STORAGE_PERMISSION
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == STORAGE_PERMISSION) {
            permissions.forEachIndexed { index, permission ->
                if (grantResults[index] == PackageManager.PERMISSION_DENIED) {
                    if (shouldShowRequestPermissionRationale(permission)) {
                        AlertDialog.Builder(this)
                            .setTitle("App Permission")
                            .setMessage("For playing videos, you must allow this app to access video files on your device")
                            .setPositiveButton("Open Settings") { _, _ ->
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                val uri = Uri.fromParts("package", packageName, null)
                                intent.data = uri
                                resultLauncher.launch(intent)
                            }
                            .create().show()
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            STORAGE_PERMISSION
                        )
                    }
                } else {
                    startMainActivity()
                }
            }
        }
    }

    private fun startMainActivity() {
        val allowAccessPreferences = getSharedPreferences("AllowAccess", MODE_PRIVATE)
        allowAccessPreferences.edit().apply {
            putBoolean(ALLOW, true)
            apply()
        }
        startActivity(Intent(this, VideoFoldersActivity::class.java))
        finish()
    }

    companion object {
        const val STORAGE_PERMISSION = 1
        const val ALLOW = "Allow"
    }
}