package com.blogee

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class EditarPost : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_post)

        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)

        val btnPost = findViewById<Button>(R.id.btn_post)
        val btnSave = findViewById<Button>(R.id.btn_save)
        val btnCancel = findViewById<Button>(R.id.btn_cancel)

        btnPost.setOnClickListener{
            onBackPressed()
        }

        btnSave.setOnClickListener{
            onBackPressed()
        }

        btnCancel.setOnClickListener{
            onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return false
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
                finish()
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