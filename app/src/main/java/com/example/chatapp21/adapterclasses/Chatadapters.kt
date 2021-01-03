package com.example.chatapp21.adapterclasses

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.contentValuesOf
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp21.Chat
import com.example.chatapp21.ChatmassageActivity
import com.example.chatapp21.Modleclass.Useradapter
import com.example.chatapp21.R
import com.example.chatapp21.viewImageActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.chat_for_tight_massager.view.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import kotlinx.android.synthetic.main.massage_itme_left.view.*
import kotlinx.android.synthetic.main.massage_itme_left.view.show_text_massage


class Chatadapters( val mcontext:Context, val  mchatlist:List<Chat>, val Imageurl:String):RecyclerView.Adapter<Chatadapters.ViewHolder>() {
    val Firebaseuser:FirebaseUser? = FirebaseAuth.getInstance().currentUser!!
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
     return if(position == 1){
         val view = LayoutInflater.from(mcontext).inflate(R.layout.chat_for_tight_massager,parent,false)
ViewHolder(view)
     }
        else{
         val view = LayoutInflater.from(mcontext).inflate(R.layout.massage_itme_left,parent,false)
         ViewHolder(view)
     }
    }

    override fun getItemCount(): Int {
     return mchatlist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
val chat:Chat = mchatlist[position]
        Picasso.get().load(Imageurl).into(holder.profile_image)
        //Image massage right side
        if(chat.massage.equals("sent you an image.") && !chat.url.equals("")){
            if(chat.sender.equals(Firebaseuser!!.uid)){
                holder.show_text_massage!!.visibility = GONE
                holder.right_image_View!!.visibility = VISIBLE
                Picasso.get().load(chat.url).into(holder.right_image_View)

              holder.right_image_View!!.setOnClickListener {

                  val option = arrayOf<CharSequence>(
                      "View Full image",
                      "Delete Image",
                      "Cancel"
                  )
                  val builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                  builder.setTitle("what do you want?")

                  builder.setItems(option, DialogInterface.OnClickListener{
                          dialog, which ->
                      if(which == 0){

                       val  intent  = Intent(mcontext,viewImageActivity::class.java)
                          intent.putExtra("url",chat.url)
                           mcontext.startActivity(intent)
                      }
                     else if(which == 1) {
delatesendtMassage(position,holder)
                      }

                  })

                  builder.show()}
            }
            else  {
                holder.show_text_massage!!.visibility = GONE
                holder.Left_image_view!!.visibility = VISIBLE
                Picasso.get().load(chat.url).into(holder.Left_image_view)
                holder.Left_image_view!!.setOnClickListener {

                    val option = arrayOf<CharSequence>(
                        "View Full image",
                        "Cancel"
                    )
                    val builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("what do you want?")

                    builder.setItems(option, DialogInterface.OnClickListener{
                            dialog, which ->
                        if(which == 0){

                            val  intent  = Intent(mcontext,viewImageActivity::class.java)
                            intent.putExtra("url",chat.url)
                            mcontext.startActivity(intent)
                        }


                    })

                    builder.show()}
            }
        }
      //text massages
      else{
            holder.show_text_massage!!.text = chat.massage
            if(Firebaseuser!!.uid == chat.sender){
                holder.right_image_View!!.setOnClickListener {

                    val option = arrayOf<CharSequence>(

                        "Delete Image",
                        "Cancel"
                    )
                    val builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("what do you want?")

                    builder.setItems(option, DialogInterface.OnClickListener{
                            dialog, which ->
                        if(which == 0){

                            delatesendtMassage(position,holder)

                        }

                    })

                    builder.show()}
            }

        }
        // sent seen massage
        if(position == mchatlist.size-1){
          if(chat.isseen){
              holder.textSeen!!.text =  "Seen"
              if(chat.massage.equals("sent you an image.") && !chat.url.equals("")){
                  val lp:RelativeLayout.LayoutParams? = holder.textSeen!!.layoutParams as  RelativeLayout.LayoutParams?
                  lp!!.setMargins(0,245,10,0)
                  holder.textSeen!!.layoutParams =lp
              }
          }
            else{
              holder.textSeen!!.text =  "Sent"
              if(chat.massage.equals("sent you an image.") && !chat.url.equals("")){
                  val lp:RelativeLayout.LayoutParams? = holder.textSeen!!.layoutParams as  RelativeLayout.LayoutParams?
                  lp!!.setMargins(0,245,10,0)
                  holder.textSeen!!.layoutParams =lp
              }
          }
        }
        else{
            holder.textSeen!!.visibility = GONE
        }
    }


    inner class  ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
var profile_image:CircleImageView ? = null
var show_text_massage:TextView? = null
var Left_image_view:ImageView? = null
var textSeen:TextView ? = null
var right_image_View:ImageView ? = null
init {
    profile_image = itemView.findViewById(R.id.Proflie_image)
    show_text_massage = itemView.findViewById(R.id.show_text_massage)
    Left_image_view = itemView.findViewById(R.id.left_image_view)
    textSeen = itemView.findViewById(R.id.text_seen)
    right_image_View = itemView.findViewById(R.id.right_image_view)
}

        }

    override fun getItemViewType(position: Int): Int {

        return if(mchatlist[position].sender.equals(Firebaseuser!!.uid)){
            1
        }
        else{
            0
        }
    }
    private fun delatesendtMassage(position: Int,holder: Chatadapters.ViewHolder){
        val ref = FirebaseDatabase.getInstance().reference.child("Chats").child(mchatlist.get(position).massageId).removeValue()


    }
    }


