package com.example.compl.viewmodel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.compl.model.Complaindata
import com.example.compl.util.Repository
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class LoginSignupViewModel(private val repository: Repository) :ViewModel() {

    fun logout() = viewModelScope.launch {
        repository.logout()
    }

    fun register(email:String,password:String)=viewModelScope.launch {
        repository.register(email,password)
    }


    fun login(email:String,password:String)=viewModelScope.launch {
        repository.login(email,password)
    }


    fun signInWithGoogle(credentials:AuthCredential)=viewModelScope.launch {
        repository.signInWithGoogle(credentials)
    }


    fun findLoggedInOrNot()=viewModelScope.launch {
        repository.findLoggedInOrNot()
    }




    var currentUser:MutableLiveData<FirebaseUser?> = repository.getCurrentUser()


    var error:MutableLiveData<String> = repository.getError()

}

class LoginSignupViewModelFactory(private val repository: Repository): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(LoginSignupViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginSignupViewModel(repository) as T
        }
        throw IllegalArgumentException("Wrong View Model")
    }

}