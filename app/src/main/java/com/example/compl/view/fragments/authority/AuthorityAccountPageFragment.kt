package com.example.compl.view.fragments.authority
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.example.compl.R
import com.example.compl.application.ComplainApplication
import com.example.compl.databinding.DialogCustomLoadingBinding
import com.example.compl.databinding.FragmentAuthorityAccountPageBinding
import com.example.compl.model.AuthorityUser
import com.example.compl.util.OfflineData
import com.example.compl.view.activities.SplashActivity
import com.example.compl.viewmodel.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.theartofdev.edmodo.cropper.CropImage

class AuthorityAccountPageFragment : Fragment() {

    private lateinit var binding:FragmentAuthorityAccountPageBinding
    private lateinit var dialog:Dialog

    private val authViewModelFactory: AuthorityViewModel by viewModels{
        AuthorityViewModelFactory((requireActivity().application as ComplainApplication).repository)
    }
    private val loginSignupViewModel: LoginSignupViewModel by viewModels {
        LoginSignupViewModelFactory((requireActivity().application as ComplainApplication).repository)
    }

    private var imageSelected: Uri? =null
    private var imageUrl:String= DEFAULT_IMAGE
    private var imageUploadedOperationCount:Int=0
    private var imageUploadedLiveData: MutableLiveData<Int> = MutableLiveData<Int>()

    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>(){
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setAspectRatio(1,1)
                .getIntent(requireContext())
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }

    }


    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        this.binding = FragmentAuthorityAccountPageBinding.inflate(inflater,container,false)

        return this.binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.binding.authorityAccountLogOutBtn.setOnClickListener {
            loginSignupViewModel.logout().invokeOnCompletion {

                OfflineData(requireActivity()).run {
                    putLoginType(null)
                }

                ActivityCompat.finishAffinity(requireActivity())

                val i = Intent(requireContext(), SplashActivity::class.java)
                startActivity(i)
            }
        }

        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract){
            it?.let {
                this.binding.authorityAccountImageView.setImageURI(it)
                imageSelected=it
            }
        }

        this.binding.authorityAccountFloatingActionBtn.setOnClickListener {
            if(checkPermissionForCameraAndGallery()) {
                cropActivityResultLauncher.launch(null)
            }
            else{
                showRationalDialogForPermission()
            }
        }

        this.binding.authorityAccountSaveDataBtn.setOnClickListener {


            showLoadingDialogBox(true)

            if(imageSelected!=null){
                val type= OfflineData(requireActivity()).getLoginType()

                authViewModelFactory.uploadImage(imageSelected,type!!,"profile")

            }

            authViewModelFactory.uploadImageError.observe(viewLifecycleOwner,{
                it?.let { error->
                    if(error=="successful"){
                        imageUploadedLiveData.postValue(++imageUploadedOperationCount)
                        Toast.makeText(requireContext(), "Image upload $error",Toast.LENGTH_SHORT).show()
                        authViewModelFactory.uploadImageUrl.observe(viewLifecycleOwner,{ url->
                            url?.let {
                                imageUrl=url
                                Log.d("raj","Image url $url")
                                imageUploadedLiveData.postValue(++imageUploadedOperationCount)
                            }
                        })
                    }
                }
            })


            imageUploadedLiveData.observe(requireActivity(),{
                it?.let {
                    if(it==2) {
                        uploadUserData()
                    }
                }
            })
        }


    }



    private fun uploadUserData(){

        Log.d("raj","Uploading data")
        val name= this.binding.authorityAccountName.text.toString()
        val email= this.binding.authorityAccountEmail.text.toString()
        val mobileNumber= this.binding.authorityAccountPhoneNumber.text.toString()

        if(imageUrl!= DEFAULT_IMAGE){

            authViewModelFactory.updateAuthorityUser(AuthorityUser(name,email,mobileNumber,imageUrl,""))

            authViewModelFactory.getAuthorityUser()

            authViewModelFactory.authorityUserData.observe(viewLifecycleOwner, {
                if(it!=null){
                    OfflineData(requireActivity()).putUserInfoSet(true)
                }
            })

            authViewModelFactory.userError.observe(viewLifecycleOwner, {
                it?.let{
                    showLoadingDialogBox(false)
                    if(it=="SuccessFull"){
                        Toast.makeText(requireContext(),"SuccessFully Updated Data", Toast.LENGTH_SHORT).show()
                        this.binding.authorityAccountName.text.clear()
                        this.binding.authorityAccountEmail.text.clear()
                        this.binding.authorityAccountPhoneNumber.text.clear()
                        this.binding.authorityAccountImageView.setImageResource(R.drawable.ic_baseline_perm_identity_24)
                    }
                }
            })
        }

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

    private fun checkPermissionForCameraAndGallery() :Boolean {

        var permissionGranted=false

        Dexter.withContext(requireContext()).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            //android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                report?.let {
                    if(report.areAllPermissionsGranted()){
                        permissionGranted=true
                    }
                    else{
                        showRationalDialogForPermission()
                    }
                }

            }

            override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                showRationalDialogForPermission()
            }

        }
        ).onSameThread().check()

        return permissionGranted

    }
    private fun showRationalDialogForPermission() {
        AlertDialog.Builder(requireContext()).setMessage("It looks like you turned Off permission require for this feature.It can be enabled under Application Settings")
            .setPositiveButton("GO TO SETTINGS"){
                    _,_->
                try{
                    val intent=Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri= Uri.fromParts("package",
                        activity?.packageName,
                        null)
                    intent.data=uri
                    startActivity(intent)
                }
                catch (e: ActivityNotFoundException){
                    e.printStackTrace()
                }
            }.setNegativeButton("Cancel"){
                    dialog,_->
                dialog.dismiss()
            }.show()
    }

    companion object{
        private var DEFAULT_IMAGE="https://st3.depositphotos.com/13159112/17145/v/1600/depositphotos_171453724-stock-illustration-default-avatar-profile-icon-grey.jpg"
    }


}