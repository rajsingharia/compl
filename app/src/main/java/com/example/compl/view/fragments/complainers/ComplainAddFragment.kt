package com.example.compl.view.fragments.complainers
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.example.compl.R
import com.example.compl.application.ComplainApplication
import com.example.compl.databinding.DialogCustomLoadingBinding
import com.example.compl.databinding.FragmentComplainAddBinding
import com.example.compl.model.ComplainUser
import com.example.compl.model.Complaindata
import com.example.compl.util.OfflineData
import com.example.compl.viewmodel.ComplainViewModel
import com.example.compl.viewmodel.ComplainViewModelFactory
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.theartofdev.edmodo.cropper.CropImage

class ComplainAddFragment : Fragment() {

    private lateinit var mBinding:FragmentComplainAddBinding
    private val dropDownList = arrayOf("Type A","Type B","Type C","Type D","Type E","Type F")
    private val complainViewModel: ComplainViewModel by viewModels {
        ComplainViewModelFactory((requireActivity().application  as ComplainApplication).repository)
    }
    private var type:String?=null
    private lateinit var dialog:Dialog
    private var imageSelected: Uri? =null
    private var imageUploadedLiveData: MutableLiveData<Int> = MutableLiveData<Int>()
    private var imageUploadedOperationCount:Int=0
    private lateinit var imageUrl:String
    private var specific: String? =null




    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>(){
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setAspectRatio(16,9)
                .getIntent(requireContext())
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }

    }


    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>






    override fun onResume() {
        super.onResume()
        val adapter = ArrayAdapter(requireContext(),R.layout.drop_down_item,dropDownList)
        mBinding.autoCompleteDropDownMenu.setAdapter(adapter)
        specific?.let{
            mBinding.autoCompleteDropDownMenu.setText(it)
            type=it
        }
        dialog= Dialog(requireContext())
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract){
            it?.let {
                mBinding.complainAddImage.setImageURI(it)
                imageSelected=it
            }

        }

        mBinding.complainAddImage.setOnClickListener {
            if(checkPermissionForCameraAndGallery()) {
                cropActivityResultLauncher.launch(null)
            }
            else{
                showRationalDialogForPermission()
            }
        }

        mBinding.autoCompleteDropDownMenu.onItemClickListener = object :AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                type = dropDownList[position]
            }
        }

        mBinding.complainAddSaveDataBtn.setOnClickListener {
            showLoadingDialogBox(true)
            if(imageSelected!=null){
                val type= OfflineData(requireActivity()).getLoginType()

                complainViewModel.uploadImage(imageSelected,type!!,"complain")

            }

            complainViewModel.uploadImageError.observe(viewLifecycleOwner,{
                it?.let {
                    if(it=="successful"){
                        imageUploadedLiveData.postValue(++imageUploadedOperationCount)
                        Toast.makeText(requireContext(), "Image upload $it",Toast.LENGTH_SHORT).show()
                        complainViewModel.uploadImageUrl.observe(viewLifecycleOwner,{
                            it?.let {
                                imageUrl=it
                                imageUploadedLiveData.postValue(++imageUploadedOperationCount)
                            }
                        })
                    }
                }
            })


            imageUploadedLiveData.observe(requireActivity(),{
                it?.let {
                    if(it==2) {
                        addComplainData()
                    }
                }
            })
        }

    }

    private fun addComplainData() {
        if(type==null){
            showLoadingDialogBox(false)
            Toast.makeText(requireContext(),"No Type Selected",Toast.LENGTH_SHORT).show()
            return
        }
        type?.let {
            val title = mBinding.complainAddTitle.text.toString()
            val description = mBinding.complainAddDescription.text.toString()
            val phoneNo = mBinding.complainAddPhoneNumber.text.toString()
            val address = mBinding.complainAddAddress.text.toString()

            val complain = Complaindata(type!!, title, description, phoneNo, imageUrl, address)
            complainViewModel.addComplain(complain)
        }

        complainViewModel.complainAddUploadedError.observe(viewLifecycleOwner,{
            it?.let {
                if(it=="successful"){
                    showLoadingDialogBox(false)
                    mBinding.complainAddTitle.text.clear()
                    mBinding.complainAddDescription.text.clear()
                    mBinding.complainAddPhoneNumber.text.clear()
                    mBinding.complainAddImage.setImageResource(R.drawable.ic_baseline_image_24)
                    val firstType=resources.getString(R.string.first_type)
                    mBinding.autoCompleteDropDownMenu.setText(firstType)
                    Toast.makeText(requireContext(),"Complain Added\nThank You For Your Support",Toast.LENGTH_SHORT).show()
                    //type=null

                    requireActivity().supportFragmentManager.popBackStackImmediate()

                }
                else {
                    showLoadingDialogBox(false)
                    Toast.makeText(requireContext(),it,Toast.LENGTH_SHORT).show()
                }
            }
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        specific = requireArguments().getString("specific")
        mBinding=FragmentComplainAddBinding.inflate(inflater,container,false)
        return mBinding.root
    }


    //To show Loading Dialog
    private fun showLoadingDialogBox(visible:Boolean) {
        if(!visible) {
            if(this::dialog.isInitialized)
                dialog.dismiss()
            return
        }
        dialog= Dialog(requireContext())
        val binding: DialogCustomLoadingBinding = DialogCustomLoadingBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        dialog.show()

    }


    //To Check permission
    private fun checkPermissionForCameraAndGallery() :Boolean {

        var permissionGranted=false

        Dexter.withContext(requireContext()).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
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
}