package com.example.compl.util
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.text.BoringLayout

const val PREFERENCE_NAME="my_preference"

class OfflineData(activity: Activity) {

    private val sharedPreferences: SharedPreferences = activity.getSharedPreferences( PREFERENCE_NAME,Context.MODE_PRIVATE)

    private val editor:SharedPreferences.Editor =  sharedPreferences.edit()

    fun putLoginType(x:String){
        editor.putString("type",x)
        editor.apply()
        editor.commit()
    }

    val type=sharedPreferences.getString("type",null)
    val userInfo:Boolean=sharedPreferences.getBoolean("userInfo",false)

    fun getLoginType():String?{
        return type
    }

    fun putUserInfoSet(x:Boolean) {
        editor.putBoolean("userInfo",x)
        editor.apply()
        editor.commit()
    }

    fun getUserInfoSet(): Boolean {
        return userInfo
    }

}