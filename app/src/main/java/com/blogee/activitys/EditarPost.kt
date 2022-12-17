package com.blogee.activitys

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.blogee.*
import com.blogee.UserApplication.Companion.prefs
import com.blogee.models.Credenciales
import com.blogee.models.Nota
import com.blogee.models.Usuario
import kotlinx.android.synthetic.main.activity_post2.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.util.*

class EditarPost : AppCompatActivity(), View.OnClickListener {

    var title: TextView? = null
    var desc: TextView? = null
    var imageUI: ImageView? = null
    var imgArray: ByteArray? = null
    var notaGeneral: Nota? = null
    var ImgNota: String = ""

    private val getCredenciales: Credenciales = UserApplication.prefs.getCredenciales()
    private val setCredenciales: Credenciales = Credenciales()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_post)

        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)

        val btnPost = findViewById<Button>(R.id.btn_post)
        btnPost.setOnClickListener(this)
        val btnDelete = findViewById<Button>(R.id.btn_delete)
        btnDelete.setOnClickListener(this)
        val btnCancel = findViewById<Button>(R.id.btn_cancel)
        btnCancel.setOnClickListener(this)
        val btnEliminarImg = findViewById<Button>(R.id.btn_EliminarImg)
        btnEliminarImg.setOnClickListener(this)
        val btnCamera = findViewById<Button>(R.id.btn_img)
        btnCamera.setOnClickListener(this)
        title = findViewById<TextView>(R.id.editTextTextName)
        desc = findViewById<EditText>(R.id.editTextTextMultiLine)
        imageUI = findViewById<ImageView>(R.id.imageView4)
        btn_galeria.setOnClickListener(this)


        val numeroNota = getCredenciales.getIdNotaGuardado()
