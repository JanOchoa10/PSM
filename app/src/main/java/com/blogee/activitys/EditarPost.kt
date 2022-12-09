package com.blogee.activitys

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.blogee.ImageUtilities
import com.blogee.R
import com.blogee.RestEngine
import com.blogee.Service
import com.blogee.models.Nota
import com.blogee.models.Usuario
import kotlinx.android.synthetic.main.activity_detalles_nota.*
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_post)

        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)

        val btnPost = findViewById<Button>(R.id.btn_post)
        btnPost.setOnClickListener(this)
        val btnDelete = findViewById<Button>(R.id.btn_delete)
        btnDelete.setOnClickListener(this)
        val btnCancel = findViewById<Button>(R.id.btn_cancel)
        val btnEliminarImg = findViewById<Button>(R.id.btn_EliminarImg)
        btnEliminarImg.setOnClickListener(this)
        val btnCamera = findViewById<Button>(R.id.btn_img)
        btnCamera.setOnClickListener(this)
        title = findViewById<TextView>(R.id.editPostTitle)
        desc = findViewById<TextView>(R.id.editPostDesc)
        imageUI = findViewById<ImageView>(R.id.imageView4)



        val nota = intent.getSerializableExtra("verNota") as Nota
        title!!.text = nota.Title
        desc!!.text = nota.Description

        if(nota.Image != ""){
            var byteArray: ByteArray? = null

            val strImage: String =
                nota.Image!!.replace("data:image/png;base64,", "")
            byteArray = Base64.getDecoder().decode(strImage)

            var bitmap: Bitmap? = null

            if (byteArray != null) {
//            //Bitmap redondo
                bitmap =
                    ImageUtilities.getBitMapFromByteArray(byteArray)
                /*val roundedBitmapWrapper: RoundedBitmapDrawable =
                    RoundedBitmapDrawableFactory.create(
                        Resources.getSystem(),
                        bitmap
                    )*/
                imageUI!!.setImageBitmap(bitmap)
            }
        }else{
            this.imageUI!!.setImageResource(R.mipmap.ic_launcher)
        }




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
                                roundedBitmapWrapper.isCircular = true
//                                imageUI!!.setImageDrawable(roundedBitmapWrapper)
                                miItem5.icon = roundedBitmapWrapper

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

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_post -> GuardarCambios()
            R.id.btn_img -> openCamera()
            R.id.btn_delete -> deletePost()
            R.id.btn_EliminarImg -> deleteImg()
        }
    }

    private fun deleteImg() {
        val nota = intent.getSerializableExtra("verNota") as Nota
        nota.Image=""
        this.imgArray = null
        this.imageUI!!.setImageResource(R.mipmap.ic_launcher)

    }

    private fun deletePost() {
        val nota = intent.getSerializableExtra("verNota") as Nota
        //Toast.makeText(this@EditarPost, nota.id_Nota.toString() , Toast.LENGTH_LONG).show()
        val service2: Service =  RestEngine.getRestEngine().create(Service::class.java)
        val result2: Call<String> = service2.deleteNota(nota.id_Nota.toString())
        val cambiarActivity = Intent(
            this,
            MainActivity::class.java
        ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        result2.enqueue(object: Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(this@EditarPost,"Error",Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<String>, response2: Response<String>) {
                //usuarioDBHelper.addUsuario(nameUser!!.text.toString(),lastNameUser!!.text.toString(),emailUser!!.text.toString(),passUser!!.text.toString())

                Toast.makeText(this@EditarPost,"Nota Eliminada",Toast.LENGTH_LONG).show()

                val idUserLog = Bundle()
                idUserLog.putString("idUserLog", intent.getStringExtra("idUserLog"))

                cambiarActivity.putExtras(idUserLog)
                startActivity(cambiarActivity)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                true
            }
        })
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
    }

    private fun GuardarCambios() {
        val nota = intent.getSerializableExtra("verNota") as Nota
        if(title!!.text.isNotBlank() && desc!!.text.isNotBlank()){
            val strEncodeImage: String
            if (this.imgArray != null) {
                val encodedString: String = Base64.getEncoder().encodeToString(this.imgArray)
                strEncodeImage = "data:image/png;base64," + encodedString
            } else if(nota.Image != "") {
                strEncodeImage = nota.Image.toString()
            }else{
                strEncodeImage = ""
            }


           //Toast.makeText(this@EditarPost,  strEncodeImage, Toast.LENGTH_LONG).show()

            val notaSave = Nota(nota.id_Nota,
                title!!.text.toString(),
                desc!!.text.toString(),
                nota.id_User,
                strEncodeImage)
            //Toast.makeText(this@EditarPost,  notaSave.Image, Toast.LENGTH_LONG).show()
            val service: Service = RestEngine.getRestEngine().create(Service::class.java)
            val result: Call<Int> = service.saveNota(notaSave)

            result.enqueue(object : Callback<Int> {
                override fun onFailure(call: Call<Int>, t: Throwable) {
                    Toast.makeText(this@EditarPost, "Error", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<Int>, response: Response<Int>) {
                    //usuarioDBHelper.addUsuario(nameUser!!.text.toString(),lastNameUser!!.text.toString(),emailUser!!.text.toString(),passUser!!.text.toString())
                    val idUserLog = Bundle()
                    idUserLog.putString("idUserLog", intent.getStringExtra("idUserLog"))
                    Toast.makeText(this@EditarPost, "Guardado", Toast.LENGTH_LONG).show()
                    val intent2 = Intent(
                        applicationContext,
                        DetallesNota::class.java
                    )
                    val notaActual: Nota =
                       notaSave
                    intent2.putExtra("verNota", notaActual)
                    intent2.putExtras(idUserLog)
                    startActivity(intent2)
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    finish()
                }
            })



        }else{

        }

    }
}