package com.jdasense.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.jdasense.app.audio.AudioRecorder
import com.jdasense.app.databinding.ActivityMainBinding
import com.jdasense.app.security.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var tokenManager: TokenManager
    private lateinit var binding: ActivityMainBinding
    private var audioRecorder: AudioRecorder? = null
    private var isRecording = false

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startRecordingProcess()
        } else {
            Toast.makeText(this, "Permission denied. Audio recording won't work.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRoleBasedUI()

        binding.btnRecord.setOnClickListener {
            if (!isRecording) {
                checkPermissionsAndStart()
            } else {
                stopRecording()
            }
        }
    }

    private fun setupRoleBasedUI() {
        val role = tokenManager.getRole()
        if (role == "SuperAdmin" || role == "HospitalOwner") {
            binding.btnManage.visibility = View.VISIBLE
            binding.btnManage.setOnClickListener {
                startActivity(Intent(this, ManagementActivity::class.java))
            }
        }
    }

    private fun checkPermissionsAndStart() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                startRecordingProcess()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    private fun startRecordingProcess() {
        isRecording = true
        binding.btnRecord.text = "Stop"
        binding.tvTimer.visibility = View.VISIBLE
        binding.waveformView.clear()

        val outputFile = File(cacheDir, "heart_sound.wav")
        audioRecorder = AudioRecorder(outputFile) { amplitude ->
            runOnUiThread {
                binding.waveformView.addAmplitude(amplitude)
            }
        }
        audioRecorder?.start()

        startTimer()
    }

    private fun startTimer() {
        object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                binding.tvTimer.text = String.format("00:%02d", seconds)
            }

            override fun onFinish() {
                if (isRecording) {
                    stopRecording()
                }
            }
        }.start()
    }

    private fun stopRecording() {
        isRecording = false
        audioRecorder?.stop()
        binding.btnRecord.text = "Start Recording"
        binding.tvTimer.visibility = View.GONE
        
        startActivity(Intent(this, ResultActivity::class.java))
    }
}
