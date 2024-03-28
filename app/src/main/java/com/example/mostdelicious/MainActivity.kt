package com.example.mostdelicious

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.example.mostdelicious.databinding.ActivityMainBinding
import com.example.mostdelicious.helpers.LoadingState
import com.example.mostdelicious.helpers.extensions.setupBottomBar
import com.example.mostdelicious.viewmodels.AuthViewModel
import com.example.mostdelicious.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    internal lateinit var binding: ActivityMainBinding
    private val authViewModel by viewModels<AuthViewModel>()
    private val mainViewModel by viewModels<MainViewModel>()

    internal var lastFragmentId: Int = R.id.homeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnMenu.setOnClickListener {
            val popupMenu  = PopupMenu(this, it)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                if(menuItem.itemId == R.id.menuOptLogout) {
                    authViewModel.logOut()
                    startActivity(Intent(this, AuthActivity::class.java))
                    finish()
                }
                return@setOnMenuItemClickListener true
            }
            popupMenu.inflate(R.menu.main_menu)
            popupMenu.show()
        }

        authViewModel.loadingState.observe(this, ::setLoading)
        setupBottomBar()


        lifecycleScope.launch {
            delay(1000)
            findNavController(R.id.fragmentContainerMain)
                .addOnDestinationChangedListener { controller, destination, arguments ->

                    when(destination.id) {
                        R.id.profileFragment -> {
                            binding.btnFeedBottomBar.setBackgroundColor(Color.WHITE)
                            binding.btnProfileBottomBar.setBackgroundColor(Color.LTGRAY)
                            binding.btnFavoritesBottomBar.setBackgroundColor(Color.WHITE)
                        }
                        R.id.favoritesFragment -> {
                            binding.btnFeedBottomBar.setBackgroundColor(Color.WHITE)
                            binding.btnProfileBottomBar.setBackgroundColor(Color.WHITE)
                            binding.btnFavoritesBottomBar.setBackgroundColor(Color.LTGRAY)
                        }
                        else  -> {
                            binding.btnFeedBottomBar.setBackgroundColor(Color.LTGRAY)
                            binding.btnProfileBottomBar.setBackgroundColor(Color.WHITE)
                            binding.btnFavoritesBottomBar.setBackgroundColor(Color.WHITE)
                        }
                    }

                }
        }

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

    override fun onBackPressed() {
        if (!findNavController(R.id.fragmentContainerMain)
                .popBackStack()
        ) //  use back button on every fragment but the feed fragment
            super.onBackPressed()

    }
}