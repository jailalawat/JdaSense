package com.jdasense.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jdasense.app.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGetStarted.setOnClickListener {
            // Save onboarding status
            getSharedPreferences("JdaSense", MODE_PRIVATE)
                .edit()
                .putBoolean("onboarding_complete", true)
                .apply()

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
