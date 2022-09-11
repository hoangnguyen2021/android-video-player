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
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                startActivity(Intent(this, VideoFoldersActivity::class.java))
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_allow_access)

        // remember user's permission
        val allowAccessPreferences = getSharedPreferences("AllowAccess", MODE_PRIVATE)
        if (allowAccessPreferences.getBoolean(ALLOW, false)) {
            startActivity(Intent(this, VideoFoldersActivity::class.java))
            finish()
        }

        val allowButton: Button = findViewById(R.id.allow_access)
        allowButton.setOnClickListener {
            // check read external storage permission
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                startMainActivity()
            } else {
                // request permission if not granted
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
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
                // if permission is denied
                if (grantResults[index] == PackageManager.PERMISSION_DENIED) {
                    if (shouldShowRequestPermissionRationale(permission)) {
                        // show rationale dialog
                        AlertDialog.Builder(this)
                            .setTitle("App Permission")
                            .setMessage("For playing videos, you must allow this app to access video files on your device")
                            .setPositiveButton("Open Settings") { _, _ ->
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                intent.data = Uri.fromParts("package", packageName, null)
                                resultLauncher.launch(intent)
                            }
                            .create().show()
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ),
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