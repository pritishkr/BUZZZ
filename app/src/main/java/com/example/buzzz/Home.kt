package com.example.buzzz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
//import android.widget.Butt
import android.content.Intent
import com.example.buzzz.databinding.ActivityHomeBinding
import kotlinx.android.synthetic.main.activity_home.*

//import kotlinx.android.synthetic.main.activity_otp.*


class Home : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

//        var button_continue = findViewById(R.id.button_continue)

        button_continue.setOnClickListener{
            val intent = Intent(this, Signin::class.java)
            startActivity(intent)
        }
    }
}