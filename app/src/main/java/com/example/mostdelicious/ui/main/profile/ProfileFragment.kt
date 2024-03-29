package com.example.mostdelicious.ui.main.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mostdelicious.R
import com.example.mostdelicious.adapters.MealPostsAdapter
import com.example.mostdelicious.databinding.FragmentProfileBinding
import com.example.mostdelicious.helpers.LoadingState
import com.example.mostdelicious.helpers.extensions.viewBinding
import com.example.mostdelicious.models.MealPost
import com.example.mostdelicious.ui.dialog.RatingDialog
import com.example.mostdelicious.ui.dialog.RecipeDialog
import com.example.mostdelicious.viewmodels.AuthViewModel
import com.example.mostdelicious.viewmodels.MainViewModel
import com.example.mostdelicious.viewmodels.PostViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private val binding by viewBinding(FragmentProfileBinding::inflate)
    private val mainViewModel by activityViewModels<MainViewModel>()
    private val postViewModel by activityViewModels<PostViewModel>()

    private lateinit var adapter: MealPostsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvPosts.layoutManager = LinearLayoutManager(requireContext())

        mainViewModel.loadingState.postValue(LoadingState.Loading)
        adapter = MealPostsAdapter(
            mutableListOf(),
            listOf(),
            listOf(),
            onLikeButtonClick = { postLikeClicked ->
                mainViewModel.likeTogglePost(postLikeClicked.id)
            },
            onRatingClicked = {
                RatingDialog(it) { post, rating ->
                    postViewModel.ratePost(post, rating) {
                        Snackbar.make(binding.root, "Rated post! (:", Snackbar.LENGTH_SHORT).show()
                    }
                }.show(childFragmentManager, "Rating dialog")
            },
            onRecipeRequest = ::recipeRequest
        )
        binding.rvPosts.adapter = adapter

        mainViewModel.profilePostsData.observe(viewLifecycleOwner) { feedData ->
            Log.d("Profile data", "Feed data is " + feedData.toString())
            if (feedData.allResourcesAvailable()) {
                adapter.updateFeedData(feedData)
                mainViewModel.loadingState.postValue(LoadingState.Loaded)
            }
        }
    }


    private fun recipeRequest(post: MealPost) {
        postViewModel.recipeRequest(post)
        RecipeDialog(viewLifecycleOwner, postViewModel.recipeLiveData)
            .show(childFragmentManager, "Recipe dialog")
    }


}