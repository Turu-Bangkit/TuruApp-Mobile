package com.capstone.turuappmobile.ui.activity.detailChallenge

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.capstone.turuappmobile.R
import com.capstone.turuappmobile.databinding.ActivityDetailChallengeBinding
import com.google.android.material.tabs.TabLayout

class DetailChallengeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailChallengeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailChallengeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tabLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener{
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when(tab?.position){
                        0 -> {
                            binding.txtDetailChallenge.text = resources.getString(R.string.lorem)
                        }
                        1 -> {
                            binding.txtDetailChallenge.text = resources.getString(R.string.lorem2)
                        }

                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }

            }
        )
    }
}