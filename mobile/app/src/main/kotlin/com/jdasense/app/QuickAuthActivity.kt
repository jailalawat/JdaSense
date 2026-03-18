package com.jdasense.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.jdasense.app.databinding.ActivityPinBinding
import com.jdasense.app.security.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class QuickAuthActivity : AppCompatActivity() {

    @Inject lateinit var tokenManager: TokenManager
    private lateinit var binding: ActivityPinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        if (tokenManager.isBiometricEnabled()) {
            showBiometricPrompt()
        }
    }

    private fun setupUI() {
        if (tokenManager.isBiometricEnabled()) {
            binding.btnBioAuth.visibility = View.VISIBLE
            binding.btnBioAuth.setOnClickListener { showBiometricPrompt() }
        }

        binding.etPin.addTextChangedListener { text ->
            if (text?.length == 4) {
                verifyPin(text.toString())
            }
        }
    }

    private fun verifyPin(enteredPin: String) {
        val storedPin = tokenManager.getPin()
        if (enteredPin == storedPin) {
            navigateToMain()
        } else {
            binding.etPin.text?.clear()
            Toast.makeText(this, "Incorrect PIN", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    navigateToMain()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // If user cancels or fails, they can still use PIN
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Hospital Staff Authentication")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use PIN Instead")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
