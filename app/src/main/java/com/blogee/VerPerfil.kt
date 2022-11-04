package com.blogee

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import java.util.*

class VerPerfil : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_perfil)

        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)

        val image = findViewById<ImageView>(R.id.imageView)
        image.setImageResource(R.mipmap.ic_launcher)

        val btnEditarPerfil = findViewById<Button>(R.id.btn_editar_perfil)

        btnEditarPerfil.setOnClickListener{
            val cambiarActivity = Intent(this, EditarPerfil::class.java)
            startActivity(cambiarActivity)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.app_menu_2, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {

            R.id.log_out -> {
                // Acción al presionar el botón
                val cambiarActivity = Intent(this, Login::class.java)
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