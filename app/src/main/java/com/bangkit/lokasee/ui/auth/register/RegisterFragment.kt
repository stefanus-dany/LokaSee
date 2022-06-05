package com.bangkit.lokasee.ui.auth.register

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bangkit.lokasee.R
import com.bangkit.lokasee.databinding.FragmentRegisterBinding
import com.bangkit.lokasee.helper.ViewHelper.gone

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupAction()
    }

    private fun setupViewModel() {
//        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
//        registerViewModel = factory.create(RegisterViewModel::class.java)
    }

    private fun setupAction() {
        binding.btnRegister.setOnClickListener {
            binding.progressBar.gone()
            val name = binding.inputName.text.toString()
            val email = binding.inputEmail.text.toString()
            val password = binding.inputPassword.text.toString()
            when {
                name.isEmpty() -> {
                    with(binding) {
                        inputName.error = getString(R.string.error_input_name)
                        inputName.requestFocus()
                        binding.progressBar.gone()
                    }
                }
                email.isEmpty() -> {
                    with(binding) {
                        inputEmail.error = getString(R.string.error_input_email)
                        inputEmail.requestFocus()
                        progressBar.gone()
                    }
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    with(binding) {
                        inputEmail.error = getString(R.string.error_email_not_valid)
                        inputEmail.requestFocus()
                        progressBar.gone()
                    }
                }
                password.isEmpty() -> {
                    with(binding) {
                        inputPassword.error = getString(R.string.error_input_password)
                        inputPassword.requestFocus()
                        progressBar.gone()
                    }
                }
                password.length < 6 -> {
                    with(binding) {
                        inputPassword.error =
                            getString(R.string.error_password_less_than_6_char)
                        inputPassword.requestFocus()
                        progressBar.gone()
                    }
                }
                else -> {
                    //TODO SAVE USER
                    binding.progressBar.gone()
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                }
            }
        }

        binding.btnGoToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}