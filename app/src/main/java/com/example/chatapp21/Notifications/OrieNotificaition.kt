package com.example.chatapp21.Notifications

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.os.Build
import com.google.firebase.messaging.RemoteMessage

class OrieNotificaition(val basecontext: Context):ContextWrapper(basecontext) {
    private  var notificationManager:NotificationManager?=null

    companion object{

        var CHANNEL_ID = "com.example.chatapp21"
        var CHANNEL_NAME = "massenger App"
    }
    init {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
           creatchannle()
        }
    }
    @TargetApi(Build.VERSION_CODES.O)
    private  fun creatchannle(){
        val channel  = NotificationChannel(CHANNEL_ID
        ,CHANNEL_NAME,
        NotificationManager.IMPORTANCE_DEFAULT)
        channel.enableLights(false)
        channel.enableVibration(true)
        channel.lockscreenVisibility =Notification.VISIBILITY_PRIVATE
        getManager!!.createNotificationChannel(channel)
    }
    val getManager:NotificationManager? get() {
        if(notificationManager == null){
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE ) as NotificationManager

        }
        return  notificationManager
    }
    @TargetApi(Build.VERSION_CODES.O)
    fun getorionotiifvation(title:String?,body:String?,Paddingintent:PendingIntent?,sounduri:Uri,icon:String?):Notification.Builder{
        return Notification.Builder(applicationContext, CHANNEL_ID)
            .setContentIntent(Paddingintent).
            setContentTitle(title).
            setContentText(body).setSmallIcon(icon!!.toInt()).
            setSound(sounduri).setAutoCancel(true)
    }
}