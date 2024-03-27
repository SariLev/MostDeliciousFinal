package com.example.mostdelicious.ui.main.post

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.example.mostdelicious.MainActivity
import com.example.mostdelicious.R
import com.example.mostdelicious.databinding.FragmentMealPostBinding
import com.example.mostdelicious.dto.PostDto
import com.example.mostdelicious.helpers.LoadingState
import com.example.mostdelicious.helpers.viewBinding
import com.example.mostdelicious.models.MealPost
import com.example.mostdelicious.viewmodels.AuthViewModel
import com.example.mostdelicious.viewmodels.MainViewModel
import com.example.mostdelicious.viewmodels.PostViewModel
import dagger.hilt.android.AndroidEntryPoint

class MealPostFragment : Fragment() {
    private val binding by viewBinding<FragmentMealPostBinding>()
    private val authViewModel by activityViewModels<AuthViewModel>()
    private val postViewModel by activityViewModels<PostViewModel>()
    private val mainViewModel by activityViewModels<MainViewModel>()

    var uri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivMealPost.setOnClickListener {
            (requireActivity() as? MainActivity)?.let {
                if (it.requestGalleryPermissions()) {
                    openGallery()
                }
            }
        }
        binding.btnSharePost.setOnClickListener {
            if (uri == null) {
                Toast.makeText(
                    requireContext(),
                    "You must choose an image to post",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            val mealName = binding.etPostTitle.text.toString()
            val mealContent = binding.etPostContent.text.toString()

            val dto = PostDto(id = null, uri, mealName, mealContent)

            postViewModel.createOrUpdatePost(dto) {
                Toast.makeText(
                    requireContext(),
                    "Your post will appear on the feed shortly!",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().popBackStack() // return to feed screen (or profile screen)
            }
        }

        postViewModel.loadingState.observe(
            viewLifecycleOwner,
            (requireActivity() as MainActivity)::setLoading
        )

    }


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setType("image/*")
        startActivityForResult(intent, 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK) {
            this.uri = data?.data
            data?.data?.let {
                binding.ivMealPost.setImageURI(it)
            }
        } else if (requestCode == 105 && resultCode == RESULT_OK) {
            openGallery()
        }
    }

}