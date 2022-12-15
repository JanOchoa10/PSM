package com.blogee.activitys

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.util.PatternsCompat
import com.blogee.*
import com.blogee.local.miSQLiteHelper
import com.blogee.models.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.regex.Pattern

class SingUp : AppCompatActivity(), View.OnClickListener {
    lateinit var usuarioDBHelper: miSQLiteHelper
    var nameUser: TextView? = null
    var lastNameUser: TextView? = null
    var emailUser: TextView? = null
    var passUser: TextView? = null
    var imageUI: ImageView? = null
    var imgArray: ByteArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_up)

        supportActionBar?.hide()

        usuarioDBHelper = miSQLiteHelper(this)

        val btnCamera = findViewById<ImageButton>(R.id.btnCamera)
        btnCamera.setOnClickListener(this)
        val btnSignUp = findViewById<Button>(R.id.button)
        btnSignUp.setOnClickListener(this)
        nameUser = findViewById<TextView>(R.id.editTextTextName)
        lastNameUser = findViewById<TextView>(R.id.editTextTextLastName)
        emailUser = findViewById<TextView>(R.id.editTextTextEmailAddress)
        passUser = findViewById<TextView>(R.id.editTextTextPassword)
        imageUI = findViewById<ImageView>(R.id.imageView2)


        // get reference to TextView
        val textSignUp = findViewById<TextView>(R.id.tienesCuenta)
        // set on-click listener
        textSignUp.setOnClickListener {
            //Toast.makeText(this@MainActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
            val cambiarActivity = Intent(
                this,
                Login::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(cambiarActivity)
            overridePendingTransition(R.anim.from_left, R.anim.to_right)
            finish()
        }


        this.imageUI!!.setImageResource(R.mipmap.ic_launcher)

    }

    companion object {
        //Estos número tu los eliges como mejor funcione para ti, no necesariamente tienen que ser 1000, puede
        // ser 1,2,3
        //Lo importante es ser congruente en su uso
        //image pick code
        private const val IMAGE_PICK_CODE = 1000

        //Permission code
        private const val PERMISSION_CODE = 1001

        //camera code
        private const val CAMERA_CODE = 1002
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnCamera -> abrirDialogo()
            R.id.button -> validate()
        }
    }




    private fun abrirDialogo() {
        val builder = AlertDialog.Builder(this@SingUp)
        builder.setIcon(R.drawable.bluebird)
        builder.setTitle("Agregar avatar")
        builder.setMessage("¿Deseas agregar tu avatar desde la galería o tomar una foto?")
        builder.setPositiveButton("Galería") { dialog, which ->
            changeImage()
        }
        builder.setNeutralButton("Cancelar", null)
        builder.setNegativeButton("Cámara") { dialog, which ->
            openCamera()
        }
        builder.show()
    }


    fun openCamera() {
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
                        Toast.makeText(this@SingUp, getString(R.string.dialog_loading_image), Toast.LENGTH_SHORT).show()
//                        Snackbar.make(View(this@SingUp), "My Message", Snackbar.LENGTH_SHORT).show()
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
//                    Toast.makeText(
//                        this@SingUp,
//                        "Imagen demasiado grande, intente con otra imagen",
//                        Toast.LENGTH_LONG
//                    ).show()
                    val builder = AlertDialog.Builder(this@SingUp)
                    builder.setIcon(R.drawable.bluebird)
                    builder.setTitle(getString(R.string.dialog_imagen_no_cargada))
                    builder.setMessage(getString(R.string.dialog_imagen_no_cargada_text))
                    builder.setPositiveButton(getString(R.string.dialog_aceptar)) { dialog, which ->

                    }
                    builder.show()

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


    private fun saveUser() {

        if (this.imgArray == null) {
            val builder = AlertDialog.Builder(this@SingUp)
            builder.setIcon(R.drawable.bluebird)
            builder.setTitle(getString(R.string.dialog_imagen_no_cargada))
            builder.setMessage(getString(R.string.dialog_ingresa_imagen_text))
            builder.setPositiveButton(getString(R.string.dialog_aceptar)) { dialog, which ->

            }
            builder.show()
//            Toast.makeText(this@SingUp, "Por favor ingresa una imagen", Toast.LENGTH_LONG).show()
        } else {
            if (nameUser!!.text.isNotBlank() && lastNameUser!!.text.isNotBlank() && emailUser!!.text.isNotBlank() && passUser!!.text.isNotBlank()) {

                val cambiarActivity = Intent(
                    this,
                    Login::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

                val encodedString: String = Base64.getEncoder().encodeToString(this.imgArray)
                val strEncodeImage: String = "data:image/png;base64," + encodedString


                //SE CONSTRUYE EL OBJECTO A ENVIAR,  ESTO DEPENDE DE COMO CONSTRUYAS EL SERVICIO
                // SI TU SERVICIO POST REQUIERE DOS PARAMETROS HACER UN OBJECTO CON ESOS DOS PARAMETROS
                val user = Usuario(
                    0,
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
//                        Toast.makeText(this@SingUp, "Error", Toast.LENGTH_LONG).show()
                        /*val builder = AlertDialog.Builder(this@SingUp)
                        builder.setIcon(R.drawable.bluebird)
                        builder.setTitle(getString(R.string.dialog_user_no_register))
                        builder.setMessage(getString(R.string.dialog_user_no_register_text))
                        builder.setPositiveButton(getString(R.string.dialog_aceptar)) { dialog, which ->

                        }
                        builder.show()*/
                        if (usuarioDBHelper.addUsuario(
                                nameUser!!.text.toString(),
                                lastNameUser!!.text.toString(),
                                emailUser!!.text.toString(),
                                passUser!!.text.toString(),
                                strEncodeImage
                            ) > -1
                        ) {
//                            Toast.makeText(this@SingUp, "Agregado", Toast.LENGTH_LONG).show()

                            Dialogo.getInstance(this@SingUp)
                                .crearDialogoSinAccion(
                                    this@SingUp,
                                    getString(R.string.dialog_usuario_agregado),
                                    getString(R.string.dialog_usuario_agregado_text),
                                    getString(R.string.dialog_aceptar)
                                )

                        }
                    }

                    override fun onResponse(call: Call<Int>, response: Response<Int>) {
                        //usuarioDBHelper.addUsuario(nameUser!!.text.toString(),lastNameUser!!.text.toString(),emailUser!!.text.toString(),passUser!!.text.toString(),strEncodeImage)


//                        Toast.makeText(this@SingUp, "Guardado", Toast.LENGTH_LONG).show()

                        val builder = AlertDialog.Builder(this@SingUp)
                        builder.setIcon(R.drawable.bluebird)
                        builder.setTitle(getString(R.string.dialog_user_register))
                        builder.setMessage(getString(R.string.dialog_user_register_text))
                        builder.setPositiveButton(getString(R.string.dialog_aceptar)) { dialog, which ->
                            startActivity(cambiarActivity)
                            overridePendingTransition(R.anim.from_left, R.anim.to_right)
                            finish()
                        }
                        builder.show()
                        usuarioDBHelper.addUsuario(
                            nameUser!!.text.toString(),
                            lastNameUser!!.text.toString(),
                            emailUser!!.text.toString(),
                            passUser!!.text.toString(),
                            strEncodeImage
                        )

                    }
                })


            } else {
//                Toast.makeText(this, "Ingresa todos los datos", Toast.LENGTH_SHORT).show()

                Dialogo.getInstance(this@SingUp)
                    .crearDialogoSinAccion(
                        this@SingUp,
                        getString(R.string.dialog_datos_faltantes),
                        getString(R.string.dialog_datos_faltantes_text),
                        getString(R.string.dialog_aceptar)
                    )


            }


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

        saveUser()
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

}