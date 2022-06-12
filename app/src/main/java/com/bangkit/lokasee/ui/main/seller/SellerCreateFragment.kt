package com.bangkit.lokasee.ui.main.seller

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Slide
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bangkit.lokasee.R
import com.bangkit.lokasee.data.Kabupaten
import com.bangkit.lokasee.data.Kecamatan
import com.bangkit.lokasee.data.Provinsi
import com.bangkit.lokasee.data.Result
import com.bangkit.lokasee.data.store.UserStore
import com.bangkit.lokasee.databinding.FragmentSellerCreateBinding
import com.bangkit.lokasee.ui.ViewModelFactory
import com.bangkit.lokasee.ui.main.seller.adapter.InputPostImageListAdapter
import com.bangkit.lokasee.util.*
import com.bangkit.lokasee.util.ViewHelper.gone
import com.bangkit.lokasee.util.ViewHelper.visible
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.transition.MaterialFadeThrough
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SellerCreateFragment : Fragment() {

    private var _binding: FragmentSellerCreateBinding? = null
    private val binding get() = _binding!!
    private var selectedImages = mutableListOf<Bitmap>()
    private var provinsiList = mutableListOf<Provinsi>()
    private var kabupatenList = mutableListOf<Kabupaten>()
    private var kecamatanList = mutableListOf<Kecamatan>()
    private lateinit var selectedProvinsi: Provinsi
    private lateinit var selectedKabupaten: Kabupaten
    private lateinit var selectedKecamatan: Kecamatan
    private lateinit var sellerViewModel: SellerViewModel
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
        setupViewModel()
        listPostImageListAdapter = InputPostImageListAdapter(selectedImages)
        binding.rvPostImageList.setHasFixedSize(true)
        binding.rvPostImageList.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.rvPostImageList.adapter = listPostImageListAdapter

        getProvinsiData()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnPostUpload.setOnClickListener{
            createPost()
        }

        binding.selectPostProvinsi.editText?.addTextChangedListener {
            if(it.toString() != ""){
                selectedProvinsi = getProvinsi(
                    binding.selectPostProvinsi.editText?.text.toString(),
                    provinsiList
                )
                binding.selectPostKabupaten.editText?.text?.clear()
                binding.selectPostKecamatan.editText?.text?.clear()
                getKabupatenByProvinsi(selectedProvinsi.id)
            }
        }

        binding.selectPostKabupaten.editText?.addTextChangedListener {
            if(it.toString() != ""){
                selectedKabupaten = getKabupaten(
                    binding.selectPostKabupaten.editText?.text.toString(),
                    kabupatenList
                )
                binding.selectPostKecamatan.editText?.text?.clear()
                getKecamatanByKabupaten(selectedKabupaten.id)
            }
        }

        binding.selectPostKecamatan.editText?.addTextChangedListener {
            if(it.toString() != "") {
                selectedKecamatan = getKecamatan(
                    binding.selectPostKecamatan.editText?.text.toString(),
                    kecamatanList
                )
            }
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

    private fun createPost(){
        val postTitle = binding.inputPostTitle.editText?.text.toString()
        val postDesc = binding.inputPostDesc.editText?.text.toString()
        val postPrice = binding.inputPostPrice.editText?.text.toString()
        val postArea = binding.inputPostArea.editText?.text.toString()
        val postAddress = binding.inputPostAddress.editText?.text.toString()

        val postProvinsi = getProvinsi(
            binding.selectPostProvinsi.editText?.text.toString(), provinsiList
        )
        val postKabupaten = getKabupaten(
            binding.selectPostKabupaten.editText?.text.toString(), kabupatenList
        )
        val postKecamatan = getKecamatan(
            binding.selectPostKecamatan.editText?.text.toString(), kecamatanList
        )

        val postLatitude = "10.8888"
        val postLongitude = "900.9933"

        val requestBody: HashMap<String, RequestBody> = HashMap()
        requestBody["title"] = createPartFromString(postTitle)
        requestBody["desc"] = createPartFromString(postDesc)
        requestBody["price"] = createPartFromString(postPrice)
        requestBody["area"] = createPartFromString(postArea)
        requestBody["address"] = createPartFromString(postAddress)
        requestBody["latitude"] = createPartFromString(postLatitude)
        requestBody["longitude"] = createPartFromString(postLongitude)
        requestBody["user_id"] = createPartFromString(UserStore.currentUser.id.toString())
        requestBody["provinsi_id"] = createPartFromString(postProvinsi.id.toString())
        requestBody["kabupaten_id"] = createPartFromString(postKabupaten.id.toString())
        requestBody["kecamatan_id"] = createPartFromString(postKecamatan.id.toString())

        val postImagesParts = mutableListOf<MultipartBody.Part>()

        val postImagesFile = mutableListOf<File>()

        for (index in 0 until selectedImages.size) {
            postImagesFile.add(convertBitmapToFile(selectedImages[index], requireContext()))
            val surveyBody: RequestBody =  postImagesFile[index].asRequestBody("image/*".toMediaTypeOrNull())
            postImagesParts.add(index, MultipartBody.Part.createFormData("images[]", postImagesFile[index].nameWithoutExtension, surveyBody))
        }

        val pDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Uploading Post"
        pDialog.setCancelable(false)
        pDialog.show()

        sellerViewModel.createPost(requestBody, postImagesParts.toTypedArray()).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Success -> {
                        val provinsiData = result.data.data
                        if (provinsiData != null){
                            pDialog.dismiss()
                            findNavController().navigateUp()
                            Toast.makeText(requireContext(), "New post uploaded!", Toast.LENGTH_LONG).show()
                        }
                        pDialog.hide()
                    }
                    is Result.Error -> {
                        pDialog.hide()
                        Log.e("Error",  result.error)
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun getProvinsiData() {
        sellerViewModel.getAllProvinsi().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progLocation.visible()
                        binding.selectPostProvinsi.gone()
                        binding.selectPostKabupaten.gone()
                        binding.selectPostKecamatan.gone()
                        binding.btnReload.gone()
                    }
                    is Result.Success -> {
                        val provinsiData = result.data.data
                        if (provinsiData?.isNotEmpty() == true){
                            provinsiList = provinsiData as MutableList<Provinsi>
                        }

                        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, provinsiList)
                        binding.atvProvinsi.setAdapter(arrayAdapter)

                        binding.progLocation.gone()
                        binding.btnReload.gone()
                        binding.selectPostProvinsi.visible()
                    }
                    is Result.Error -> {
                        Log.e("Error",  result.error)
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_LONG).show()
                        binding.progLocation.gone()
                        binding.selectPostProvinsi.gone()
                        binding.btnReload.visible()
                        binding.btnReload.setOnClickListener {
                            getProvinsiData()
                        }
                    }
                }
            }
        }
    }

    private fun getKabupatenByProvinsi(provinsiId: Int) {
        sellerViewModel.getKabupatenByProvinsi(provinsiId).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progLocation.visible()
                        binding.selectPostKabupaten.gone()
                        binding.selectPostKecamatan.gone()
                        binding.btnReload.gone()
                    }
                    is Result.Success -> {
                        val kabupatenData = result.data.data
                        if (kabupatenData?.isNotEmpty() == true){
                            kabupatenList = kabupatenData as MutableList<Kabupaten>
                        }

                        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, kabupatenList)
                        binding.atvKabupaten.setAdapter(arrayAdapter)

                        binding.selectPostKabupaten.visible()
                        binding.progLocation.gone()
                        binding.btnReload.gone()
                    }
                    is Result.Error -> {
                        Log.e("Error",  result.error)
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_LONG).show()
                        binding.progLocation.gone()
                        binding.selectPostKabupaten.gone()
                        binding.btnReload.visible()
                        binding.btnReload.setOnClickListener {
                            getKabupatenByProvinsi(provinsiId)
                        }
                    }
                }
            }
        }
    }

    private fun getKecamatanByKabupaten(kabupatenId: Int) {
        sellerViewModel.getKecamatanByKabupaten(kabupatenId).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.selectPostKecamatan.gone()
                        binding.progLocation.gone()
                        binding.btnReload.gone()
                    }
                    is Result.Success -> {
                        val kecamatanData = result.data.data
                        if (kecamatanData?.isNotEmpty() == true){
                            kecamatanList = kecamatanData as MutableList<Kecamatan>
                        }

                        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, kecamatanList)
                        binding.atvKecamatan.setAdapter(arrayAdapter)

                        binding.selectPostKecamatan.visible()
                        binding.progLocation.gone()
                        binding.btnReload.gone()
                    }
                    is Result.Error -> {
                        Log.e("Error",  result.error)
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_LONG).show()
                        binding.progLocation.gone()
                        binding.selectPostKecamatan.gone()
                        binding.btnReload.visible()
                        binding.btnReload.setOnClickListener {
                            getKecamatanByKabupaten(kabupatenId)
                        }
                    }
                }
            }
        }
    }

    private fun setupViewModel(){
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        sellerViewModel = factory.create(SellerViewModel::class.java)
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
            }
        }
}