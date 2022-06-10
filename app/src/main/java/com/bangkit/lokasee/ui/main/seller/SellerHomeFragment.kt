package com.bangkit.lokasee.ui.main.seller

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bangkit.lokasee.R
import com.bangkit.lokasee.databinding.FragmentSellerHomeBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class SellerHomeFragment : Fragment() {

    private var _binding: FragmentSellerHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSellerHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabSellerAddPost.setOnClickListener {
            findNavController().navigate(R.id.action_sellerHomeFragment_to_sellerCreateFragment)
        }
        binding.btnBack.setOnClickListener{
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}