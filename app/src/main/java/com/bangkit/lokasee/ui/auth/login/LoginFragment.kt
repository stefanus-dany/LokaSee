package com.bangkit.lokasee.ui.auth.login

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bangkit.lokasee.R
import com.bangkit.lokasee.databinding.FragmentLoginBinding
import com.bangkit.lokasee.util.ViewHelper.gone
import com.bangkit.lokasee.util.ViewHelper.visible


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupAction()
    }

    private fun setupViewModel() {
        //TODO
//        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
//        loginViewModel = factory.create(LoginViewModel::class.java)
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            binding.progressBar.visible()
            val email = binding.inputEmail.text.toString()
            val password = binding.inputPassword.text.toString()
            when {
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
                    //TODO LOGIN
                    binding.progressBar.gone()
                    findNavController().navigate(R.id.action_loginFragment_to_navigation_graph)
                    //TODO CLEAR ALL BACKSTACK
                }
            }
        }

        binding.btnGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}