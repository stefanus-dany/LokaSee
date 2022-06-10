package com.bangkit.lokasee.ui.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
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
import com.bangkit.lokasee.ui.main.MainViewModel
import com.bangkit.lokasee.util.ViewHelper.gone
import com.bangkit.lokasee.util.ViewHelper.visible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class LocationFilterBottomSheet : BottomSheetDialogFragment() {
    private var tempFilter: HashMap<String, Int?> = HashMap()
    private var arrProvinsi = mutableListOf<String>()
    private var arrKabupaten = mutableListOf<String>()
    private var arrKecamatan = mutableListOf<String>()
    private val mainViewModel: MainViewModel by activityViewModels()
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
        getProvinsi()
        tempFilter = currentFilter
        with(binding){
            if (tempFilter[KABUPATEN] != null)
                selectFilterKabupaten.editText?.setText(kabupatenList.filter {
                    it.id == tempFilter[KABUPATEN]!!
                }.first().title)
            if (tempFilter[KECAMATAN] != null)
                selectFilterKecamatan.editText?.setText(kecamatanList.filter {
                    it.id == tempFilter[KECAMATAN]!!
                }.first().title)

            btnClearFilter.setOnClickListener{
                for ((key, value) in tempFilter) {
                    tempFilter[key] = null
                }
                selectFilterProvinsi.editText?.text?.clear()
                selectFilterKabupaten.editText?.text?.clear()
                selectFilterKecamatan.editText?.text?.clear()
            }

            btnApplyFilter.setOnClickListener{

                val provinsi: Provinsi? = provinsiList.firstOrNull{
                    it.title == selectFilterProvinsi.editText?.text.toString()
                }
                val kabupaten: Kabupaten? = kabupatenList.firstOrNull{
                    it.title == selectFilterKabupaten.editText?.text.toString()
                }
                val kecamatan: Kecamatan? = kecamatanList.firstOrNull{
                    it.title == selectFilterKecamatan.editText?.text.toString()
                }



                tempFilter[PROVINSI] = provinsi!!.id
                tempFilter[KABUPATEN] = kabupaten!!.id
                tempFilter[KECAMATAN] = kecamatan!!.id

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

    private fun getProvinsi() {
        mainViewModel.getAllProvinsi().observe(this) { result ->
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

                        arrProvinsi = provinsiList.mapTo(mutableListOf()) {
                            it.title
                        }
                        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, arrProvinsi)
                        if (tempFilter[PROVINSI] != null)
                            binding.selectFilterProvinsi.editText?.setText(provinsiList[tempFilter[PROVINSI]!!].title)
                        binding.atvProvinsi.setAdapter(arrayAdapter)
                        if (currentFilter[PROVINSI] != null) {
                            binding.selectFilterProvinsi.editText?.setText(provinsiList.filter {
                                it.id == currentFilter[PROVINSI]!!
                            }.first().title)
                            getKabupatenByProvinsi(currentFilter[PROVINSI]!!)
                        }
                        binding.selectFilterProvinsi.editText?.addTextChangedListener {
                            val provinsi: Provinsi? = provinsiList.firstOrNull{
                                it.title == binding.selectFilterProvinsi.editText?.text.toString()
                            }
                            getKabupatenByProvinsi(provinsi!!.id)
                            binding.selectFilterKabupaten.editText?.text?.clear()
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
        mainViewModel.getKabupatenByProvinsi(provinsiId).observe(this) { result ->
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
                        arrKabupaten = kabupatenList.mapTo(mutableListOf()) {
                            it.title
                        }
                        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, arrKabupaten)
                        if (currentFilter[KABUPATEN] != null)
                            binding.selectFilterKabupaten.editText?.setText(kabupatenList[currentFilter[KABUPATEN]!!].title)
                        binding.atvKabupaten.setAdapter(arrayAdapter)
                        if (currentFilter[KABUPATEN] != null) {
                            binding.selectFilterKabupaten.editText?.setText(kabupatenList.filter {
                                it.id == currentFilter[KABUPATEN]!!
                            }.first().title)
                            getKecamatanByKabupaten(currentFilter[KABUPATEN]!!)
                        }
                        binding.selectFilterKabupaten.editText?.addTextChangedListener {
                            val kabupaten: Kabupaten? = kabupatenList.firstOrNull{
                                it.title == binding.selectFilterKabupaten.editText?.text.toString()
                            }
                            getKecamatanByKabupaten(kabupaten!!.id)
                            binding.selectFilterKecamatan.editText?.text?.clear()
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
        mainViewModel.getKecamatanByKabupaten(kabupatenId).observe(this) { result ->
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
                        arrKecamatan = kecamatanList.mapTo(mutableListOf()) {
                            it.title
                        }
                        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, arrKecamatan)
                        if (currentFilter[KECAMATAN] != null)
                            binding.selectFilterKecamatan.editText?.setText(kecamatanList[currentFilter[KECAMATAN]!!].title)
                        binding.atvKecamatan.setAdapter(arrayAdapter)
                        binding.selectFilterKecamatan.editText?.addTextChangedListener {
                            val kecamatan: Kecamatan? = kecamatanList.firstOrNull{
                                it.title == binding.selectFilterKecamatan.editText?.text.toString()
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
        fun newInstance(): LocationFilterBottomSheet {
            return LocationFilterBottomSheet()
        }
    }
}