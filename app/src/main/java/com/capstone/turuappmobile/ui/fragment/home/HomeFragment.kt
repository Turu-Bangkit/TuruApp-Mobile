package com.capstone.turuappmobile.ui.fragment.home

import android.app.PendingIntent
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.capstone.turuappmobile.data.db.SleepTimeEntity
import com.capstone.turuappmobile.data.viewModelFactory.ViewModelFactoryUser
import com.capstone.turuappmobile.databinding.FragmentHomeBinding
import com.capstone.turuappmobile.data.repository.Result
import com.capstone.turuappmobile.data.viewModelFactory.ViewModelFactory
import com.capstone.turuappmobile.receiver.SleepReceiver
import com.capstone.turuappmobile.ui.activity.trackSleep.SleepActivity
import com.capstone.turuappmobile.ui.activity.trackSleep.SleepViewModel
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sleepViewModel.subscribedToSleepDataLiveData.observe(viewLifecycleOwner) { newSubscribedToSleepData ->
            if (newSubscribedToSleepData) {
                val sleepPendingIntent =
                    SleepReceiver.createSleepReceiverPendingIntent(context = requireContext().applicationContext)
                unsubscribeToSleepSegmentUpdates(requireContext().applicationContext, sleepPendingIntent)
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

        homeFragmentViewModel.getUserSession.observe(viewLifecycleOwner) {
            Log.d("Testing get UID and Token", "UID: ${it.UID} Token: ${it.jwtToken}")
            homeFragmentViewModel.checkPoints(it.jwtToken, it.UID)
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


    }

    private fun showLoading(isLoading: Boolean) {
        binding.layoutLoading.layoutAllLoading.visibility =
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