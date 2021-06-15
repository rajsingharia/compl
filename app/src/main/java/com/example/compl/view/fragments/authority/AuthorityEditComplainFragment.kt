package com.example.compl.view.fragments.authority

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.compl.R
import com.example.compl.application.ComplainApplication
import com.example.compl.databinding.DialogCustomLoadingBinding
import com.example.compl.databinding.FragmentAuthorityEditComplainBinding
import com.example.compl.model.Complaindata
import com.example.compl.viewmodel.ComplainViewModel
import com.example.compl.viewmodel.ComplainViewModelFactory

class AuthorityEditComplainFragment : Fragment() {

    private lateinit var binding:FragmentAuthorityEditComplainBinding
    private val dropDownList = arrayOf("Close","Under Process","Completed")
    private val complainViewModel: ComplainViewModel by viewModels {
        ComplainViewModelFactory((requireActivity().application as ComplainApplication).repository)
    }
    private var id:String?=null
    private lateinit var dialog: Dialog
    private lateinit var complainData: Complaindata
    private var currentStatus:String?=null


    override fun onResume() {
        super.onResume()
        val adapter = ArrayAdapter(requireContext(),R.layout.drop_down_item,dropDownList)
        binding.authorityAutoCompleteDropDownMenu.setAdapter(adapter)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.authorityAutoCompleteDropDownMenu.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentStatus = dropDownList[position]
                val status="Current Status :${currentStatus}"
                binding.authorityComplainCurrentStatus.text = status
            }
        }

        complainViewModel.specificComplainData.observe(viewLifecycleOwner,{ data->

            showLoadingDialogBox(false)

            if(data!=null){
                if(data.isNotEmpty()){
                    Log.d("raj",data[0].uid)
                    this.complainData = data[0]
                    binding.authorityComplainTitle.text = complainData.title
                    binding.authorityComplainAddress.text = complainData.address
                    binding.authorityComplainDescription.text = complainData.description
                    binding.authorityComplainPhoneNo.text = complainData.phoneNo
                    binding.authorityComplainType.text=complainData.type
                    val status="Current Status :${complainData.resolvedStatus}"
                    binding.authorityComplainCurrentStatus.text = status
                }
                else{
                    Toast.makeText(requireContext(),"Nothing Found", Toast.LENGTH_SHORT).show()
                }
            }
        })

        binding.authorityCurrentStateSaveDataBtn.setOnClickListener {
            showLoadingDialogBox(true)

            if(currentStatus==null){
                Toast.makeText(requireContext(),"Please Select Current Status",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val editedComplainData = Complaindata(complainData.type,complainData.title,complainData.description,complainData.phoneNo,complainData.imageUrl,complainData.address,complainData.uid,currentStatus!!,complainData.id)

            id?.let { id->

                complainViewModel.editSpecificComplain(id,editedComplainData)

                complainViewModel.complainAddUploadedError.observe(viewLifecycleOwner,{ e->
                    e?.let {
                        showLoadingDialogBox(false)
                        Toast.makeText(requireContext(),e,Toast.LENGTH_SHORT).show()

                        if(e=="successful"){
                            requireActivity().supportFragmentManager.popBackStackImmediate()
                        }
                    }

                })

            }

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAuthorityEditComplainBinding.inflate(inflater,container,false)
        id = requireArguments().getString("id")
        complainViewModel.getSpecificComplain(null,id,null)
        showLoadingDialogBox(true)

        return binding.root
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