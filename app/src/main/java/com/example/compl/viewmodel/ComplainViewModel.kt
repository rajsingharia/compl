package com.example.compl.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.compl.model.ComplainUser
import com.example.compl.model.Complaindata
import com.example.compl.util.Repository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class ComplainViewModel(private val repository: Repository) :ViewModel() {

    fun updateComplainUser(user:ComplainUser) = viewModelScope.launch {
        repository.upDateComplainUser(user)
    }

    fun getComplainUser() = viewModelScope.launch {
        print("getComplain View Model")
        repository.getComplainUser()
    }

    fun uploadImage(file: Uri?, type:String, branch:String) = viewModelScope.launch {
        repository.uploadImage(file, type,branch)
    }

    fun addComplain(complain:Complaindata) = viewModelScope.launch {
        repository.addComplain(complain)
    }

    fun getAllComplains() = viewModelScope.launch {
        repository.getAllComplains()
    }

    fun getSpecificComplain(type: String) = viewModelScope.launch {
        repository.getSpecificComplain(type)
    }

    var complainUserData: MutableLiveData<ComplainUser?> = repository.getComplainUserData()
    var complainUserError:MutableLiveData<String> = repository.getComplainUserError()
    var uploadImageError:MutableLiveData<String?> = repository.getUploadImageError()
    var uploadImageUrl:MutableLiveData<String?> = repository.getUploadImageUrl()
    var complainAddUploadedError:MutableLiveData<String?> = repository.getAddComplainUploadedError()
    var allComplainsData:MutableLiveData<MutableList<Complaindata>?> = repository.getAllComplainsData()
    var specificComplainData:MutableLiveData<MutableList<Complaindata>> = repository.getSpecificComplainsData()

}


class ComplainViewModelFactory(private val repository: Repository): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ComplainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ComplainViewModel(repository) as T
        }
        throw IllegalArgumentException("Wrong View Model")
    }
}