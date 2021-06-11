package com.example.compl

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.compl.databinding.ActivityMainBinding
import com.example.compl.view.activities.complainer.ComplainWelcomePage

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mBinding=ActivityMainBinding.inflate(layoutInflater)

        setContentView(mBinding.root)

        mBinding.complLoginSignupBtn.setOnClickListener {
            Log.d("Raj","welcome button clicked")
            startActivity(Intent(this, ComplainWelcomePage::class.java))
        }

    }
}