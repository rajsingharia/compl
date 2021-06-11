package com.example.compl.view.fragments.complainers
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.compl.adapter.ComplainAllAdapter
import com.example.compl.application.ComplainApplication
import com.example.compl.databinding.DialogCustomLoadingBinding
import com.example.compl.databinding.FragmentComplainAllBinding
import com.example.compl.model.Complaindata
import com.example.compl.viewmodel.ComplainViewModel
import com.example.compl.viewmodel.ComplainViewModelFactory

class ComplainAllFragment : Fragment() , ComplainAllAdapter.OnItemClickListener{

    private lateinit var binding:FragmentComplainAllBinding
    private val complainViewModel: ComplainViewModel by viewModels {
        ComplainViewModelFactory((requireActivity().application  as ComplainApplication).repository)
    }
    private lateinit var dialog:Dialog
    private lateinit var allComplains:MutableList<Complaindata>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showLoadingDialogBox(true)

        val adapter = ComplainAllAdapter(this)
        binding.complainAllRecyclerView.adapter=adapter
        binding.complainAllRecyclerView.layoutManager= LinearLayoutManager(context)

        complainViewModel.getAllComplains()
        complainViewModel.allComplainsData.observe(viewLifecycleOwner,{
            showLoadingDialogBox(false)
            it?.let {
                allComplains=it
                adapter.differ.submitList(it)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentComplainAllBinding.inflate(layoutInflater,container,false)
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

    override fun OnItemClick(position: Int) {

        //TODO: Open Edit Screen For Complains

        val clickedComplain:Complaindata=allComplains[position]
        Toast.makeText(requireContext(),clickedComplain.title,Toast.LENGTH_SHORT).show()
    }

}