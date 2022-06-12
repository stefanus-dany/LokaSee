package com.bangkit.lokasee.ui.main.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bangkit.lokasee.R
import com.bangkit.lokasee.data.Result
import com.bangkit.lokasee.databinding.FragmentMapBinding
import com.bangkit.lokasee.ui.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFadeThrough

class MapFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap
    private lateinit var mapViewModel: MapViewModel

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
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun setupViewModel() {
        val factory = ViewModelFactory.getInstance(requireContext())
        mapViewModel = factory.create(MapViewModel::class.java)
    }

    private fun setupMapView() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun loadPost() {
        mapViewModel.getAllPosts().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                    }

                    is Result.Success -> {
                        val resultResponse = result.data.data
                        val mapMarker = mutableMapOf<Int, Any>()
                        if (resultResponse != null) {
                            resultResponse.forEach {
                                if (it != null) {
                                    val latLng = LatLng(it.latitude, it.longitude)
                                    mMap.addMarker(
                                        MarkerOptions()
                                            .position(latLng)
                                            .title(it.title)
                                    )?.let { it1 -> mapMarker.put(it.id, it1)  }
                                }
                            }

                            val defaultCameraSetup = LatLng(-6.200000, 106.816666)
                            mMap.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    defaultCameraSetup,
                                    5f
                                )
                            )

                            mMap.setOnMarkerClickListener {
                                for (i in mapMarker){
                                    if (i.value == it){
                                        val bundle = Bundle()
                                        bundle.putParcelable("POST", resultResponse[i.key])
                                        findNavController().navigate(R.id.action_mapFragment_to_postFragment, bundle)
                                    }
                                }
                                false
                            }
                        }
                    }

                    is Result.Error -> {
                        Snackbar.make(
                            binding.root,
                            getString(R.string.message_alert_register_failed),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        setupViewModel()
        setupMapView()
        loadPost()
    }
}