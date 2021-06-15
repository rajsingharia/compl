package com.example.compl.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.compl.model.AuthorityUser
import com.example.compl.model.ComplainUser
import com.example.compl.util.Repository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class AuthorityViewModel (val repository: Repository) : ViewModel() {

    fun updateAuthorityUser(user: AuthorityUser) = viewModelScope.launch {
        repository.updateAuthorityUser(user)
    }

    fun getAuthorityUser() = viewModelScope.launch {
        repository.getAuthorityUser()
    }

    fun uploadImage(file: Uri?, type:String, branch:String) = viewModelScope.launch {
        repository.uploadImage(file, type,branch)
    }

    var uploadImageError:MutableLiveData<String?> = repository.getUploadImageError()
    var uploadImageUrl:MutableLiveData<String?> = repository.getUploadImageUrl()
    var authorityUserData: MutableLiveData<ComplainUser?> = repository.getUserData()
    var userError: MutableLiveData<String> = repository.getComplainUserError()

}


class AuthorityViewModelFactory(private val repository: Repository): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AuthorityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthorityViewModel(repository) as T
        }
        throw IllegalArgumentException("Wrong View Model")
    }
}