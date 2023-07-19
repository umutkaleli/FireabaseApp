package com.sample.firebaseapp.ui.profile

import android.app.Application
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sample.firebaseapp.model.UserModel

class ProfileViewModel(application: Application) : AndroidViewModel(application){

    private val context = getApplication<Application>()

    private lateinit var storageReference: StorageReference

    private val userModelLiveData: MutableLiveData<UserModel?> = MutableLiveData()

    fun getUserModel(username: String): MutableLiveData<UserModel?> {
        FirebaseHelper.getCurrentUserModel(null, username) { userModel ->
            userModelLiveData.value = userModel
        }
        return userModelLiveData
    }

    fun uploadImageToStorage(username: String, imageUri: Uri) {
        storageReference = FirebaseStorage.getInstance().reference
        val storageRef = storageReference.child("images/$username")
        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                // Handle success if needed
            }
            .addOnFailureListener { exception ->
                // Handle failure if needed
            }
    }

     fun loadProfileImage(imageView :ImageView,username: String) {
         storageReference = FirebaseStorage.getInstance().getReference("images/$username")
         storageReference.downloadUrl.addOnSuccessListener { uri ->

                 Glide.with(context)
                     .load(uri)
                     .centerCrop()
                     .into(imageView)


         }.addOnFailureListener{ exception ->
             Log.e("ProfileViewModel","Resim yükleme hatası: $exception")

         }

     }
}