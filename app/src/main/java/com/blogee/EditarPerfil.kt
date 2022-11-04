package com.blogee

import android.annotation.SuppressLint
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import java.util.*


class EditarPerfil : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_perfil)

        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)

        val image = findViewById<ImageView>(R.id.imageView)
        image.setImageResource(R.mipmap.ic_launcher)

        val btnSaveChanges = findViewById<Button>(R.id.btn_guardar_cambios)
        val btnCancel = findViewById<Button>(R.id.btn_cancel)

        btnSaveChanges.setOnClickListener{
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
        inflater.inflate(R.menu.app_menu_3, menu)

        val item: MenuItem = menu.findItem(R.id.dark_mode)

        val myPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val f = myPreferences.getInt(getString(R.string.modo_oscuro), 0)


        if (f == 0) {
            //Toast.makeText(this,"Modo oscuro activado", Toast.LENGTH_SHORT).show()
            item.setIcon(R.drawable.ic_baseline_dark_mode_24)
        } else {
            if (f == 1) {
                //Toast.makeText(this,"Modo oscuro desactivado", Toast.LENGTH_SHORT).show()
                item.setIcon(R.drawable.ic_baseline_light_mode_24)
            }
        }



        return true
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.dark_mode -> {
                // Acción al presionar el botón
                /*AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                Toast.makeText(
                    this,
                    "Modo oscuro activado",
                    Toast.LENGTH_LONG
                ).show()*/

                val myPreferences = PreferenceManager.getDefaultSharedPreferences(this)
                val myEditor = myPreferences.edit()
                val f = myPreferences.getInt(getString(R.string.modo_oscuro), 0)



                if (f == 0) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    //Toast.makeText(this,"Modo oscuro activado", Toast.LENGTH_SHORT).show()
                    myEditor.putInt(getString(R.string.modo_oscuro), 1)
                } else {
                    if (f == 1) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        //Toast.makeText(this,"Modo oscuro desactivado", Toast.LENGTH_SHORT).show()
                        myEditor.putInt(getString(R.string.modo_oscuro), 0)
                    }
                }

                myEditor.apply()
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