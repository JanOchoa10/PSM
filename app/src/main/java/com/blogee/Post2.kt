package com.blogee

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.blogee.Models.Nota
import com.blogee.Models.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.util.*

class Post2 : AppCompatActivity(), View.OnClickListener {
    var titlePost: TextView? = null
    var descPost: TextView? = null
    var imageUI: ImageView? =  null
    var imgArray:ByteArray? =  null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post2)

        titlePost = findViewById<TextView>(R.id.editText_PostTitle)
        descPost = findViewById<TextView>(R.id.editText_PostDescrip)
        imageUI = findViewById<ImageView>(R.id.imageView3)
        val btnPost = findViewById<Button>(R.id.btn_PostPost)
        btnPost.setOnClickListener(this)
        val btnCam = findViewById<Button>(R.id.btn_PostUpImages)
        btnCam.setOnClickListener(this)

        this.imageUI!!.setImageResource(R.mipmap.ic_launcher)


        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.app_menu, menu)
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
            R.id.btn_PostPost-> post()
            R.id.btn_PostUpImages-> openCamera()
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

    private fun post() {

        if(titlePost!!.text.isNotBlank() && descPost!!.text.isNotBlank()){
            var id_User = intent.getStringExtra("idUserLog")?.toInt()

            val cambiarActivity = Intent(this, MainActivity::class.java)

            val strEncodeImage:String
            if(this.imgArray != null){
                val encodedString:String =  Base64.getEncoder().encodeToString(this.imgArray)
                strEncodeImage= "data:image/png;base64," + encodedString
            }else{
                strEncodeImage=""
            }


            //SE CONSTRUYE EL OBJECTO A ENVIAR,  ESTO DEPENDE DE COMO CONSTRUYAS EL SERVICIO
            // SI TU SERVICIO POST REQUIERE DOS PARAMETROS HACER UN OBJECTO CON ESOS DOS PARAMETROS
            val nota =   Nota(0, titlePost!!.text.toString(),descPost!!.text.toString(),id_User,strEncodeImage)
            val service: Service =  RestEngine.getRestEngine().create(Service::class.java)
            val result: Call<Int> = service.saveNota(nota)

            result.enqueue(object: Callback<Int> {
                override fun onFailure(call: Call<Int>, t: Throwable) {
                    Toast.makeText(this@Post2,"Error",Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<Int>, response: Response<Int>) {
                    //usuarioDBHelper.addUsuario(nameUser!!.text.toString(),lastNameUser!!.text.toString(),emailUser!!.text.toString(),passUser!!.text.toString())
                    titlePost!!.text = ""
                    descPost!!.text = ""
                    val idUserLog = Bundle()
                    idUserLog.putString("idUserLog", intent.getStringExtra("idUserLog"))
                    Toast.makeText(this@Post2,"Publicado", Toast.LENGTH_LONG).show()
                    cambiarActivity.putExtras(idUserLog)
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