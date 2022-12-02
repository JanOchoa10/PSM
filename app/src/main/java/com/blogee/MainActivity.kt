package com.blogee

import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.blogee.Models.Usuario
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val btnfavNewPost = findViewById<FloatingActionButton>(R.id.fab_new_post)

        btnfavNewPost.setOnClickListener{
            val idUserLog = Bundle()
            idUserLog.putString("idUserLog", intent.getStringExtra("idUserLog"))
            val cambiarActivity = Intent(this, Post2::class.java)
            cambiarActivity.putExtras(idUserLog)
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
                val idUserLog = Bundle()
                idUserLog.putString("idUserLog", intent.getStringExtra("idUserLog"))
                val cambiarActivity = Intent(this, VerPerfil::class.java)
                cambiarActivity.putExtras(idUserLog)
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