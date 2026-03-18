package com.jdasense.app

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.airbnb.android.lottie.LottieAnimationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.jdasense.app.databinding.ActivityResultBinding
import com.jdasense.app.network.ApiService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class ResultActivity : AppCompatActivity() {

    @Inject
    lateinit var apiService: ApiService

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val outputFile = File(cacheDir, "heart_sound.wav")
        if (outputFile.exists()) {
            performPrediction(outputFile)
        } else {
            Toast.makeText(this, "No recording found", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnRetry.setOnClickListener {
            binding.btnRetry.visibility = View.GONE
            binding.btnBack.visibility = View.GONE
            binding.lottieAnalyzing.visibility = View.VISIBLE
            binding.tvAnalyzingStatus.visibility = View.VISIBLE
            binding.tvAnalyzingStatus.text = "Analyzing Heart Sounds..."
            binding.tvAnalyzingStatus.setTextColor(getColor(R.color.hospital_blue_primary))
            performPrediction(outputFile)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun performPrediction(file: File) {
        lifecycleScope.launch {
            try {
                val requestFile = file.asRequestBody("audio/wav".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("audio", file.name, requestFile)
                
                val response = apiService.predict(body)
                
                if (response.isSuccessful && response.body() != null) {
                    val prediction = response.body()!!
                    showResult(prediction.result, prediction.is_anomaly)
                } else {
                    Log.e("ResultActivity", "API Error: ${response.code()}")
                    showError("Error processing heart sounds.")
                }
            } catch (e: Exception) {
                Log.e("ResultActivity", "Network Error", e)
                showError("Network error. Please check your connection.")
            }
        }
    }

    private fun showError(message: String) {
        binding.lottieAnalyzing.visibility = View.GONE
        binding.tvAnalyzingStatus.text = message
        binding.tvAnalyzingStatus.setTextColor(getColor(R.color.hospital_red_alert))
        binding.btnRetry.visibility = View.VISIBLE
        binding.btnBack.visibility = View.VISIBLE
    }

    private fun showResult(result: String, isAnomaly: Boolean) {
        binding.lottieAnalyzing.visibility = View.GONE
        binding.tvAnalyzingStatus.visibility = View.GONE

        binding.tvResultTitle.visibility = View.VISIBLE
        binding.tvResultValue.visibility = View.VISIBLE
        binding.tvDisclaimer.visibility = View.VISIBLE
        binding.btnBack.visibility = View.VISIBLE

        binding.tvResultValue.text = result
        if (isAnomaly) {
            binding.tvResultValue.setTextColor(getColor(R.color.hospital_red_alert))
        } else {
            binding.tvResultValue.setTextColor(getColor(R.color.hospital_green_safe))
        }
    }
}
