package com.bangkit.lokasee.ui.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.bangkit.lokasee.R
import com.bangkit.lokasee.data.Kabupaten
import com.bangkit.lokasee.data.Kecamatan
import com.bangkit.lokasee.data.Provinsi
import com.bangkit.lokasee.data.Result
import com.bangkit.lokasee.data.store.*
import com.bangkit.lokasee.data.store.FilterStore.currentFilter
import com.bangkit.lokasee.data.store.FilterStore.kabupatenList
import com.bangkit.lokasee.data.store.FilterStore.kecamatanList
import com.bangkit.lokasee.data.store.FilterStore.provinsiList
import com.bangkit.lokasee.databinding.ModalLocationFilterBinding
import com.bangkit.lokasee.util.ViewHelper.gone
import com.bangkit.lokasee.util.ViewHelper.visible
import com.bangkit.lokasee.util.getKabupaten
import com.bangkit.lokasee.util.getKecamatan
import com.bangkit.lokasee.util.getProvinsi
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class LocationFilterBottomSheet(homeViewModel: HomeViewModel) : BottomSheetDialogFragment() {
    private var tempFilter: HashMap<String, Int?> = HashMap()
    private val homeViewModel = homeViewModel
    private lateinit var binding: ModalLocationFilterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ModalLocationFilterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tempFilter = currentFilter
        getProvinsiData()
        with(binding){
            btnClearFilter.setOnClickListener{
                for ((key, value) in tempFilter) {
                    tempFilter[key] = null
                }
                selectFilterProvinsi.editText?.text?.clear()
                selectFilterKabupaten.editText?.text?.clear()
                selectFilterKecamatan.editText?.text?.clear()
                binding.selectFilterKabupaten.gone()
                binding.selectFilterKecamatan.gone()
            }

            btnApplyFilter.setOnClickListener{
                currentFilter[PROVINSI] = tempFilter[PROVINSI]
                currentFilter[KABUPATEN] = tempFilter[KABUPATEN]
                currentFilter[KECAMATAN] = tempFilter[KECAMATAN]

                for ((key, value) in currentFilter) {
                    Log.e(key, value.toString())
                }

                dismiss()
            }
        }
    }

    private fun getProvinsiData() {
        homeViewModel.getAllProvinsi().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progLocation.visible()
                        binding.selectFilterProvinsi.gone()
                        binding.selectFilterKabupaten.gone()
                        binding.selectFilterKecamatan.gone()
                    }
                    is Result.Success -> {
                        binding.progLocation.gone()
                        val provinsiData = result.data.data
                        if (provinsiData?.isNotEmpty() == true){
                            provinsiList = provinsiData as MutableList<Provinsi>
                        }

                        if (tempFilter[PROVINSI] != null) {
                            binding.selectFilterProvinsi.editText?.setText(
                                getProvinsi(
                                    tempFilter[PROVINSI]!!,
                                    provinsiList
                                ).title
                            )
                            getKabupatenByProvinsi(tempFilter[PROVINSI]!!)
                        }

                        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, provinsiList)
                        binding.atvProvinsi.setAdapter(arrayAdapter)
                        binding.selectFilterProvinsi.editText?.addTextChangedListener {
                            if(it.toString() != ""){
                                val selectedProvinsi = getProvinsi(
                                    binding.selectFilterProvinsi.editText?.text.toString(),
                                    provinsiList
                                )
                                binding.selectFilterKabupaten.editText?.text?.clear()
                                binding.selectFilterKecamatan.editText?.text?.clear()
                                tempFilter[PROVINSI] = selectedProvinsi.id
                                tempFilter[KABUPATEN] = null
                                tempFilter[KECAMATAN] = null
                                getKabupatenByProvinsi(selectedProvinsi.id)
                            }
                        }

                        binding.selectFilterProvinsi.visible()
                    }
                    is Result.Error -> {
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_LONG).show()
                        binding.progLocation.gone()
                        binding.selectFilterProvinsi.gone()
                    }
                }
            }
        }
    }

    private fun getKabupatenByProvinsi(provinsiId: Int) {
        homeViewModel.getKabupatenByProvinsi(provinsiId).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.selectFilterKabupaten.gone()
                        binding.selectFilterKecamatan.gone()
                        binding.progLocation.visible()
                    }
                    is Result.Success -> {
                        binding.progLocation.gone()
                        binding.selectFilterKabupaten.visible()
                        val kabupatenData = result.data.data
                        if (kabupatenData?.isNotEmpty() == true){
                            kabupatenList = kabupatenData as MutableList<Kabupaten>
                        }

                        if (tempFilter[KABUPATEN] != null) {
                            binding.selectFilterKabupaten.editText?.setText(
                                getKabupaten(
                                    tempFilter[KABUPATEN]!!,
                                    kabupatenList
                                ).title
                            )
                            getKecamatanByKabupaten(tempFilter[KABUPATEN]!!)
                        }

                        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, kabupatenList)
                        binding.atvKabupaten.setAdapter(arrayAdapter)
                        binding.selectFilterKabupaten.editText?.addTextChangedListener {
                            if(it.toString() != ""){
                                val selectedKabupaten = getKabupaten(
                                    binding.selectFilterKabupaten.editText?.text.toString(),
                                    kabupatenList
                                )
                                tempFilter[KABUPATEN] = selectedKabupaten.id
                                tempFilter[KECAMATAN] = null
                                binding.selectFilterKecamatan.editText?.text?.clear()
                                getKecamatanByKabupaten(selectedKabupaten.id)
                            }
                        }

                        binding.selectFilterKabupaten.visible()
                    }
                    is Result.Error -> {
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_LONG).show()
                        binding.progLocation.gone()
                        binding.selectFilterKabupaten.gone()
                    }
                }
            }
        }
    }

    private fun getKecamatanByKabupaten(kabupatenId: Int) {
        homeViewModel.getKecamatanByKabupaten(kabupatenId).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.selectFilterKecamatan.gone()
                        binding.progLocation.visible()
                    }
                    is Result.Success -> {
                        binding.progLocation.gone()
                        binding.selectFilterKecamatan.visible()
                        val kecamatanData = result.data.data
                        if (kecamatanData?.isNotEmpty() == true){
                            kecamatanList = kecamatanData as MutableList<Kecamatan>
                        }

                        if (tempFilter[KECAMATAN] != null) {
                            binding.selectFilterKecamatan.editText?.setText(
                                getKecamatan(
                                    tempFilter[KECAMATAN]!!,
                                    kecamatanList
                                ).title
                            )
                        }

                        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, kecamatanList)
                        binding.atvKecamatan.setAdapter(arrayAdapter)
                        binding.selectFilterKecamatan.editText?.addTextChangedListener {
                            if(it.toString() != "") {
                                val selectedKecamatan = getKecamatan(
                                    binding.selectFilterKecamatan.editText?.text.toString(),
                                    kecamatanList
                                )
                                tempFilter[KECAMATAN] = selectedKecamatan.id
                            }
                        }

                        binding.selectFilterKecamatan.visible()
                    }
                    is Result.Error -> {
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_LONG).show()
                        binding.progLocation.gone()
                        binding.selectFilterKecamatan.gone()
                    }
                }
            }
        }
    }


    companion object {
        const val TAG = "CustomBottomSheetDialogFragment"
    }
}