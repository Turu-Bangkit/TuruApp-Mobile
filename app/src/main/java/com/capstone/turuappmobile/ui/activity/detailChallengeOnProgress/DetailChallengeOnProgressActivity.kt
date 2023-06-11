package com.capstone.turuappmobile.ui.activity.detailChallengeOnProgress

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.turuappmobile.R
import com.capstone.turuappmobile.adapter.ChallengeSleepAdapter
import com.capstone.turuappmobile.adapter.OnProgressChallengeAdapter
import com.capstone.turuappmobile.data.api.model.DataItem
import com.capstone.turuappmobile.data.api.model.DetailChallengeResponse
import com.capstone.turuappmobile.data.api.model.StatusChallengeResponse
import com.capstone.turuappmobile.data.db.custom.OnProgress
import com.capstone.turuappmobile.data.repository.Result
import com.capstone.turuappmobile.data.viewModelFactory.ViewModelFactoryUser
import com.capstone.turuappmobile.databinding.ActivityDetailChallengeOnProgressBinding
import com.capstone.turuappmobile.ui.activity.detailChallenge.DetailChallengeActivity
import com.capstone.turuappmobile.ui.activity.detailChallenge.DetailChallengeViewModel
import com.capstone.turuappmobile.ui.fragment.challenge.ChallengeFragmentViewModel
import com.capstone.turuappmobile.utils.convertEpochToHourMinute
import com.capstone.turuappmobile.utils.convertEpochToJustDateTime
import com.capstone.turuappmobile.utils.loadImage
import com.capstone.turuappmobile.utils.onedayinseconds

class DetailChallengeOnProgressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailChallengeOnProgressBinding
    private var statusChallengeResponse: StatusChallengeResponse? = null
    private var detailChallengeRespone: DetailChallengeResponse? = null
    private var token = ""

    private val detailChallengeOnProgressViewModel by viewModels<DetailChallengeOnProgressViewModel> {
        ViewModelFactoryUser.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailChallengeOnProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        detailChallengeOnProgressViewModel.getUserSession.observe(this) { user ->
            token = user.jwtToken
            detailChallengeOnProgressViewModel.statusChallenge(token, user.UID)
        }

        detailChallengeOnProgressViewModel.checkStatusChallenge.observe(this) {
            when (it) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    statusChallengeResponse = it.data
                    statusChallengeResponse!!.data.idChallenge?.let { it1 ->
                        detailChallengeOnProgressViewModel.detailChallenge(
                            token,
                            it1
                        )
                    }
                }
                is Result.Error -> {
                    showLoading(false)
                    toastMaker(it.error)
                }
            }
        }

        detailChallengeOnProgressViewModel.detailChallengeResult.observe(this) {
            when (it) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    detailChallengeRespone = it.data
                    setUpListData()
                }
                is Result.Error -> {
                    showLoading(false)
                    toastMaker(it.error)
                }
            }
        }


    }

    private fun setUpListData() {
        binding.apply {
            imageCollapsing.loadImage(detailChallengeRespone!!.data.img)
            collapsingToolbar.title = detailChallengeRespone!!.data.name
            bigDayProgress.text =
                resources.getString(R.string.current_day, statusChallengeResponse!!.data.levelUser)
            challengeDetailProgress.progress =
                100 / statusChallengeResponse!!.data.maxLevel!! * statusChallengeResponse!!.data.levelUser!!
        }

        val listOnProgress = mutableListOf<OnProgress>()
        for (i in 1..statusChallengeResponse!!.data.levelUser!!) {
            val dateStart =
                statusChallengeResponse!!.data.startRulesTime?.plus((i - 1) * onedayinseconds)
            val dateEnd = statusChallengeResponse!!.data.endRulesTime?.plus((i - 1) * onedayinseconds)
            val dateOnProgress =
                "${dateStart?.let { convertEpochToJustDateTime(it) }}-${dateEnd?.let {
                    convertEpochToJustDateTime(
                        it
                    )
                }}"
            val hourOnProgress =
                "${dateStart?.let { convertEpochToHourMinute(it) }}-${dateEnd?.let {
                    convertEpochToHourMinute(
                        it
                    )
                }}"
            val statusOnProgress =
                if (i < statusChallengeResponse!!.data.levelUser!!) 0 else if (i == statusChallengeResponse!!.data.levelUser) 1 else 2

            listOnProgress.add(OnProgress(dateOnProgress, hourOnProgress, statusOnProgress, i))
        }

        val adapter = OnProgressChallengeAdapter(listOnProgress)
        binding.rvChallengeOnprogressList.apply {
            layoutManager = LinearLayoutManager(this@DetailChallengeOnProgressActivity)
            setHasFixedSize(true)
            this.adapter = adapter
        }
    }

    private fun showLoading(isLoading: Boolean) {
    }

    private fun toastMaker(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}