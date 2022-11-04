package com.blogee

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()

        val btnLogin = findViewById<Button>(R.id.btn_login)

        btnLogin.setOnClickListener{
            val cambiarActivity = Intent(this, MainActivity::class.java)
            startActivity(cambiarActivity)
            finish()
        }

        // get reference to TextView
        val textSignUp = findViewById<TextView>(R.id.textView5)
        // set on-click listener
        textSignUp.setOnClickListener {
            //Toast.makeText(this@MainActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
            val cambiarActivity = Intent(this, SingUp::class.java)
            startActivity(cambiarActivity)
            finish()
        }

        val myPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val f: Int = myPreferences.getInt(getString(R.string.modo_oscuro), 0)
        if (f == 0) {
            //imageViewCM.setImageResource(R.drawable.ic_filter_hdr_white_24dp);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            if (f == 1) {
                // imageViewCM.setImageResource(R.drawable.ic_filter_hdr_black_24dp);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }
}