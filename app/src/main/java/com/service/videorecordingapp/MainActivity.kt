package com.service.videorecordingapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.service.videorecordingapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var cameraService: VideoRecordingService? = null
    private var isCameraBound = false

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { it.value }) {
                startCameraService()
            } else {
                val denied = permissions.filter { !it.value }.keys.joinToString()
                Toast.makeText(this, "Permissions denied: $denied", Toast.LENGTH_LONG).show()
            }
        }


    private val cameraConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            cameraService = (binder as VideoRecordingService.LocalBinder).getService()
            isCameraBound = true

            attachPreview()
            updateCameraBtn(isRecording = cameraService?.isRecording() == true)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isCameraBound = false
            cameraService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.previewView.implementationMode =
            PreviewView.ImplementationMode.COMPATIBLE

        createNotificationChannel()
        initButtonStates()
        requestPermissions()
        onClicks()
    }


    private fun attachPreview() {
        cameraService?.attachPreview(binding.previewView.surfaceProvider)
    }

    private fun initButtonStates() {
        binding.btnStart.isEnabled = true
        binding.btnStop.isEnabled = false
        binding.btnSave.isEnabled = true
    }

    private fun requestPermissions() {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
        )
    }

    private fun startCameraService() {
        Intent(this, VideoRecordingService::class.java).also {
            ContextCompat.startForegroundService(this, it)
            bindService(it, cameraConnection, BIND_AUTO_CREATE)
        }
    }

    private fun onClicks() {

        binding.btnStart.setOnClickListener {
            cameraService?.startRecording()
            updateCameraBtn(isRecording = true)
        }

        binding.btnStop.setOnClickListener {
            cameraService?.stopRecording()
            updateCameraBtn(isRecording = false)
        }

        binding.btnSave.setOnClickListener {
            cameraService?.saveRecording()
        }
    }

    private fun updateCameraBtn(isRecording: Boolean) {
        binding.btnStart.isEnabled = !isRecording
        binding.btnStop.isEnabled = isRecording
        binding.btnSave.isEnabled = !isRecording
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "video_channel",
                "Video Recording",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isCameraBound) {
            unbindService(cameraConnection)
            isCameraBound = false
        }
    }

    override fun onResume() {
        super.onResume()
        if (isCameraBound) {
            attachPreview()
        }
    }
}