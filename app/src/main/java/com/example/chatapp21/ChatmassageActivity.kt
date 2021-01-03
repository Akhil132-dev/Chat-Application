package com.example.chatapp21

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp21.FragmentS.ApiServies
import com.example.chatapp21.Modleclass.Useradapter.Companion.visit_id
import com.example.chatapp21.Modleclass.Users
import com.example.chatapp21.Notifications.*
import com.example.chatapp21.adapterclasses.Chatadapters
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chatmassage.*
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatmassageActivity : AppCompatActivity() {
var chatadapters:Chatadapters?=null
    var chtatlist:List<Chat>?=null
    var userid: String = ""
    var firebaseuser: FirebaseUser? = null
    var RequestCode = 456
    var notify = false


    var apiservierc:ApiServies?= null
    lateinit var     recyclewview_chatmassage:RecyclerView
 var  ref:DatabaseReference?=null

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatmassage)
        /**
         * here we are geting the visit user id mean where i want to visit mean when i search for someone and then click that perdon id that im getting from here
         *
         */
        val toolbar:androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main_Chatmassage)
        setSupportActionBar(toolbar)
        supportActionBar!!.title =""
        supportActionBar!!.setDefaultDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {

            val  intent  = Intent(this,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
apiservierc = client.client.getclinet("https://fcm.googleapis.com/")!!.create(ApiServies::class.java)
        userid = intent.getStringExtra(visit_id).toString()
        //getting current user id
        recyclewview_chatmassage = findViewById(R.id.recyclewview_chatmassage)
        recyclewview_chatmassage.setHasFixedSize(true)
        var layoutManager = LinearLayoutManager(applicationContext)
        layoutManager.stackFromEnd = true
        recyclewview_chatmassage.layoutManager = layoutManager
        firebaseuser = FirebaseAuth.getInstance().currentUser
        //reference to the firebasedata nbase class user where we save he data
     ref = FirebaseDatabase.getInstance().reference.child("user").child(userid)
        ref!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                //getting all the data form the user id
                val user: Users? = snapshot.getValue(Users::class.java)
                User_name_chats.text = user?.Username
                /**
                 * placeholder is used to hold the data while the data is not retriwe from the data base
                 */
                Picasso.get().load(user?.ProfileImage).placeholder(R.drawable.ic_profile)
                    .into(chatmassage_image)
                retriveMassages(firebaseuser!!.uid,userid,user!!.ProfileImage)
            }

        })
        /**
         * we set here clicklisitener
         */
        send_massage_files_chatmassage.setOnClickListener {
            notify  = true
            val massage = text_massage_chat.text.toString()
//cheaking if massage is null
            if (massage == ""){
                Toast.makeText(
                    applicationContext,
                    "Please write a massage before sending",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                //calling the sendmassagetouser function
                sendmassagetouser(firebaseuser!!.uid, userid, massage)
            }
            text_massage_chat.setText("")
            /**
             * now deleting the thing which type in side of the edittext
             */
        }

        //calling the function where we want to send the file to user
        attach_files_chatmassage.setOnClickListener {
            notify  = true
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Pick Image"), 438)
        }
        seenmassage(userid)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("this ","This is iamge")
        if (requestCode == 438 && resultCode == RESULT_OK && data!=null && data!!.data != null) {

            val progressBar = ProgressDialog(this)
            progressBar.setMessage("uploading please wait....")
            progressBar.show()
            val imageuri = data.data

            /**
             * creating the chat image file to the firebase storage for saving the all send
             */

            val storageReference = FirebaseStorage.getInstance().reference.child("Chat Images")
            val ref = FirebaseDatabase.getInstance().reference
            val massageId = ref.push().key

            /**
             * creating another stroage chiled to save the iamge for the uniqe user
             */
            val filePath = storageReference.child("$massageId.jpg")
            var uploadTask: StorageTask<*>
            //putting the file to the firebase storage
            uploadTask = filePath.putFile(imageuri!!)
            //cheaking for the exception
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it

                    }

                }
                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //it will return the firebasae storage link for the the user image
                    val downloadUri = task.result
                    val url = downloadUri.toString()
                    //cheaking the image it is for the cover of the profile

                    val massageHaseMap = HashMap<String, Any?>()
                    massageHaseMap["sender"] = firebaseuser!!.uid
                    massageHaseMap["massage"] = "sent you an image."
                    massageHaseMap["receiver"] = userid
                    massageHaseMap["isseen"] = false
                    massageHaseMap["url"] = url
                    massageHaseMap["massageId"] = massageId
                    ref.child("Chats").child(massageId!!).setValue(massageHaseMap).addOnSuccessListener {
                        progressBar.dismiss()
                        val ref = FirebaseDatabase.getInstance().reference.child("user").child(firebaseuser!!.uid)
                        ref.addValueEventListener(object :ValueEventListener{
                            override fun onCancelled(error: DatabaseError) {

                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                val user  = snapshot.getValue(Users::class.java)
                                if(notify){
                                    sendNotificaiton(userid,user!!.Username,"sent you an image.")
                                    notify = false
                                }
                            }

                        })
                    }


                }
            }

        }
    }
    /**
     * this function is used to send the massage to the firebase
     */
    private fun sendmassagetouser(senderid: String, reciverid: String, massage: String) {
        /**
         * getting reference to the firebase
         */
        val ref = FirebaseDatabase.getInstance().reference

        /**
         * now finding the key
         */
        val massagekey = ref.push().key

        val massageHaseMap = HashMap<String, Any?>()
        /**
         * storing all into the hasmap
         */
        massageHaseMap["sender"] = senderid
        massageHaseMap["massage"] = massage
        massageHaseMap["receiver"] = reciverid
        massageHaseMap["isseen"] = false
        massageHaseMap["url"] = ""
        massageHaseMap["massageId"] = massagekey
        /**
         * set the data to the chats class whit the uniqe massage key to the firebase
         */
        ref.child("Chats").child(massagekey!!).setValue(massageHaseMap).addOnCompleteListener {

            if (it.isSuccessful) {
                /*
                creating another massage folder to the firebase for the chatlist
                 */
                val chatlistreference = FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseuser!!.uid).child(userid)
                chatlistreference.addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {

                    }

                    /**
                     * here we are getting the  both user data form the firebase data base
                     */
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(!snapshot.exists()){
/*
the massage which i send to you
 */
                            chatlistreference.child("id").setValue(userid)
                        }
                        /**
                         * the massage which the user sent to us
                         */
                        val chatlistRecieverref = FirebaseDatabase.getInstance().reference.child("ChatList").child(userid).child(firebaseuser!!.uid)
                        chatlistRecieverref.child("id").setValue(firebaseuser!!.uid)
                    }

                })
                //this will implement whit push notificaiton


            }
        }
        val ref1 = FirebaseDatabase.getInstance().reference.child("user").child(firebaseuser!!.uid)
                ref1.addValueEventListener(object :ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user  = snapshot.getValue(Users::class.java)
                        if(notify){
                            sendNotificaiton(reciverid,user!!.Username,massage)
                            notify = false
                        }
                    }

                })

    }
