package com.example.mostdelicious.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mostdelicious.R
import com.example.mostdelicious.adapters.MealPostsAdapter
import com.example.mostdelicious.databinding.FragmentHomeBinding
import com.example.mostdelicious.helpers.LoadingState
import com.example.mostdelicious.helpers.extensions.viewBinding
import com.example.mostdelicious.helpers.observeNotNull
import com.example.mostdelicious.ui.dialog.RatingDialog
import com.example.mostdelicious.viewmodels.AuthViewModel
import com.example.mostdelicious.viewmodels.MainViewModel
import com.example.mostdelicious.viewmodels.PostViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val binding by viewBinding(FragmentHomeBinding::inflate)
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

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = MealPostsAdapter(
            onLikeButtonClick = { postLikeClicked ->
                mainViewModel.likeTogglePost(postLikeClicked.id)
            },
            onRatingClicked = {
                RatingDialog(it) { post, rating ->
                    postViewModel.ratePost(post, rating) {
                        Snackbar.make(binding.root, "Rated post! (:", Snackbar.LENGTH_SHORT).show()
                    }
                }.show(childFragmentManager, "Rating dialog")
            })

        binding.rvPosts.layoutManager = LinearLayoutManager(requireContext())
        binding.btnAddPost.setOnClickListener {
            val action = HomeFragmentDirections.actionGlobalToPostMealFragment()
            findNavController()
                .navigate(action)
        }

        mainViewModel.currentUser.observeNotNull(viewLifecycleOwner) {
            binding.userNameTv.text = "Hello ${it.name}"
        }

        binding.rvPosts.adapter = adapter
        mainViewModel.feedData.observe(viewLifecycleOwner) { feedData ->
            if (feedData.allResourcesAvailable()) {
                adapter.updateFeedData(feedData)
                mainViewModel.loadingState.postValue(LoadingState.Loaded)
            }
        }
    }
}