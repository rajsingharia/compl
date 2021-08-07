package com.example.compl.view.fragments.authority

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
import com.example.compl.databinding.FragmentAuthorityAllComplainBinding
import com.example.compl.model.Complaindata
import com.example.compl.view.fragments.complainers.ComplainEditFragment
import com.example.compl.viewmodel.ComplainViewModel
import com.example.compl.viewmodel.ComplainViewModelFactory

class AuthorityAllComplainFragment : Fragment() , ComplainAllAdapter.OnItemClickListener{

    private lateinit var binding:FragmentAuthorityAllComplainBinding
    private lateinit var dialog:Dialog
    private lateinit var allComplains:MutableList<Complaindata>
    private val complainViewModel: ComplainViewModel by viewModels {
        ComplainViewModelFactory((requireActivity().application  as ComplainApplication).repository)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showLoadingDialogBox(true)

        val adapter = ComplainAllAdapter(this)
        binding.authorityAllComplainRecyclerView.adapter=adapter
        binding.authorityAllComplainRecyclerView.layoutManager= LinearLayoutManager(context)

            complainViewModel.getAllComplains()
            complainViewModel.allComplainsData.observe(viewLifecycleOwner, {
                showLoadingDialogBox(false)
                it?.let {
                    allComplains = it
                    adapter.differ.submitList(it)
                }
            })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentAuthorityAllComplainBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun OnItemClick(position: Int) {

        //TODO: Open Edit Screen For Complains

        val clickedComplain:Complaindata=allComplains[position]
        val id=clickedComplain.id

        goToSpecificAuthorityEditFragment(id)

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