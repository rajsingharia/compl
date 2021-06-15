package com.example.compl.util

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.example.compl.firebase.FirebaseSource
import com.example.compl.model.AuthorityUser
import com.example.compl.model.ComplainUser
import com.example.compl.model.Complaindata
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser

class Repository(private val firebase:FirebaseSource) {

    fun logout() = firebase.logout()

    fun register(email:String,password:String)=firebase.register(email,password)

    fun login(email:String,password:String)=firebase.login(email,password)

    fun signInWithGoogle(credentials:AuthCredential)=firebase.signInWithGoogle(credentials)

    fun findLoggedInOrNot()=firebase.findLoggedInOrNot()

    fun upDateComplainUser(complainUser:ComplainUser)=firebase.updateComplainUser(complainUser)

    fun getComplainUser()=firebase.getComplainUser()

    fun updateAuthorityUser(authorityUser: AuthorityUser)=firebase.updateAuthorityUser(authorityUser)

    fun getAuthorityUser()=firebase.getAuthorityUser()

    fun uploadImage(file: Uri?, type:String,branch:String)=firebase.upLoadImage(file, type,branch)


    fun addComplain(complain:Complaindata)=firebase.addComplain(complain)

    fun getAllComplains()=firebase.getAllComplains()

    fun getSpecificComplain(type: String?,id:String?,uid:String?)=firebase.getSpecificComplain(type,id,uid)

    fun editSpecificComplain(id: String,complain: Complaindata) = firebase.editSpecificComplain(id, complain)

    private val currentUser = firebase.getCurrentUser()
    private val error = firebase.getError()
    private val userData=firebase.getUserData()
    private val complainUserError=firebase.getUserError()
    private val uploadImageError=firebase.getUploadImageError()
    private val uploadImageUrl=firebase.getUploadImageUrl()
    private val complainAddUploadedError=firebase.getAddComplainUploadedError()
    private val allComplainsData = firebase.getAllComplainsData()
    private val specificComplainsData = firebase.getSpecificComplainsData()



    fun getCurrentUser(): MutableLiveData<FirebaseUser?> = currentUser
    fun getError():MutableLiveData<String> = error
    fun getUserData():MutableLiveData<ComplainUser?> = userData
    fun getComplainUserError():MutableLiveData<String> = complainUserError
    fun getUploadImageError():MutableLiveData<String?> = uploadImageError
    fun getUploadImageUrl():MutableLiveData<String?> = uploadImageUrl
    fun getAddComplainUploadedError():MutableLiveData<String?> = complainAddUploadedError
    fun getAllComplainsData():MutableLiveData<MutableList<Complaindata>?> = allComplainsData
    fun getSpecificComplainsData():MutableLiveData<MutableList<Complaindata>?> = specificComplainsData

}