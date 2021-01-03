package com.example.chatapp21

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chatapp21.Modleclass.Useradapter
import com.example.chatapp21.Modleclass.Users
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_viewprofile.*

class viewprofile : AppCompatActivity() {
    var user:Users?=null
    companion object {
        const val visit_id = "visit_profile_id"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewprofile)
        val url  = intent.getStringExtra(Useradapter.visit_id)
        val ref = url?.let { FirebaseDatabase.getInstance().reference.child(it) }
        if (ref != null) {
            ref.addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        user = snapshot.getValue(Users::class.java)
                        username_setting_view.text = user!!.Username
                        Picasso.get().load(user?.ProfileImage).into(profile_settin_view)
                        Picasso.get().load(user?.cover).into(cover_image_setting_view)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
        fb_id_view.setOnClickListener {
            val uri = Uri.parse(user!!.facebook)
            val intent = Intent(Intent.ACTION_VIEW,uri)
            startActivity(intent)
        }
        instragram_id_view.setOnClickListener {
            val uri = Uri.parse(user!!.instagram)
            val intent = Intent(Intent.ACTION_VIEW,uri)
            startActivity(intent)
        }
        website_id_view.setOnClickListener {
            val uri = Uri.parse(user!!.website)
            val intent = Intent(Intent.ACTION_VIEW,uri)
            startActivity(intent)
        }
        button_send.setOnClickListener {
            val  intent  = Intent(this,ChatmassageActivity::class.java)
            intent.putExtra(visit_id, user!!.uid)
          startActivity(intent)
        }
    }
}