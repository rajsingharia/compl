package com.example.compl.view.fragments.complainers
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.compl.R
import com.example.compl.adapter.ComplainAllAdapter
import com.example.compl.application.ComplainApplication
import com.example.compl.databinding.DialogCustomLoadingBinding
import com.example.compl.databinding.FragmentComplainAddSpecificBinding
import com.example.compl.model.Complaindata
import com.example.compl.viewmodel.ComplainViewModel
import com.example.compl.viewmodel.ComplainViewModelFactory

class ComplainAddSpecificFragment : Fragment() ,ComplainAllAdapter.OnItemClickListener{


    //VARIABLES

    private lateinit var binding:FragmentComplainAddSpecificBinding
    private lateinit var specific:String
    private val complainViewModel:ComplainViewModel by viewModels {
        ComplainViewModelFactory((requireActivity().application as ComplainApplication).repository)
    }
    private var specificTypeComplainData:MutableList<Complaindata> = mutableListOf()
    private lateinit var dialog:Dialog
    private var dataFound:MutableLiveData<Boolean> = MutableLiveData()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        specific = requireArguments().getString("specific").toString()

        binding= FragmentComplainAddSpecificBinding.inflate(inflater,container,false)
        return binding.root

    }


    override fun onResume() {
        super.onResume()
        dataFound.value=true
        getSpecificDataAndManageRecyclerView()
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.complainAddSpecificFab.setOnClickListener {
            addSpecificComplain(specific)
        }

    }



    private fun getSpecificDataAndManageRecyclerView() {

        showLoadingDialogBox(true)

        val adapter = ComplainAllAdapter(this)
        binding.complainAddSpecificRecyclerView.adapter=adapter
        binding.complainAddSpecificRecyclerView.layoutManager = LinearLayoutManager(context)

        complainViewModel.getSpecificComplain(specific,null,null)

        complainViewModel.specificComplainData.observe(viewLifecycleOwner,{
            it?.let {
                if(it.isEmpty())
                    dataFound.postValue(false)
                else{
                    specificTypeComplainData=it
                    adapter.differ.submitList(it)
                    dataFound.postValue(true)
                }
                showLoadingDialogBox(false)
            }
        })

        dataFound.observe(viewLifecycleOwner,{
            it?.let {
                if(!it){
                    binding.complainAddSpecificRecyclerView.visibility=View.GONE
                    binding.complainAddSpecificNothingToShow.visibility=View.VISIBLE
                }
            }
        })

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

        val clickedComplain:Complaindata=specificTypeComplainData[position]

        //TODO: Edit Specific Complain

        val id=clickedComplain.id

        goToSpecificComplainFragment(id)

    }

    private fun goToSpecificComplainFragment(id:String) {

        val ldf = ComplainEditFragment()
        val args = Bundle()
        args.putString("id", id)
        ldf.arguments = args

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.complain_fragment_view, ldf)
            .addToBackStack(null)
            .commit()
    }


    private fun addSpecificComplain(specific:String) {

        val ldf = ComplainAddFragment()
        val args = Bundle()
        args.putString("specific", specific)
        ldf.arguments = args

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.complain_fragment_view, ldf,"ComplainAddFragment")
            .addToBackStack(null)
            .commit()
    }

}