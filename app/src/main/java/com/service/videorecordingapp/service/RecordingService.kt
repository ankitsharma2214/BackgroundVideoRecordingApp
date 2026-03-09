package com.service.videorecordingapp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Binder
import android.os.Environment
import android.os.IBinder
import android.provider.MediaStore
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import java.io.File

class VideoRecordingService : LifecycleService() {

    inner class LocalBinder : Binder() {
        fun getService(): VideoRecordingService = this@VideoRecordingService
    }

    private val binder = LocalBinder()

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    private var cameraProvider: ProcessCameraProvider? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var preview: Preview? = null
    private var activeRecording: Recording? = null
    private var tempFile: File? = null

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        startCamera()
    }

    private fun startForegroundService() {
        val notification = NotificationCompat.Builder(this, "video_channel")
            .setContentTitle("Video Recording")
            .setContentText("Camera running in background")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(1, notification)
    }

    private fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({

            cameraProvider = cameraProviderFuture.get()

            val recorder = Recorder.Builder().build()
            videoCapture = VideoCapture.withOutput(recorder)

            preview = Preview.Builder().build()

            cameraProvider?.unbindAll()

            cameraProvider?.bindToLifecycle(
                this,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                videoCapture
            )

        }, ContextCompat.getMainExecutor(this))
    }

    fun attachPreview(surfaceProvider: Preview.SurfaceProvider) {
        preview?.setSurfaceProvider(surfaceProvider)
    }

    @SuppressLint("MissingPermission")
    fun startRecording() {

        tempFile = File(cacheDir, "temp_${System.currentTimeMillis()}.mp4")

        val outputOptions = FileOutputOptions.Builder(tempFile!!).build()

        activeRecording = videoCapture!!.output
            .prepareRecording(this, outputOptions)
            .withAudioEnabled()
            .start(ContextCompat.getMainExecutor(this)) { event ->

                when (event) {

                    is VideoRecordEvent.Start -> {
                        Toast.makeText(this, "Recording Start", Toast.LENGTH_SHORT).show()
                    }

                    is VideoRecordEvent.Finalize -> {

                        activeRecording = null

                        if (!event.hasError()) {
                            Toast.makeText(
                                this,
                                "Recording stop",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {

                            Toast.makeText(
                                this,
                                "Recording failed: ${event.error}",
                                Toast.LENGTH_LONG
                            ).show()

                            tempFile?.delete()
                            tempFile = null
                        }
                    }
                }
            }
    }

    fun stopRecording() {
        activeRecording?.stop()
    }

    fun saveRecording() {

        val file = tempFile ?: run {
            Toast.makeText(this, "No recording to save", Toast.LENGTH_SHORT).show()
            return
        }

        if (!file.exists() || file.length() == 0L) {
            Toast.makeText(this, "Recording not ready yet", Toast.LENGTH_SHORT).show()
            return
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, file.name)
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            put(
                MediaStore.Video.Media.RELATIVE_PATH,
                Environment.DIRECTORY_MOVIES + "/MyVideos"
            )
        }

        val uri = contentResolver.insert(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )

        uri?.let {
            contentResolver.openOutputStream(it)?.use { out ->
                file.inputStream().copyTo(out)
            }
        }

        Toast.makeText(this, "Video Saved to Gallery", Toast.LENGTH_LONG).show()

        file.delete()
        tempFile = null
    }

    fun isRecording(): Boolean {
        return activeRecording != null
    }
}