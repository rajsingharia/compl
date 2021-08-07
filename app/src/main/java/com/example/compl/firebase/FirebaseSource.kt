package com.example.compl.firebase
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.compl.model.AuthorityUser
import com.example.compl.model.ComplainUser
import com.example.compl.model.Complaindata
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.*

class FirebaseSource {

    private val firebaseAuth:FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val fireStore :FirebaseFirestore by lazy{
        FirebaseFirestore.getInstance()
    }

    private val fireUpload by lazy {
        Firebase.storage.reference
    }

    private val fireRealtimeData by lazy {
        Firebase.database
    }


    //mutable variables
    private var currentUser: MutableLiveData<FirebaseUser?> = MutableLiveData<FirebaseUser?>()
    private var error:MutableLiveData<String> = MutableLiveData<String>()
    private var userError:MutableLiveData<String> = MutableLiveData<String>()
    private var userDetails:MutableLiveData<ComplainUser?> = MutableLiveData<ComplainUser?>()
    private var uploadImageError:MutableLiveData<String?> = MutableLiveData<String?>()
    private var uploadImageUrl:MutableLiveData<String?> = MutableLiveData<String?>()
    private var complainUploadedError:MutableLiveData<String?> = MutableLiveData<String?>()
    private var complainData:MutableLiveData<MutableList<Complaindata>?> = MutableLiveData<MutableList<Complaindata>?>()
    private var specificComplainData:MutableLiveData<MutableList<Complaindata>?> = MutableLiveData<MutableList<Complaindata>?>()



    fun logout(){
        firebaseAuth.signOut()
    }


