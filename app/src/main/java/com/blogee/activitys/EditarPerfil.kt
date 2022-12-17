package com.blogee.activitys

import android.Manifest
import android.annotation.SuppressLint
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
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.util.PatternsCompat
import com.blogee.*
import com.blogee.UserApplication.Companion.prefs
import com.blogee.local.miSQLiteHelper
import com.blogee.models.Credenciales
import com.blogee.models.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.regex.Pattern


class EditarPerfil : AppCompatActivity(), View.OnClickListener {
    @SuppressLint("MissingInflatedId")
    lateinit var usuarioDBHelper: miSQLiteHelper
    var nameUser: TextView? = null
    var lastNameUser: TextView? = null
    var emailUser: TextView? = null
    var passUser: TextView? = null
    var imageUI: ImageView? = null
    var imgArray: ByteArray? = null
    var imgArray2: String? = null

    private val getCredenciales: Credenciales = prefs.getCredenciales()
    private val setCredenciales: Credenciales = Credenciales()

    @SuppressLint("Recycle")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_perfil)

        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)
        usuarioDBHelper = miSQLiteHelper(this)

        nameUser = findViewById<TextView>(R.id.editTextTextName)
        lastNameUser = findViewById<TextView>(R.id.editTextTextLastName)
        emailUser = findViewById<TextView>(R.id.editTextTextEmailAddress)
        passUser = findViewById<TextView>(R.id.editTextTextPassword)
        imageUI = findViewById<ImageView>(R.id.imageView2)
        infoUserEditar()

        val btnSaveChanges = findViewById<Button>(R.id.btn_guardar_cambios)
        btnSaveChanges.setOnClickListener(this)
        val btnCancel = findViewById<Button>(R.id.btn_cancel)
        val btnCamera2 = findViewById<ImageButton>(R.id.btnCamera2)
        btnCamera2.setOnClickListener(this)

        /*btnSaveChanges.setOnClickListener{
            onBackPressed()
        }*/
        val emailUser = getCredenciales.emailGuardado
        val db = usuarioDBHelper.readableDatabase
        val c = db.rawQuery(
            "Select * from usuarios where emailUser ='$emailUser'",
            null
        )
        if (c.moveToFirst()) {

            imgArray2 = c.getString(5).toString()
        }


        btnCancel.setOnClickListener {


            val cambiarActivity = Intent(
                this,
                VerPerfil::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

            startActivity(cambiarActivity)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

    }

    private fun infoUserEditar() {
        val id_User = getCredenciales.idUserGuardado.toString()
        if (id_User != null && (isConnectedWifi(this@EditarPerfil) || isConnectedMobile(this@EditarPerfil))) {

            val service: Service = RestEngine.getRestEngine().create(Service::class.java)
            val result: Call<List<Usuario>> = service.getUser(id_User)
            //Toast.makeText(this,"Hasta aquí bien",Toast.LENGTH_SHORT).show()
            result.enqueue(object : Callback<List<Usuario>> {
                override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
//                    Toast.makeText(this@EditarPerfil, "Error", Toast.LENGTH_LONG).show()

                    Dialogo.getInstance(this@EditarPerfil)
                        .crearDialogoSinAccion(
                            this@EditarPerfil,
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
//                                this@EditarPerfil,
//                                "No tiene información",
//                                Toast.LENGTH_LONG
//                            ).show()

                            Dialogo.getInstance(this@EditarPerfil)
                                .crearDialogoSinAccion(
                                    this@EditarPerfil,
                                    getString(R.string.dialog_error_de_usuario),
                                    getString(R.string.dialog_error_de_usuario_text),
                                    getString(R.string.dialog_aceptar)
                                )

                        } else {
                            var byteArray: ByteArray? = null
                            nameUser!!.text = item[0].Name
                            lastNameUser!!.text = item[0].LastName
                            emailUser!!.text = item[0].Email
                            passUser!!.text = item[0].Password

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
//                        Toast.makeText(this@EditarPerfil, "Incorrectas", Toast.LENGTH_LONG).show()

                        Dialogo.getInstance(this@EditarPerfil)
                            .crearDialogoSinAccion(
                                this@EditarPerfil,
                                getString(R.string.dialog_error_de_usuario),
                                getString(R.string.dialog_error_de_usuario_text),
                                getString(R.string.dialog_aceptar)
                            )
                    }


                }
            })
        } else {
//            Toast.makeText(this, "Error de usuario", Toast.LENGTH_SHORT).show()


            var email_User = getCredenciales.emailGuardado
            val db = usuarioDBHelper.readableDatabase
            val c = db.rawQuery(
                "Select * from usuarios where emailUser ='$email_User'",
                null
            )
            if (c.moveToFirst()) {
                var byteArray: ByteArray? = null
                nameUser!!.text = c.getString(1).toString()
                lastNameUser!!.text = c.getString(2).toString()
                emailUser!!.text = c.getString(3).toString()
                passUser!!.text = c.getString(4).toString()
                imgArray2 = c.getString(5).toString()
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
                    roundedBitmapWrapper.isCircular = true
                    imageUI!!.setImageDrawable(roundedBitmapWrapper)
                }

            }


            /*Dialogo.getInstance(this@EditarPerfil)
                .crearDialogoSinAccion(
                    this@EditarPerfil,
                    getString(R.string.dialog_error_de_usuario),
                    getString(R.string.dialog_error_de_usuario_text),
                    getString(R.string.dialog_aceptar)
                )*/

        }
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

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_guardar_cambios -> validate()
            R.id.btnCamera2 -> abrirDialogo()
        }
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

                val bitmap = (imageUI!!.drawable as BitmapDrawable).bitmap
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
                            this@EditarPerfil,
                            getString(R.string.dialog_loading_image),
                            Toast.LENGTH_SHORT
                        )
                            .show()
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

                    Dialogo.getInstance(this@EditarPerfil)
                        .crearDialogoSinAccion(
                            this@EditarPerfil,
                            getString(R.string.dialog_imagen_no_cargada),
                            getString(R.string.dialog_imagen_no_cargada_text),
                            getString(R.string.dialog_aceptar)
                        )


