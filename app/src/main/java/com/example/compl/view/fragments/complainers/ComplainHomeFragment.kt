package com.example.compl.view.fragments.complainers
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.compl.R
import com.example.compl.application.ComplainApplication
import com.example.compl.databinding.DialogCustomLoadingBinding
import com.example.compl.databinding.FragmentComplainHomeBinding
import com.example.compl.util.Constants
import com.example.compl.util.OfflineData
import com.example.compl.viewmodel.ComplainViewModel
import com.example.compl.viewmodel.ComplainViewModelFactory
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class ComplainHomeFragment : Fragment() {

    private lateinit var mBinding:FragmentComplainHomeBinding
    private lateinit var dialog:Dialog
    private val complainViewModel: ComplainViewModel by viewModels {
        ComplainViewModelFactory((requireActivity().application  as ComplainApplication).repository)
    }
    private var pieData = MutableList(6) {0}


    override fun onResume() {
        super.onResume()
        pieData= MutableList(6){0}
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        mBinding= FragmentComplainHomeBinding.inflate(inflater,container,false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showLoadingDialogBox(true)

        //complainViewModel.getAllComplains()

        complainViewModel.allComplainsData.observe(viewLifecycleOwner,{
            it?.let {
                for(data in it){
                    pieData[Constants.pieDataTypeToPosition[data.type]!!]++
                }
                showLoadingDialogBox(false)
                setPieData()
            }
        })

        if(!OfflineData(requireActivity()).getUserInfoSet()){

            showAccountDialogBox()

        }

        mBinding.image1a.setOnClickListener {
            startSpecificComplain(mBinding.text1a.text.toString())
        }
        mBinding.image1b.setOnClickListener {
            startSpecificComplain(mBinding.text1b.text.toString())
        }
        mBinding.image2a.setOnClickListener {
            startSpecificComplain(mBinding.text2a.text.toString())
        }
        mBinding.image2b.setOnClickListener {
            startSpecificComplain(mBinding.text2b.text.toString())
        }
        mBinding.image3a.setOnClickListener {
            startSpecificComplain(mBinding.text3a.text.toString())
        }
        mBinding.image3b.setOnClickListener {
            startSpecificComplain(mBinding.text3b.text.toString())
        }

    }

    private fun setPieData() {
        val listPie = ArrayList<PieEntry>()
        val listColors = ArrayList<Int>()

        for (i in 0..5){
            val t=Constants.pieDataTypes[i]
            listPie.add(PieEntry(pieData[Constants.pieDataTypeToPosition[t]!!].toFloat(), t))
            listColors.add(Color.parseColor(Constants.pieDataColour[i]))
        }

        val pieDataSet = PieDataSet(listPie,"")
        pieDataSet.colors = listColors

        mBinding.complainHomePieChart.description.text="Visual Representation Of Complains"
        val pieData = PieData(pieDataSet)
        mBinding.complainHomePieChart.setCenterTextSizePixels(20F)
        mBinding.complainHomePieChart.setEntryLabelTextSize(15F)
        mBinding.complainHomePieChart.data = pieData
        mBinding.complainHomePieChart.invalidate()
        mBinding.complainHomePieChart.isDrawHoleEnabled = true
        mBinding.complainHomePieChart.setEntryLabelColor(Color.parseColor("#FFFFFF"))
        mBinding.complainHomePieChart.animateY(1400, Easing.EaseInOutQuad)
    }


    private fun startSpecificComplain(specific:String) {

        val ldf = ComplainAddSpecificFragment()
        val args = Bundle()
        args.putString("specific", specific)
        ldf.arguments = args

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.complain_fragment_view, ldf,"ComplainAddSpecificFragment")
            .addToBackStack(null)
            .commit()
    }



    private fun showAccountDialogBox() {

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Alert")
        builder.setMessage("Please Set Up Account First")
        builder.setIcon(R.drawable.ic_baseline_warning_24)

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
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