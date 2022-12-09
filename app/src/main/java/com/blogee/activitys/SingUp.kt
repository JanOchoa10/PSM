package com.blogee.activitys

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.PatternsCompat
import com.blogee.R
import com.blogee.RestEngine
import com.blogee.Service
import com.blogee.miSQLiteHelper
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
        private val IMAGE_PICK_CODE = 1000;

        //Permission code
        private val PERMISSION_CODE = 1001;

        //camera code
        private val CAMERA_CODE = 1002;
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnCamera -> openCamera()
            R.id.button -> validate()
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

                val bitmap = (imageUI!!.getDrawable() as BitmapDrawable).bitmap
            }

        }
    }


    private fun saveUser() {

        if (this.imgArray == null) {
            Toast.makeText(this@SingUp, "Por favor ingresa una imagen", Toast.LENGTH_LONG).show()
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
                        Toast.makeText(this@SingUp, "Error", Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<Int>, response: Response<Int>) {
                        //usuarioDBHelper.addUsuario(nameUser!!.text.toString(),lastNameUser!!.text.toString(),emailUser!!.text.toString(),passUser!!.text.toString())
                        nameUser!!.text = ""
                        lastNameUser!!.text = ""
                        emailUser!!.text = ""
                        passUser!!.text = ""
                        Toast.makeText(this@SingUp, "Guardado", Toast.LENGTH_LONG).show()
                        startActivity(cambiarActivity)
                        overridePendingTransition(R.anim.from_left, R.anim.to_right)
                        finish()
                    }
                })


            } else {
                Toast.makeText(this, "Ingresa todos los datos", Toast.LENGTH_SHORT).show()
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
                    + "(?=.*[" + caracteresEspeciales + "])"    // Al menos un caracter especial
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

}