    fun register(email: String, password: String){

        currentUser.value=null

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful){
                currentUser.postValue(it.result?.user)
            }
            else{
                error.postValue(it.exception?.message.toString())
            }
        }
    }



    fun login(email: String, password: String){

        currentUser.value=null

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful){
                currentUser.postValue(it.result?.user)
            }
            else{
                error.postValue(it.exception?.message.toString())
            }
        }
    }



    fun signInWithGoogle(credentials: AuthCredential){

        currentUser.value=null

        firebaseAuth.signInWithCredential(credentials).addOnCompleteListener {
            if(it.isSuccessful){
                currentUser.postValue(it.result?.user)
            }
            else{
                error.postValue(it.exception?.message.toString())
            }
        }
    }



    fun findLoggedInOrNot(){
        val user:FirebaseUser?=firebaseAuth.currentUser
        currentUser.postValue(user)
    }



    fun updateComplainUser(complainUser: ComplainUser){
        fireStore
                .collection("ComplainUser")
                .document(currentUser.value?.uid.toString())
                .set(complainUser)
                .addOnSuccessListener {
                    userError.postValue("SuccessFull")
                }
    }



    fun getComplainUser() {

                fireStore
                .collection("ComplainUser")
                .document(currentUser.value?.uid.toString())
                .get()
                        .addOnCompleteListener {
                            if(it.isSuccessful){

                                val user= it.result!!.toObject(ComplainUser::class.java)
                                user?.let {
                                    Log.d("raj","user name -> "+user.userName)
                                    userDetails.postValue(user)
                                }
                            }
                            else{
                                userError.postValue(it.exception?.message.toString())
                                userDetails.postValue(null)
                            }
                        }
    }

    fun updateAuthorityUser(authorityUser: AuthorityUser){
        fireStore
            .collection("AuthorityUser")
            .document(currentUser.value?.uid.toString())
            .set(authorityUser)
            .addOnSuccessListener {
                userError.postValue("SuccessFull")
            }
    }



    fun getAuthorityUser() {


        fireStore
            .collection("AuthorityUser")
            .document(currentUser.value?.uid.toString())
            .get()
            .addOnCompleteListener {
                if(it.isSuccessful){

                    val user= it.result!!.toObject(ComplainUser::class.java)
                    user?.let {
                        Log.d("raj","user name -> "+user.userName)
                        userDetails.postValue(user)
                    }
                }
                else{
                    userError.postValue(it.exception?.message.toString())
                    userDetails.postValue(null)
                }
            }
    }



    fun upLoadImage(file: Uri?, type:String,branch:String){

        uploadImageError.value=null
        uploadImageUrl.value=null

        file?.let{ f->
            if(branch=="profile"){
                val fileName:String=firebaseAuth.currentUser!!.uid

                val ref=fireUpload.child(type+"_images/"+fileName+".jpg")

                ref.putFile(f).addOnCompleteListener{
                    if(it.isSuccessful){
                        uploadImageError.postValue("successful")
                        ref.downloadUrl.addOnSuccessListener { imageUrl->
                            imageUrl?.let {
                                uploadImageUrl.postValue(imageUrl.toString())
                            }
                        }

                    }
                    else{
                        uploadImageError.postValue("unsuccessful")
                    }
                }
            }
            else {

                //uploadImageUrl.
                val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
                val now=Date()
                val fileName = formatter.format(now)

                val ref=fireUpload.child(branch+"_images/"+fileName+".jpg")

                ref.putFile(f)
                    .addOnCompleteListener{
                    if(it.isSuccessful){
                        uploadImageError.postValue("successful")
                        ref.downloadUrl.addOnSuccessListener {imageUrl->
                            imageUrl?.let {
                                uploadImageUrl.postValue(imageUrl.toString())
                            }
                        }

                    }
                    else{
                        uploadImageError.postValue("unsuccessful")
                    }
                }
            }
        }

    }


    fun addComplain(complain:Complaindata){

        complainUploadedError.value=null

        complain.uid=firebaseAuth.currentUser!!.uid
        val reference=fireRealtimeData.getReference("complains")
        val id=reference.push().key

        id?.let {
            complain.id=id

            reference.child(id).setValue(complain)
                .addOnSuccessListener { complainUploadedError.postValue("successful") }
                .addOnFailureListener { complainUploadedError.postValue(it.message.toString()) }
                .addOnCanceledListener { complainUploadedError.postValue("unsuccessful") }
        }
    }



    fun getAllComplains(){

        complainData.value=null

        fireRealtimeData.getReference("complains").addValueEventListener(object :ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                val complainDataList:MutableList<Complaindata> = mutableListOf()
                for(data in snapshot.children){
                    val singleData=data.getValue(Complaindata::class.java)
                    singleData?.let {
                        complainDataList.add(it)
                    }
                }
                complainData.postValue(complainDataList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("raj","Firebase : ${error}")
            }

        })

    }

    fun editSpecificComplain(id: String?,complain:Complaindata){

        complainUploadedError.value=null

        val ref=fireRealtimeData.getReference("complains")
        id?.let {
            ref.child(id).setValue(complain)
                .addOnSuccessListener { complainUploadedError.postValue("successful") }
                .addOnFailureListener { complainUploadedError.postValue(it.message.toString()) }
                .addOnCanceledListener { complainUploadedError.postValue("unsuccessful") }

        }

    }



    fun getSpecificComplain(type:String?,id:String?,uid:String?){

        specificComplainData.value=null

        val ref=fireRealtimeData.getReference("complains")

        if(id==null && uid==null && type!=null){
            ref.addValueEventListener(object :ValueEventListener{

                override fun onDataChange(snapshot: DataSnapshot) {

                    val specificComplainDataList:MutableList<Complaindata> = mutableListOf()
                    for(data in snapshot.children){
                        val singleData=data.getValue(Complaindata::class.java)
                        singleData?.let {
                            if(it.type==type)
                                specificComplainDataList.add(it)
                        }
                    }
                    specificComplainData.postValue(specificComplainDataList)
                }

                override fun onCancelled(error: DatabaseError) {}

            })
        }
        else if(id!=null && type==null && uid==null){

            Log.d("raj","getting complain $id")

            ref.addValueEventListener(object :ValueEventListener{

                override fun onDataChange(snapshot: DataSnapshot) {
                    val specificComplainDataList:MutableList<Complaindata> = mutableListOf()
                    for(data in snapshot.children){
                        val singleData=data.getValue(Complaindata::class.java)
                        singleData?.let {
                            if(it.id==id)
                                specificComplainDataList.add(it)
                        }
                    }
                    specificComplainData.postValue(specificComplainDataList)
                }

                override fun onCancelled(error: DatabaseError) {}

            })

        }
        else{

            ref.addValueEventListener(object :ValueEventListener{

                override fun onDataChange(snapshot: DataSnapshot) {
                    val specificComplainDataList:MutableList<Complaindata> = mutableListOf()
                    for(data in snapshot.children){
                        val singleData=data.getValue(Complaindata::class.java)
                        singleData?.let {
                            if(it.uid==uid)
                                specificComplainDataList.add(it)
                        }
                    }
                    specificComplainData.postValue(specificComplainDataList)
                }

                override fun onCancelled(error: DatabaseError) {}

            })

        }

    }




    fun getCurrentUser():MutableLiveData<FirebaseUser?> = currentUser
    fun getError():MutableLiveData<String> = error
    fun getUserData():MutableLiveData<ComplainUser?> = userDetails
    fun getUserError():MutableLiveData<String> = userError
    fun getUploadImageError():MutableLiveData<String?> = uploadImageError
    fun getUploadImageUrl():MutableLiveData<String?> = uploadImageUrl
    fun getAddComplainUploadedError():MutableLiveData<String?> = complainUploadedError
    fun getAllComplainsData():MutableLiveData<MutableList<Complaindata>?> = complainData
    fun getSpecificComplainsData():MutableLiveData<MutableList<Complaindata>?> = specificComplainData

}

