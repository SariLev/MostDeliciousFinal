package com.example.mostdelicious.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.example.mostdelicious.MainActivity
import com.example.mostdelicious.R
import com.example.mostdelicious.databinding.FragmentLoginBinding
import com.example.mostdelicious.helpers.extensions.setDebouncedOnClickListener
import com.example.mostdelicious.helpers.extensions.viewBinding
import com.example.mostdelicious.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val binding by viewBinding(FragmentLoginBinding::inflate)
    private val viewModel by activityViewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvNoAccount.setDebouncedOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            findNavController().navigate(action)
        }

        binding.btnLogin.setDebouncedOnClickListener {
            val email = binding.etEmailLogin.text.toString()
            val password = binding.etPasswordLogin.text.toString()


            if(email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill email and password",Toast.LENGTH_SHORT).show()
                return@setDebouncedOnClickListener
            }
            viewModel.signIn(email, password) {
                requireActivity().finish()
                startActivity(Intent(requireContext(), MainActivity::class.java))

            }
        }
    }
}