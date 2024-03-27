package com.example.mostdelicious

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.example.mostdelicious.databinding.ActivityAuthBinding
import com.example.mostdelicious.helpers.LoadingState
import com.example.mostdelicious.helpers.observeNotNull
import com.example.mostdelicious.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    private val viewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.loadingState.observe(this) {
            when (it) {
                is LoadingState.Loading -> {
                    binding.progressBarAuth.visibility = View.VISIBLE
                }

                is LoadingState.Loaded -> {
                    binding.progressBarAuth.visibility = View.GONE
                }
            }
        }

        viewModel.currentUser.observeNotNull(this) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            viewModel.currentUser.removeObservers(this)
        }
    }
}