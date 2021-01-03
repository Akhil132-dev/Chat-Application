package com.example.chatapp21.FragmentS

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp21.Modleclass.Useradapter
import com.example.chatapp21.Modleclass.Users
import com.example.chatapp21.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_serarch.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import kotlinx.android.synthetic.main.search_for_list_layout.view.*


class SerarchFragment : Fragment() {
    /**
     * this is used to get all the refrence form the over firebase data base and firebase storage
     */
    private   var recyclerView: RecyclerView?  =null
    private var muser: List<Users>? = null
    private  var useradapter:Useradapter? = null
private var searchfor :EditText?=null

    var progressBar:ProgressDialog?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       val view = inflater.inflate(R.layout.fragment_serarch, container, false)
        progressBar = ProgressDialog(context)
        progressBar!!.setMessage("Loading please wait....")
        progressBar!!.show()
        //here we are finding the id u cant directily id to the object other wise you will get null exception
searchfor = view?.findViewById(R.id.serachforuser_text)
        muser = ArrayList()
        retrieveallusers()

            recyclerView = view?.findViewById(R.id.Search_list_view)
            recyclerView!!.setHasFixedSize(true)
        /**
         * it is neccessary to insilize the id befor you use all of them  otherwise you will get erro and app will get creash so it is naccessary
         */
        /**
         * this is used to search other user on the firebase
         */
    searchfor?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchforuser(s.toString().toLowerCase())
            }

        })
return view
    }



    //this is used to retrieveall the user form the firebase for over search fragments
    private fun retrieveallusers() {
        val firebaseuserId = FirebaseAuth.getInstance().currentUser?.uid
        val refusers = FirebaseDatabase.getInstance().reference.child("user")

        refusers.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                /**
                 * cheaking the searchforuser if it is null the we have to getting the all the user form the over network
                 */
                if (serachforuser_text.text.toString() == "") {

                    (muser as ArrayList<Users>).clear()
                    for (i in snapshot.children) {
                        val user: Users? = i.getValue(Users::class.java)
                        if (!user?.uid.equals(firebaseuserId)) {

                            if (user != null) {
                                (muser as ArrayList<Users>).add(user)
                            }
                        }
                    }

//now upadating the ove recuclerview with the user list
                    recyclerView!!.adapter = Useradapter(context!!, muser!!, false)
                    progressBar!!.dismiss()
                }

            }

        })
    }

    //this  function is used for the coustom search while the user search for the coustom user wthe we have to run this funciton
    private fun searchforuser(str: String) {
        //geting the id of the current to not shwo the user it self while he searach for coustom user
        val firebaseuserId = FirebaseAuth.getInstance().currentUser?.uid
        //searching for the user on the firebase using the str which is passing to function
        val queary = FirebaseDatabase.getInstance().reference.child("user").orderByChild("search")
            .startAt(str).endAt(str + "\uf8ff")

        queary.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                //clearing the pervies data while showing the coustom search
                (muser as ArrayList<Users>).clear()
//                geting all the data form the friebase into the user class
                for (i in snapshot.children) {
                    val user: Users? = i.getValue(Users::class.java)
                    if (!user?.uid.equals(firebaseuserId)) {
                        if (user != null) {
                            (muser as ArrayList<Users>).add(user)
                        }
                    }
                }



                recyclerView!!.adapter =  Useradapter(context!!, muser!!, false)
                progressBar!!.dismiss()
            }
        })
    }

}

