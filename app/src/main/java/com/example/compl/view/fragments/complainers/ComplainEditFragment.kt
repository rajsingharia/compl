package com.example.compl.view.fragments.complainers
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.compl.R
import com.example.compl.databinding.FragmentComplainEditBinding

class ComplainEditFragment : Fragment() {

    private lateinit var binding:FragmentComplainEditBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentComplainEditBinding.inflate(inflater,container,false)
        return binding.root
    }

}