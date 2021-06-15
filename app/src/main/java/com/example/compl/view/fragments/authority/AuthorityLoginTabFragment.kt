package com.example.compl.view.fragments.authority

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.compl.R
import com.example.compl.application.ComplainApplication
import com.example.compl.databinding.FragmentAuthorityLoginTabBinding
import com.example.compl.databinding.FragmentComplainLoginTabBinding
import com.example.compl.util.OfflineData
import com.example.compl.view.activities.authority.AuthorityHomePage
import com.example.compl.view.activities.complainer.ComplainHomePage
import com.example.compl.viewmodel.LoginSignupViewModel
import com.example.compl.viewmodel.LoginSignupViewModelFactory
import com.google.android.material.snackbar.Snackbar

class AuthorityLoginTabFragment : Fragment() {

    private lateinit var binding:FragmentAuthorityLoginTabBinding
    private val loginSignupViewModel: LoginSignupViewModel by viewModels{
        LoginSignupViewModelFactory((requireActivity().application as ComplainApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentAuthorityLoginTabBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.authorityLoginBtn.setOnClickListener {
            if(checkDetailValidity()){
                binding.authorityLoginProgressbar.visibility=View.VISIBLE
                val email=binding.authorityLoginEmail.text.toString()
                val password=binding.authorityLoginPassword.text.toString()

                loginSignupViewModel.login(email,password)

                loginSignupViewModel.currentUser.observe(viewLifecycleOwner,{
                    it?.let {
                        //saving offline type
                        OfflineData(requireActivity()).putLoginType("auth")
                        OfflineData(requireActivity()).putUserInfoSet(false)

                        binding.authorityLoginProgressbar.visibility=View.GONE
                        Snackbar.make(requireView(),"Login Successful", Snackbar.LENGTH_SHORT).apply { show() }
                        val intent= Intent(context, AuthorityHomePage::class.java)
                        intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                    }
                })
                loginSignupViewModel.error.observe(viewLifecycleOwner,{
                    it?.let {
                        binding.authorityLoginProgressbar.visibility=View.GONE
                        Snackbar.make(requireView(),it, Snackbar.LENGTH_SHORT).apply { show() }
                    }
                })
            }
        }

    }

    private fun checkDetailValidity() :Boolean{
        val email=binding.authorityLoginEmail.text.toString()
        val password=binding.authorityLoginPassword.text.toString()

        if(email.isEmpty()||password.isEmpty()){
            Snackbar.make(requireView(),"Credentials Empty", Snackbar.LENGTH_SHORT).apply { show() }
            return false
        }
        return true
    }

}