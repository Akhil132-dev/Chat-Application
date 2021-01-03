package com.example.chatapp21
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.UserHandle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_mai_welcome.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    companion object{
        val Tag = " This is Tag"
    }
    var progressBar:ProgressDialog?=null
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setSupportActionBar(toolbar_main_register)
        supportActionBar?.title = "Register"
        Register.setOnClickListener {
            progressBar = ProgressDialog(this)
            progressBar!!.setMessage("Log In please wait....")
            progressBar!!.show()
            Registerfirebase()

        }

    }
   private fun Registerfirebase(){
        val ref = FirebaseAuth.getInstance()
       val username =  username_Register.text.toString()
       val email = email_Register.text.toString()
       val password  = passord_register.text.toString()

       if(username.isEmpty() or email.isEmpty() or password.isEmpty()){
           Toast.makeText(applicationContext,"Please Enter Complete information",Toast.LENGTH_SHORT).show()
       }
       else{
           ref.createUserWithEmailAndPassword(email,password).addOnSuccessListener {
           var FirebaseUserid=  ref.currentUser?.uid.toString()
            var    refrencetodatabase  = FirebaseDatabase.getInstance().reference.child("user").child(FirebaseUserid)
            val userhasMap = HashMap<String,Any>()
               userhasMap["uid"] =  FirebaseUserid
               userhasMap["Username"] =  username
               userhasMap["ProfileImage"] =  "https://firebasestorage.googleapis.com/v0/b/chatapp21-2b53e.appspot.com/o/OIP.jpg?alt=media&token=7f36285a-05a2-44ce-b4e5-fadc8eb6d7ff"
               userhasMap["cover"] =   "https://firebasestorage.googleapis.com/v0/b/chatapp21-2b53e.appspot.com/o/facebook-cover-photos.jpg?alt=media&token=af6c68f0-0e91-48c0-b73f-db996aad39e1"
               userhasMap["Status"] =  "Offline"
               userhasMap["search"] =  username.toLowerCase()
               userhasMap["facebook"] =  "https://m.facebook.com"
               userhasMap["instagram"] = "https://m.instagram.com"
               userhasMap["website"] =   "https://m.google.com"
               refrencetodatabase.updateChildren(userhasMap).addOnSuccessListener {
Log.d("this","finally add to fireabase")
//                       val  intent  = Intent(this,MainActivity::class.java)
//                   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//                       startActivity(intent)
                   progressBar!!.dismiss()
//                       finish()

               }

           }
               .addOnFailureListener{
                   Toast.makeText(applicationContext,"Error Massage"+it.message.toString(),Toast.LENGTH_SHORT).show()
                   progressBar!!.dismiss()
               }
       }
    }
}