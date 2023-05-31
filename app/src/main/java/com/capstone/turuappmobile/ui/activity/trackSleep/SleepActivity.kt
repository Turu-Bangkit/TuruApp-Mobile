package com.capstone.turuappmobile.ui.activity.trackSleep

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.capstone.turuappmobile.R
import com.capstone.turuappmobile.data.db.SleepTimeEntity
import com.capstone.turuappmobile.data.viewModelFactory.ViewModelFactory
import com.capstone.turuappmobile.databinding.ActivitySleepBinding
import com.capstone.turuappmobile.receiver.SleepReceiver
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.SleepSegmentRequest
import com.google.android.material.snackbar.Snackbar
import java.time.Instant
import java.util.*
import com.capstone.turuappmobile.BuildConfig
import com.capstone.turuappmobile.data.viewModelFactory.ViewModelFactoryUser
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SleepActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySleepBinding

    private val sleepViewModel by viewModels<SleepViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val sleepActivityViewModel by viewModels<SleepActivityViewModel> {
        ViewModelFactoryUser.getInstance(this)
    }

    private var sleepClassifyOutput: String = ""
    private var userUID: String = ""

    private var subscribedToSleepData = false
        set(newSubscribedToSleepData) {
            field = newSubscribedToSleepData
            if (newSubscribedToSleepData) {
                binding.button.text = getString(R.string.sleep_button_unsubscribe_text)
                Log.d("subscribedToSleepData5", "insert")

            } else {
                binding.button.text = getString(R.string.sleep_button_subscribe_text)
            }
            updateOutput()
        }

    private lateinit var sleepPendingIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySleepBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sleepViewModel.subscribedToSleepDataLiveData.observe(this) { newSubscribedToSleepData ->
            if (subscribedToSleepData != newSubscribedToSleepData) {
                subscribedToSleepData = newSubscribedToSleepData
            }
        }

        sleepViewModel.allSleepClassifyEventEntities.observe(this) { sleepClassifyEventEntities ->
            Log.d(TAG, "sleepClassifyEventEntities: $sleepClassifyEventEntities")

            if (sleepClassifyEventEntities.isNotEmpty()) {
                // Constructor isn't accessible for [SleepClassifyEvent], so we just output the
                // database table version.
                sleepClassifyOutput = sleepClassifyEventEntities.joinToString {
                    "\t$it\n"
                }
                updateOutput()
            }
        }

        sleepPendingIntent =
            SleepReceiver.createSleepReceiverPendingIntent(context = applicationContext)

        sleepActivityViewModel.getUserSession.observe(this) { user ->
            userUID = user.UID
        }
    }

    fun onClickRequestSleepData(view: View) {
        if (activityRecognitionPermissionApproved()) {
            if (subscribedToSleepData) {
                unsubscribeToSleepSegmentUpdates(applicationContext, sleepPendingIntent)
                val instant = Instant.now()
                Log.d("subscribedToSleepData5", "update ")
                sleepViewModel.updateEndTimeSleep(instant.epochSecond.toInt())
            } else {

                MaterialAlertDialogBuilder(this)
                    .setTitle("Start Sleep ?")
                    .setMessage("Aplikasi Akan Keluar dan Masuk ke Mode Sleep")
                    .setNegativeButton("Cancel") { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("Start") { dialog, which ->
                        dialog.dismiss()
                        subscribeToSleepSegmentUpdates(applicationContext, sleepPendingIntent)
                        val instant = Instant.now()
                        val sleepTimeEntity = SleepTimeEntity(
                            userUID = userUID,
                            startTime = instant.epochSecond.toInt()
                        )
                        sleepViewModel.insertStartTimeSleep(sleepTimeEntity)
                        finishAffinity()
                    }
                    .show()


            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        }
    }

    @SuppressLint("MissingPermission")
    private fun subscribeToSleepSegmentUpdates(context: Context, pendingIntent: PendingIntent) {
        Log.d(TAG, "requestSleepSegmentUpdates()")
        // TODO: Request Sleep API updates
        val task = ActivityRecognition.getClient(context).requestSleepSegmentUpdates(
            pendingIntent,
            // Registers for both [SleepSegmentEvent] and [SleepClassifyEvent] data.
            SleepSegmentRequest.getDefaultSleepSegmentRequest()
        )

        task.addOnSuccessListener {
            sleepViewModel.updateSubscribedToSleepData(true)
            Log.d(TAG, "Successfully subscribed to sleep data.")
        }
        task.addOnFailureListener { exception ->
            Log.d(TAG, "Exception when subscribing to sleep data: $exception")
        }
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


    private fun activityRecognitionPermissionApproved(): Boolean {
        // Because this app targets 29 and above (recommendation for using the Sleep APIs), we
        // don't need to check if this is on a device before runtime permissions, that is, a device
        // prior to 29 / Q.
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACTIVITY_RECOGNITION
        )
    }

    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                // Permission denied on Android platform that supports runtime permissions.
                displayPermissionSettingsSnackBar()
            } else {
                // Permission was granted (either by approval or Android version below Q).
                binding.outputTextView.text = getString(R.string.permission_approved)
            }
        }

    private fun displayPermissionSettingsSnackBar() {
        Snackbar.make(
            binding.mainActivity,
            R.string.permission_rational,
            Snackbar.LENGTH_LONG
        )
            .setAction(R.string.action_settings) {
                // Build intent that displays the App settings screen.
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts(
                    "package",
                    BuildConfig.APPLICATION_ID,
                    null
                )
                intent.data = uri
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            .show()
    }

    private fun updateOutput() {
        Log.d(TAG, "updateOutput()")

        val header = if (subscribedToSleepData) {
            val timestamp = Calendar.getInstance().time.toString()
            getString(R.string.main_output_header1_subscribed_sleep_data, timestamp)
        } else {
            getString(R.string.main_output_header1_unsubscribed_sleep_data)
        }

        val sleepData = getString(
            R.string.main_output_header2_and_sleep_data,
            sleepClassifyOutput
        )

        val newOutput = header + sleepData
        binding.outputTextView.text = newOutput
    }


    companion object {
        private const val TAG = "MainActivity"
    }
}