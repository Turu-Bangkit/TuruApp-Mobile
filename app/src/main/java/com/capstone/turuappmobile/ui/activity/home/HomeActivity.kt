package com.capstone.turuappmobile.ui.activity.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.capstone.turuappmobile.R
import com.capstone.turuappmobile.databinding.ActivityHomeBinding
import com.capstone.turuappmobile.ui.activity.trackSleep.SleepActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding : ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navHost = supportFragmentManager.findFragmentById(R.id.navHostMain)
        binding.bottomNavigationView.setupWithNavController(navHost?.findNavController() ?: return)

        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(this, SleepActivity::class.java))
        }

    }
}