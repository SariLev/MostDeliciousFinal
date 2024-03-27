package com.example.mostdelicious.ui.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.mostdelicious.databinding.FragmentRegisterBinding
import com.example.mostdelicious.dto.UserRegisterForm
import com.example.mostdelicious.helpers.observeNotNull
import com.example.mostdelicious.helpers.viewBinding
import com.example.mostdelicious.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private val binding by viewBinding<FragmentRegisterBinding>()
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

        binding.tvHaveAccount.setOnClickListener {
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

        binding.btnRegister.setOnClickListener {
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
                return@setOnClickListener
            }

            viewModel.createUser(UserRegisterForm(email, password, fullName))
        }


    }
}