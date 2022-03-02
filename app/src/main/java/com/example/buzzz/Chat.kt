package com.example.buzzz

import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.buzzz.databinding.ActivityChatBinding
import com.example.buzzz.databinding.ActivitySplashBinding

class Chat : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}