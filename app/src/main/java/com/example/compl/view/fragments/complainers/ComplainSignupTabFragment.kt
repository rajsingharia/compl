package com.example.compl.view.fragments.complainers
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.compl.databinding.FragmentComplainSignupTabBinding
import com.example.compl.viewmodel.LoginSignupViewModel
import com.google.android.material.snackbar.Snackbar
import com.example.compl.application.ComplainApplication
import com.example.compl.databinding.DialogCustomLoadingBinding
import com.example.compl.util.OfflineData
import com.example.compl.view.activities.complainer.ComplainHomePage
import com.example.compl.viewmodel.LoginSignupViewModelFactory

class ComplainSignupTabFragment : Fragment() {

    private lateinit var mBinding:FragmentComplainSignupTabBinding

    private val loginSignupViewModel:LoginSignupViewModel by viewModels{
        LoginSignupViewModelFactory((requireActivity().application as ComplainApplication).repository)
    }
    private lateinit var dialog:Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding=FragmentComplainSignupTabBinding.inflate(inflater,container,false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.complainSignupBtn.setOnClickListener {
            if(checkDetailsValidity()){
                showLoadingDialogBox(true)
                val email=mBinding.complainSignupEmail.text.toString()
                val password=mBinding.complainSignupPassword.text.toString()
                loginSignupViewModel.register(email,password)

                loginSignupViewModel.error.observe(viewLifecycleOwner,{
                    it?.let {
                        showLoadingDialogBox(false)
                        Snackbar.make(requireView(),it, Snackbar.LENGTH_SHORT).apply { show() }
                    }
                })

                loginSignupViewModel.currentUser.observe(viewLifecycleOwner,{
                    it?.let {

                        //saving offline
                        OfflineData(requireActivity()).putLoginType("com")
                        OfflineData(requireActivity()).putUserInfoSet(false)

                        showLoadingDialogBox(false)
                        Snackbar.make(requireView(),"Registered Successful", Snackbar.LENGTH_SHORT).apply { show() }
                        val intent=Intent(context,ComplainHomePage::class.java)
                        intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                    }
                })

            }
        }
    }

    private fun checkDetailsValidity():Boolean {
        val email=mBinding.complainSignupEmail.text.toString()
        val password=mBinding.complainSignupPassword.text.toString()
        val confirmPassword=mBinding.complainSignupConfirmPassword.text.toString()

        if(email.isEmpty()||password.isEmpty()||confirmPassword.isEmpty()){
            Snackbar.make(requireView(),"Credentials Empty", Snackbar.LENGTH_SHORT).apply { show() }
            return false
        }
        else if(password!=confirmPassword){
            Snackbar.make(requireView(),"Password miss-match", Snackbar.LENGTH_SHORT).apply { show() }
            return false
        }
        return true
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