package com.jdasense.app

import android.app.Application
import com.google.android.gms.security.ProviderInstaller
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class JdaSenseApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Ensure the security provider is installed for EncryptedSharedPreferences
        try {
            ProviderInstaller.installIfNeeded(applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
