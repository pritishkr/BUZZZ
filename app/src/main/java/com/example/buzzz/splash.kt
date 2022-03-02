package com.example.buzzz

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.buzzz.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_home.*

class splash : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var dialog : Dialog
    private lateinit var user: User
    private lateinit var uid : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()

        if(uid.isNotEmpty()){
            Toast.makeText(applicationContext,"Welcome back to BUZZZ...", Toast.LENGTH_SHORT).show()
            val intent = Intent(this,Chat::class.java)
            startActivity(intent)
        }else{
            Toast.makeText(applicationContext,"Welcome to BUZZZ...", Toast.LENGTH_SHORT).show()
            val intent = Intent(this,home::class.java)
            startActivity(intent)
        }
    }
}