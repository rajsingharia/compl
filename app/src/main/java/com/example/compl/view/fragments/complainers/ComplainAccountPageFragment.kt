package com.example.compl.view.fragments.complainers
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.example.compl.R
import com.example.compl.application.ComplainApplication
import com.example.compl.databinding.DialogCustomLoadingBinding
import com.example.compl.databinding.FragmentComplainAccountPageBinding
import com.example.compl.model.ComplainUser
import com.example.compl.util.OfflineData
import com.example.compl.viewmodel.ComplainViewModel
import com.example.compl.viewmodel.ComplainViewModelFactory
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.theartofdev.edmodo.cropper.CropImage

class ComplainAccountPageFragment : Fragment() {

    private lateinit var mBinding:FragmentComplainAccountPageBinding
    private val complainViewModel: ComplainViewModel by viewModels{
        ComplainViewModelFactory((requireActivity().application as ComplainApplication).repository)
    }
    private var imageSelected: Uri? =null
    private var imageUrl:String=DEFAULT_IMAGE
    private var imageUploadedOperationCount:Int=0
    private var imageUploadedLiveData:MutableLiveData<Int> = MutableLiveData<Int>()
    private lateinit var dialog:Dialog


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




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        mBinding= FragmentComplainAccountPageBinding.inflate(inflater,container,false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract){
            it?.let {
                mBinding.complainAccountImageView.setImageURI(it)
                imageSelected=it
            }
        }

        mBinding.complainAccountFloatingActionBtn.setOnClickListener {
            if(checkPermissionForCameraAndGallery()) {
                cropActivityResultLauncher.launch(null)
            }
            else{
                showRationalDialogForPermission()
            }
        }

        mBinding.complainAccountSaveDataBtn.setOnClickListener {


            showLoadingDialogBox(true)

            if(imageSelected!=null){
                val type= OfflineData(requireActivity()).getLoginType()

                complainViewModel.uploadImage(imageSelected,type!!,"profile")

            }

            complainViewModel.uploadImageError.observe(viewLifecycleOwner,{
                it?.let { error->
                    if(error=="successful"){
                        imageUploadedLiveData.postValue(++imageUploadedOperationCount)
                        Toast.makeText(requireContext(), "Image upload $error",Toast.LENGTH_SHORT).show()
                        complainViewModel.uploadImageUrl.observe(viewLifecycleOwner,{ url->
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
        val name=mBinding.complainAccountName.text.toString()
        val email=mBinding.complainAccountEmail.text.toString()
        val mobileNumber=mBinding.complainAccountPhoneNumber.text.toString()

        if(imageUrl!=DEFAULT_IMAGE){

            complainViewModel.updateComplainUser(ComplainUser(name,email,mobileNumber,imageUrl))

            complainViewModel.getComplainUser()

            complainViewModel.complainUserData.observe(viewLifecycleOwner, {
                if(it!=null){
                    OfflineData(requireActivity()).putUserInfoSet(true)
                }
            })

            complainViewModel.complainUserError.observe(viewLifecycleOwner, {
                it?.let{
                    showLoadingDialogBox(false)
                    if(it=="SuccessFull"){
                        Toast.makeText(requireContext(),"SuccessFully Updated Data",Toast.LENGTH_SHORT).show()
                        mBinding.complainAccountName.text.clear()
                        mBinding.complainAccountEmail.text.clear()
                        mBinding.complainAccountPhoneNumber.text.clear()
                        mBinding.complainAccountImageView.setImageResource(R.drawable.ic_baseline_perm_identity_24)
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
        dialog=Dialog(requireContext())
        val binding:DialogCustomLoadingBinding = DialogCustomLoadingBinding.inflate(layoutInflater)
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
                    catch (e:ActivityNotFoundException){
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