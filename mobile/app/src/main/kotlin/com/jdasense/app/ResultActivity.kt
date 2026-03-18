package com.jdasense.app

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.jdasense.app.databinding.ActivityResultBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        simulateAnalysis()

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun simulateAnalysis() {
        lifecycleScope.launch {
            // Simulate API processing delay
            delay(3000)
            
            showResult("Normal", isAnomaly = false)
        }
    }

    private fun showResult(result: String, isAnomaly: Boolean) {
        binding.lottieAnalyzing.visibility = View.GONE
        binding.tv_analyzing_status.visibility = View.GONE

        binding.tv_result_title.visibility = View.VISIBLE
        binding.tv_result_value.visibility = View.VISIBLE
        binding.tv_disclaimer.visibility = View.VISIBLE
        binding.btn_back.visibility = View.VISIBLE

        binding.tv_result_value.text = result
        if (isAnomaly) {
            binding.tv_result_value.setTextColor(getColor(R.color.hospital_red_alert))
        } else {
            binding.tv_result_value.setTextColor(getColor(R.color.hospital_green_safe))
        }
    }
}
