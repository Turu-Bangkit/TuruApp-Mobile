package com.capstone.turuappmobile.ui.fragment.home

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.capstone.turuappmobile.R
import com.capstone.turuappmobile.data.api.model.DataStatusChallenge
import com.capstone.turuappmobile.data.api.model.StatusChallengeResponse
import com.capstone.turuappmobile.data.db.SleepTimeEntity
import com.capstone.turuappmobile.data.viewModelFactory.ViewModelFactoryUser
import com.capstone.turuappmobile.databinding.FragmentHomeBinding
import com.capstone.turuappmobile.data.repository.Result
import com.capstone.turuappmobile.data.viewModelFactory.ViewModelFactory
import com.capstone.turuappmobile.receiver.SleepReceiver
import com.capstone.turuappmobile.ui.activity.detailChallengeOnProgress.DetailChallengeOnProgressActivity
import com.capstone.turuappmobile.ui.activity.trackSleep.SleepActivity
import com.capstone.turuappmobile.ui.activity.trackSleep.SleepViewModel
import com.capstone.turuappmobile.utils.onedayinseconds
import com.google.android.gms.location.ActivityRecognition
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.Instant


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    private val homeFragmentViewModel by viewModels<HomeFragmentViewModel> {
        ViewModelFactoryUser.getInstance(requireActivity())
    }

    private val sleepViewModel by viewModels<SleepViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    private var UIDUser = ""
    private var JWTtoken = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.challengeProgressHome.progress = 50

        sleepViewModel.subscribedToSleepDataLiveData.observe(viewLifecycleOwner) { newSubscribedToSleepData ->
            if (newSubscribedToSleepData) {
                val sleepPendingIntent =
                    SleepReceiver.createSleepReceiverPendingIntent(context = requireContext().applicationContext)
                unsubscribeToSleepSegmentUpdates(
                    requireContext().applicationContext,
                    sleepPendingIntent
                )
                val instant = Instant.now()
                sleepViewModel.updateEndTimeSleep(instant.epochSecond.toInt())

                MaterialAlertDialogBuilder(requireActivity())
                    .setTitle("Mode Sleep Off")
                    .setPositiveButton("Ok") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }

        homeFragmentViewModel.getUserSession.observe(viewLifecycleOwner) { preferencesModel ->

            UIDUser = preferencesModel.UID
            JWTtoken = preferencesModel.jwtToken
            Log.d("UID", UIDUser)
            sleepViewModel.allSleepQuality(preferencesModel.UID).observe(viewLifecycleOwner) {
                val sleepQuality: String
                val textSize: Float
                if (it.isNotEmpty()) {
                    sleepQuality = requireActivity().getString(
                        R.string.result_quality,
                        it.last().sleepQuality.toInt()
                    )
                    textSize = 26F
                } else {
                    sleepQuality = requireActivity().getString(R.string.sleep_quality_nodata)
                    textSize = 20F
                }

                binding.sleepQualityHomeTxt.apply {
                    text = sleepQuality
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
                }

            }
            homeFragmentViewModel.checkPoints(preferencesModel.jwtToken, preferencesModel.UID)
            homeFragmentViewModel.statusChallenge(preferencesModel.jwtToken, preferencesModel.UID)

        }

        homeFragmentViewModel.checkPointResult.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    binding.totalPoint.text = it.data.point
                }
                is Result.Error -> {
                    showLoading(false)
                    toastMaker(it.error)
                }
            }
        }

        homeFragmentViewModel.checkStatusChallenge.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    startCheckChallenge(it.data.data)
                }
                is Result.Error -> {
                    showLoading(false)
                    toastMaker(it.error)
                }
            }
        }

        binding.btnToChallengeOnProgress.setOnClickListener {
            startActivity(Intent(requireActivity(), DetailChallengeOnProgressActivity::class.java))
        }

    }

    private fun startCheckChallenge(data: DataStatusChallenge) {
        val levelUser = data.levelUser
        if (levelUser != 0) {
            val timeNowInEpoch = Instant.now().epochSecond.toInt()
            val newStartRules = data.startRulesTime + (levelUser - 1) * onedayinseconds
            val newEndRules = data.endRulesTime + (levelUser - 1) * onedayinseconds
            var newLevelUser = 0

            if (timeNowInEpoch >= newEndRules) {
                sleepViewModel.checkChallengePass(newStartRules, newEndRules, UIDUser)
                    .observe(viewLifecycleOwner) {
                        if (it > 0) newLevelUser++
                        homeFragmentViewModel.updateLevel(JWTtoken, UIDUser, newLevelUser)
                    }
            }

            updateUIChallenge(newLevelUser, data.maxLevel)

        } else {
            // Update UI No data

        }
    }

    private fun updateUIChallenge(newLevelUser: Int, maxLevel: Int) {
        if (newLevelUser > 0) {
            binding.constraintLayoutData.visibility = View.VISIBLE
            binding.constraintLayoutNoData.visibility = View.GONE
            binding.challengeProgressHome.progress = 100 / maxLevel * newLevelUser

        }else{
            binding.constraintLayoutData.visibility = View.GONE
            binding.constraintLayoutNoData.visibility = View.VISIBLE
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.layoutLoadingPoints.itemPointsShimmer.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }

    private fun toastMaker(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    private fun unsubscribeToSleepSegmentUpdates(context: Context, pendingIntent: PendingIntent) {
        Log.d(TAG, "unsubscribeToSleepSegmentUpdates()")
        val task = ActivityRecognition.getClient(context).removeSleepSegmentUpdates(pendingIntent)

        task.addOnSuccessListener {
            sleepViewModel.updateSubscribedToSleepData(false)
            Log.d(TAG, "Successfully unsubscribed to sleep data.")
        }
        task.addOnFailureListener { exception ->
            Log.d(TAG, "Exception when unsubscribing to sleep data: $exception")
        }
    }

    companion object {
        const val TAG = "HomeFragment"
    }

}