package com.blogee.activitys

import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.blogee.*
import com.blogee.adapters.PostsAdapter
import com.blogee.models.Nota
import com.blogee.models.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class VerPerfil : AppCompatActivity() {

    lateinit var usuarioDBHelper: miSQLiteHelper
    var namePerfil: TextView? = null
    var lastnamePerfil: TextView? = null
    var emailPerfil: TextView? = null
    var imageUI: ImageView? = null
    var imgArray: ByteArray? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_perfil)

        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)

        usuarioDBHelper = miSQLiteHelper(this)

        //val image = findViewById<ImageView>(R.id.imageView)
        //image.setImageResource(R.mipmap.ic_launcher)

        val btnEditarPerfil = findViewById<Button>(R.id.btn_editar_perfil)

        namePerfil = findViewById<TextView>(R.id.lbName)
        lastnamePerfil = findViewById<TextView>(R.id.lbLastName)
        emailPerfil = findViewById<TextView>(R.id.lbEmail)
        imageUI = findViewById(R.id.imageView)


        //Buscar la info del usuario

        infoUser()
        traerNotasUsuario()

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

        btnEditarPerfil.setOnClickListener {
            val idUserLog = Bundle()
            idUserLog.putString("idUserLog", intent.getStringExtra("idUserLog"))
            val cambiarActivity = Intent(
                this,
                EditarPerfil::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            cambiarActivity.putExtras(idUserLog)
            startActivity(cambiarActivity)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        //Toast.makeText(this,args[0], Toast.LENGTH_SHORT).show()

    }

    private fun infoUser() {
        val id_UserVP = intent.getStringExtra("idUserLog")
        if (id_UserVP != null) {
            val service: Service = RestEngine.getRestEngine().create(Service::class.java)
            val result: Call<List<Usuario>> = service.getUser(id_UserVP)

            result.enqueue(object : Callback<List<Usuario>> {
                override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
                    Toast.makeText(this@VerPerfil, "Error", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(
                    call: Call<List<Usuario>>,
                    response: Response<List<Usuario>>
                ) {
                    val item = response.body()
                    if (item != null) {
                        if (item.isEmpty()) {
                            Toast.makeText(
                                this@VerPerfil,
                                "No tiene información",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {

                            var byteArray: ByteArray? = null
                            namePerfil!!.text = getString(R.string.name) + ": " + item[0].Name
                            lastnamePerfil!!.text =
                                getString(R.string.last_name) + ": " + item[0].LastName
                            emailPerfil!!.text = getString(R.string.email) + ": " + item[0].Email

                            val strImage: String =
                                item[0].Image!!.replace("data:image/png;base64,", "")
                            byteArray = Base64.getDecoder().decode(strImage)
                            if (byteArray != null) {
                                //Bitmap redondo
                                val bitmap: Bitmap =
                                    ImageUtilities.getBitMapFromByteArray(byteArray)
                                val roundedBitmapWrapper: RoundedBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(
                                        Resources.getSystem(),
                                        bitmap
                                    )
                                roundedBitmapWrapper.setCircular(true)
                                imageUI!!.setImageDrawable(roundedBitmapWrapper)
                            }
                        }
                    } else {
                        Toast.makeText(this@VerPerfil, "Incorrectas", Toast.LENGTH_LONG).show()
                    }


                }
            })
        } else {
            Toast.makeText(this, "Error de usuario", Toast.LENGTH_SHORT).show()
        }
    }

    fun traerNotasUsuario() {
        var listaPosts: MutableList<Nota> = mutableListOf()
        val id_UserVP = intent.getStringExtra("idUserLog")
        val service: Service = RestEngine.getRestEngine().create(Service::class.java)
        val result: Call<List<Nota>> = service.getNotaUser(id_UserVP)

        result.enqueue(object : Callback<List<Nota>> {
            override fun onFailure(call: Call<List<Nota>>, t: Throwable) {
                Toast.makeText(this@VerPerfil, "Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<List<Nota>>,
                response: Response<List<Nota>>
            ) {
                val arrayPosts = response.body()
                if (arrayPosts != null) {
                    if (arrayPosts.isEmpty()) {
                        Toast.makeText(
                            this@VerPerfil,
                            "No tiene notas",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        //      Visibilidad del texto cuando no hay publicaciones
                        val textoInicial = findViewById<TextView>(R.id.txtNoNotas)
                        textoInicial.visibility = View.GONE

                        for (item in arrayPosts) {
                            listaPosts.add(
                                Nota(
                                    item.id_Nota,
                                    item.Title,
                                    item.Description,
                                    item.id_User,
                                    item.Image
                                )
                            )
                        }

                        val adaptador = PostsAdapter(this@VerPerfil, listaPosts)

                        // Elementos dentro del listview
                        val lvPost = findViewById<ListView>(R.id.lvPostsUsuario)

                        lvPost.adapter = adaptador

                        lvPost.setOnItemClickListener { parent, view, position, id ->

                            val notaActual: Nota =
                                parent.getItemAtPosition(position) as Nota

                            Toast.makeText(
                                applicationContext,
                                notaActual.Title
                                        + "\n\n" + notaActual.Description
                                        + "\n\n" + notaActual.id_User,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                    }
                } else {
                    Toast.makeText(this@VerPerfil, "No hay notas", Toast.LENGTH_LONG).show()
                }
            }
        })
    }


    override fun onSupportNavigateUp(): Boolean {
//        onBackPressed()
        val idUserLog = Bundle()
        idUserLog.putString("idUserLog", intent.getStringExtra("idUserLog"))
        val cambiarActivity = Intent(
            this,
            MainActivity::class.java
        ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        cambiarActivity.putExtras(idUserLog)
        startActivity(cambiarActivity)
        overridePendingTransition(R.anim.from_left, R.anim.to_right)
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.app_menu_ver_perfil, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {

            R.id.log_out -> {
                // Acción al presionar el botón

                val myPreferences =
                    PreferenceManager.getDefaultSharedPreferences(applicationContext)
                val myEditor = myPreferences.edit()
//                            val f = myPreferences.getInt(getString(R.string.modo_oscuro), 0)
                myEditor.putString("emailLogged", "")
                myEditor.putString("passLogged", "")

                myEditor.apply()

                val cambiarActivity = Intent(
                    this,
                    Login::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(cambiarActivity)
                overridePendingTransition(R.anim.from_left, R.anim.to_right)

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