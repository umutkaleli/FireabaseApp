package com.sample.firebaseapp.ui.profile

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.sample.firebaseapp.databinding.ActivityProfileBinding
import android.annotation.SuppressLint
import androidx.activity.viewModels
import kotlin.properties.Delegates

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    private var imageUri: Uri = Uri.EMPTY

    private lateinit var username: String

    private var isOwner by Delegates.notNull<Boolean>()

    private val viewModel: ProfileViewModel by viewModels()

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageUri = it
                binding.profileImage.setImageURI(it)
                viewModel.uploadImageToStorage(username,imageUri)
            }
        }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backButton.setBackgroundColor(Color.TRANSPARENT)

        username = intent.getStringExtra("username").toString()

        isOwner = intent.getBooleanExtra("isOwner", false)

        binding.usernameOnProfileTextView.text = username


        viewModel.getUserModel(username).observe(this) { userModel ->

            userModel?.let {
                binding.nameOnprofileTextview.text = "${it.name} ${it.surName}"
                loadProfileImage(username)
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

    private fun loadProfileImage(username: String) {
        viewModel.loadProfileImage(binding.profileImage,username)
    }

}



