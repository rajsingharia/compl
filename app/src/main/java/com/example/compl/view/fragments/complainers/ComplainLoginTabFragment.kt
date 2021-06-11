package com.example.compl.view.fragments.complainers
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.compl.application.ComplainApplication
import com.example.compl.databinding.FragmentComplainLoginTabBinding
import com.example.compl.util.OfflineData
import com.example.compl.view.activities.complainer.ComplainHomePage
import com.example.compl.viewmodel.LoginSignupViewModel
import com.example.compl.viewmodel.LoginSignupViewModelFactory
import com.google.android.material.snackbar.Snackbar

class ComplainLoginTabFragment : Fragment() {

    private lateinit var mBinding:FragmentComplainLoginTabBinding
    private val loginSignupViewModel:LoginSignupViewModel by viewModels{
        LoginSignupViewModelFactory((requireActivity().application as ComplainApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
       mBinding=FragmentComplainLoginTabBinding.inflate(inflater,container,false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.complainLoginBtn.setOnClickListener {
            if(checkDetailValidity()){
                mBinding.complainLoginProgressbar.visibility=View.VISIBLE
                val email=mBinding.complainLoginEmail.text.toString()
                val password=mBinding.complainLoginPassword.text.toString()

                loginSignupViewModel.login(email,password)

                loginSignupViewModel.currentUser.observe(viewLifecycleOwner,{
                    it?.let {
                        //saving offline type
                        OfflineData(requireActivity()).putLoginType("com")
                        OfflineData(requireActivity()).putUserInfoSet(false)

                        mBinding.complainLoginProgressbar.visibility=View.GONE
                        Snackbar.make(requireView(),"Login Successful", Snackbar.LENGTH_SHORT).apply { show() }
                        val intent= Intent(context, ComplainHomePage::class.java)
                        intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                    }
                })
                loginSignupViewModel.error.observe(viewLifecycleOwner,{
                    it?.let {
                        mBinding.complainLoginProgressbar.visibility=View.GONE
                        Snackbar.make(requireView(),it, Snackbar.LENGTH_SHORT).apply { show() }
                    }
                })
            }
        }

    }

    private fun checkDetailValidity() :Boolean{
        val email=mBinding.complainLoginEmail.text.toString()
        val password=mBinding.complainLoginPassword.text.toString()

        if(email.isEmpty()||password.isEmpty()){
            Snackbar.make(requireView(),"Credentials Empty", Snackbar.LENGTH_SHORT).apply { show() }
            return false
        }
        return true
    }
}