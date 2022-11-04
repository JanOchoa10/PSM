package com.blogee

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class SingUp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_up)

        supportActionBar?.hide()

        val btnSignUp = findViewById<Button>(R.id.button)

        btnSignUp.setOnClickListener{
            val cambiarActivity = Intent(this, MainActivity::class.java)
            startActivity(cambiarActivity)
            finish()
        }


        // get reference to TextView
        val textSignUp = findViewById<TextView>(R.id.textView5)
        // set on-click listener
        textSignUp.setOnClickListener {
            //Toast.makeText(this@MainActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
            val cambiarActivity = Intent(this, Login::class.java)
            startActivity(cambiarActivity)
            finish()
        }

        val image = findViewById<ImageView>(R.id.imageView)
        image.setImageResource(R.mipmap.ic_launcher)
    }
}