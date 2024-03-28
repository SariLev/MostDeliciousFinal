package com.example.mostdelicious

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.example.mostdelicious.databinding.ActivityAuthBinding
import com.example.mostdelicious.helpers.LoadingState
import com.example.mostdelicious.helpers.observeNotNull
import com.example.mostdelicious.viewmodels.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    private val viewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)


        viewModel.exceptions.observeNotNull(this) {
            Log.d("Error", it.message!!)
            Snackbar.make(binding.root, it.message ?: "Unknown error please try again soon", Toast.LENGTH_SHORT).show()
        }


        viewModel.currentUser.observeNotNull(this) {
            viewModel.currentUser.removeObservers(this)
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        }


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

    }
}