package com.example.chatapp21

import ChatFragment
import android.app.ProgressDialog
import android.content.Intent
import android.icu.text.CaseMap
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.chatapp21.FragmentS.SerarchFragment
import com.example.chatapp21.FragmentS.SettingsFragment
import com.example.chatapp21.Modleclass.Users
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
 var Firebaseadatabasea:DatabaseReference? = null
    var firebaseuser : FirebaseUser? =  null
    var progressBar:ProgressDialog?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar_main)
        progressBar = ProgressDialog(this)
        progressBar!!.setMessage("Loading please wait....")
        progressBar!!.show()
        firebaseuser = FirebaseAuth.getInstance().currentUser
        Firebaseadatabasea = FirebaseDatabase.getInstance().reference.child("user").child(firebaseuser!!.uid)
        supportActionBar?.title = ""
 val ref = FirebaseDatabase.getInstance().reference.child("Chats")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val ViewpagerAdapter = ViewpagerAdapter(supportFragmentManager)
                var countunreafmasssage = 0
                for(datasnapshot in snapshot.children){
                    val chatls = datasnapshot.getValue(Chat::class.java)
                    if(chatls!!.receiver.equals(firebaseuser!!.uid) && !chatls.isseen){
                        countunreafmasssage +=1
                    }
                }
                if(countunreafmasssage == 0){
                     ViewpagerAdapter.addFragment(ChatFragment(),"Chats")
                }
                else{
                      ViewpagerAdapter.addFragment(ChatFragment(),"Chats ($countunreafmasssage) ")
                }
                ViewpagerAdapter.addFragment(SerarchFragment(),"Search")
                ViewpagerAdapter.addFragment(SettingsFragment(),"Settings")
                viewpager.adapter =  ViewpagerAdapter
            }

        })
        Tablayout_main.setupWithViewPager(viewpager)

        /**
         * display the user name  and profile pic
         */
        Firebaseadatabasea!!.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user:Users? = snapshot.getValue(Users::class.java)
                    username.text = user?.Username
                    /**
                     * placeholder is used to hold the data while the data is not retriwe from the data base
                     */
                    Picasso.get().load(user?.ProfileImage).placeholder(R.drawable.ic_profile).into(Profileiamge)
                    progressBar!!.dismiss()
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
      when (item.itemId) {
            R.id.action_Logout ->
            {
                FirebaseAuth.getInstance().signOut()
                val  intent  = Intent(this,MaiWelcomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                return true
            }

        }
        return false
    }
    internal class ViewpagerAdapter(fragmentManager: FragmentManager):FragmentPagerAdapter(fragmentManager){
        private val fragments  = ArrayList<Fragment>()
      private  val title  = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return  fragments[position]
        }

        override fun getCount(): Int {
        return  fragments.size
        }
fun addFragment(fragment: Fragment,titles:String){
    fragments.add(fragment)
    title.add(titles)
}

        override fun getPageTitle(position: Int): CharSequence? {
            return title[position]
        }
    }
    private  fun upadatestaus(Status:String){
       val ref = FirebaseDatabase.getInstance().reference.child("user").child(firebaseuser!!.uid)
        val hasmap = HashMap<String,Any>()
        hasmap["Status"] = Status
        ref.updateChildren(hasmap)

    }

    override fun onResume() {
        super.onResume()
        upadatestaus("online")
    }

    override fun onPause() {
        super.onPause()
        upadatestaus("oflline")
    }
}


