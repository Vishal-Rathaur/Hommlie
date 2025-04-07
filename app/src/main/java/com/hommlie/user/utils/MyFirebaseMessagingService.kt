package com.hommlie.user.utils

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hommlie.user.activity.ActMain


class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.e("NEW_TOKEN", s)
    }

    override fun onMessageReceived(rm: RemoteMessage) {
        // Handle the incoming message here
       // Log.d("complete payload", remoteMessage.toString())
       // Log.d("payload", remoteMessage.data.toString())
       // Log.d("username", remoteMessage.data["username"]!!)
        Log.d("title", rm.notification!!.title!!)
        Log.d("body", rm.notification!!.body!!)


        // create notification channel
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            var channel = NotificationChannel("MyNotification","MyNotification",
                NotificationManager.IMPORTANCE_DEFAULT)
            var manager = getSystemService(NotificationManager::class.java) as NotificationManager;
            manager.createNotificationChannel(channel)
        }
        //log data
        Log.e("TAG", "onMessageReceived: "+rm.data["title"]+rm.data["description"]+rm.data["body"], )
        showNotification(rm.notification!!.title!!,rm.notification!!.body!!)

        super.onMessageReceived(rm);
    }

    private fun showNotification(title: String?, desc: String?) {
        //val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val notificationIntent =
            Intent(applicationContext, ActMain::class.java)
        //create pending intent
        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        //create builder
        var builder = NotificationCompat.Builder(this,"MyNotification")
            .setContentTitle("$title")
            .setSmallIcon(R.drawable.ic_menu_camera)
            .setContentText(desc)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
        var manager = NotificationManagerCompat.from(this)
        manager.apply {
            notify(123,builder.build())
        }
    }

    override fun handleIntent(intent: Intent) {
        try {
            if (intent.extras != null) {
                val builder = RemoteMessage.Builder("MessagingService")
                for (key in intent.extras!!.keySet()) {
                    builder.addData(key!!, intent.extras!![key].toString())
                }
                onMessageReceived(builder.build())
            } else {
                super.handleIntent(intent)
            }
        } catch (e: Exception) {
            super.handleIntent(intent)
        }
    }

    /* Reference
    * https://codingwithtashi.medium.com/firebase-onmessagereceived-not-called-when-app-in-background-18e78280c6cb
    * */

}
