package com.example.projeto1.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.projeto1.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.maintenance)
    }

    fun edit( view: View ) {
        val intent = Intent(this@MainActivity, EditUserProfileActivity::class.java)
        startActivity(intent)
        finish()
    }
}