//        var userIdDeNota: Int? = null

        val serviceNota: Service = RestEngine.getRestEngine().create(Service::class.java)
        val resultNota: Call<List<Nota>> = serviceNota.getNota(numeroNota.toString())

        resultNota.enqueue(object : Callback<List<Nota>> {
            override fun onFailure(call: Call<List<Nota>>, t: Throwable) {
//                Toast.makeText(this@EditarPost, "Error", Toast.LENGTH_LONG).show()

                Dialogo.getInstance(this@EditarPost)
                    .crearDialogoSinAccion(
                        this@EditarPost,
                        getString(R.string.dialog_error_de_usuario),
                        getString(R.string.dialog_error_de_usuario_text),
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
//                            this@EditarPost,
//                            "No tiene notas",
//                            Toast.LENGTH_LONG
//                        ).show()

                        Dialogo.getInstance(this@EditarPost)
                            .crearDialogoSinAccion(
                                this@EditarPost,
                                getString(R.string.dialog_no_tiene_notas),
                                getString(R.string.dialog_no_tiene_notas_text),
                                getString(R.string.dialog_aceptar)
                            )
                    } else {
//                        Toast.makeText(this@DetallesNota, "Hay notas", Toast.LENGTH_LONG).show()

                        title!!.text = arrayPosts[0].Title
                        desc!!.text = arrayPosts[0].Description
                        notaGeneral = arrayPosts[0]
//                        numeroNotaBack = arrayPosts[0].id_Nota
//                        numeroUserBack = arrayPosts[0].id_User

                        if (arrayPosts[0].Image != "") {
                            var byteArray: ByteArray? = null

                            val strImage: String =
                                arrayPosts[0].Image!!.replace("data:image/png;base64,", "")
                            byteArray = Base64.getDecoder().decode(strImage)

                            var bitmap: Bitmap? = null

                            if (byteArray != null) {
                                bitmap =
                                    ImageUtilities.getBitMapFromByteArray(byteArray)

                                imageUI!!.setImageBitmap(bitmap)
                            }
                        } else {
//                            imageUI!!.setImageResource(R.mipmap.ic_launcher)
                        }

                    }
                } else {
//                    Toast.makeText(this@EditarPost, "No hay notas", Toast.LENGTH_LONG).show()

                    Dialogo.getInstance(this@EditarPost)
                        .crearDialogoSinAccion(
                            this@EditarPost,
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

        var id_User = getCredenciales.idUserGuardado.toString()
        if (id_User != null && (isConnectedWifi(this@EditarPost) || isConnectedMobile(this@EditarPost))) {

            val service: Service = RestEngine.getRestEngine().create(Service::class.java)
            val result: Call<List<Usuario>> = service.getUser(id_User)
            //Toast.makeText(this,"Hasta aquí bien",Toast.LENGTH_SHORT).show()
            result.enqueue(object : Callback<List<Usuario>> {
                override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
//                    Toast.makeText(applicationContext, "Error", Toast.LENGTH_LONG).show()
                    Dialogo.getInstance(this@EditarPost)
                        .crearDialogoSinAccion(
                            this@EditarPost,
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
                            Dialogo.getInstance(this@EditarPost)
                                .crearDialogoSinAccion(
                                    this@EditarPost,
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
                                roundedBitmapWrapper.isCircular = true
//                                imageUI!!.setImageDrawable(roundedBitmapWrapper)
                                miItem5.icon = roundedBitmapWrapper

                            }
                        }
                    } else {
//                        Toast.makeText(applicationContext, "Incorrectas", Toast.LENGTH_LONG).show()

                        Dialogo.getInstance(this@EditarPost)
                            .crearDialogoSinAccion(
                                this@EditarPost,
                                getString(R.string.dialog_no_login),
                                getString(R.string.dialog_credenciales_incorrectas_text),
                                getString(R.string.dialog_aceptar)
                            )
                    }


                }
            })
        } else {
//            Toast.makeText(this, "Error de usuario", Toast.LENGTH_SHORT).show()

            Dialogo.getInstance(this@EditarPost)
                .crearDialogoSinAccion(
                    this@EditarPost,
                    getString(R.string.dialog_error_de_usuario),
                    getString(R.string.dialog_error_de_usuario_text),
                    getString(R.string.dialog_aceptar)
                )
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()


        overridePendingTransition(R.anim.from_left, R.anim.to_right)
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.app_menu_solo_perfil, menu)

        asignaFotoUsuario(menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.user_profile -> {
                // Acción al presionar el botón

                val cambiarActivity = Intent(
                    this,
                    VerPerfil::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

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

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_post -> GuardarCambios()
            R.id.btn_img -> openCamera()
            R.id.btn_delete -> deletePost()
            R.id.btn_EliminarImg -> deleteImg()
            R.id.btn_cancel -> cancelEdit()
            R.id.btn_galeria -> changeImage()
        }
    }

    private fun cancelEdit() {
        onBackPressed()


        overridePendingTransition(R.anim.from_left, R.anim.to_right)
    }

    private fun deleteImg() {

        notaGeneral!!.Image = ""
        this.imgArray = null
        this.imageUI!!.setImageDrawable(null)

    }

    private fun deletePost() {

        val builder = AlertDialog.Builder(this@EditarPost)
        builder.setIcon(R.drawable.bluebird)
        builder.setTitle(getString(R.string.dialog_eliminar_nota))
//                builder.setMessage(getString(R.string.dialog_cerrar_sesion_text))
        builder.setPositiveButton(getString(R.string.dialog_yes)) { dialog, which ->


            val service2: Service = RestEngine.getRestEngine().create(Service::class.java)
            val result2: Call<String> = service2.deleteNota(notaGeneral?.id_Nota.toString())
            val cambiarActivity = Intent(
                this,
                MainActivity::class.java
            )
            result2.enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
//                Toast.makeText(this@EditarPost, "Error", Toast.LENGTH_LONG).show()

                    Dialogo.getInstance(this@EditarPost)
                        .crearDialogoSinAccion(
                            this@EditarPost,
                            getString(R.string.dialog_error_de_notas),
                            getString(R.string.dialog_error_nota_eliminar),
                            getString(R.string.dialog_aceptar)
                        )
                }

                override fun onResponse(call: Call<String>, response2: Response<String>) {
                    //usuarioDBHelper.addUsuario(nameUser!!.text.toString(),lastNameUser!!.text.toString(),emailUser!!.text.toString(),passUser!!.text.toString())

//                Toast.makeText(this@EditarPost, "Nota Eliminada", Toast.LENGTH_LONG).show()

                    Dialogo.getInstance(this@EditarPost)
                        .crearDialogoSinAccion(
                            this@EditarPost,
                            getString(R.string.dialog_nota_deleted),
                            getString(R.string.dialog_nota_deleted_text),
                            getString(R.string.dialog_aceptar)
                        )


                    val builder = AlertDialog.Builder(this@EditarPost)
                    builder.setIcon(R.drawable.bluebird)
                    builder.setTitle(getString(R.string.dialog_nota_deleted))
                    builder.setMessage(getString(R.string.dialog_nota_deleted_text))
                    builder.setPositiveButton(getString(R.string.dialog_aceptar)) { dialog, which ->
                        startActivity(cambiarActivity)
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                        finishAffinity()
                    }
                    builder.show()


                }
            })


        }
        builder.setNegativeButton(getString(R.string.dialog_no), null)
        builder.show()


    }

    companion object {
        //Estos número tu los eliges como mejor funcione para ti, no necesariamente tienen que ser 1000, puede
        // ser 1,2,3
        //Lo importante es ser congruente en su uso
        //image pick code
        private val IMAGE_PICK_CODE = 1000;

        //Permission code
        private val PERMISSION_CODE = 1001;

        //camera code
        private val CAMERA_CODE = 1002;
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_CODE)
    }

    override fun onActivityResult(requestcode: Int, resultcode: Int, data: Intent?) {
        super.onActivityResult(requestcode, resultcode, intent)

        if (resultcode == Activity.RESULT_OK) {
            //RESPUESTA DE LA CÁMARA CON TIENE LA IMAGEN
            if (requestcode == CAMERA_CODE) {

                val photo = data?.extras?.get("data") as Bitmap
                val stream = ByteArrayOutputStream()
                //Bitmap.CompressFormat agregar el formato desado, estoy usando aqui jpeg
                photo.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                //Agregamos al objecto album el arreglo de bytes
                imgArray = stream.toByteArray()
                //Mostramos la imagen en la vista
                this.imageUI!!.setImageBitmap(photo)

                val bitmap = (imageUI!!.getDrawable() as BitmapDrawable).bitmap
            }

        }

        if (requestcode == IMAGE_PICK_CODE) {
            this.imageUI!!.setImageURI(data?.data)
            var bitmap = (imageUI!!.drawable as BitmapDrawable).bitmap
            var baos = ByteArrayOutputStream()


            var calidad = 80
            bitmap.compress(Bitmap.CompressFormat.JPEG, calidad, baos)
            imgArray = baos.toByteArray()


            var strEncodeImage2: String
            var encodedString2: String = Base64.getEncoder().encodeToString(this.imgArray)
            strEncodeImage2 = "data:image/png;base64," + encodedString2

            val tamanoPermitido = 16777215
            var tamano = strEncodeImage2.count()

            var mostrarCargando = true
            var entroAWhile = false

            while (tamano > tamanoPermitido && calidad > 1) {
                if (mostrarCargando) {
                    Toast.makeText(
                        this@EditarPost,
                        getString(R.string.dialog_loading_image),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                mostrarCargando = false

                calidad -= 1
                if (!mostrarCargando && calidad % 40 == 0) {
                    mostrarCargando = true
                }


                bitmap = (imageUI!!.drawable as BitmapDrawable).bitmap
                baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, calidad, baos)
//                    bitmap.scale(80,80,true).compress(Bitmap.CompressFormat.JPEG, calidad, baos)

                imgArray = baos.toByteArray()

                encodedString2 = Base64.getEncoder().encodeToString(this.imgArray)
                strEncodeImage2 = "data:image/png;base64," + encodedString2

                tamano = strEncodeImage2.count()

                entroAWhile = true

            }

//                this.imageUI!!.setImageURI(baos)

            if (tamano > tamanoPermitido) {
//                Toast.makeText(
//                    this@EditarPost,
//                    "Imagen demasiado grande, intente con otra imagen",
//                    Toast.LENGTH_LONG
//                ).show()

                Dialogo.getInstance(this@EditarPost)
                    .crearDialogoSinAccion(
                        this@EditarPost,
                        getString(R.string.dialog_imagen_no_cargada),
                        getString(R.string.dialog_ingresa_imagen_text),
                        getString(R.string.dialog_aceptar)
                    )
                this.imageUI!!.setImageURI(null)
                baos = ByteArrayOutputStream()
                imgArray = baos.toByteArray()
            } else if (entroAWhile) {


//                    Toast.makeText(
//                        this@Post2,
//                        "Entro al while",
//                        Toast.LENGTH_LONG
//                    ).show()

//                    this.imageUI!!.setImageURI(null)

                if (strEncodeImage2 != "") {
                    var byteArray: ByteArray? = null
                    val strImage: String =
                        strEncodeImage2.replace("data:image/png;base64,", "")
                    byteArray = Base64.getDecoder().decode(strImage)
                    var bitmap: Bitmap? = null
                    if (byteArray != null) {
                        bitmap =
                            ImageUtilities.getBitMapFromByteArray(byteArray)
                        val roundedBitmapWrapper: RoundedBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(
                                Resources.getSystem(),
                                bitmap
                            )
                        this.imageUI!!.setImageDrawable(roundedBitmapWrapper)
                    }

                }
            }

        }


    }

    private fun GuardarCambios() {
        //val nota = intent.getSerializableExtra("verNota") as Nota
        if (title!!.text.isNotBlank() && desc!!.text.isNotBlank()) {
            val strEncodeImage: String
            if (this.imgArray != null) {
                val encodedString: String = Base64.getEncoder().encodeToString(this.imgArray)
                strEncodeImage = "data:image/png;base64," + encodedString
            } else if (notaGeneral?.Image != "") {
                strEncodeImage = notaGeneral?.Image.toString()

            } else {
                strEncodeImage = ""
            }

            val notaSave = Nota(
                notaGeneral?.id_Nota,
                title!!.text.toString(),
                desc!!.text.toString(),
                notaGeneral?.id_User,
                strEncodeImage
            )

            val service: Service = RestEngine.getRestEngine().create(Service::class.java)
            val result: Call<Int> = service.saveNota(notaSave)

            result.enqueue(object : Callback<Int> {
                override fun onFailure(call: Call<Int>, t: Throwable) {
//                    Toast.makeText(this@EditarPost, "Error", Toast.LENGTH_LONG).show()
                    Dialogo.getInstance(this@EditarPost)
                        .crearDialogoSinAccion(
                            this@EditarPost,
                            getString(R.string.dialog_error_guardar_nota),
                            getString(R.string.dialog_error_guardar_nota_text),
                            getString(R.string.dialog_aceptar)
                        )
                }

                override fun onResponse(call: Call<Int>, response: Response<Int>) {
                    //usuarioDBHelper.addUsuario(nameUser!!.text.toString(),lastNameUser!!.text.toString(),emailUser!!.text.toString(),passUser!!.text.toString())

//                    Toast.makeText(this@EditarPost, "Guardado", Toast.LENGTH_LONG).show()


                    val builder = AlertDialog.Builder(this@EditarPost)
                    builder.setIcon(R.drawable.bluebird)
                    builder.setTitle(getString(R.string.dialog_nota_guardada))
                    builder.setMessage(getString(R.string.dialog_nota_guardada_text))
                    builder.setPositiveButton(getString(R.string.dialog_aceptar)) { dialog, which ->
                        val intent2 = Intent(
                            applicationContext,
                            DetallesNota::class.java
                        )

                        setCredenciales.idUserGuardado = getCredenciales.idUserGuardado
                        setCredenciales.emailGuardado = getCredenciales.emailGuardado
                        setCredenciales.passGuardado = getCredenciales.passGuardado

                        setCredenciales.setIdNotaGuardado(notaSave.id_Nota!!)
                        setCredenciales.setIdUserDeNota(notaSave.id_User!!)

                        val activo: Boolean = getCredenciales.getModoOscuro()
                        setCredenciales.setModoOscuro(activo)
                        setCredenciales.setFiltro(getCredenciales.getFiltro())

                        prefs.saveCredenciales(setCredenciales)


                        startActivity(intent2)
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                        finish()
                    }
                    builder.show()


                }
            })


        } else {

        }

    }

    private fun changeImage() {
        //check runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var boolDo = false
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED
            ) {
                //permission denied
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                //show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE)
            } else {
                //permission already granted
                boolDo = true

            }

            if (boolDo) {
                pickImageFromGallery()
            }

        }

    }

    private fun pickImageFromGallery() {
        //Abrir la galería
        val intent = Intent()
        intent.action = Intent.ACTION_PICK
        //intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.type = "image/*"
        //startActivityForResult(Intent.createChooser(intent,"Selecciona"), IMAGE_PICK_CODE)
        startActivityForResult(intent, IMAGE_PICK_CODE)

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
}