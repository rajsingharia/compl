package com.example.compl.view.fragments.authority

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.compl.R
import com.example.compl.adapter.ComplainAllAdapter
import com.example.compl.application.ComplainApplication
import com.example.compl.databinding.DialogCustomLoadingBinding
import com.example.compl.databinding.FragmentAuthorityHomeBinding
import com.example.compl.model.Complaindata
import com.example.compl.util.Constants
import com.example.compl.viewmodel.ComplainViewModel
import com.example.compl.viewmodel.ComplainViewModelFactory
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class AuthorityHomeFragment : Fragment() ,ComplainAllAdapter.OnItemClickListener{

    private lateinit var binding:FragmentAuthorityHomeBinding
    private lateinit var dialog: Dialog
    private val complainViewModel: ComplainViewModel by viewModels {
        ComplainViewModelFactory((requireActivity().application  as ComplainApplication).repository)
    }
    private var pieData = MutableList(6) {0}
    private var pieData2 = MutableList(6) {0}
    private lateinit var latestComplains:MutableList<Complaindata>


    override fun onResume() {
        super.onResume()
        pieData= MutableList(6){0}
        pieData2= MutableList(6){0}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLoadingDialogBox(true)

        complainViewModel.allComplainsData.observe(viewLifecycleOwner,{
            it?.let {
                for(data in it){
                    pieData[Constants.pieDataTypeToPosition[data.type]!!]++
                    pieData2[Constants.pieDataTypeToPosition2[data.resolvedStatus]!!]++
                }
                showLoadingDialogBox(false)
                setPieData()
                setPieData2()
            }
        })

        val adapter = ComplainAllAdapter(this)
        binding.authorityComplainHomeRecyclerView.adapter=adapter
        binding.authorityComplainHomeRecyclerView.layoutManager= LinearLayoutManager(context)

        //complainViewModel.getAllComplains()
        complainViewModel.allComplainsData.observe(viewLifecycleOwner, {
            showLoadingDialogBox(false)
            it?.let {
                latestComplains = it
                while(latestComplains.size>3){
                    latestComplains.removeFirst()
                }
                adapter.differ.submitList(latestComplains)
            }
        })

        binding.authorityComplainHomeMore.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.authority_fragment_view,AuthorityAllComplainFragment())
                .addToBackStack(null)
                .commit()
        }

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

        binding.authorityComplainHomePieChart.description.text="Visual Representation Of Status"
        val pieData = PieData(pieDataSet)
        binding.authorityComplainHomePieChart.setCenterTextSizePixels(20F)
        binding.authorityComplainHomePieChart.setEntryLabelTextSize(15F)
        binding.authorityComplainHomePieChart.data = pieData
        binding.authorityComplainHomePieChart.invalidate()
        binding.authorityComplainHomePieChart.isDrawHoleEnabled = true
        binding.authorityComplainHomePieChart.setEntryLabelColor(Color.parseColor("#FFFFFF"))
        binding.authorityComplainHomePieChart.animateY(1400, Easing.EaseInOutQuad)
    }

    private fun setPieData2() {
        val listPie2 = ArrayList<PieEntry>()
        val listColors2 = ArrayList<Int>()

        for (i in 0..2){
            val t= Constants.pieDataTypes2[i]
            listPie2.add(PieEntry(pieData2[Constants.pieDataTypeToPosition2[t]!!].toFloat(), t))
            listColors2.add(Color.parseColor(Constants.pieDataColour2[i]))
        }

        val pieDataSet2 = PieDataSet(listPie2,"")
        pieDataSet2.colors = listColors2

        binding.authorityComplainHomePieChart2.description.text="Visual Representation Of Complains"
        val pieData2 = PieData(pieDataSet2)
        binding.authorityComplainHomePieChart2.setCenterTextSizePixels(20F)
        binding.authorityComplainHomePieChart2.setEntryLabelTextSize(15F)
        binding.authorityComplainHomePieChart2.data = pieData2
        binding.authorityComplainHomePieChart2.invalidate()
        binding.authorityComplainHomePieChart2.isDrawHoleEnabled = true
        binding.authorityComplainHomePieChart2.setEntryLabelColor(Color.parseColor("#FFFFFF"))
        binding.authorityComplainHomePieChart2.animateY(1400, Easing.EaseInOutQuad)
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

    override fun OnItemClick(position: Int) {
        val clickedComplain:Complaindata=latestComplains[position]
        val id=clickedComplain.id

        goToSpecificAuthorityEditFragment(id)
    }

    private fun goToSpecificAuthorityEditFragment(id:String) {

        val ldf = AuthorityEditComplainFragment()
        val args = Bundle()
        args.putString("id", id)
        ldf.arguments = args

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.authority_fragment_view, ldf)
            .addToBackStack(null)
            .commit()
    }


}