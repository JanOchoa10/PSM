package com.blogee.activitys

import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.blogee.ImageUtilities
import com.blogee.R
import com.blogee.RestEngine
import com.blogee.Service
import com.blogee.models.Nota
import com.blogee.models.Usuario
import kotlinx.android.synthetic.main.activity_detalles_nota.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class DetallesNota : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalles_nota)

        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)

        val nota = intent.getSerializableExtra("verNota") as Nota


        val service: Service = RestEngine.getRestEngine().create(Service::class.java)
        val result: Call<List<Usuario>> = service.getUser(nota.id_User.toString())

        result.enqueue(object : Callback<List<Usuario>> {
            override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
                Toast.makeText(applicationContext, "Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<List<Usuario>>,
                response: Response<List<Usuario>>
            ) {
                val item = response.body()
                if (item != null) {
                    if (item.isEmpty()) {
                        Toast.makeText(
                            applicationContext,
                            "No tiene información",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {

                        var byteArray2: ByteArray? = null
//                            namePerfil!!.text = getString(R.string.name) + ": " + item[0].Name
//                            lastnamePerfil!!.text =
//                                getString(R.string.last_name) + ": " + item[0].LastName
//                            emailPerfil!!.text = getString(R.string.email) + ": " + item[0].Email

                        nombre2.text = item[0].Name

                        val strImage: String =
                            item[0].Image!!.replace("data:image/png;base64,", "")
                        byteArray2 = Base64.getDecoder().decode(strImage)
                        if (byteArray2 != null) {
                            //Bitmap redondo
                            val bitmap: Bitmap =
                                ImageUtilities.getBitMapFromByteArray(byteArray2)
                            val roundedBitmapWrapper: RoundedBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(
                                    Resources.getSystem(),
                                    bitmap
                                )
                            roundedBitmapWrapper.setCircular(true)
                            imgPerfil2.setImageDrawable(roundedBitmapWrapper)
                        }
                    }
                } else {
                    Toast.makeText(applicationContext, "Incorrectas", Toast.LENGTH_LONG).show()
                }


            }
        })



        titulo2.text = nota.Title
        descripcion2.text = nota.Description

        if (nota.Image != "") {
            var byteArray: ByteArray? = null

            val strImage: String =
                nota.Image!!.replace("data:image/png;base64,", "")
            byteArray = Base64.getDecoder().decode(strImage)

            var bitmap: Bitmap? = null

            if (byteArray != null) {
//            //Bitmap redondo
                bitmap =
                    ImageUtilities.getBitMapFromByteArray(byteArray)
                val roundedBitmapWrapper: RoundedBitmapDrawable =
                    RoundedBitmapDrawableFactory.create(
                        Resources.getSystem(),
                        bitmap
                    )
                imgNota2.setImageDrawable(roundedBitmapWrapper)
            }
        }


    }

    fun asignaFotoUsuario(menu: Menu) {

        var miItem5: MenuItem = menu.findItem(R.id.user_profile)

        var id_User = intent.getStringExtra("idUserLog")
        if(id_User != null){

            val service: Service =  RestEngine.getRestEngine().create(Service::class.java)
            val result: Call<List<Usuario>> = service.getUser( id_User)
            //Toast.makeText(this,"Hasta aquí bien",Toast.LENGTH_SHORT).show()
            result.enqueue(object: Callback<List<Usuario>> {
                override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
                    Toast.makeText(applicationContext,"Error",Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<List<Usuario>>, response: Response<List<Usuario>>) {
                    val item =  response.body()
                    if(item!=null){
                        if(item.isEmpty()){
                            Toast.makeText(applicationContext,"No tiene información",Toast.LENGTH_LONG).show()
                        }else{
                            var byteArray:ByteArray? = null
//                            nameUser!!.text = item[0].Name
//                            lastNameUser!!.text = item[0].LastName
//                            emailUser!!.text = item[0].Email
//                            passUser!!.text = item[0].Password

                            val strImage:String =  item[0].Image!!.replace("data:image/png;base64,","")
                            byteArray =  Base64.getDecoder().decode(strImage)
                            if(byteArray != null){
                                //Bitmap redondo
                                val bitmap: Bitmap =
                                    ImageUtilities.getBitMapFromByteArray(byteArray)
                                val roundedBitmapWrapper: RoundedBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(Resources.getSystem(), bitmap)
                                roundedBitmapWrapper.setCircular(true)
//                                imageUI!!.setImageDrawable(roundedBitmapWrapper)
                                miItem5.setIcon(roundedBitmapWrapper)

                            }
                        }
                    }else{
                        Toast.makeText(applicationContext,"Incorrectas",Toast.LENGTH_LONG).show()
                    }


                }
            })
        }else{
            Toast.makeText(this,"Error de usuario", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val idUserLog = Bundle()
        idUserLog.putString("idUserLog", intent.getStringExtra("idUserLog"))
        val cambiarActivity = Intent(this, MainActivity::class.java)
        cambiarActivity.putExtras(idUserLog)
        startActivity(cambiarActivity)
        overridePendingTransition(R.anim.from_left, R.anim.to_right)
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.app_menu_editar_nota, menu)

        asignaFotoUsuario(menu)

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
            R.id.app_bar_edit_note -> {
                val idUserLog = Bundle()
                idUserLog.putString("idUserLog", intent.getStringExtra("idUserLog"))
                val cambiarActivity = Intent(this, EditarPost::class.java)
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