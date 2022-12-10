package com.blogee.activitys

import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.blogee.*
import com.blogee.models.Nota
import com.blogee.models.Usuario
import kotlinx.android.synthetic.main.activity_detalles_nota.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class DetallesNota : AppCompatActivity() {

//    var numeroIdNota: Int? = null
//    var numeroIdUser: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalles_nota)

        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)

//        val nota = intent.getSerializableExtra("verNota") as Nota

//        val imagenDeNota = intent.getStringExtra("stringBlob")
//        nota.Image = imagenDeNota


        val numeroNota = intent.getSerializableExtra("idDeMiNotaActualClave")
//        var userIdDeNota: Int? = null

        val serviceNota: Service = RestEngine.getRestEngine().create(Service::class.java)
        val resultNota: Call<List<Nota>> = serviceNota.getNotas()

        resultNota.enqueue(object : Callback<List<Nota>> {
            override fun onFailure(call: Call<List<Nota>>, t: Throwable) {
//                Toast.makeText(this@DetallesNota, "Error", Toast.LENGTH_LONG).show()
                Dialogo.getInstance(this@DetallesNota)
                    .crearDialogoSinAccion(
                        this@DetallesNota,
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
//                        Toast.makeText(
//                            this@DetallesNota,
//                            "No tiene notas",
//                            Toast.LENGTH_LONG
//                        ).show()

                        Dialogo.getInstance(this@DetallesNota)
                            .crearDialogoSinAccion(
                                this@DetallesNota,
                                getString(R.string.dialog_no_tiene_notas),
                                getString(R.string.dialog_no_tiene_notas_text),
                                getString(R.string.dialog_aceptar)
                            )


                    } else {
//                        Toast.makeText(this@DetallesNota, "Hay notas", Toast.LENGTH_LONG).show()

                        for (itemNota in arrayPosts) {
                            if (itemNota.id_Nota == numeroNota) {

//                                numeroIdUser = itemNota.id_User
//                                numeroIdNota = itemNota.id_Nota
//                                val miNotaSelecta: Nota = itemNota
//                                userIdDeNota = itemNota.id_User

                                titulo2.text = itemNota.Title
                                descripcion2.text = itemNota.Description

                                if (itemNota.Image != "") {
                                    var byteArray3: ByteArray? = null
                                    val strImage: String =
                                        itemNota.Image!!.replace("data:image/png;base64,", "")
                                    byteArray3 = Base64.getDecoder().decode(strImage)

                                    var bitmap: Bitmap? = null
                                    if (byteArray3 != null) {
                                        bitmap =
                                            ImageUtilities.getBitMapFromByteArray(byteArray3)
                                        val roundedBitmapWrapper: RoundedBitmapDrawable =
                                            RoundedBitmapDrawableFactory.create(
                                                Resources.getSystem(),
                                                bitmap
                                            )
                                        imgNota2.setImageDrawable(roundedBitmapWrapper)
                                    }
                                }

                                imgNota2.setOnClickListener {

//                                    val notaActual: Nota = nota

                                    val intent = Intent(
                                        applicationContext,
                                        ImagenCompleta::class.java
                                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

//                                    intent.putExtra("verNota", itemNota)
                                    intent.putExtra("idDeMiNotaActualClave", itemNota.id_Nota)
                                    startActivity(intent)
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

                                }


                                val service: Service =
                                    RestEngine.getRestEngine().create(Service::class.java)
                                val result: Call<List<Usuario>> =
                                    service.getUser(itemNota.id_User.toString())

                                result.enqueue(object : Callback<List<Usuario>> {
                                    override fun onFailure(
                                        call: Call<List<Usuario>>,
                                        t: Throwable
                                    ) {
//                                        Toast.makeText(
//                                            applicationContext,
//                                            "Error",
//                                            Toast.LENGTH_LONG
//                                        ).show()
                                        Dialogo.getInstance(this@DetallesNota)
                                            .crearDialogoSinAccion(
                                                this@DetallesNota,
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
//                                                Toast.makeText(
//                                                    applicationContext,
//                                                    "No tiene información",
//                                                    Toast.LENGTH_LONG
//                                                ).show()

                                                Dialogo.getInstance(this@DetallesNota)
                                                    .crearDialogoSinAccion(
                                                        this@DetallesNota,
                                                        getString(R.string.dialog_error_de_usuario),
                                                        getString(R.string.dialog_error_de_usuario_text),
                                                        getString(R.string.dialog_aceptar)
                                                    )


                                            } else {

                                                var byteArray2: ByteArray? = null
//                            namePerfil!!.text = getString(R.string.name) + ": " + item[0].Name
//                            lastnamePerfil!!.text =
//                                getString(R.string.last_name) + ": " + item[0].LastName
//                            emailPerfil!!.text = getString(R.string.email) + ": " + item[0].Email

                                                nombre2.text = item[0].Name

                                                val strImage: String =
                                                    item[0].Image!!.replace(
                                                        "data:image/png;base64,",
                                                        ""
                                                    )
                                                byteArray2 = Base64.getDecoder().decode(strImage)
                                                if (byteArray2 != null) {
                                                    //Bitmap redondo
                                                    val bitmap: Bitmap =
                                                        ImageUtilities.getBitMapFromByteArray(
                                                            byteArray2
                                                        )
                                                    val roundedBitmapWrapper: RoundedBitmapDrawable =
                                                        RoundedBitmapDrawableFactory.create(
                                                            Resources.getSystem(),
                                                            bitmap
                                                        )
                                                    roundedBitmapWrapper.isCircular = true
                                                    imgPerfil2.setImageDrawable(roundedBitmapWrapper)
                                                }
                                            }
                                        } else {
//                                            Toast.makeText(
//                                                applicationContext,
//                                                "Incorrectas",
//                                                Toast.LENGTH_LONG
//                                            ).show()
                                            Dialogo.getInstance(this@DetallesNota)
                                                .crearDialogoSinAccion(
                                                    this@DetallesNota,
                                                    getString(R.string.dialog_error_de_usuario),
                                                    getString(R.string.dialog_error_de_usuario_text),
                                                    getString(R.string.dialog_aceptar)
                                                )
                                        }


                                    }
                                })

                            }
                        }

                    }
                } else {
//                    Toast.makeText(this@DetallesNota, "No hay notas", Toast.LENGTH_LONG).show()

                    Dialogo.getInstance(this@DetallesNota)
                        .crearDialogoSinAccion(
                            this@DetallesNota,
                            getString(R.string.dialog_no_tiene_notas),
                            getString(R.string.dialog_no_tiene_notas_text),
                            getString(R.string.dialog_aceptar)
                        )
                }
            }
        })


    }

    fun asignaFotoUsuario(menu: Menu) {

        var miItem5: MenuItem = menu.findItem(R.id.user_profile)

        var id_User = intent.getStringExtra("idUserLog")
        if (id_User != null) {

            val service: Service = RestEngine.getRestEngine().create(Service::class.java)
            val result: Call<List<Usuario>> = service.getUser(id_User)
            //Toast.makeText(this,"Hasta aquí bien",Toast.LENGTH_SHORT).show()
            result.enqueue(object : Callback<List<Usuario>> {
                override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
//                    Toast.makeText(applicationContext, "Error", Toast.LENGTH_LONG).show()

                    Dialogo.getInstance(this@DetallesNota)
                        .crearDialogoSinAccion(
                            this@DetallesNota,
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
//                                applicationContext,
//                                "No tiene información",
//                                Toast.LENGTH_LONG
//                            ).show()

                            Dialogo.getInstance(this@DetallesNota)
                                .crearDialogoSinAccion(
                                    this@DetallesNota,
                                    getString(R.string.dialog_error_de_usuario),
                                    getString(R.string.dialog_error_de_usuario_text),
                                    getString(R.string.dialog_aceptar)
                                )

                        } else {
                            var byteArray: ByteArray? = null
//                            nameUser!!.text = item[0].Name
//                            lastNameUser!!.text = item[0].LastName
//                            emailUser!!.text = item[0].Email
//                            passUser!!.text = item[0].Password

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
//                                imageUI!!.setImageDrawable(roundedBitmapWrapper)
                                miItem5.setIcon(roundedBitmapWrapper)

                            }
                        }
                    } else {
//                        Toast.makeText(applicationContext, "Incorrectas", Toast.LENGTH_LONG).show()
                        Dialogo.getInstance(this@DetallesNota)
                            .crearDialogoSinAccion(
                                this@DetallesNota,
                                getString(R.string.dialog_error_de_usuario),
                                getString(R.string.dialog_error_de_usuario_text),
                                getString(R.string.dialog_aceptar)
                            )

                    }


                }
            })
        } else {
//            Toast.makeText(this, "Error de usuario", Toast.LENGTH_SHORT).show()

            Dialogo.getInstance(this@DetallesNota)
                .crearDialogoSinAccion(
                    this@DetallesNota,
                    getString(R.string.dialog_error_de_usuario),
                    getString(R.string.dialog_error_de_usuario_text),
                    getString(R.string.dialog_aceptar)
                )
        }

    }

    fun ocultaSiNoEsSuya(menu: Menu) {
        val searchItem = menu.findItem(R.id.app_bar_edit_note)

        val id_User = intent.getStringExtra("idUserLog")

//        val nota = intent.getSerializableExtra("verNota") as Nota

        val numeroUser = intent.getSerializableExtra("idDeMiUsuarioDeNotaActualClave")

        if (id_User != numeroUser.toString()) {
            searchItem.isVisible = false
        }

    }


    override fun onSupportNavigateUp(): Boolean {
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
        inflater.inflate(R.menu.app_menu_editar_nota, menu)

        asignaFotoUsuario(menu)

        ocultaSiNoEsSuya(menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.user_profile -> {
                // Acción al presionar el botón
                val idUserLog = Bundle()
                idUserLog.putString("idUserLog", intent.getStringExtra("idUserLog"))
                val cambiarActivity = Intent(
                    this,
                    VerPerfil::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                cambiarActivity.putExtras(idUserLog)
                startActivity(cambiarActivity)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                true
            }
            R.id.app_bar_edit_note -> {
                val idUserLog = Bundle()
                idUserLog.putString("idUserLog", intent.getStringExtra("idUserLog"))
                val cambiarActivity = Intent(
                    this,
                    EditarPost::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                cambiarActivity.putExtras(idUserLog)

                val numeroNota = intent.getSerializableExtra("idDeMiNotaActualClave")

                cambiarActivity.putExtra("idDeMiNotaActualClave", numeroNota)

                startActivity(cambiarActivity)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
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