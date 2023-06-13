package com.capstone.turuappmobile.ui.activity.detailChallenge

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.capstone.turuappmobile.R
import com.capstone.turuappmobile.data.api.model.Data
import com.capstone.turuappmobile.data.repository.Result
import com.capstone.turuappmobile.data.viewModelFactory.ViewModelFactoryUser
import com.capstone.turuappmobile.databinding.ActivityDetailChallengeBinding
import com.capstone.turuappmobile.ui.activity.login.LoginActivity
import com.capstone.turuappmobile.utils.loadImage
import com.example.awesomedialog.*
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class DetailChallengeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailChallengeBinding

    private val detailChallengeFragmentViewModel by viewModels<DetailChallengeViewModel> {
        ViewModelFactoryUser.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailChallengeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idChallenge =  intent.getIntExtra(CHALLENGE_ID, 0).toString()

        detailChallengeFragmentViewModel.getUserSession.observe(this){ user ->


            detailChallengeFragmentViewModel.detailChallenge(user.jwtToken, idChallenge)


            binding.btnStartChallenge.setOnClickListener {


                AwesomeDialog.build(this)
                    .title("Register Challenge")
                    .body(
                        "Are you sure want to register this challenge?",
                    )
                    .background(R.drawable.bg_rounded_blue200)
                    .position(AwesomeDialog.POSITIONS.CENTER)
                    .onPositive(
                        "Yes",
                        buttonBackgroundColor = R.drawable.bg_rounded_blue500,
                        textColor = ContextCompat.getColor(this, R.color.white)
                    ) {
                        detailChallengeFragmentViewModel.startChallenge(user.jwtToken, user.UID, idChallenge)
                    }
                    .onNegative(
                        "Cancel",
                        buttonBackgroundColor = R.drawable.bg_rounded_white,
                        textColor = ContextCompat.getColor(this, R.color.green_200)
                    ) {

                    }



            }
        }

        detailChallengeFragmentViewModel.detailChallengeResult.observe(this){
            when(it){
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    setAllData(it.data.data)
                }
                is Result.Error -> {
                    showLoading(false)
                    toastMaker(it.error)
                }
            }
        }

        detailChallengeFragmentViewModel.startChallengeResult.observe(this){
            when(it){
                is Result.Loading -> {
                    showLoadingNotShimmer(true)
                }
                is Result.Success -> {
                    showLoadingNotShimmer(false)
                    toastMaker(it.data.message)
                    finish()
                }
                is Result.Error -> {
                    showLoadingNotShimmer(false)
                    toastMaker(it.error)
                }
            }
        }


        binding.btnBackDetailChallenge.setOnClickListener {
            finish()
        }



    }

    private fun setAllData(data: Data){
        binding.apply {
            imageChallengeDetail.loadImage(data.img)
            challengeNameTxtDetail.text = data.name
            streakDaysDetail.text = resources.getString(R.string.streak_days, data.howManyDays)
            rangeHourDetail.text = resources.getString(R.string.range_hour, data.startTime, data.endTime)
            pointsTxtDetail.text = resources.getString(R.string.points, data.point.toString())
            txtDetailChallenge.text = data.desc
        }
        binding.tabLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener{
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when(tab?.position){
                        0 -> {
                            binding.txtDetailChallenge.text = data.desc
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

    private fun showLoading(isLoading: Boolean) {
        binding.shimmerLayoutDetailChallenge.visibility =
            if (isLoading){
                binding.shimmerLayoutDetailChallenge.startShimmer()
                View.VISIBLE
            } else {
                binding.shimmerLayoutDetailChallenge.stopShimmer()
                View.GONE
            }
    }

    private fun showLoadingNotShimmer(isLoading: Boolean) {
        binding.layoutLoading.layoutAllLoading.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }

    private fun toastMaker(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object{
        const val CHALLENGE_ID = "challenge_id"
    }
}