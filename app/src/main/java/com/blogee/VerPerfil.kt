package com.blogee

import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.blogee.Models.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class VerPerfil : AppCompatActivity() {

    lateinit var usuarioDBHelper: miSQLiteHelper
    var namePerfil:TextView? = null
    var lastnamePerfil:TextView? = null
    var emailPerfil:TextView? = null
    var imageUI:ImageView? =  null
    var imgArray:ByteArray? =  null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_perfil)

        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)

        usuarioDBHelper = miSQLiteHelper(this)

        //val image = findViewById<ImageView>(R.id.imageView)
        //image.setImageResource(R.mipmap.ic_launcher)

        val btnEditarPerfil = findViewById<Button>(R.id.btn_editar_perfil)

        namePerfil = findViewById<TextView>(R.id.textView6)
        lastnamePerfil = findViewById<TextView>(R.id.textView7)
        emailPerfil = findViewById<TextView>(R.id.textView3)
        imageUI = findViewById(R.id.imageView)


        //Buscar la info del usuario

        infoUser()

        /*var args = arrayOf(intent.getStringExtra("emailUserLog"))
        val db : SQLiteDatabase = usuarioDBHelper.readableDatabase
        val cursor = db.rawQuery("Select * From usuarios where emailUser = ?", args)

       if(cursor.moveToFirst()){
           namePerfil.text = getString(R.string.name) + ": " +cursor.getString(1)
           lastnamePerfil.text = getString(R.string.last_name) + ": " +cursor.getString(2)
           emailPerfil.text = getString(R.string.email) + ": " + cursor.getString(3)
        }else{
            Toast.makeText(this,"No encontro el usuario",Toast.LENGTH_SHORT).show()
        }*/

        btnEditarPerfil.setOnClickListener{
            val idUserLog = Bundle()
            idUserLog.putString("idUserLog", intent.getStringExtra("idUserLog"))
            val cambiarActivity = Intent(this, EditarPerfil::class.java)
            cambiarActivity.putExtras(idUserLog)
            startActivity(cambiarActivity)
        }

        //Toast.makeText(this,args[0], Toast.LENGTH_SHORT).show()

    }

    private fun infoUser() {
        var id_UserVP = intent.getStringExtra("idUserLog")
        if(id_UserVP != null){
            val service: Service =  RestEngine.getRestEngine().create(Service::class.java)
            val result: Call<List<Usuario>> = service.getUser( id_UserVP)

                result.enqueue(object: Callback<List<Usuario>> {
                override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
                    Toast.makeText(this@VerPerfil,"Error",Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<List<Usuario>>, response: Response<List<Usuario>>) {
                    val item =  response.body()
                    if(item!=null){
                        if(item.isEmpty()){
                            Toast.makeText(this@VerPerfil,"No tiene información",Toast.LENGTH_LONG).show()
                        }else{

                            var byteArray:ByteArray? = null
                            namePerfil!!.text = getString(R.string.name) + ": " +item[0].Name
                            lastnamePerfil!!.text = getString(R.string.last_name) + ": " +item[0].LastName
                            emailPerfil!!.text = getString(R.string.email) + ": " + item[0].Email

                            val strImage:String =  item[0].Image!!.replace("data:image/png;base64,","")
                            byteArray =  Base64.getDecoder().decode(strImage)
                            if(byteArray != null){
                                //Bitmap redondo
                                val bitmap:Bitmap =ImageUtilities.getBitMapFromByteArray(byteArray)
                                val roundedBitmapWrapper: RoundedBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(Resources.getSystem(), bitmap)
                                roundedBitmapWrapper.setCircular(true)
                                imageUI!!.setImageDrawable(roundedBitmapWrapper)
                            }
                        }
                    }else{
                        Toast.makeText(this@VerPerfil,"Incorrectas",Toast.LENGTH_LONG).show()
                    }


                }
            })
        }else{
            Toast.makeText(this,"Error de usuario", Toast.LENGTH_SHORT).show()
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