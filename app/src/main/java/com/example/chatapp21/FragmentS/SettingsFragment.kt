package com.example.chatapp21.FragmentS

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.example.chatapp21.Modleclass.Users
import com.example.chatapp21.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_settings.view.*
import kotlinx.android.synthetic.main.search_for_list_layout.view.*
import java.util.HashMap

class SettingsFragment : Fragment() {
companion object{
    val user = "me"
}
    /**
     * this is used to get all the refrence form the over firebase data base and firebase storage
     */
    var userRefrence: DatabaseReference? = null
    var firebaseuser: FirebaseUser? = null
    private var imageuri: Uri? = null
    private var RequestCode = 438
    private var StorageReference: StorageReference? = null
    var covercheacker: String? = null
    var socilecheacker: String? = null
    var progressBar:ProgressDialog?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        progressBar = ProgressDialog(context)
        progressBar!!.setMessage("Loading please wait....")
        progressBar!!.show()
        /**
         * here we are getting reference for the current user who is login
         */
        firebaseuser = FirebaseAuth.getInstance().currentUser
        //this will return form the last of the ocreate it is naccessray if you dont do you will get erro
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        /** Here we are creating a folder to the firebase data base which name is user and creating a chile for it whit the uniqe user id */
        userRefrence =
            FirebaseDatabase.getInstance().reference.child("user").child(firebaseuser!!.uid)
        //creating storage reference from the firebase storage
        StorageReference = FirebaseStorage.getInstance().reference.child("User Images")
        //in this we are just getting the dat form the firebase database using @userReference
        userRefrence!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    val user: Users? = snapshot.getValue(Users::class.java)
                    if (context != null) {
                        view.username_setting.text = user?.Username
                        Picasso.get().load(user?.ProfileImage).into(view.profile_setting)
                        Picasso.get().load(user?.cover).into(view.cover_image_setting)
                        progressBar!!.dismiss()
                    }

                }
            }

        })
        /**
         * Here we are just setting on click listener to the over button and image view
         */
        view.profile_setting.setOnClickListener {
            pickimage()
        }
        view.cover_image_setting.setOnClickListener {

            covercheacker = "cover"
            pickimage()
        }
        view.fb_id.setOnClickListener {
//            Log.d(user,"FUNCITON FB")

            socilecheacker = "facebook"
            setsocilelinks()
        }
        view.instragram_id.setOnClickListener {
//            Log.d(user,"FUNCITON INSTA")
            socilecheacker = "instagram"
            setsocilelinks()
        }
        view.website_id.setOnClickListener {
            Log.d(user,"FUNCITON 1WEB")
            socilecheacker = "website"
            setsocilelinks()
        }

        return view
    }

    /**
     * this function is used to set the socile link
     */
    private fun setsocilelinks() {
        Log.d(user,"FUNCITON 1")
        // this is used to show a dialog box to enter the user information
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(context, R.style.Theme_AppCompat_DayNight_Dialog_Alert)
        /**
         * now we cheack that for the title of the dialog box
         */
        if (socilecheacker == "website") {
            builder.setTitle("Write Url:")
        } else {
            builder.setTitle("Write Username")
        }
//now we are takaing the input into the edittext
        val editText = EditText(context)
        /**
         * cheacking to show the hint accroding to the user click
         */
        if (socilecheacker == "website") {
            editText.hint = "e.g.www.google.com"
        }
        else {
            editText.hint = "e.g.akh132"
        }
        /**
         * now we are just show the edit text while we are setting it
         */
        builder.setView(editText)
        /**
         * this is used to show the create button on the dialog box
         */
        builder.setPositiveButton("Create", DialogInterface.OnClickListener { dialog, which ->
            val str = editText.text.toString()
            //cheaking if someone write something into the edittext
            if (str == null) {
                Toast.makeText(context, "Please Write something..", Toast.LENGTH_LONG).show()
            }
            else {
                saveSocillink(str)
            }
        })
        Log.d(user,"FUNCITON END")
        /**
         *  this is used to show the cancel button on dialog biox
         */
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()

        })
        /**
         * this is used to show the dialog box to the mobile screen
         */
        builder.show()
    }

    /**
     * this is used to save the link to the firebasae  acroding to the user onclick
     */
    private fun saveSocillink(Str: String) {
        val sociallink = HashMap<String, Any>()
        when (socilecheacker) {
            "fb" -> {
                sociallink["facebook"] = "https://m.facebook.com/$Str"
            }

            "instagram" -> {
                sociallink["instagram"] = "https://www.instagram.com/$Str"
            }
            "websiter" -> {
                sociallink["website"] = "https://$Str"
            }

        }

//this will update the firebase link for that user
        userRefrence!!.updateChildren(sociallink).addOnSuccessListener {
            Toast.makeText(context, "Updated SuccessFully", Toast.LENGTH_LONG).show()
        }

    }

    /**
     * it is used to take picutre form the itrnal memeroy and we can esaly set to firebase
     */
    private fun pickimage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, RequestCode)
    }
//this is used to geting the image and set to the firebsae using @uploadtofirebaseaimage
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCode && resultCode == Activity.RESULT_OK && data!!.data != null) {
            imageuri = data.data
            Toast.makeText(context, "Uploading.....", Toast.LENGTH_LONG).show()
            uploadtofirebaseimage()
        }
    }

    private fun uploadtofirebaseimage() {
        /**
         * this is used for showing dailog box
         */

        val progressBar = ProgressDialog(context)
        progressBar.setMessage("uploading please wait....")
        progressBar.show()
        //cheaking that the imageuri is null or not
        if (imageuri != null) {
            val fileRef = StorageReference!!.child(System.currentTimeMillis().toString() + ".jpg")
            var uploadTask: StorageTask<*>
            //putting the file to the firebase storage
            uploadTask = fileRef.putFile(imageuri!!)
            //cheaking for the exception
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it

                    }

                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener { task ->
                /**
                 * in this we are just downloading the image form the firebase storage and show into the over app
                 */
                if (task.isSuccessful) {
                    //it will return the firebasae storage link for the the user image
                    val downloadUri = task.result
                    val url = downloadUri.toString()
                    //cheaking the image it is for the cover of the profile
                    if (covercheacker == "cover") {
                        val mapcoverImg = HashMap<String, Any>()
                        mapcoverImg["cover"] = url
                        userRefrence?.updateChildren(mapcoverImg)
                        covercheacker = ""
                    } else {
                        val mapprofileImg = HashMap<String, Any>()
                        mapprofileImg["ProfileImage"] = url
                        userRefrence?.updateChildren(mapprofileImg)
                        covercheacker = ""
                    }
                    /**
                     * dismiss the f dialog box
                     */
                    progressBar.dismiss()
                }
            }
        }
    }


}