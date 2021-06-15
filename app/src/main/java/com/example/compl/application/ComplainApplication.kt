package com.example.compl.application
import android.app.Application
import com.example.compl.firebase.FirebaseSource
import com.example.compl.util.Repository

class ComplainApplication :Application() {

    private val firebaseSource by lazy {
        FirebaseSource()
    }

    val repository by lazy {
        Repository(firebaseSource)
    }

}