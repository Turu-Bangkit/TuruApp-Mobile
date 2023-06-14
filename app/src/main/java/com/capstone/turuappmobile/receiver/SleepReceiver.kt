package com.capstone.turuappmobile.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.capstone.turuappmobile.R
import com.capstone.turuappmobile.data.db.SleepClassifyEventEntity
import com.capstone.turuappmobile.data.repository.SleepRepository
import com.capstone.turuappmobile.di.Injection
import com.capstone.turuappmobile.ui.fragment.home.HomeFragment
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.SleepClassifyEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant

class SleepReceiver : BroadcastReceiver() {

    private val scope: CoroutineScope = MainScope()
    private var receiveCount = 0

    override fun onReceive(context: Context, intent: Intent) {

        Log.d(TAG, "onReceive(): $intent")

        val repository: SleepRepository = Injection.provideRepository(context)

        if (SleepClassifyEvent.hasEvents(intent)) {


            val sleepClassifyEvents: List<SleepClassifyEvent> =
                SleepClassifyEvent.extractEvents(intent)
            Log.d(TAG, "SleepClassifyEvent List: $sleepClassifyEvents")
            addSleepClassifyEventsToDatabase(repository, sleepClassifyEvents, context)
        }

    }

    private fun addSleepClassifyEventsToDatabase(
        repository: SleepRepository,
        sleepClassifyEvents: List<SleepClassifyEvent>,
        context: Context
    ) {
        if (sleepClassifyEvents.isNotEmpty()) {
            scope.launch {
                receiveCount = repository.totalOnReceiveSleep.first() + 1
                Log.d(TAG, "SleepClassifyEvent receiveCount: $receiveCount")
                repository.updateTotalOnReceiveSleep(receiveCount)

                val convertedToEntityVersion: List<SleepClassifyEventEntity> =
                    sleepClassifyEvents.map {
                        SleepClassifyEventEntity.from(it)
                    }
                repository.insertSleepClassifyEvents(convertedToEntityVersion)
                if( receiveCount > 10 && convertedToEntityVersion[0].confidence < 60){
                    showAlarmNotification(
                        context,
                        "You are Awake!",
                        "Sleep Session Ended",
                        ID
                    )
                    repository.updateEndTime(Instant.now().epochSecond.toInt())
                    val sleepPendingIntent =
                        createSleepReceiverPendingIntent(context = context.applicationContext)
                    unsubscribeToSleepSegmentUpdates(context.applicationContext, sleepPendingIntent, repository)
                }
                if(convertedToEntityVersion[0].confidence > 80){
                    repository.updateRealStartTime(Instant.now().epochSecond.toInt())
                }
            }

        }
    }

    private fun showAlarmNotification(context: Context, title: String, message: String, notifId: Int) {
        Log.e("NOTIF", "showAlarmNotification: $message")
        val channelId = "Channel_1"
        val channelName = "AlarmManager channel"
        val notificationManagerCompat = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.sleep_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setColor(ContextCompat.getColor(context, android.R.color.transparent))
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setSound(alarmSound)
        val channel = NotificationChannel(channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH)
        channel.enableVibration(true)
        channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
        builder.setChannelId(channelId)
        notificationManagerCompat.createNotificationChannel(channel)
        val notification = builder.build()
        notificationManagerCompat.notify(notifId, notification)
    }

    private fun unsubscribeToSleepSegmentUpdates(context: Context, pendingIntent: PendingIntent, repository: SleepRepository) {
        Log.d(HomeFragment.TAG, "unsubscribeToSleepSegmentUpdates()")
        val task = ActivityRecognition.getClient(context).removeSleepSegmentUpdates(pendingIntent)

        task.addOnSuccessListener {
            scope.launch {
                repository.updateSubscribedToSleepData(false)
            }
            Log.d(HomeFragment.TAG, "Successfully unsubscribed to sleep data.")
        }
        task.addOnFailureListener { exception ->
            Log.d(HomeFragment.TAG, "Exception when unsubscribing to sleep data: $exception")
        }
    }

    companion object {
        const val TAG = "SleepReceiver"
        private const val ID = 100
        fun createSleepReceiverPendingIntent(context: Context): PendingIntent {
            val sleepIntent = Intent(context, SleepReceiver::class.java)
            return PendingIntent.getBroadcast(
                context,
                0,
                sleepIntent,
                PendingIntent.FLAG_MUTABLE
            )
        }
    }

}