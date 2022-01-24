package com.example.buzzz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.buzzz.databinding.ActivityProfileBinding
import kotlinx.android.synthetic.main.activity_profile.*

class Profile : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    val img_profile by lazy {
         findViewById<ImageView>(R.id.img_profile)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        img_profile.setOnClickListener{
            openGallery()
        }

        button_continueprofile.setOnClickListener{
            val intent = Intent(this,Bio::class.java)
            startActivity(intent)
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        startActivityForResult(intent, 1001)
    }
}