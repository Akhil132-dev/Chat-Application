package com.example.chatapp21

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*


class LoginActivity : AppCompatActivity() {
    var progressBar:ProgressDialog?=null
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setSupportActionBar(toolbar_main_logine)
        supportActionBar?.title = "Login"
        supportActionBar?.setDefaultDisplayHomeAsUpEnabled(true)

Login_button.setOnClickListener {
    progressBar = ProgressDialog(this)
    progressBar!!.setMessage("Log In please wait....")
    progressBar!!.show()
    LoginToid()
}
    }
    private fun LoginToid(){
        val email = email_Login.text.toString()
        val password  = password_Login.text.toString()
        if( email.isEmpty() or password.isEmpty()){
            Toast.makeText(applicationContext,"Please Enter Complete information", Toast.LENGTH_SHORT).show()
        }
        else{
            val maAuth =  FirebaseAuth.getInstance()
            maAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener {
                val  intent  = Intent(this,MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                progressBar!!.dismiss()
                finish()

            }
                .addOnFailureListener{
                    Toast.makeText(applicationContext,"Error Massage"+it.message.toString(),Toast.LENGTH_SHORT).show()
                    progressBar!!.dismiss()
                }
        }

    }
}