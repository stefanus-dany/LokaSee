package com.bangkit.lokasee.ui.main.seller

import android.app.Activity
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
import com.bangkit.lokasee.data.Kabupaten
import com.bangkit.lokasee.data.Kecamatan
import com.bangkit.lokasee.data.Post
import com.bangkit.lokasee.data.Provinsi
import com.bangkit.lokasee.databinding.FragmentSellerUpdateBinding
import com.bangkit.lokasee.ui.main.seller.adapter.InputPostImageListAdapter
import com.bangkit.lokasee.util.uriToFile
import com.github.dhaval2404.imagepicker.ImagePicker
import java.io.File

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SellerUpdateFragment : Fragment() {

    private var _binding: FragmentSellerUpdateBinding? = null
    private val binding get() = _binding!!
    private var selectedImages = mutableListOf<File>()
    private val arrProvinsi = mutableListOf<Provinsi>()
    private val arrKabupaten = mutableListOf<Kabupaten>()
    private val arrKecamatan = mutableListOf<Kecamatan>()
    private lateinit var listPostImageListAdapter: InputPostImageListAdapter
    private lateinit var post: Post

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSellerUpdateBinding.inflate(inflater, container, false)

        post = arguments?.getParcelable("POST")!!
        listPostImageListAdapter = InputPostImageListAdapter(selectedImages)
        binding.rvPostImageList.setHasFixedSize(true)
        binding.rvPostImageList.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.rvPostImageList.adapter = listPostImageListAdapter

        val provinsiList = arrProvinsi.mapTo(mutableListOf()) { it.title }
        val kabupatenList = arrKabupaten.mapTo(mutableListOf()) { it.title }
        val kecamatanList = arrKecamatan.mapTo(mutableListOf()) { it.title }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            inputPostTitle.editText?.setText(post.title)
            inputPostDesc.editText?.setText(post.desc)
            inputPostPrice.editText?.setText(post.price)
            inputPostArea.editText?.setText(post.area)
            inputPostAddress.editText?.setText(post.address)
            inputPostAddress.editText?.setText(post.address)
            inputPostAddress.editText?.setText(post.address)
            selectPostProvinsi.editText?.setText(post.provinsi?.title)
            selectPostKabupaten.editText?.setText(post.kabupaten?.title)
            selectPostKecamatan.editText?.setText(post.kecamatan?.title)
        }
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
        binding.btnPostUpdate.setOnClickListener{
            //TODO ADD UPDATE LOGIC
        }
        binding.btnPostUpdate.setOnClickListener{
            //TODO ADD DELETE LOGIC
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
                selectedImages.add(imageFile)
                listPostImageListAdapter.notifyItemInserted(selectedImages.size - 1);
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(activity, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
}