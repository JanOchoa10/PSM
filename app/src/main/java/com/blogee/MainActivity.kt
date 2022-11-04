package com.blogee

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnfavNewPost = findViewById<FloatingActionButton>(R.id.fab_new_post)

        btnfavNewPost.setOnClickListener{
            val cambiarActivity = Intent(this, EditarPost::class.java)
            startActivity(cambiarActivity)
        }



    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.app_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.user_profile -> {
                // Acción al presionar el botón
                val cambiarActivity = Intent(this, VerPerfil::class.java)
                startActivity(cambiarActivity)
                true
            }
            /**R.id.create_new -> {
            //newGame()
            true
            }
            R.id.open -> {
            //showHelp()
            true
            }*/
            else -> super.onOptionsItemSelected(item)
        }
    }
}