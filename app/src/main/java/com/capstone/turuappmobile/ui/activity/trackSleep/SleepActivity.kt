package com.capstone.turuappmobile.ui.activity.trackSleep

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.capstone.turuappmobile.BuildConfig
import com.capstone.turuappmobile.R
import com.capstone.turuappmobile.data.db.SleepTimeEntity
import com.capstone.turuappmobile.data.viewModelFactory.ViewModelFactory
import com.capstone.turuappmobile.data.viewModelFactory.ViewModelFactoryUser
import com.capstone.turuappmobile.databinding.ActivitySleepBinding
import com.capstone.turuappmobile.receiver.SleepReceiver
import com.example.awesomedialog.*
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.SleepSegmentRequest
import com.google.android.material.snackbar.Snackbar
import java.time.Instant
import java.util.*

class SleepActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySleepBinding

    private val sleepViewModel by viewModels<SleepViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val sleepActivityViewModel by viewModels<SleepActivityViewModel> {
        ViewModelFactoryUser.getInstance(this)
    }

    private var userUID: String = ""

    private lateinit var sleepPendingIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySleepBinding.inflate(layoutInflater)
        setContentView(binding.root)


        sleepPendingIntent =
            SleepReceiver.createSleepReceiverPendingIntent(context = applicationContext)

        sleepActivityViewModel.getUserSession.observe(this) { user ->
            userUID = user.UID
        }

        if (activityRecognitionPermissionApproved()) {
            binding.outputTextView.text = getString(R.string.permission_approved)
        }

        binding.btnStartSleep.setOnClickListener {
            onClickRequestSleepData()
        }
    }

    private fun onClickRequestSleepData() {
        if (activityRecognitionPermissionApproved()) {

            AwesomeDialog.build(this)
                .title("Start Sleep ?")
                .body(
                    "Aplikasi Akan Keluar dan Masuk ke Mode Sleep"
                )
                .background(R.drawable.bg_rounded_green100)
                .onPositive("Start",
                    buttonBackgroundColor = R.drawable.bg_rounded_green150,
                    textColor = ContextCompat.getColor(this, R.color.white)) {
                    subscribeToSleepSegmentUpdates(applicationContext, sleepPendingIntent)
                    val instant = Instant.now()
                    val sleepTimeEntity = SleepTimeEntity(
                        userUID = userUID,
                        startTime = instant.epochSecond.toInt()
                    )
                    sleepViewModel.insertStartTimeSleep(sleepTimeEntity)

                }
                .onNegative(
                    "Cancel",
                    buttonBackgroundColor = R.drawable.bg_rounded_white,
                    textColor = ContextCompat.getColor(this, R.color.green_200)
                ) {
                    Log.d("TAG", "negative ")
                }


        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        }
    }

    @SuppressLint("MissingPermission")
    private fun subscribeToSleepSegmentUpdates(context: Context, pendingIntent: PendingIntent) {
        Log.d(TAG, "requestSleepSegmentUpdates()")

        val task = ActivityRecognition.getClient(context).requestSleepSegmentUpdates(
            pendingIntent,

            SleepSegmentRequest.getDefaultSleepSegmentRequest()
        )

        task.addOnSuccessListener {
            sleepViewModel.updateSubscribedToSleepData(true)
            sleepViewModel.updateTotalOnReceiveSleep(0)
            Log.d(TAG, "Successfully subscribed to sleep data.")
            finishAffinity()
        }
        task.addOnFailureListener { exception ->
            Log.d(TAG, "Exception when subscribing to sleep data: $exception")
        }
    }


    private fun activityRecognitionPermissionApproved(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACTIVITY_RECOGNITION
        )
    }

    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {

                displayPermissionSettingsSnackBar()
            } else {

                binding.outputTextView.text = getString(R.string.permission_approved)
            }
        }

    private fun displayPermissionSettingsSnackBar() {
        Snackbar.make(
            binding.sleepActivity,
            R.string.permission_rational,
            Snackbar.LENGTH_LONG
        )
            .setAction(R.string.action_settings) {

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


    companion object {
        private const val TAG = "SleepActivity"
    }
}