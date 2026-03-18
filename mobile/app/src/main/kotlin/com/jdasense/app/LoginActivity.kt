package com.jdasense.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jdasense.app.databinding.ActivityLoginBinding
import com.jdasense.app.security.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    @Inject lateinit var tokenManager: TokenManager
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                handleLoginSuccess("mock_jwt_token")
            } else {
                Toast.makeText(this, "Please enter credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleLoginSuccess(token: String) {
        tokenManager.saveToken(token)
        
        // For first login, default to PIN setup
        tokenManager.savePin("1234") // Mock setup
        tokenManager.setBiometricEnabled(true)
        
        Toast.makeText(this, "Login Successful. PIN set to 1234", Toast.LENGTH_LONG).show()
        
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
