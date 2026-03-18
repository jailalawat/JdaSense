package com.jdasense.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jdasense.app.security.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val sharedPreferences = getSharedPreferences("JdaSense", MODE_PRIVATE)
        val onboardingComplete = sharedPreferences.getBoolean("onboarding_complete", false)
        
        if (!onboardingComplete) {
            startActivity(Intent(this, OnboardingActivity::class.java))
        } else if (tokenManager.getToken() == null) {
            // Need initial login
            startActivity(Intent(this, LoginActivity::class.java))
        } else if (tokenManager.getPin() != null || tokenManager.isBiometricEnabled()) {
            // Quick Auth (PIN or Bio)
            startActivity(Intent(this, QuickAuthActivity::class.java))
        } else {
            // Logged in but no quick auth setup yet
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }
}
