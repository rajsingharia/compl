package com.example.compl.view.fragments.complainers
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.compl.R
import com.example.compl.application.ComplainApplication
import com.example.compl.databinding.DialogCustomLoadingBinding
import com.example.compl.databinding.FragmentComplainEditBinding
import com.example.compl.model.ComplainUser
import com.example.compl.model.Complaindata
import com.example.compl.viewmodel.ComplainViewModel
import com.example.compl.viewmodel.ComplainViewModelFactory

class ComplainEditFragment : Fragment() {

    private lateinit var binding:FragmentComplainEditBinding

    private val complainViewModel:ComplainViewModel by viewModels {
        ComplainViewModelFactory((requireActivity().application as ComplainApplication).repository)
    }

    private var id: String?=null
    private lateinit var dialog:Dialog
    private lateinit var complainData:Complaindata

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        complainViewModel.specificComplainData.observe(viewLifecycleOwner,{ data->

            showLoadingDialogBox(false)

            if(data!=null){
                if(data.isNotEmpty()){
                    Log.d("raj",data[0].uid)
                    this.complainData = data[0]
                    binding.complainEditTitle.setText(complainData.title)
                    binding.complainEditAddress.setText(complainData.address)
                    binding.complainEditDescription.setText(complainData.description)
                    binding.complainEditPhoneNumber.setText(complainData.phoneNo)
                }
                else{
                    Toast.makeText(requireContext(),"Nothing Found",Toast.LENGTH_SHORT).show()
                }
            }
        })

        binding.complainEditSaveDataBtn.setOnClickListener {
            showLoadingDialogBox(true)
            val title=binding.complainEditTitle.text.toString()
            val address=binding.complainEditAddress.text.toString()
            val description=binding.complainEditDescription.text.toString()
            val phoneNo=binding.complainEditPhoneNumber.text.toString()

            val editedComplainData = Complaindata(complainData.type,title,description,phoneNo,complainData.imageUrl,address,complainData.uid,complainData.resolvedStatus,complainData.id)

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


        binding= FragmentComplainEditBinding.inflate(inflater,container,false)

        showLoadingDialogBox(true)

        id = requireArguments().getString("id")

        Log.d("raj",id.toString())

        if(id!==null){
            complainViewModel.getSpecificComplain(null,id,null)
        }
        else{
            showLoadingDialogBox(false)
        }
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