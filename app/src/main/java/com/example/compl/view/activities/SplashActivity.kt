package com.example.compl.view.activities
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.example.compl.MainActivity
import com.example.compl.R
import com.example.compl.application.ComplainApplication
import com.example.compl.databinding.ActivitySplashBinding
import com.example.compl.util.OfflineData
import com.example.compl.view.activities.complainer.ComplainHomePage
import com.example.compl.viewmodel.ComplainViewModel
import com.example.compl.viewmodel.ComplainViewModelFactory
import com.example.compl.viewmodel.LoginSignupViewModel
import com.example.compl.viewmodel.LoginSignupViewModelFactory

class SplashActivity : AppCompatActivity() {

    private val loginSignupViewModel: LoginSignupViewModel by viewModels {
        LoginSignupViewModelFactory((application as ComplainApplication).repository)
    }

    private val complainViewModel: ComplainViewModel by viewModels{
        ComplainViewModelFactory((this.application as ComplainApplication).repository)
    }

    private val showSplashScreen :MutableLiveData<Boolean> = MutableLiveData()

    private lateinit var splashBinding:ActivitySplashBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        loginSignupViewModel.findLoggedInOrNot()
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


        complainViewModel.getComplainUser().invokeOnCompletion {
            complainViewModel.getAllComplains().invokeOnCompletion {
                showSplashScreen.postValue(false)
            }
        }

        showSplashScreen.observe(this,{
            it?.let {
                if(!it){
                    val type = OfflineData(this).getLoginType()

                    loginSignupViewModel.currentUser.observe(this, {
                        it.let {

                            Log.d("raj", type.toString()+"  -- user-> "+it.toString())

                            if (it == null) {

                                startActivity((Intent(this@SplashActivity, MainActivity::class.java)))
                                finish()

                            }
                            if (it != null && type != null && type == "com") {

                                Log.d("Raj", "User verified and going to home page")

                                val intent = Intent(this@SplashActivity, ComplainHomePage::class.java)

                                startActivity(intent)
                                finish()

                            } else if (it != null && type != null && type == "auth") {

                                val intent = Intent(this@SplashActivity, ComplainHomePage::class.java)

                                startActivity(intent)
                                finish()

                            }

                        }
                    })
                }
            }
        })
    }
}