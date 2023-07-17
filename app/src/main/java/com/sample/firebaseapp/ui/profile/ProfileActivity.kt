package com.sample.firebaseapp.ui.profile

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sample.firebaseapp.databinding.ActivityProfileBinding
import FirebaseHelper
import com.sample.firebaseapp.model.UserModel
import kotlin.properties.Delegates

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    private var imageUri: Uri = Uri.EMPTY

    private lateinit var storageReference: StorageReference

    private lateinit var username: String

    private var isOwner by Delegates.notNull<Boolean>()

    private var userModel: UserModel? = null

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageUri = it
                binding.profileImage.setImageURI(it)
                uploadImageToStorage()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backButton.setBackgroundColor(Color.TRANSPARENT)
        username = intent.getStringExtra("username").toString()
        isOwner = intent.getBooleanExtra("isOwner", false)
        binding.usernameOnProfileTextView.text = username
        storageReference = FirebaseStorage.getInstance().getReference("images/$username")

        FirebaseHelper.getCurrentUserModel(null,username) { user ->
            userModel = user

            val name = userModel?.name + " " + (userModel?.surName)
            binding.nameOnprofileTextview.text = name

            storageReference.downloadUrl.addOnSuccessListener { uri ->
                Glide
                    .with(this)
                    .load(uri)
                    .centerCrop()
                    .into(binding.profileImage)
                Log.d("ProfilePageTest", uri.toString())
            }.addOnFailureListener { exception ->
                Log.e("ProfilePageTest", "Resim indirme hatası: $exception")
            }
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        if (isOwner) {
            binding.editProfileimageButton.setOnClickListener {
                editProfilePhoto()
            }
        } else {
            binding.editProfileimageButton.isVisible = false
        }
    }

    private fun editProfilePhoto() {
        galleryLauncher.launch("image/*")
    }

    private fun uploadImageToStorage() {
        if (imageUri != Uri.EMPTY) {
            storageReference.putFile(imageUri)
                .addOnSuccessListener {
                    Log.d("ProfilePageTest", "Image upload successful")
                    // Other operations if needed
                }
                .addOnFailureListener { exception ->
                    Log.e("ProfilePageTest", "Image upload failed: $exception")
                    // Error handling if needed
                }
        }
    }
}
