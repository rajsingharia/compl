package com.example.compl.view.activities
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.example.compl.MainActivity
import com.example.compl.R
import com.example.compl.application.ComplainApplication
import com.example.compl.databinding.ActivitySplashBinding
import com.example.compl.util.OfflineData
import com.example.compl.view.activities.authority.AuthorityHomePage
import com.example.compl.view.activities.complainer.ComplainHomePage
import com.example.compl.viewmodel.*

class SplashActivity : AppCompatActivity() {

    private val loginSignupViewModel: LoginSignupViewModel by viewModels {
        LoginSignupViewModelFactory((application as ComplainApplication).repository)
    }


    private val complainViewModel: ComplainViewModel by viewModels{
        ComplainViewModelFactory((application as ComplainApplication).repository)
    }

    private val authorityViewModel: AuthorityViewModel by viewModels{
        AuthorityViewModelFactory((this.application as ComplainApplication).repository)
    }

    private val showSplashScreen :MutableLiveData<Boolean> = MutableLiveData()

    private lateinit var splashBinding:ActivitySplashBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        loginSignupViewModel.findLoggedInOrNot()
        showSplashScreen.postValue(true)
        super.onCreate(savedInstanceState)
        splashBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(splashBinding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            //For Other devices
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        //Animation of logo
        //<--

        val splashAnimationComplain = AnimationUtils.loadAnimation(this, R.anim.splash_anim_complain)

        splashBinding.splashTitleTextComplain.animation = splashAnimationComplain



        val splashAnimationAnd = AnimationUtils.loadAnimation(this, R.anim.splash_anim_and)

        splashBinding.splashTitleTextAnd.animation = splashAnimationAnd



        val splashAnimationRegistration = AnimationUtils.loadAnimation(this, R.anim.splash_anim_registration)

        splashBinding.splashTitleTextRegistration.animation = splashAnimationRegistration



        val splashAnimationLogo = AnimationUtils.loadAnimation(this, R.anim.splash_anim_logo)

        splashBinding.splashComplainLogo.animation = splashAnimationLogo

        //-->>

        findLastUserType()


        showSplashScreen.observe(this,{
            Log.d("raj", "splash screen ->$it")
                if(it==false){
                    val type = OfflineData(this).getLoginType()

                    if(type==null){
                        Toast.makeText(this,"Type Missing\nContact Administration",Toast.LENGTH_SHORT).show()
                    }

                    if (type != null && type == "com") {

                        Log.d("Raj", "User verified and going to home page")
                        val intent = Intent(this@SplashActivity, ComplainHomePage::class.java)
                        startActivity(intent)
                        finish()

                    } else if (type != null && type == "auth") {

                        Toast.makeText(this,"Authorised entry",Toast.LENGTH_SHORT).show()
                        finishAffinity()
                       val intent = Intent(this@SplashActivity, AuthorityHomePage::class.java)

                        startActivity(intent)
                        finish()

                    }
                }
        })
    }

    private fun findLastUserType() {

        when(OfflineData(this).getLoginType()){
            "auth" -> {
                getAuthUserDetails()
            }
            "com" -> {
                getComUserDetails()
            }
            else -> {
                startActivity((Intent(this@SplashActivity, MainActivity::class.java)))
                finish()
            }
        }

    }

    private fun getComUserDetails() {

        Log.d("raj","user is com")

        loginSignupViewModel.currentUser.observe(this,{ user->
            if(user==null){
                startActivity((Intent(this@SplashActivity, MainActivity::class.java)))
                finish()
            }
            else{
                if(complainViewModel.complainUserData.value==null && complainViewModel.allComplainsData.value==null) {
                    Log.d("raj","com user data is null")
                    complainViewModel.getComplainUser()
                    complainViewModel.getAllComplains()
                }


                complainViewModel.complainUserData.observe(this,{ userdata->
                    userdata?.let {
                        complainViewModel.allComplainsData.observe(this,{
                            it?.let {
                                showSplashScreen.postValue(false)
                            }
                        })
                    }
                })

                complainViewModel.complainUserError.observe(this,{
                    it?.let{
                        Toast.makeText(this,it,Toast.LENGTH_SHORT).show()
                    }
                })

            }

        })

    }

    private fun getAuthUserDetails() {

        Log.d("raj","user is auth")

        loginSignupViewModel.currentUser.observe(this,{ user->
            if(user==null){
                startActivity((Intent(this@SplashActivity, MainActivity::class.java)))
                finish()
            }
            else{
                if(authorityViewModel.authorityUserData.value==null && complainViewModel.allComplainsData.value==null) {
                    Log.d("raj","getting auth user data")
                    authorityViewModel.getAuthorityUser()
                    complainViewModel.getAllComplains()
                }

                authorityViewModel.authorityUserData.observe(this,{ userdata->
                    userdata?.let {
                        Log.d("raj","got user data")
                        complainViewModel.allComplainsData.observe(this,{
                            it?.let {
                                showSplashScreen.postValue(false)
                            }
                        })
                    }

                })

                authorityViewModel.userError.observe(this,{
                    it?.let{
                        Toast.makeText(this,it,Toast.LENGTH_SHORT).show()
                    }
                })
            }

        })

    }
}