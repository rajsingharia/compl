package com.example.compl.view.activities.complainer
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.example.compl.R
import com.example.compl.adapter.ComplainViewPagerAdapter
import com.example.compl.application.ComplainApplication
import com.example.compl.databinding.ActivityComplainWelcomePageBinding
import com.example.compl.util.OfflineData
import com.example.compl.view.fragments.complainers.ComplainLoginTabFragment
import com.example.compl.view.fragments.complainers.ComplainSignupTabFragment
import com.example.compl.viewmodel.LoginSignupViewModel
import com.example.compl.viewmodel.LoginSignupViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.GoogleAuthProvider

class ComplainWelcomePage : AppCompatActivity() {

    private lateinit var mBinding:ActivityComplainWelcomePageBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    companion object {
        private const val RC_SIGN_IN=123
    }
    private val loginSignupViewModel:LoginSignupViewModel by viewModels{
        LoginSignupViewModelFactory((application as ComplainApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        Log.d("Raj","welcome page")

        super.onCreate(savedInstanceState)
        mBinding=ActivityComplainWelcomePageBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }else{
            //For Other devices
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        val fragmentList= arrayListOf(
                ComplainLoginTabFragment(),
                ComplainSignupTabFragment()
        )

        val fragmentListTitle= arrayListOf(
                "Login",
                "Sign up"
        )


        val adapter= ComplainViewPagerAdapter(fragmentList,this.supportFragmentManager,lifecycle)

        mBinding.viewPager.adapter=adapter

        TabLayoutMediator(mBinding.tabLayout,mBinding.viewPager){tab,pos->
            tab.text= fragmentListTitle[pos]
        }.attach()

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        mBinding.fabGoogle.setOnClickListener {
            mBinding.fabGoogleProgress.visibility= View.VISIBLE
            signIn()
        }

        loginSignupViewModel.currentUser.observe(this,{
            it.let {

                if(it!=null) {
                    //saving offline type
                    OfflineData(this).putLoginType("com")


                    mBinding.fabGoogleProgress.visibility = View.INVISIBLE
                    Snackbar.make(mBinding.root, "SignIn Successful", Snackbar.LENGTH_SHORT).apply { show() }
                    val intent = Intent(this, ComplainHomePage::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                }
            }
        })

        loginSignupViewModel.error.observe(this,{
            it?.let {
                Snackbar.make(mBinding.root,it.toString(), Snackbar.LENGTH_SHORT).apply { show() }
                mBinding.fabGoogleProgress.visibility= View.INVISIBLE
            }
        })

    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if(task.isSuccessful) {
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    Snackbar.make(mBinding.root, "Google sign in failed :$e", Snackbar.LENGTH_SHORT).apply { show() }
                    mBinding.fabGoogleProgress.visibility= View.INVISIBLE
                }
            }
            else{
                Snackbar.make(mBinding.root,exception?.message.toString(), Snackbar.LENGTH_SHORT).apply { show() }
                mBinding.fabGoogleProgress.visibility = View.INVISIBLE
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        loginSignupViewModel.signInWithGoogle(credential)
    }

}