/**
this is used to send the notificaiton form one user to another user
 */
    private fun sendNotificaiton(reciverid: String, username: String, massage: String) {
val ref = FirebaseDatabase.getInstance().reference.child("Token")
        val quesery = ref.orderByKey().equalTo(reciverid)
        quesery.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                /**
                 * get the massag eand the user name form the firebase databse
                 */
                for (datasnapshot in snapshot.children){
                     val token  = datasnapshot.getValue(Token::class.java)

                    val  Data = data(firebaseuser!!.uid,R.mipmap.ic_launcher,"New Massage","$username: $massage",userid)


//noe passing the data in sneder class
                    val sender = sender(Data, token!!.Token)


                    apiservierc!!.sendNotification(sender).enqueue(object :Callback<MyResoureces>{
                        override fun onFailure(call: Call<MyResoureces>, t: Throwable) {

                        }

                        override fun onResponse(
                            call:Call<MyResoureces>,
                            response: Response<MyResoureces>
                        ) {
                            if(response.code()==200){
                                if(response.body()!!.scsccuess !==1){
                                    Toast.makeText(this@ChatmassageActivity,"failed,Nothing happed",Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    })
                }


            }


            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    /**
     * this is used to get the image form the internal storage into firebase
     */
    private fun retriveMassages(Sendrid: String, Reciverid: String, reicvierprofileImage: String) {
        chtatlist = ArrayList()
        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                 ( chtatlist as ArrayList<Chat>).clear()
                for( i in snapshot.children){
                    val chat = i.getValue(Chat::class.java)
                    if(chat!!.receiver.equals(Sendrid) && chat.sender.equals(Reciverid)
                        || chat!!.receiver.equals(Reciverid)&& chat.sender.equals(Sendrid)){
                        ( chtatlist as ArrayList<Chat>).add(chat)

                    }
                    chatadapters = Chatadapters(this@ChatmassageActivity, ( chtatlist as ArrayList<Chat>),reicvierprofileImage)
                    recyclewview_chatmassage.adapter = chatadapters
                }
            }

        })
    }

    var seenlistent:ValueEventListener?=null
private  fun seenmassage(userid:String){
    val ref = FirebaseDatabase.getInstance().reference.child("Chats")
    seenlistent = ref.addValueEventListener(object :ValueEventListener{
        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

        override fun onDataChange(snapshot: DataSnapshot) {
for (datasnapshot in snapshot.children){
    val chat = datasnapshot.getValue(Chat::class.java)
    if(chat!!.receiver.equals(firebaseuser!!.uid) && chat!!.sender.equals(userid)){
        val hashMap = HashMap<String,Any>()
        hashMap["isseen"] = true
        datasnapshot.ref.updateChildren(hashMap)
    }
}
        }

    })
}

    override fun onPause() {
        super.onPause()
        ref!!.removeEventListener(seenlistent!!)
    }
}