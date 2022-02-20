package com.example.buzzz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.viewbinding.ViewBinding
import com.example.buzzz.databinding.ActivityBioBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_bio.*

class Bio : AppCompatActivity() {
    private lateinit var binding: ActivityBioBinding
    lateinit var auth: FirebaseAuth
    val bioet by lazy {
        findViewById<EditText>(R.id.bioet)
    }
    val database by lazy {
        FirebaseFirestore.getInstance()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth= Firebase.auth
        button_upload.setOnClickListener{
            if(bioet.text.isEmpty())
            {
                Toast.makeText(applicationContext,"No Bio added...", Toast.LENGTH_SHORT).show()
                Toast.makeText(applicationContext,"Welcome to BUZZZ...",Toast.LENGTH_SHORT).show()
                val intent = Intent(this,Chat::class.java)
                startActivity(intent)
            }
            else{
//                database.collection("users").document(auth.uid!!).set(bioet.text.toString())
                Toast.makeText(applicationContext,"Bio added...",Toast.LENGTH_SHORT).show()
                Toast.makeText(applicationContext,"Welcome to BUZZZ...",Toast.LENGTH_SHORT).show()
                val intent = Intent(this,Chat::class.java)
                startActivity(intent)
            }
        }
    }
}