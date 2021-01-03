package com.example.chatapp21

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_mai_welcome.*

class MaiWelcomeActivity : AppCompatActivity() {
   var firebaseUser:FirebaseUser? = null
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mai_welcome)

        Register_welcome.setOnClickListener {
            val  intent  = Intent(this,RegisterActivity::class.java)
            startActivity(intent)

        }

        Login_Welcome_button.setOnClickListener {
            val  intent  = Intent(this,LoginActivity::class.java)
            startActivity(intent)

        }
    }

    override fun onStart() {
        super.onStart()
        firebaseUser =  FirebaseAuth.getInstance().currentUser
        if(firebaseUser!=null){
            val  intent  = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}