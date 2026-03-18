package com.jdasense.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jdasense.app.databinding.ActivityManagementBinding
import com.jdasense.app.network.ApiService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ManagementActivity : AppCompatActivity() {

    @Inject lateinit var apiService: ApiService
    private lateinit var binding: ActivityManagementBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        // Future: Implement RecyclerView adapters for Users and Audit Logs
    }
}
