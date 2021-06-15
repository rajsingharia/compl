package com.example.compl.view.activities.authority
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.compl.R
import com.example.compl.application.ComplainApplication
import com.example.compl.databinding.ActivityAuthorityHomePageBinding
import com.example.compl.view.fragments.authority.AuthorityAccountPageFragment
import com.example.compl.view.fragments.authority.AuthorityAllComplainFragment
import com.example.compl.view.fragments.authority.AuthorityHomeFragment
import com.example.compl.view.fragments.complainers.ComplainAllFragment
import com.example.compl.viewmodel.ComplainViewModel
import com.example.compl.viewmodel.ComplainViewModelFactory
import com.example.compl.viewmodel.LoginSignupViewModel
import com.example.compl.viewmodel.LoginSignupViewModelFactory
import com.google.android.material.navigation.NavigationView
import com.mikhaellopez.circularimageview.CircularImageView

class AuthorityHomePage : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener{

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var binding: ActivityAuthorityHomePageBinding

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
        super.onCreate(savedInstanceState)

        binding = ActivityAuthorityHomePageBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setSupportActionBar(binding.authorityToolbar)

        val actionbar=supportActionBar
        actionbar?.title="Authority"

        actionbar?.setDisplayHomeAsUpEnabled(true)

        toggle = ActionBarDrawerToggle(this, binding.DrawerLayoutAuthority, R.string.open,R.string.close)

        toggle.isDrawerIndicatorEnabled = true

        binding.DrawerLayoutAuthority.addDrawerListener(toggle)

        toggle.syncState()

        binding.authorityNavView.setNavigationItemSelectedListener (this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpNavHeadData() {

        val navView = binding.authorityNavView

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


    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        Log.d("raj",item.itemId.toString())
        when(item.itemId){
            R.id.authority_menu_nav_home -> {

                replaceFragment(AuthorityHomeFragment(),"AuthorityHomeFragment")

            }
            R.id.authority_menu_nav_all_complaints -> {

                replaceFragment(AuthorityAllComplainFragment(),"AuthorityAllComplainFragment")

            }
            R.id.authority_menu_nav_account -> {

                replaceFragment(AuthorityAccountPageFragment(),"AuthorityAccountPageFragment")

            }
            R.id.authority_menu_nav_share -> Toast.makeText(this,"Share", Toast.LENGTH_SHORT).show()
            R.id.authority_menu_nav_rate_us -> Toast.makeText(this,"Rate Us", Toast.LENGTH_SHORT).show()
        }
        binding.DrawerLayoutAuthority.closeDrawer(GravityCompat.START)
        return true
    }

    private fun replaceFragment(fragment: Fragment,tag:String) {

        if(currentFragment!=null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.authority_fragment_view, fragment,tag)
                .commit()
        }

        else{
            supportFragmentManager.beginTransaction()
                .replace(R.id.authority_fragment_view, fragment,tag)
                .addToBackStack(null)
                .commit()

            currentFragment=fragment
        }
    }


}