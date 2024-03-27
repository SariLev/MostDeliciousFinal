package com.example.mostdelicious

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mostdelicious.databinding.ActivityMainBinding
import com.example.mostdelicious.helpers.LoadingState
import com.example.mostdelicious.helpers.observeOnlyNull
import com.example.mostdelicious.viewmodels.AuthViewModel
import com.example.mostdelicious.viewmodels.PostViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<AuthViewModel>()
    private val postViewModel by viewModels<PostViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.currentUser.observeOnlyNull(this) {
            if (it == null) {
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            }
        }

        viewModel.loadingState.observe(this, ::setLoading)
    }

    fun setLoading(state: LoadingState) {
        binding.pbMain.visibility = when (state) {
            is LoadingState.Loading -> View.VISIBLE
            is LoadingState.Loaded -> View.GONE
        }
    }


    fun requestGalleryPermissions(): Boolean {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S_V2) {
            return true
        }
        if (checkSelfPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PERMISSION_DENIED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                105
            )
            return false
        }
        return true
    }
}