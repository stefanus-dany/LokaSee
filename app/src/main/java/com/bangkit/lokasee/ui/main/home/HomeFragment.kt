package com.bangkit.lokasee.ui.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.lokasee.R
import com.bangkit.lokasee.data.Post
import com.bangkit.lokasee.data.Result
import com.bangkit.lokasee.data.store.UserStore.currentUserToken
import com.bangkit.lokasee.databinding.FragmentHomeBinding
import com.bangkit.lokasee.ui.ViewModelFactory
import com.bangkit.lokasee.ui.auth.login.LoginViewModel
import com.bangkit.lokasee.ui.main.MainViewModel
import com.bangkit.lokasee.util.ViewHelper.gone
import com.bangkit.lokasee.util.ViewHelper.visible

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var listPost = mutableListOf<Post>()
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupViewModel()
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLocation.setOnClickListener{
            showFilterModal()
        }
        loadPost()
    }

    private fun setupViewModel() {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireContext())
        homeViewModel = factory.create(HomeViewModel::class.java)
    }

    private fun loadPost() {
        Log.e("ssssssssss", currentUserToken)
        homeViewModel.getAllPostsFiltered().observe(viewLifecycleOwner) { result ->

            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visible()
                    }

                    is Result.Success -> {
                        binding.progressBar.gone()
                        val resultResponse = result.data.data
                        if (resultResponse != null){
                            listPost = resultResponse as MutableList<Post>
                            setPostData(listPost)
                        }
                    }

                    is Result.Error -> {
                        binding.progressBar.gone()
                        AlertDialog.Builder(requireContext()).apply {
                            setTitle(getString(R.string.title_alert_failed))
                            setMessage(result.error)
                            setPositiveButton(getString(R.string.back)) { dialog, _ ->
                                dialog.dismiss()
                            }
                            create()
                            show()
                        }
                    }
                }
            }
        }
    }

    private fun setPostData(listPost: List<Post>) {
        binding.rvNearbyLand.setHasFixedSize(true)
        binding.rvNearbyLand.layoutManager = LinearLayoutManager(requireContext())
        val listPostAdapter = PostListAdapter(listPost as MutableList<Post>)
        binding.rvNearbyLand.adapter = listPostAdapter
    }

    private fun showFilterModal() {
        getFragmentManager()?.let { LocationFilterBottomSheet(homeViewModel).show(it, LocationFilterBottomSheet.TAG) }
    }

}