//                    Toast.makeText(
//                        this@EditarPerfil,
//                        "Imagen demasiado grande, intente con otra imagen",
//                        Toast.LENGTH_LONG
//                    ).show()


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
    }


    private fun GuardarCambios() {
        if (nameUser!!.text.isNotBlank() && lastNameUser!!.text.isNotBlank() && emailUser!!.text.isNotBlank() && passUser!!.text.isNotBlank()) {
            val id_User = getCredenciales.idUserGuardado
            val cambiarActivity = Intent(
                this,
                VerPerfil::class.java
            )
            val strEncodeImage: String
            if (this.imgArray != null) {
                val encodedString: String = Base64.getEncoder().encodeToString(this.imgArray)
                strEncodeImage = "data:image/png;base64,$encodedString"
            } else {
                strEncodeImage = ""
            }


            //nameUser!!.text=strEncodeImage

            //SE CONSTRUYE EL OBJECTO A ENVIAR,  ESTO DEPENDE DE COMO CONSTRUYAS EL SERVICIO
            // SI TU SERVICIO POST REQUIERE DOS PARAMETROS HACER UN OBJECTO CON ESOS DOS PARAMETROS
            val user = Usuario(
                id_User,
                nameUser!!.text.toString(),
                lastNameUser!!.text.toString(),
                emailUser!!.text.toString(),
                passUser!!.text.toString(),
                strEncodeImage
            )
            val service: Service = RestEngine.getRestEngine().create(Service::class.java)
            val result: Call<Int> = service.saveUser(user)

            result.enqueue(object : Callback<Int> {
                override fun onFailure(call: Call<Int>, t: Throwable) {
//                    Toast.makeText(this@EditarPerfil, "Error", Toast.LENGTH_LONG).show()
                    if (user.Image == "") {
                        user.Image = imgArray2
                    }
                    usuarioDBHelper.updateUser(user, getCredenciales.emailGuardado)
                    setCredenciales.emailGuardado = emailUser!!.text.toString()
                    setCredenciales.passGuardado = passUser!!.text.toString()
                    setCredenciales.idUserGuardado = getCredenciales.idUserGuardado
                    setCredenciales.setModoOscuro(getCredenciales.getModoOscuro())
                    prefs.saveCredenciales(setCredenciales)
                    /*Dialogo.getInstance(this@EditarPerfil)
                        .crearDialogoSinAccion(
                            this@EditarPerfil,
                            getString(R.string.dialog_user_no_register),
                            getString(R.string.dialog_user_no_register_text),
                            getString(R.string.dialog_aceptar)
                        )*/
                    val builder = AlertDialog.Builder(this@EditarPerfil)
                    builder.setIcon(R.drawable.bluebird)
                    builder.setTitle(getString(R.string.dialog_user_edited))
                    builder.setMessage(getString(R.string.dialog_user_edited_text))
                    builder.setPositiveButton(getString(R.string.dialog_aceptar)) { dialog, which ->
                        startActivity(cambiarActivity)
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                        finish()
                    }
                    builder.show()
                }

                override fun onResponse(call: Call<Int>, response: Response<Int>) {

                    if (user.Image == "") {
                        user.Image = imgArray2
                    }
                    usuarioDBHelper.updateUser(user, getCredenciales.emailGuardado)

                    setCredenciales.idUserGuardado = getCredenciales.idUserGuardado
                    setCredenciales.emailGuardado = emailUser!!.text.toString()
                    setCredenciales.passGuardado = passUser!!.text.toString()
                    setCredenciales.setModoOscuro(getCredenciales.getModoOscuro())

                    prefs.saveCredenciales(setCredenciales)


                    val builder = AlertDialog.Builder(this@EditarPerfil)
                    builder.setIcon(R.drawable.bluebird)
                    builder.setTitle(getString(R.string.dialog_user_edited))
                    builder.setMessage(getString(R.string.dialog_user_edited_text))
                    builder.setPositiveButton(getString(R.string.dialog_aceptar)) { dialog, which ->
                        startActivity(cambiarActivity)
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                        finish()
                    }
                    builder.show()


                }
            })


        } else {
//            Toast.makeText(this, "Ingresa todos los datos", Toast.LENGTH_SHORT).show()
            val builder = AlertDialog.Builder(this@EditarPerfil)
            builder.setIcon(R.drawable.bluebird)
            builder.setTitle(getString(R.string.dialog_datos_faltantes))
            builder.setMessage(getString(R.string.dialog_datos_faltantes_text))
            builder.setPositiveButton(getString(R.string.dialog_aceptar)) { dialog, which ->

            }
            builder.show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
//                onBackPressed()

        val cambiarActivity = Intent(
            this,
            VerPerfil::class.java
        ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(cambiarActivity)
        overridePendingTransition(R.anim.from_left, R.anim.to_right)
        return false
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.app_menu_editar_perfil, menu)

        val item: MenuItem = menu.findItem(R.id.dark_mode)


        val activo: Boolean = getCredenciales.getModoOscuro()

        if (activo) {
            item.setIcon(R.drawable.ic_baseline_light_mode_24)
        } else {
            item.setIcon(R.drawable.ic_baseline_dark_mode_24)
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


//                val credenciales: Credenciales = prefs.getCredenciales()
                val activo: Boolean = getCredenciales.getModoOscuro()

                if (activo) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    getCredenciales.setModoOscuro(false)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    getCredenciales.setModoOscuro(true)
                }
                //ESTAMOS GRABANDO
                prefs.saveCredenciales(getCredenciales)

//
//                if (f == 0) {
//                    myEditor.putInt(getString(R.string.modo_oscuro), 1)
//                    myEditor.apply()
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//
//                } else {
//                    if (f == 1) {
//                        myEditor.putInt(getString(R.string.modo_oscuro), 0)
//                        myEditor.apply()
//                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//
//
//                        //Toast.makeText(this,"Modo oscuro desactivado", Toast.LENGTH_SHORT).show()
//
//                    }
//                }


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

    val caracteresEspeciales = "[:punct:]"

    private fun validateEmail(): Boolean {
        // /^([a-z0-9_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})$/
        val email = emailUser!!.text.toString()
        return if (email.isEmpty()) {
            emailUser!!.error = getString(R.string.can_not_be_empty)
            emailUser!!.requestFocus()
            false
        } else if (!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
            emailUser!!.error = getString(R.string.valid_email_address)
            emailUser!!.requestFocus()
            false
        } else {
            emailUser!!.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        val password = passUser!!.text.toString()

        val passwordRegex = Pattern.compile(
            "^"
                    + "(?=.*[0-9])"         // Al menos un digito
                    + "(?=.*[a-z])"         // Al menos una minuscula
                    + "(?=.*[A-Z])"         // Al menos una mayuscula
//                    + "(?=.*[" + caracteresEspeciales + "])"    // Al menos un caracter especial
                    + "(?=\\S+$)"           // No espacios en blanco
                    + ".{8,50}"               // Al menos 8 caracteres
                    + "$"

        )

        return if (password.isEmpty()) {
//            passUser!!.setErrorEnabled(true)
            passUser!!.setError(getString(R.string.can_not_be_empty), null)
            passUser!!.requestFocus()
            false
        } else if (!passwordRegex.matcher(password).matches()) {
            passUser!!.setError(getString(R.string.password_is_too_weak), null)
            passUser!!.requestFocus()
            false
        } else {
            passUser!!.error = null
            true
        }
    }

    private fun validateName(): Boolean {
        val name = nameUser!!.text.toString()

        val nameRegex = Pattern.compile(
            "^"
//                    + "([A-Z])"               // Empezar con mayúscula
                    + "(?!.*[0-9])"         // Al menos un digito
                    + "(?=.*[a-z])"         // Al menos una minuscula
                    + "(?=.*[A-Z])"         // Al menos una mayuscula
                    + "(?!.*[" + caracteresEspeciales + "])"    // Al menos un caracter especial
//                    + "(?=\\S+$)"           // No espacios en blanco
                    + ".{2,50}"               // Al menos 2 caracteres
                    + "$"

        )

        return if (name.isEmpty()) {
//            passUser!!.setErrorEnabled(true)
            nameUser!!.setError(getString(R.string.can_not_be_empty))
            nameUser!!.requestFocus()
            false
        } else if (!nameRegex.matcher(name).matches()) {
            nameUser!!.setError(getString(R.string.enter_valid_name))
            nameUser!!.requestFocus()
            false
        } else {
            nameUser!!.error = null
            true
        }
    }

    private fun validateLastName(): Boolean {
        val lastName = lastNameUser!!.text.toString()

        val nameRegex = Pattern.compile(

//            "^(?!.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?!.*[$caracteresEspeciales])(?=\\S+$).{2,50}\$"

            "^"
//                    + "[A-Z]"               // Empezar con mayúscula
                    + "(?!.*[0-9])"         // Al menos un digito
                    + "(?=.*[a-z])"         // Al menos una minuscula
                    + "(?=.*[A-Z])"         // Al menos una mayuscula
                    + "(?!.*[" + caracteresEspeciales + "])"    // Al menos un caracter especial
//                    + "(?=\\S+$)"           // No espacios en blanco
//                    + "[a-z]"             // Terminar con minúscula
                    + ".{2,50}"               // Al menos 2 caracteres
                    + "$"

        )

        return if (lastName.isEmpty()) {
//            passUser!!.setErrorEnabled(true)
            lastNameUser!!.setError(getString(R.string.can_not_be_empty))
            lastNameUser!!.requestFocus()
            false
        } else if (!nameRegex.matcher(lastName).matches()) {
            lastNameUser!!.setError(getString(R.string.enter_valid_lastname))
            lastNameUser!!.requestFocus()
            false
        } else {
            lastNameUser!!.error = null
            true
        }
    }


    private fun validate() {

        if (!validateName()) {
            return
        }

        if (!validateLastName()) {
            return
        }

        if (!validateEmail()) {
            return
        }

        if (!validatePassword()) {
            return
        }

        GuardarCambios()
    }

    private fun abrirDialogo() {

//        Dialogo.getInstance(this@EditarPerfil)
//            .crearDialogoConDobleAccion(
//                this@EditarPerfil,
//                getString(R.string.dialog_error_de_usuario),
//                getString(R.string.dialog_error_de_usuario_text),
//                getString(R.string.dialog_galeria),
//                getString(R.string.dialog_cancelar),
//                getString(R.string.dialog_camera),
//                changeImage(),
//                openCamera()
//            )

        val builder = AlertDialog.Builder(this@EditarPerfil)
        builder.setTitle("Cambiar avatar")
        builder.setMessage("¿Deseas cambiar tu avatar desde la galería o tomar una foto?")
        builder.setPositiveButton("Galería") { dialog, which ->
            changeImage()
        }
        builder.setNeutralButton("Cancelar", null)
        builder.setNegativeButton("Cámara") { dialog, which ->
            openCamera()
        }
        builder.show()
    }


    fun changeImage() {
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