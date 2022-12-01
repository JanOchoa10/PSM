package com.blogee

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.blogee.Models.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.util.*

class SingUp : AppCompatActivity(), View.OnClickListener {
    lateinit var usuarioDBHelper: miSQLiteHelper
    var nameUser:TextView? = null
    var lastNameUser:TextView? = null
    var emailUser:TextView? = null
    var passUser:TextView? = null
    var imageUI:ImageView? =  null
    var imgArray:ByteArray? =  null

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
        imageUI = findViewById<ImageView>(R.id.imageView)




        // get reference to TextView
        val textSignUp = findViewById<TextView>(R.id.textView5)
        // set on-click listener
        textSignUp.setOnClickListener {
            //Toast.makeText(this@MainActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
            val cambiarActivity = Intent(this, Login::class.java)
            startActivity(cambiarActivity)
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
        when(v!!.id){
            R.id.btnCamera-> openCamera()
            R.id.button -> saveUser()
        }
    }


    private fun openCamera(){
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_CODE)
    }

    override fun onActivityResult(requestcode: Int, resultcode: Int, data: Intent?) {
        super.onActivityResult(requestcode, resultcode, intent)

        if (resultcode == Activity.RESULT_OK) {
            //RESPUESTA DE LA CÁMARA CON TIENE LA IMAGEN
            if (requestcode == CAMERA_CODE) {

                val photo =  data?.extras?.get("data") as Bitmap
                val stream = ByteArrayOutputStream()
                //Bitmap.CompressFormat agregar el formato desado, estoy usando aqui jpeg
                photo.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                //Agregamos al objecto album el arreglo de bytes
                imgArray =  stream.toByteArray()
                //Mostramos la imagen en la vista
                this.imageUI!!.setImageBitmap(photo)

                val bitmap = (imageUI!!.getDrawable() as BitmapDrawable).bitmap
            }

        }
    }


    private fun saveUser(){

    if(this.imgArray == null){
        Toast.makeText(this@SingUp,"Por favor ingresa una imagen",Toast.LENGTH_LONG).show()
    }else{
        if(nameUser!!.text.isNotBlank() && lastNameUser!!.text.isNotBlank() && emailUser!!.text.isNotBlank() && passUser!!.text.isNotBlank()){

            val cambiarActivity = Intent(this, Login::class.java)
            val encodedString:String =  Base64.getEncoder().encodeToString(this.imgArray)

            val strEncodeImage:String = "data:image/png;base64," + encodedString


            //SE CONSTRUYE EL OBJECTO A ENVIAR,  ESTO DEPENDE DE COMO CONSTRUYAS EL SERVICIO
            // SI TU SERVICIO POST REQUIERE DOS PARAMETROS HACER UN OBJECTO CON ESOS DOS PARAMETROS
            val user =   Usuario(0,
                nameUser!!.text.toString(),
                lastNameUser!!.text.toString(),
                emailUser!!.text.toString(),
                passUser!!.text.toString(),
                strEncodeImage)
            val service: Service =  RestEngine.getRestEngine().create(Service::class.java)
            val result: Call<Int> = service.saveUser(user)

            result.enqueue(object: Callback<Int>{
                override fun onFailure(call: Call<Int>, t: Throwable) {
                    Toast.makeText(this@SingUp,"Error",Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<Int>, response: Response<Int>) {
                    //usuarioDBHelper.addUsuario(nameUser!!.text.toString(),lastNameUser!!.text.toString(),emailUser!!.text.toString(),passUser!!.text.toString())
                    nameUser!!.text = ""
                    lastNameUser!!.text = ""
                    emailUser!!.text = ""
                    passUser!!.text = ""
                    Toast.makeText(this@SingUp,"Guardado",Toast.LENGTH_LONG).show()
                    startActivity(cambiarActivity)
                    finish()
                }
            })


        }
        else{
            Toast.makeText(this,"Ingresa todos los datos",Toast.LENGTH_SHORT).show()
        }


    }






    }

}