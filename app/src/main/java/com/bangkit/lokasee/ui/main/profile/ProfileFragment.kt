package com.bangkit.lokasee.ui.main.profile

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.bangkit.lokasee.R
import com.bangkit.lokasee.data.store.UserStore.currentUser
import com.bangkit.lokasee.databinding.FragmentProfileBinding
import com.bangkit.lokasee.util.getAvatarUrl
import com.bangkit.lokasee.util.uriToFile
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.transition.MaterialFadeThrough

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private var selectedAvatar: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.lokasee_motion_duration_large).toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        binding.txtUserName.text = currentUser.name
        binding.txtUserEmail.text = currentUser.email
        binding.imgUserProfile.setOnClickListener {
            ImagePicker.with(this)
                .compress(1024)
                .crop(2f, 2f)
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
        Glide.with(requireActivity())
            .load(getAvatarUrl(currentUser))
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .into(binding.imgUserProfile)

        if(currentUser.avatarUrl != ""){
            Glide.with(this)
                .asBitmap()
                .load(getAvatarUrl(currentUser))
                .into(object : CustomTarget<Bitmap>(){
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                    ) {
                        selectedAvatar = resource
                    }
                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                })
        }
        return binding.root
    }

    private val startForInputImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == Activity.RESULT_OK) {
                val fileUri = data?.data!!
                val imageFile = uriToFile(fileUri, requireContext())
                val bitmap = BitmapFactory.decodeFile(imageFile.path)
                selectedAvatar = bitmap
                Glide.with(requireActivity())
                    .load(bitmap)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(binding.imgUserProfile)
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(activity, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
        }
}