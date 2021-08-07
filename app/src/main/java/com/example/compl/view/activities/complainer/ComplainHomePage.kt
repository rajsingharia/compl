package com.example.compl.view.activities.complainer
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.compl.R
import com.example.compl.application.ComplainApplication
import com.example.compl.databinding.ActivityComplainHomePageBinding
import com.example.compl.view.fragments.complainers.ComplainAccountPageFragment
import com.example.compl.view.fragments.complainers.ComplainAddFragment
import com.example.compl.view.fragments.complainers.ComplainAllFragment
import com.example.compl.view.fragments.complainers.ComplainHomeFragment
import com.example.compl.viewmodel.ComplainViewModel
import com.example.compl.viewmodel.ComplainViewModelFactory
import com.example.compl.viewmodel.LoginSignupViewModel
import com.example.compl.viewmodel.LoginSignupViewModelFactory
import com.google.android.material.navigation.NavigationView
import com.mikhaellopez.circularimageview.CircularImageView

class ComplainHomePage : AppCompatActivity() ,NavigationView.OnNavigationItemSelectedListener{

    private lateinit var toggle:ActionBarDrawerToggle
    private lateinit var mBinding:ActivityComplainHomePageBinding
    private val complainViewModel: ComplainViewModel by viewModels{
        ComplainViewModelFactory((this.application as ComplainApplication).repository)
    }

    private val loginSignupViewModel: LoginSignupViewModel by viewModels {
        LoginSignupViewModelFactory((application as ComplainApplication).repository)
    }

    private var currentFragment: Fragment?=null

    override fun onStart() {
        super.onStart()
        setUpNavHeadData()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("Raj","home page")
        super.onCreate(savedInstanceState)
        mBinding= ActivityComplainHomePageBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setSupportActionBar(mBinding.complainToolbar)

        val actionbar=supportActionBar
        actionbar?.title="Compl"

        actionbar?.setDisplayHomeAsUpEnabled(true)

        toggle = ActionBarDrawerToggle(this, mBinding.drawerLayoutComplain, R.string.open,R.string.close)

        toggle.isDrawerIndicatorEnabled = true

        mBinding.drawerLayoutComplain.addDrawerListener(toggle)

        toggle.syncState()

        mBinding.complainNavView.setNavigationItemSelectedListener (this)


    }

    private fun setUpNavHeadData() {

            val navView = mBinding.complainNavView

            val navHeadView = navView.getHeaderView(0)

            val profileName=navHeadView.findViewById<TextView>(R.id.nav_head_profile_name)

            val profileImage=navHeadView.findViewById<CircularImageView>(R.id.nav_head_profile_image)

            val profileEmail=navHeadView.findViewById<TextView>(R.id.nav_head_profile_email)

            complainViewModel.complainUserData.observe(this,{

                it?.let {
                    profileName.text= it.userName
                    profileEmail.text=it.userEmail
                    Glide.with(this).load(it.imageUrl).into(profileImage)
                }

            })

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        Log.d("raj",item.itemId.toString())
        when(item.itemId){
                R.id.menu_nav_home -> {

                    replaceFragment(ComplainHomeFragment(),"ComplainHomeFragment")

                }
                R.id.menu_nav_add_complaints -> {

                    val ldf = ComplainAddFragment()
                    val args = Bundle()
                    args.putString("specific", null)
                    ldf.arguments = args

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.complain_fragment_view, ldf,"ComplainAddFragment")
                        //.addToBackStack(null)
                        .commit()

                }
                R.id.menu_nav_my_complaints -> {

                    val uid: String? = loginSignupViewModel.currentUser.value?.uid

                    if(uid!=null){
                        val ldf = ComplainAllFragment()
                        val args = Bundle()
                        args.putString("uid", uid)
                        ldf.arguments = args

                        supportFragmentManager.beginTransaction()
                            .replace(R.id.complain_fragment_view, ldf,"ComplainAllFragment")
                            //.addToBackStack(null)
                            .commit()
                    }else{
                        Toast.makeText(this,"unable to find user",Toast.LENGTH_SHORT).show()
                    }



                }
                R.id.menu_nav_all_complaints -> {

                    val ldf = ComplainAllFragment()
                    val args = Bundle()
                    args.putString("uid", null)
                    ldf.arguments = args

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.complain_fragment_view, ldf,"ComplainAllFragment")
                        //.addToBackStack(null)
                        .commit()

                }
                R.id.menu_nav_account -> {

                    replaceFragment(ComplainAccountPageFragment(),"ComplainAccountPageFragment")

                }
                R.id.menu_nav_share -> Toast.makeText(this,"Share",Toast.LENGTH_SHORT).show()
                R.id.menu_nav_rate_us -> Toast.makeText(this,"Rate Us",Toast.LENGTH_SHORT).show()
             }
        mBinding.drawerLayoutComplain.closeDrawer(GravityCompat.START)
        return true
    }

    //replacing fragments

     private fun replaceFragment(fragment: Fragment,tag:String) {

        //if(currentFragment!=null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.complain_fragment_view, fragment,tag)
                .commit()
        //}
//
//        else{
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.complain_fragment_view, fragment,tag)
//                    .addToBackStack(null)
//                    .commit()
//
//            currentFragment=fragment
//        }
    }
}