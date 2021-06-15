package com.example.compl.view.fragments.authority

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.compl.R
import com.example.compl.application.ComplainApplication
import com.example.compl.databinding.DialogCustomLoadingBinding
import com.example.compl.databinding.FragmentAuthorityHomeBinding
import com.example.compl.util.Constants
import com.example.compl.viewmodel.ComplainViewModel
import com.example.compl.viewmodel.ComplainViewModelFactory
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class AuthorityHomeFragment : Fragment() {

    private lateinit var binding:FragmentAuthorityHomeBinding
    private lateinit var dialog: Dialog
    private val complainViewModel: ComplainViewModel by viewModels {
        ComplainViewModelFactory((requireActivity().application  as ComplainApplication).repository)
    }
    private var pieData = MutableList(6) {0}


    override fun onResume() {
        super.onResume()
        pieData= MutableList(6){0}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLoadingDialogBox(true)

        complainViewModel.allComplainsData.observe(viewLifecycleOwner,{
            it?.let {
                for(data in it){
                    pieData[Constants.pieDataTypeToPosition[data.type]!!]++
                }
                showLoadingDialogBox(false)
                setPieData()
            }
        })

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentAuthorityHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    private fun setPieData() {
        val listPie = ArrayList<PieEntry>()
        val listColors = ArrayList<Int>()

        for (i in 0..5){
            val t= Constants.pieDataTypes[i]
            listPie.add(PieEntry(pieData[Constants.pieDataTypeToPosition[t]!!].toFloat(), t))
            listColors.add(Color.parseColor(Constants.pieDataColour[i]))
        }

        val pieDataSet = PieDataSet(listPie,"")
        pieDataSet.colors = listColors

        binding.authorityComplainHomePieChart.description.text="Visual Representation Of Complains"
        val pieData = PieData(pieDataSet)
        binding.authorityComplainHomePieChart.setCenterTextSizePixels(20F)
        binding.authorityComplainHomePieChart.setEntryLabelTextSize(15F)
        binding.authorityComplainHomePieChart.data = pieData
        binding.authorityComplainHomePieChart.invalidate()
        binding.authorityComplainHomePieChart.isDrawHoleEnabled = true
        binding.authorityComplainHomePieChart.setEntryLabelColor(Color.parseColor("#FFFFFF"))
        binding.authorityComplainHomePieChart.animateY(1400, Easing.EaseInOutQuad)
    }

    private fun showLoadingDialogBox(visible:Boolean) {
        if(!visible) {
            dialog.dismiss()
            return
        }
        dialog= Dialog(requireContext())
        val binding: DialogCustomLoadingBinding = DialogCustomLoadingBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        dialog.show()

    }

}