package com.blogee.activitys

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.blogee.*
import com.blogee.UserApplication.Companion.prefs
import com.blogee.adapters.PostsAdapter
import com.blogee.local.miSQLiteHelper
import com.blogee.models.Credenciales
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

    private val getCredenciales: Credenciales = prefs.getCredenciales()
    private val setCredenciales: Credenciales = Credenciales()

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

        btnEditarPerfil.setOnClickListener {
            val cambiarActivity = Intent(
                this,
                EditarPerfil::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(cambiarActivity)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        //Toast.makeText(this,args[0], Toast.LENGTH_SHORT).show()
    }

    private fun infoUser() {
        val id_UserVP = getCredenciales.idUserGuardado.toString()
        if (id_UserVP != null && (isConnectedWifi(this@VerPerfil) || isConnectedMobile(this@VerPerfil))) {
            val service: Service = RestEngine.getRestEngine().create(Service::class.java)
            val result: Call<List<Usuario>> = service.getUser(id_UserVP)

            result.enqueue(object : Callback<List<Usuario>> {
                override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
//                    Toast.makeText(this@VerPerfil, "Error", Toast.LENGTH_LONG).show()
                    Dialogo.getInstance(this@VerPerfil)
                        .crearDialogoSinAccion(
                            this@VerPerfil,
                            getString(R.string.dialog_error_de_usuario),
                            getString(R.string.dialog_error_de_usuario_text),
                            getString(R.string.dialog_aceptar)
                        )
                }

                override fun onResponse(
                    call: Call<List<Usuario>>,
                    response: Response<List<Usuario>>
                ) {
                    val item = response.body()
                    if (item != null) {
                        if (item.isEmpty()) {
//                            Toast.makeText(
//                                this@VerPerfil,
//                                "No tiene información",
//                                Toast.LENGTH_LONG
//                            ).show()

                            Dialogo.getInstance(this@VerPerfil)
                                .crearDialogoSinAccion(
                                    this@VerPerfil,
                                    getString(R.string.dialog_error_de_usuario),
                                    getString(R.string.dialog_error_de_usuario_text),
                                    getString(R.string.dialog_aceptar)
                                )
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
                                    ImageUtilities.getBitMapFromByteArray(byteArray!!)
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
//                        Toast.makeText(this@VerPerfil, "Incorrectas", Toast.LENGTH_LONG).show()
                        Dialogo.getInstance(this@VerPerfil)
                            .crearDialogoSinAccion(
                                this@VerPerfil,
                                getString(R.string.dialog_no_login),
                                getString(R.string.dialog_credenciales_incorrectas_text),
                                getString(R.string.dialog_aceptar)
                            )
                    }


                }
            })
        } else {
            val email_User = getCredenciales.emailGuardado
            val db = usuarioDBHelper.readableDatabase
            val c = db.rawQuery(
                "Select * from usuarios where emailUser ='$email_User'",
                null
            )
            if (c.moveToFirst()) {
                var byteArray: ByteArray? = null
                namePerfil!!.text = getString(R.string.name) + ": " + c.getString(1).toString()
                lastnamePerfil!!.text =
                    getString(R.string.last_name) + ": " + c.getString(2).toString()
                emailPerfil!!.text = getString(R.string.email) + ": " + c.getString(3).toString()

                val strImage: String =
                    c.getString(5).toString().replace("data:image/png;base64,", "")
                byteArray = Base64.getDecoder().decode(strImage)
                if (byteArray != null) {
                    //Bitmap redondo
                    val bitmap: Bitmap =
                        ImageUtilities.getBitMapFromByteArray(byteArray!!)
                    val roundedBitmapWrapper: RoundedBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(
                            Resources.getSystem(),
                            bitmap
                        )
                    roundedBitmapWrapper.setCircular(true)
                    imageUI!!.setImageDrawable(roundedBitmapWrapper)
                }

            }
            //Toast.makeText(this, "Error de usuario", Toast.LENGTH_SHORT).show()
        }
    }

    fun isConnectedWifi(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.type == ConnectivityManager.TYPE_WIFI
    }

    fun isConnectedMobile(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.type == ConnectivityManager.TYPE_MOBILE
    }

    fun traerNotasUsuario() {
        var listaPosts: MutableList<Nota> = mutableListOf()
        val id_UserVP = getCredenciales.idUserGuardado.toString()

        if (id_UserVP != null && (isConnectedWifi(this@VerPerfil) || isConnectedMobile(this@VerPerfil))) {
            val service: Service = RestEngine.getRestEngine().create(Service::class.java)
            val result: Call<List<Nota>> = service.getNotaUser(id_UserVP)

            result.enqueue(object : Callback<List<Nota>> {
                override fun onFailure(call: Call<List<Nota>>, t: Throwable) {
//                    Toast.makeText(this@VerPerfil, "Error", Toast.LENGTH_LONG).show()

                    Dialogo.getInstance(this@VerPerfil)
                        .crearDialogoSinAccion(
                            this@VerPerfil,
                            getString(R.string.dialog_error_de_notas),
                            getString(R.string.dialog_error_de_notas_text),
                            getString(R.string.dialog_aceptar)
                        )
                }

                override fun onResponse(
                    call: Call<List<Nota>>,
                    response: Response<List<Nota>>
                ) {
                    val arrayPosts = response.body()
                    if (arrayPosts != null) {
                        if (arrayPosts.isEmpty()) {
//                            Toast.makeText(
//                                this@VerPerfil,
//                                "No tiene notas",
//                                Toast.LENGTH_LONG
//                            ).show()


                            Dialogo.getInstance(this@VerPerfil)
                                .crearDialogoSinAccion(
                                    this@VerPerfil,
                                    getString(R.string.dialog_no_tiene_notas),
                                    getString(R.string.dialog_no_tiene_notas_text),
                                    getString(R.string.dialog_aceptar)
                                )
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

                                if (isConnectedWifi(this@VerPerfil) || isConnectedMobile(this@VerPerfil)) {
                                    val notaActual: Nota =
                                        parent.getItemAtPosition(position) as Nota


                                    val intent = Intent(
                                        this@VerPerfil,
                                        DetallesNota::class.java
                                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

                                    setCredenciales.idUserGuardado = getCredenciales.idUserGuardado
                                    setCredenciales.emailGuardado = getCredenciales.emailGuardado
                                    setCredenciales.passGuardado = getCredenciales.passGuardado

                                    setCredenciales.setIdNotaGuardado(notaActual.id_Nota!!)
                                    setCredenciales.setIdUserDeNota(notaActual.id_User!!)

                                    val activo: Boolean = getCredenciales.getModoOscuro()
                                    setCredenciales.setModoOscuro(activo)
                                    setCredenciales.setFiltro(getCredenciales.getFiltro())
                                    prefs.saveCredenciales(setCredenciales)

                                    startActivity(intent)
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                                } else {
                                    Dialogo.getInstance(this@VerPerfil)
                                        .crearDialogoSinAccion(
                                            this@VerPerfil,
                                            getString(R.string.dialog_sin_internet),
                                            getString(R.string.dialog_sin_internet_text),
                                            getString(R.string.dialog_aceptar)
                                        )
                                }
                            }

                        }
                    } else {
//                        Toast.makeText(this@VerPerfil, "No hay notas", Toast.LENGTH_LONG).show()

                        Dialogo.getInstance(this@VerPerfil)
                            .crearDialogoSinAccion(
                                this@VerPerfil,
                                getString(R.string.dialog_no_tiene_notas),
                                getString(R.string.dialog_no_tiene_notas_text),
                                getString(R.string.dialog_aceptar)
                            )
                    }
                }
            })
        } else {
            val email_User = getCredenciales.emailGuardado
            val db = usuarioDBHelper.readableDatabase
            val c = db.rawQuery(
                "Select * from notas where emailUser ='$email_User' and status != 2",
                null
            )

            val myUserID = getCredenciales.idUserGuardado


            if (c.moveToFirst()) {
                val textoInicial = findViewById<TextView>(R.id.txtNoNotas)
                textoInicial.visibility = View.GONE
                do {
                    listaPosts.add(
                        Nota(
                            c.getInt(0),
                            c.getString(2),
                            c.getString(3),
                            myUserID,
                            c.getString(4)
                        )
                    )
                } while (c.moveToNext())
            }
            val adaptador = PostsAdapter(this@VerPerfil, listaPosts)

            // Elementos dentro del listview
            val lvPost = findViewById<ListView>(R.id.lvPostsUsuario)

            lvPost.adapter = adaptador

            lvPost.setOnItemClickListener { parent, view, position, id ->
                Dialogo.getInstance(this@VerPerfil)
                    .crearDialogoSinAccion(
                        this@VerPerfil,
                        getString(R.string.dialog_sin_internet),
                        getString(R.string.dialog_sin_internet_text),
                        getString(R.string.dialog_aceptar)
                    )
            }

        }

    }


    override fun onSupportNavigateUp(): Boolean {
        val cambiarActivity = Intent(
            this@VerPerfil,
            MainActivity::class.java
        )
        startActivity(cambiarActivity)
        overridePendingTransition(R.anim.from_left, R.anim.to_right)
        finish()
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

                val builder = AlertDialog.Builder(this@VerPerfil)
                builder.setIcon(R.drawable.bluebird)
                builder.setTitle(getString(R.string.dialog_cerrar_sesion))
                builder.setPositiveButton(getString(R.string.dialog_yes)) { dialog, which ->
                    setCredenciales.emailGuardado = ""
                    setCredenciales.passGuardado = ""
                    val activo: Boolean = getCredenciales.getModoOscuro()
                    setCredenciales.setModoOscuro(activo)
                    prefs.saveCredenciales(setCredenciales)

                    val cambiarActivity = Intent(
                        this,
                        Login::class.java
                    )
                    startActivity(cambiarActivity)
                    overridePendingTransition(R.anim.from_left, R.anim.to_right)
                    finishAffinity()
                }
                builder.setNegativeButton(getString(R.string.dialog_no), null)
                builder.show()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}