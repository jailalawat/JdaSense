package com.jdasense.app.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(@ApplicationContext context: Context) {

    private val sharedPreferences: SharedPreferences

    init {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        sharedPreferences = EncryptedSharedPreferences.create(
            context,
            "secret_shared_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveToken(token: String) {
        sharedPreferences.edit().putString("JWT_TOKEN", token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("JWT_TOKEN", null)
    }
    
    fun clearToken() {
        sharedPreferences.edit().remove("JWT_TOKEN").apply()
    }

    // Biometric / PIN flags
    fun isBiometricEnabled(): Boolean {
        return sharedPreferences.getBoolean("BIO_ENABLED", false)
    }

    fun setBiometricEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("BIO_ENABLED", enabled).apply()
    }
    
    fun savePin(pin: String) {
        sharedPreferences.edit().putString("USER_PIN", pin).apply()
    }
    
    fun getPin(): String? {
        return sharedPreferences.getString("USER_PIN", null)
    }
}
