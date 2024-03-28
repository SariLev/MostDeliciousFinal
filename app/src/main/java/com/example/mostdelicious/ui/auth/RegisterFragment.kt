package com.example.mostdelicious.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.mostdelicious.databinding.FragmentRegisterBinding
import com.example.mostdelicious.dto.UserRegisterForm
import com.example.mostdelicious.helpers.LoadingState
import com.example.mostdelicious.helpers.extensions.setDebouncedOnClickListener
import com.example.mostdelicious.helpers.extensions.viewBinding
import com.example.mostdelicious.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private val binding by viewBinding(FragmentRegisterBinding::inflate)
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

        binding.tvHaveAccount.setDebouncedOnClickListener {
            findNavController().popBackStack()
        }

        binding.etFullNameRegister.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.etFullNameRegister.error = null
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })


        binding.btnRegister.setDebouncedOnClickListener {
            val email = binding.etEmailRegister.text.toString()
            val password = binding.etPasswordRegister.text.toString()
            val fullName = binding.etFullNameRegister.text.toString()
            if (fullName.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please enter a valid full name",
                    Toast.LENGTH_SHORT
                ).show()
                binding.etFullNameRegister.error = "Please enter a valid full name"
                return@setDebouncedOnClickListener
            }

            viewModel.createUser(UserRegisterForm(email, password, fullName)) {
                requireActivity().finish()
                startActivity(Intent(requireContext(), MainActivity::class.java))
            }
        }


    }
}