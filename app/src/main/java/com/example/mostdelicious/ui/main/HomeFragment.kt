package com.example.mostdelicious.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mostdelicious.adapters.MealPostsAdapter
import com.example.mostdelicious.databinding.FragmentHomeBinding
import com.example.mostdelicious.helpers.LoadingState
import com.example.mostdelicious.helpers.observeNotNull
import com.example.mostdelicious.helpers.viewBinding
import com.example.mostdelicious.viewmodels.AuthViewModel
import com.example.mostdelicious.viewmodels.MainViewModel
import com.example.mostdelicious.viewmodels.PostViewModel

class HomeFragment : Fragment() {

    private val binding by viewBinding<FragmentHomeBinding>()
    private val authViewModel by activityViewModels<AuthViewModel>()
    private val mainViewModel by activityViewModels<MainViewModel>()

    private var adapter: MealPostsAdapter? = null

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

        binding.rvPosts.layoutManager = LinearLayoutManager(requireContext())
        binding.btnAddPost.setOnClickListener {
            val action = HomeFragmentDirections.actionGlobalToPostFragment()
            findNavController().navigate(action)
        }

        authViewModel.currentUser.observeNotNull(viewLifecycleOwner) {
            binding.userNameTv.text = "Hello ${it.name}"
        }


        mainViewModel.loadingState.postValue(LoadingState.Loading)
        mainViewModel.feedData.observe(viewLifecycleOwner) {
            if (it.isAllMet()) {
                adapter = MealPostsAdapter(
                    it.posts!!.toMutableList(),
                    it.allUsers!!,
                    it.userLikedPosts!!
                )
                mainViewModel.loadingState.postValue(LoadingState.Loaded)
                binding.rvPosts.adapter = adapter
            }
        }
    }
}