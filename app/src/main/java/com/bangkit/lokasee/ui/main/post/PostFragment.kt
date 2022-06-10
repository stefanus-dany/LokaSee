package com.bangkit.lokasee.ui.main.post

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bangkit.lokasee.R
import com.bangkit.lokasee.data.Post
import com.bangkit.lokasee.data.retrofit.ApiConfig
import com.bangkit.lokasee.databinding.FragmentPostBinding
import com.bangkit.lokasee.ui.main.MainViewModel
import com.bangkit.lokasee.util.getAvatarUrl
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem

class PostFragment : Fragment() {
    private lateinit var post: Post
    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        post = arguments?.getParcelable("POST")!!
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageUrlList = mutableListOf<CarouselItem>()
        post.images.forEach {
            imageUrlList.add(CarouselItem(ApiConfig.HOST + it))
        }
        with(binding){
            carouselPost.setData(imageUrlList)
            txtPostTitle.text = post.title
            txtPostDesc.text = post.desc
            txtPostPrice.text = "Rp ${post.price.toString()}"
            txtPostArea.text = "${post.area} m²"
            txtPostLocation.text = "${post.kecamatan!!.title}, ${post.kabupaten!!.title}"
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }
            btnRoute.setOnClickListener {

            }
        }
    }
}