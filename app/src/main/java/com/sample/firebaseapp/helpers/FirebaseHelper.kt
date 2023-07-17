import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.sample.firebaseapp.model.UserModel

object FirebaseHelper {

    fun getCurrentUserModel(
        userId: String?,
        username: String?,
        callback: (UserModel?) -> Unit
    ) {
        if (userId != null) {
            Firebase.database.reference.child("Users").child(userId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        callback(snapshot.getValue(UserModel::class.java))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        callback(null)
                    }
                })
        } else if (username != null) {
            Firebase.database.reference.child("Users")
                .orderByChild("name")
                .equalTo(username)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userModel =
                            snapshot.children.firstOrNull()?.getValue(UserModel::class.java)
                        callback(userModel)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        callback(null)
                    }
                })
        } else {
            callback(null)
        }
    }

    fun getCurrentUserModel(callback: (UserModel?) -> Unit) {
        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            getCurrentUserModel(currentUser.uid, null, callback)
        } else {
            callback(null)
        }
    }
}
