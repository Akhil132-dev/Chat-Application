package com.example.chatapp21.Notifications

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService

class MyfirebaseinstantId:FirebaseMessagingService() {
    override fun onNewToken(p0: String) {

        super.onNewToken(p0)
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val ret = FirebaseInstanceId.getInstance().token
        if(firebaseUser!=null){
            if (ret != null) {
                upadattoken(ret)
            }
        }
    }
    fun   upadattoken(reftoken:String){
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val Tokens = Token(ref.toString()!!)
        ref.child(firebaseUser!!.uid).setValue(Tokens)
    }
}