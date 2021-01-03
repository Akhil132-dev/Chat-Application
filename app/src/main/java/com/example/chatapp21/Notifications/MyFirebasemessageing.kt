package com.example.chatapp21.Notifications

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import androidx.media.app.NotificationCompat
import com.example.chatapp21.ChatmassageActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebasemessageing : FirebaseMessagingService() {
    override fun onMessageReceived(MRemoteMassage: RemoteMessage) {
        super.onMessageReceived(MRemoteMassage)
        val sented = MRemoteMassage.data["sented"]
        val user = MRemoteMassage.data["user"]
        val sharepref = getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        val currentonlineuser = sharepref.getString("currentUSer", "none")
        val firebaseuser = FirebaseAuth.getInstance().currentUser
        if (firebaseuser != null && sented == firebaseuser!!.uid) {
            if (currentonlineuser != user) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sendorionotifiction(MRemoteMassage)
                } else {
                    sendorionotifictionlessethen_orio(MRemoteMassage)
                }
            }
        }
    }

    private fun sendorionotifictionlessethen_orio(MRemoteMassage: RemoteMessage) {
        val user = MRemoteMassage.data["user"]
        val icon = MRemoteMassage.data["icon"]
        val title = MRemoteMassage.data["title"]
        val body = MRemoteMassage.data["body"]
        val notification = MRemoteMassage.notification
        val j = user!!.replace("[\\D]".toRegex(), "").toInt()
        val intent = Intent(this, ChatmassageActivity::class.java)
        val bundle = Bundle()
        bundle.putString("userid", user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT)
        val defualsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = androidx.core.app.NotificationCompat.Builder(this)
            .setSmallIcon(icon!!.toInt()).setContentTitle(title).setContentText(body)
            .setAutoCancel(true).setSound(defualsound).setContentIntent(pendingIntent)

        val noti = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        var i = 0
        if (j > 0) {
            i = j

        }
    }

    private fun sendorionotifiction(MRemoteMassage: RemoteMessage) {
        val user = MRemoteMassage.data["user"]
        val icon = MRemoteMassage.data["icon"]
        val title = MRemoteMassage.data["title"]
        val body = MRemoteMassage.data["body"]
        val notification = MRemoteMassage.notification
        val j = user!!.replace("[\\D]".toRegex(), "").toInt()
        val intent = Intent(this, ChatmassageActivity::class.java)
        val bundle = Bundle()
        bundle.putString("userid", user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT)
        val defualsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val oreoNotification = OrieNotificaition(this)
        val builder: Notification.Builder =
            oreoNotification.getorionotiifvation(title, body, pendingIntent, defualsound, icon)
        var i = 0
        if (j > 0) {
            i = j
            oreoNotification.getManager!!.notify(i, builder.build())
        }
    }
}


