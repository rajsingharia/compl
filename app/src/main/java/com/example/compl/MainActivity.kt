package com.example.compl

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.compl.databinding.ActivityMainBinding
import com.example.compl.view.activities.authority.AuthorityWelcomePage
import com.example.compl.view.activities.complainer.ComplainWelcomePage

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.complLoginSignupBtn.setOnClickListener {
            Log.d("Raj","welcome button clicked")
            startActivity(Intent(this, ComplainWelcomePage::class.java))
            finish()
        }

        binding.authLoginSignupBtn.setOnClickListener {
            startActivity(Intent(this,AuthorityWelcomePage::class.java))
            finish()
        }

    }
}