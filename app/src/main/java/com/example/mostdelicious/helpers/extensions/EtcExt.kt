package com.example.mostdelicious.helpers.extensions

import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.viewbinding.ViewBinding
import com.example.mostdelicious.MainActivity
import com.example.mostdelicious.R
import com.example.mostdelicious.helpers.AutoClearedViewBinding
import com.example.mostdelicious.ui.main.HomeFragmentDirections
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun Long.toDateString(
    format: String = "yyyy-MM-dd HH:mm:ss",
    locale: Locale = Locale.getDefault(),
): String {
    val formatter = SimpleDateFormat(format, locale)
    val date = Date(this)
    return formatter.format(date)
}

fun View.setDebouncedOnClickListener(debounceTime: Long = 500, action: (View) -> Unit) {
    var lastClickTime = 0L

    setOnClickListener {
        val currentTime = SystemClock.elapsedRealtime()
        if (currentTime - lastClickTime >= debounceTime) {
            lastClickTime = currentTime
            action(it)
        }
    }
}


// LifeCycle
inline fun <reified T : FragmentActivity> Fragment.typedActivity(): T {
    return requireActivity() as T
}

fun <T : ViewBinding> Fragment.viewBinding(
    bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> T
): AutoClearedViewBinding<T> = AutoClearedViewBinding(this, bindingInflater)


// Etc
fun MainActivity.setupBottomBar() {
    binding.btnFavoritesBottomBar.setDebouncedOnClickListener {
        val navController = findNavController(R.id.fragmentContainerMain)

        // Prevent navigating to the same fragment
        if (navController.currentDestination?.id == R.id.favoritesFragment) {
            return@setDebouncedOnClickListener
        }

        // Navigate back if the last fragment was Favorites
        if (lastFragmentId == R.id.favoritesFragment) {
            navController.popBackStack()
        } else {
            // Record the current destination before navigating away
            lastFragmentId = navController.currentDestination?.id ?: R.id.homeFragment

            val action = HomeFragmentDirections.actionGlobalToFavoritesFragment()
            navController.navigate(action)
        }
    }

    binding.btnProfileBottomBar.setDebouncedOnClickListener {
        val navController = findNavController(R.id.fragmentContainerMain)

        if (navController.currentDestination?.id == R.id.profileFragment) {
            return@setDebouncedOnClickListener
        }

        if (lastFragmentId == R.id.profileFragment) {
            navController.popBackStack()
        } else {
            lastFragmentId = navController.currentDestination?.id ?: R.id.homeFragment

            val action = HomeFragmentDirections.actionGlobalToProfileFragment()
            navController.navigate(action)
        }
    }

    binding.btnFeedBottomBar.setDebouncedOnClickListener {
        val navController = findNavController(R.id.fragmentContainerMain)

        if (navController.currentDestination?.id == R.id.homeFragment) {
            return@setDebouncedOnClickListener
        }

        // Clear back stack to return to the Home fragment
        navController.popBackStack(R.id.homeFragment, false)
        lastFragmentId = R.id.homeFragment
    }
}

const val EMPTY_VALUE = ""
const val LAST_UPDATED_KEY = "lastUpdated"
