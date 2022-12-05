package com.blogee.activitys

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
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
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.blogee.ImageUtilities
import com.blogee.R
import com.blogee.RestEngine
import com.blogee.Service
import com.blogee.models.Nota
import com.blogee.models.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.util.*

class Post2 : AppCompatActivity(), View.OnClickListener {
    var titlePost: TextView? = null
    var descPost: TextView? = null
    var imageUI: ImageView? = null
    var imgArray: ByteArray? = null


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

        val btnCancel = findViewById<Button>(R.id.btn_PostCancel)
        btnCancel.setOnClickListener {
            val idUserLog = Bundle()
            idUserLog.putString("idUserLog", intent.getStringExtra("idUserLog"))
            val cambiarActivity = Intent(
                this,
                VerPerfil::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            cambiarActivity.putExtras(idUserLog)
            startActivity(cambiarActivity)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
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

    fun asignaFotoUsuario(menu: Menu) {

        var miItem5: MenuItem = menu.findItem(R.id.user_profile)

        var id_User = intent.getStringExtra("idUserLog")
        if (id_User != null) {

            val service: Service = RestEngine.getRestEngine().create(Service::class.java)
            val result: Call<List<Usuario>> = service.getUser(id_User)
            //Toast.makeText(this,"Hasta aquí bien",Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(applicationContext, "Incorrectas", Toast.LENGTH_LONG).show()
                    }


                }
            })
        } else {
            Toast.makeText(this, "Error de usuario", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.app_menu_main, menu)

        asignaFotoUsuario(menu)

        val searchItem = menu.findItem(R.id.app_bar_search)
        searchItem.isVisible = false

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
        when (v!!.id) {
            R.id.btn_PostPost -> post()
            R.id.btn_PostUpImages -> openCamera()
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

    private fun post() {

        if (titlePost!!.text.isNotBlank() && descPost!!.text.isNotBlank()) {
            var id_User = intent.getStringExtra("idUserLog")?.toInt()

            val cambiarActivity = Intent(
                this,
                MainActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

            val strEncodeImage: String
            if (this.imgArray != null) {
                val encodedString: String = Base64.getEncoder().encodeToString(this.imgArray)
                strEncodeImage = "data:image/png;base64," + encodedString
            } else {
                strEncodeImage = ""
            }


            //SE CONSTRUYE EL OBJECTO A ENVIAR,  ESTO DEPENDE DE COMO CONSTRUYAS EL SERVICIO
            // SI TU SERVICIO POST REQUIERE DOS PARAMETROS HACER UN OBJECTO CON ESOS DOS PARAMETROS
            val nota = Nota(
                0,
                titlePost!!.text.toString(),
                descPost!!.text.toString(),
                id_User,
                strEncodeImage
            )
            val service: Service = RestEngine.getRestEngine().create(Service::class.java)
            val result: Call<Int> = service.saveNota(nota)

            result.enqueue(object : Callback<Int> {
                override fun onFailure(call: Call<Int>, t: Throwable) {
                    Toast.makeText(this@Post2, "Error", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<Int>, response: Response<Int>) {
                    //usuarioDBHelper.addUsuario(nameUser!!.text.toString(),lastNameUser!!.text.toString(),emailUser!!.text.toString(),passUser!!.text.toString())
                    titlePost!!.text = ""
                    descPost!!.text = ""
                    val idUserLog = Bundle()
                    idUserLog.putString("idUserLog", intent.getStringExtra("idUserLog"))
                    Toast.makeText(this@Post2, "Publicado", Toast.LENGTH_LONG).show()
                    cambiarActivity.putExtras(idUserLog)
                    startActivity(cambiarActivity)
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    finish()
                }
            })


        } else {
            Toast.makeText(this, "Ingresa todos los datos", Toast.LENGTH_SHORT).show()
        }
    }


}