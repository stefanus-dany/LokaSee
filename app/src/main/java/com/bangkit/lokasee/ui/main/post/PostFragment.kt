package com.bangkit.lokasee.ui.main.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bangkit.lokasee.R
import com.bangkit.lokasee.data.Post
import com.bangkit.lokasee.data.retrofit.ApiConfig
import com.bangkit.lokasee.databinding.FragmentPostBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.transition.MaterialFadeThrough
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem

class PostFragment : Fragment(), OnMapReadyCallback {
    private lateinit var post: Post
    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.lokasee_motion_duration_large).toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        post = arguments?.getParcelable("POST")!!
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMapView()

        val imageUrlList = mutableListOf<CarouselItem>()
        post.images.forEach {
            imageUrlList.add(CarouselItem(ApiConfig.HOST + it))
        }
        with(binding) {
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

    private fun setupMapView() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        // TODO ADD LAT LONG OF POST
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                sydney,
                5f
            )
        )
    }
}