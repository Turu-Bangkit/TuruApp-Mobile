package com.capstone.turuappmobile.ui.activity.detailChallenge

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.capstone.turuappmobile.R
import com.capstone.turuappmobile.data.api.model.Data
import com.capstone.turuappmobile.data.api.model.DetailChallengeResponse
import com.capstone.turuappmobile.data.repository.Result
import com.capstone.turuappmobile.data.viewModelFactory.ViewModelFactoryUser
import com.capstone.turuappmobile.databinding.ActivityDetailChallengeBinding
import com.capstone.turuappmobile.ui.fragment.home.HomeFragmentViewModel
import com.capstone.turuappmobile.utils.loadImage
import com.google.android.material.tabs.TabLayout

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
                detailChallengeFragmentViewModel.startChallenge(user.jwtToken, user.UID, idChallenge)
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

    private fun setAllData(data: Data){
        binding.apply {
            imageChallengeDetail.loadImage(data.img)
            challengeNameTxtDetail.text = data.name
            streakDaysDetail.text = resources.getString(R.string.streak_days, data.howManyDays)
            rangeHourDetail.text = resources.getString(R.string.range_hour, data.startTime, data.endTime)
            pointsTxtDetail.text = resources.getString(R.string.points, data.point.toString())
            txtDetailChallenge.text = data.desc
        }
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

    private fun toastMaker(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object{
        const val CHALLENGE_ID = "challenge_id"
    }
}