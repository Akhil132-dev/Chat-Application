package com.example.chatapp21.Modleclass

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp21.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.search_for_list_layout.view.*

class Useradapter(
    private val mcontext: Context,
    private val mUser: List<Users>,
    private val b: Boolean
) : RecyclerView.Adapter<Useradapter.coustomviewholder?>() {


    companion object {
        const val visit_id = "visit_profile_id"
    }

    var lastmassage: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): coustomviewholder {
        val view =
            LayoutInflater.from(mcontext).inflate(R.layout.search_for_list_layout, parent, false)
        return coustomviewholder(view)
    }

    override fun getItemCount(): Int {
        return mUser.size
    }


    override fun onBindViewHolder(holder: coustomviewholder, position: Int) {
        val muser = mUser[position]
        holder.itemView.username_search?.text = muser.Username
        Picasso.get().load(muser?.ProfileImage).placeholder(R.drawable.ic_profile)
            .into(holder.itemView.imageView_search)
        if (b) {
            lastMassage(muser.uid, holder.itemView.lastmassage)
        } else {
            holder.itemView.lastmassage.visibility = GONE

        }
        if (b) {
            if (muser.Status == "online") {
                holder.itemView.onlinestust3.visibility = View.VISIBLE
                holder.itemView.onlinestust3soff.visibility = GONE

            } else {
                holder.itemView.onlinestust3.visibility = GONE
                holder.itemView.onlinestust3soff.visibility = View.VISIBLE
            }
        } else {
            holder.itemView.onlinestust3.visibility = View.GONE
            holder.itemView.onlinestust3soff.visibility = View.GONE
        }
        holder.itemView.setOnClickListener {
            val option = arrayOf<CharSequence>(
                "Send Massage",
                "Visit  Profile"
            )
            val builder: AlertDialog.Builder = AlertDialog.Builder(mcontext)
            builder.setTitle("what do you want?")
            builder.setItems(option, DialogInterface.OnClickListener { dialog, which ->
                if (which == 0) {
                    val intent = Intent(mcontext, ChatmassageActivity::class.java)
                    intent.putExtra(visit_id, muser.uid)
                    mcontext.startActivity(intent)
                    holder.itemView.lastmassage.setTextColor(Color.GRAY)
                }
                if (which == 1) {
                   val  intent  = Intent(mcontext,viewprofile::class.java)
                    intent.putExtra(visit_id, muser.uid)
                  mcontext.startActivity(intent)

                }
            })

            builder.show()
        }
    }

    private fun lastMassage(uid: String, Lastmassagetext: TextView?) {
        lastmassage = "defaultMsg"
        val firebaseuser = FirebaseAuth.getInstance().currentUser
        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (datasnapshot in snapshot.children) {
                    val chats = datasnapshot.getValue(Chat::class.java)
                    if (firebaseuser != null && chats != null) {
                        if (chats.receiver == firebaseuser!!.uid && chats.sender == uid || chats.receiver == uid && chats.sender == firebaseuser!!.uid) {
                            lastmassage = chats.massage!!
                        }
                    }
                }
                when (lastmassage) {

                    "defaultMsg" -> Lastmassagetext!!.text = "No Massage "
                    "sent you an image." -> {
                        Lastmassagetext!!.text = "Imagesent "
                        Lastmassagetext!!.setTextColor(Color.BLACK)
                    }
                    else -> {
                        Lastmassagetext!!.text = lastmassage
                        Lastmassagetext!!.setTextColor(Color.BLACK)
                    }

                }
                lastmassage = "defaultMsg"
            }


        })
    }

    class coustomviewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}