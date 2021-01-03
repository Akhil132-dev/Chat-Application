
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp21.Chatlsit
import com.example.chatapp21.Modleclass.Useradapter
import com.example.chatapp21.Modleclass.Users
import com.example.chatapp21.Notifications.Token
import com.example.chatapp21.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId


class ChatFragment : Fragment() {

    private var muser: List<Users>? = null
    private var userschatlist: List<Chatlsit>? = null
    private  var recycalview_for_showing:RecyclerView?=null
    private  var firebaseUser:FirebaseUser?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

       val view = inflater.inflate(R.layout.fragment_chat, container, false)
        recycalview_for_showing = view.findViewById(R.id.receyclew_vuiew_chatList)
        recycalview_for_showing!!.setHasFixedSize(true)
recycalview_for_showing!!.layoutManager = LinearLayoutManager(context)
        firebaseUser = FirebaseAuth.getInstance().currentUser

        userschatlist = ArrayList()
        val ref = FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid)
        ref.addValueEventListener(object  : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
               ( userschatlist as ArrayList).clear()
                for (datasnapshot in snapshot.children){
                    val chatlits = datasnapshot.getValue(Chatlsit::class.java)
                    ( userschatlist as ArrayList).add(chatlits!!)
                }
                retrievechatlist()
            }

        })
upadateToken(FirebaseInstanceId.getInstance().token)
    return  view}

    private fun upadateToken(token: String?) {
val ref = FirebaseDatabase.getInstance().reference.child("Token")
        val tokens = Token(token!!)
        ref.child(firebaseUser!!.uid).setValue(tokens)
    }

    private fun retrievechatlist(){
    muser= ArrayList()
    val ref  = FirebaseDatabase.getInstance().reference.child("user")
    ref!!.addValueEventListener(object :ValueEventListener{


        override fun onDataChange(snapshot: DataSnapshot) {
      ( muser as ArrayList).clear()
            for(i in snapshot.children){
                val user = i.getValue(Users::class.java)
                for(eachchatlis in userschatlist!! ){
                    if(user!!.uid.equals(eachchatlis.id)){
                        (muser as ArrayList).add(user!!)
                    }

                }
            }
            recycalview_for_showing!!.adapter = Useradapter(context!!, (muser as ArrayList<Users>), true)
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }
    })


}

}