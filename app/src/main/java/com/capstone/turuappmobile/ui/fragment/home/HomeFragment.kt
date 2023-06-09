package com.capstone.turuappmobile.ui.fragment.home

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.turuappmobile.R
import com.capstone.turuappmobile.adapter.CatalogAdapter
import com.capstone.turuappmobile.data.api.model.DataCatalog
import com.capstone.turuappmobile.data.api.model.DataStatusChallenge
import com.capstone.turuappmobile.data.repository.Result
import com.capstone.turuappmobile.data.viewModelFactory.ViewModelFactory
import com.capstone.turuappmobile.data.viewModelFactory.ViewModelFactoryUser
import com.capstone.turuappmobile.databinding.FragmentHomeBinding
import com.capstone.turuappmobile.receiver.SleepReceiver
import com.capstone.turuappmobile.ui.activity.catalog.CatalogActivity
import com.capstone.turuappmobile.ui.activity.detailAnalysist.DetailAnalysistActivity
import com.capstone.turuappmobile.ui.activity.detailCatalog.DetailCatalogActivity
import com.capstone.turuappmobile.ui.activity.detailChallengeOnProgress.DetailChallengeOnProgressActivity
import com.capstone.turuappmobile.ui.activity.trackSleep.SleepViewModel
import com.capstone.turuappmobile.utils.onedayinseconds
import com.example.awesomedialog.*
import com.google.android.gms.location.ActivityRecognition
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.time.Instant


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var auth: FirebaseAuth? = null

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

        auth = Firebase.auth
        val firebaseUser = auth?.currentUser

        binding.usernameHome.text =
            requireActivity().getString(R.string.greeting, firebaseUser?.displayName)

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
            homeFragmentViewModel.statusChallenge(JWTtoken, preferencesModel.UID)
            homeFragmentViewModel.checkPoints(JWTtoken, preferencesModel.UID)
            homeFragmentViewModel.allcatalog(JWTtoken)


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
                    if (it.data.data != null) startCheckChallenge(it.data.data)
                    else updateUIChallenge(0,0)
                }
                is Result.Error -> {
                    showLoading(false)
                    toastMaker(it.error)
                }
            }
        }

        homeFragmentViewModel.catalogResult.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    showLoadingShimmer(true)
                }
                is Result.Success -> {
                    showLoadingShimmer(false)
                    setDataCatalog(it.data.data)
                }
                is Result.Error -> {
                    showLoadingShimmer(false)
                    toastMaker(it.error)
                }
            }
        }

        homeFragmentViewModel.updateLevelResult.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {

                }
                is Result.Success -> {

                    if (!homeFragmentViewModel.getAlreadyCall()) {


                        if (it.data.message == "Challenge Failed !") {
                            AwesomeDialog.build(requireActivity())
                                .title(
                                    it.data.message,
                                    titleColor = ContextCompat.getColor(
                                        requireActivity(),
                                        R.color.white
                                    ),
                                )
                                .background(R.drawable.bg_rounded_blue200)
                                .position(AwesomeDialog.POSITIONS.CENTER)
                                .onPositive(
                                    "Ok",
                                    buttonBackgroundColor = R.drawable.bg_rounded_red,
                                    textColor = ContextCompat.getColor(
                                        requireActivity(),
                                        R.color.white
                                    )
                                ) {

                                }
                        } else if (it.data.message == "Success Finished Challenge !") {
                            AwesomeDialog.build(requireActivity())
                                .title(
                                    it.data.message,
                                    titleColor = ContextCompat.getColor(
                                        requireActivity(),
                                        R.color.white
                                    ),
                                )
                                .body(
                                    "You got the points !",
                                    color = ContextCompat.getColor(requireActivity(), R.color.white)
                                )
                                .background(R.drawable.bg_rounded_blue200)
                                .position(AwesomeDialog.POSITIONS.CENTER)
                                .onPositive(
                                    "Ok",
                                    buttonBackgroundColor = R.drawable.bg_rounded_blue500,
                                    textColor = ContextCompat.getColor(
                                        requireActivity(),
                                        R.color.white
                                    )
                                ) {

                                }
                        } else {
                            AwesomeDialog.build(requireActivity())
                                .title(
                                    it.data.message,
                                    titleColor = ContextCompat.getColor(
                                        requireActivity(),
                                        R.color.white
                                    ),
                                )
                                .body(
                                    "Success Update Level !",
                                    color = ContextCompat.getColor(requireActivity(), R.color.white)
                                )
                                .background(R.drawable.bg_rounded_blue200)
                                .position(AwesomeDialog.POSITIONS.CENTER)
                                .onPositive(
                                    "Ok",
                                    buttonBackgroundColor = R.drawable.bg_rounded_blue500,
                                    textColor = ContextCompat.getColor(
                                        requireActivity(),
                                        R.color.white
                                    )
                                ) {

                                }
                        }
                        homeFragmentViewModel.alreadyCall()
                    }
                }
                is Result.Error -> {
                    toastMaker(it.error)
                }
            }
        }

        binding.apply {
            btnToChallengeOnProgress.setOnClickListener {
                startActivity(Intent(requireActivity(), DetailChallengeOnProgressActivity::class.java))
            }
            btnToCatalog.setOnClickListener {
                startActivity(Intent(requireActivity(), CatalogActivity::class.java))
            }
            btnSeeHistorySleepHome.setOnClickListener {
                startActivity(Intent(requireActivity(), DetailAnalysistActivity::class.java))
            }
        }

    }

    private fun startCheckChallenge(data: DataStatusChallenge) {
        var levelUser = data.levelUser
        if (levelUser != null && levelUser > 0) {
            val timeNowInEpoch = Instant.now().epochSecond.toInt()
            val newStartRules = data.startRulesTime?.plus(
                (levelUser.minus(1)).times(
                    onedayinseconds
                )
            )
            val newEndRules = data.endRulesTime?.plus((levelUser.minus(1)).times(onedayinseconds))

            if (timeNowInEpoch >= newEndRules!!) {
                if (newStartRules != null) {
                    sleepViewModel.checkChallengePass(newStartRules, newEndRules, UIDUser)
                        .observe(viewLifecycleOwner) {
                            levelUser = if (it > 0) {
                                levelUser!! + 1
                            } else {
                                0
                            }

                            homeFragmentViewModel.updateLevel(JWTtoken, UIDUser, levelUser!!)
                            data.maxLevel?.let { updateUIChallenge(levelUser!!, it) }
                        }
                }
            }

            data.maxLevel?.let { updateUIChallenge(levelUser!!, it) }

        } else {
            updateUIChallenge(0, 0)
        }
    }

    private fun updateUIChallenge(newLevelUser: Int, maxLevel: Int) {
        if (newLevelUser in 1..maxLevel) {
            binding.constraintLayoutData.visibility = View.VISIBLE
            binding.constraintLayoutNoData.visibility = View.GONE
            binding.challengeProgressHome.progress = 100 / maxLevel * newLevelUser
            binding.bigStatusLevel.text = requireActivity().getString(
                R.string.current_day,
                newLevelUser
            )

        } else {
            binding.constraintLayoutData.visibility = View.GONE
            binding.constraintLayoutNoData.visibility = View.VISIBLE
        }

        showLoadingStatusChallenge(false)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.layoutLoadingPoints.itemPointsShimmer.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showLoadingShimmer(isLoading: Boolean) {
        binding.shimmerLayoutCatalogHome.visibility =
            if (isLoading) {
                binding.shimmerLayoutCatalogHome.startShimmer()
                View.VISIBLE
            } else {
                binding.shimmerLayoutCatalogHome.stopShimmer()
                View.GONE
            }
    }

    private fun showLoadingStatusChallenge(isLoading: Boolean) {
        binding.layoutLoadingChallengeStatus.itemPointsShimmer.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }

    private fun toastMaker(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    private fun setDataCatalog(listChallenge: List<DataCatalog>) {
        val adapter = CatalogAdapter(listChallenge, requireActivity()) {
            val intent = Intent(requireActivity(), DetailCatalogActivity::class.java)
            intent.putExtra(DetailCatalogActivity.CATALOG_ID, it.id)
            startActivity(intent)
        }

        binding.rvCatalog.apply {
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            this.adapter = adapter
        }

    }

    private fun unsubscribeToSleepSegmentUpdates(context: Context, pendingIntent: PendingIntent) {
        Log.d(TAG, "unsubscribeToSleepSegmentUpdates()")
        val task = ActivityRecognition.getClient(context).removeSleepSegmentUpdates(pendingIntent)

        task.addOnSuccessListener {
            sleepViewModel.updateSubscribedToSleepData(false)
            Toast.makeText(
                context,
                "Sleep Mode Off",
                Toast.LENGTH_SHORT
            ).show()

            requireActivity().finish()
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