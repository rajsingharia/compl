package com.example.compl.view.fragments.complainers
import android.app.Dialog
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
    private var uid:String?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showLoadingDialogBox(true)

        val adapter = ComplainAllAdapter(this)
        binding.complainAllRecyclerView.adapter=adapter
        binding.complainAllRecyclerView.layoutManager= LinearLayoutManager(context)

        if(uid==null) {
            complainViewModel.getAllComplains()
            complainViewModel.allComplainsData.observe(viewLifecycleOwner, {
                showLoadingDialogBox(false)
                it?.let {
                    allComplains = it
                    adapter.differ.submitList(it)
                }
            })
        }
        else{
            complainViewModel.getSpecificComplain(null,null,uid)
            complainViewModel.specificComplainData.observe(viewLifecycleOwner, {
                showLoadingDialogBox(false)
                it?.let {
                    allComplains = it
                    adapter.differ.submitList(it)
                }
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        uid = requireArguments().getString("uid")

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
        val id=clickedComplain.id

        goToSpecificComplainEditFragment(id)

    }

    private fun goToSpecificComplainEditFragment(id:String) {

        val ldf = ComplainEditFragment()
        val args = Bundle()
        args.putString("id", id)
        ldf.arguments = args

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.complain_fragment_view, ldf)
            .addToBackStack(null)
            .commit()
    }

}