package com.bangkit.lokasee.ui.main.seller

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Slide
import com.bangkit.lokasee.R
import com.bangkit.lokasee.databinding.FragmentSellerCreateBinding
import com.bangkit.lokasee.ui.main.seller.adapter.InputPostImageListAdapter
import com.bangkit.lokasee.util.themeColor
import com.bangkit.lokasee.util.uriToFile
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialFadeThrough
import java.io.File

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SellerCreateFragment : Fragment() {

    private var _binding: FragmentSellerCreateBinding? = null
    private val binding get() = _binding!!
    private var selectedImages = mutableListOf<Bitmap>()
    private lateinit var listPostImageListAdapter: InputPostImageListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.lokasee_motion_duration_large).toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSellerCreateBinding.inflate(inflater, container, false)

        listPostImageListAdapter = InputPostImageListAdapter(selectedImages)
        binding.rvPostImageList.setHasFixedSize(true)
        binding.rvPostImageList.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.rvPostImageList.adapter = listPostImageListAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnPostAddImage.setOnClickListener{
            ImagePicker.with(this)
                .compress(2048)
                .crop(4f, 3f)
                .galleryMimeTypes(
                    mimeTypes = arrayOf(
                        "image/png",
                        "image/jpg",
                        "image/jpeg"
                    )
                )
                .createIntent { intent ->
                    startForInputImageResult.launch(intent)
                }
        }

        returnTransition = Slide().apply {
            duration = resources.getInteger(R.integer.lokasee_motion_duration_medium).toLong()
            addTarget(R.id.constraintLayout1)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val startForInputImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == Activity.RESULT_OK) {
                val fileUri = data?.data!!
                val imageFile = uriToFile(fileUri, requireContext())
                val bitmap = BitmapFactory.decodeFile(imageFile.path)
                selectedImages.add(bitmap)
                listPostImageListAdapter.notifyDataSetChanged()
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(activity, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
}