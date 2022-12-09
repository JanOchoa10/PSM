package com.blogee.activitys

//import com.blogee.Manifest
//import com.blogee.databinding.ActivityMainBinding
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64.DEFAULT
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.webkit.URLUtil.decode
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.blogee.ImageUtilities
import com.blogee.R
import com.blogee.RestEngine
import com.blogee.Service
import com.blogee.models.Nota
import com.blogee.models.NotaG
import com.blogee.models.Usuario
import kotlinx.android.synthetic.main.activity_post2.*
import kotlinx.android.synthetic.main.activity_post2.view.*
import kotlinx.android.synthetic.main.item_publicacion.view.*
import okhttp3.internal.http2.Huffman.decode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.util.*


class Post2 : AppCompatActivity(), View.OnClickListener {
    var titlePost: TextView? = null
    var descPost: TextView? = null
    var imageUI: ImageView? = null

    //    lateinit var imageUI : ImageView
    var imgArray: ByteArray? = null
    var ImgNota: String? = null

    val pickMedia = registerForActivityResult(PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

//    private lateinit var btn_galeria : Button
//    lateinit var imageView3 : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post2)

        titlePost = findViewById(R.id.editText_PostTitle)
        descPost = findViewById(R.id.editText_PostDescrip)
        imageUI = findViewById(R.id.imageView3)
        val btnPost = findViewById<Button>(R.id.btn_PostPost)
        btnPost.setOnClickListener(this)
        val btnPostSave = findViewById<Button>(R.id.btn_PostSave)
        btnPostSave.setOnClickListener(this)
        val btnCam = findViewById<Button>(R.id.btn_PostUpImages)
        btnCam.setOnClickListener(this)
        btn_galeria.setOnClickListener(this)


//        this.imageUI.setImageResource(R.mipmap.ic_launcher)
        notaGuardada()

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

//        btn_galeria = findViewById(R.id.btn_galeria)
//        imageView3 = findViewById(R.id.imageView3)

//        btn_galeria.setOnClickListener {
////            if(PickVisualMedia.isPhotoPickerAvailable())
//            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
//        }
    }

    private fun notaGuardada() {
        val id_UserVP = intent.getStringExtra("idUserLog")


        if (id_UserVP != null) {
            val service: Service = RestEngine.getRestEngine().create(Service::class.java)
            val result: Call<List<NotaG>> = service.getNotaGUser(id_UserVP)

            result.enqueue(object : Callback<List<NotaG>> {
                override fun onFailure(call: Call<List<NotaG>>, t: Throwable) {
                    Toast.makeText(this@Post2, "Error", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(
                    call: Call<List<NotaG>>,
                    response: Response<List<NotaG>>
                ) {
                    val item = response.body()
                    if (item != null) {
                        if (item.isEmpty()) {
                            Toast.makeText(
                                this@Post2,
                                "No hay nota guardada",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {

                            var byteArray3: ByteArray? = null
                            titlePost!!.text = item[0].Title
                            descPost!!.text = item[0].Description
                            if(item[0].Image != ""){
                                ImgNota = item[0].Image
                                val strImage: String =
                                    item[0].Image!!.replace("data:image/png;base64,", "")
                                byteArray3 = Base64.getDecoder().decode(strImage)
                                if (byteArray3 != null) {
                                    //Bitmap redondo
                                    val bitmap: Bitmap =
                                        ImageUtilities.getBitMapFromByteArray(byteArray3)
                                    /*val roundedBitmapWrapper: RoundedBitmapDrawable =
                                        RoundedBitmapDrawableFactory.create(
                                            Resources.getSystem(),
                                            bitmap
                                        )
                                    roundedBitmapWrapper.setCircular(true)*/
                                    imageUI!!.setImageBitmap(bitmap)
                                }
                            }


                        }
                    } else {

                    }


                }
            })
        } else {
            Toast.makeText(this, "Error de usuario", Toast.LENGTH_SHORT).show()
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
            R.id.btn_galeria -> changeImage()
            R.id.btn_PostSave -> savePost()
        }
    }

    private fun savePost() {
        if(titlePost!!.text.isNotBlank() && descPost!!.text.isNotBlank()){
            var id_User = intent.getStringExtra("idUserLog")?.toInt()

            val cambiarActivity = Intent(
                this,
                MainActivity::class.java
            )

            val strEncodeImage:String
            if(this.imgArray != null){
                val encodedString:String =  Base64.getEncoder().encodeToString(this.imgArray)
                strEncodeImage= "data:image/png;base64," + encodedString
            }else{
                strEncodeImage=""
            }

            //Primero borramos la existente
            val service2: Service =  RestEngine.getRestEngine().create(Service::class.java)
            val result2: Call<String> = service2.deleteNotaG(id_User.toString())
            result2.enqueue(object: Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Toast.makeText(this@Post2,"Error",Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<String>, response2: Response<String>) {

                    val item = response2.body()


                }
            })

            val nota =   NotaG(0, titlePost!!.text.toString(),descPost!!.text.toString(),id_User,strEncodeImage)
            val service: Service =  RestEngine.getRestEngine().create(Service::class.java)
            val result: Call<Int> = service.saveNotaG(nota)

            result.enqueue(object: Callback<Int> {
                override fun onFailure(call: Call<Int>, t: Throwable) {
                    Toast.makeText(this@Post2,"Error",Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<Int>, response: Response<Int>) {
                    titlePost!!.text = ""
                    descPost!!.text = ""
                    val idUserLog = Bundle()
                    idUserLog.putString("idUserLog", intent.getStringExtra("idUserLog"))
                    Toast.makeText(this@Post2,"Guardado", Toast.LENGTH_LONG).show()
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
                        Toast.makeText(this@Post2, "Cargando imagen...", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(
                        this@Post2,
                        "Imagen demasiado grande, intente con otra imagen",
                        Toast.LENGTH_LONG
                    ).show()
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

    private fun post() {

        if (titlePost!!.text.isNotBlank() && descPost!!.text.isNotBlank()) {
            var id_User = intent.getStringExtra("idUserLog")?.toInt()

            val cambiarActivity = Intent(
                this,
                MainActivity::class.java
            )

            val strEncodeImage: String
            if (this.imgArray != null) {
                val encodedString: String = Base64.getEncoder().encodeToString(this.imgArray)
                strEncodeImage = "data:image/png;base64," + encodedString
            } else if(ImgNota != "") {
                strEncodeImage = ImgNota.toString()
            }else {
                strEncodeImage = ""
            }

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

                    val service2: Service =  RestEngine.getRestEngine().create(Service::class.java)
                    val result2: Call<String> = service2.deleteNotaG(id_User.toString())
                    result2.enqueue(object: Callback<String> {
                        override fun onFailure(call: Call<String>, t: Throwable) {
                            Toast.makeText(this@Post2,"Error",Toast.LENGTH_LONG).show()
                        }

                        override fun onResponse(call: Call<String>, response2: Response<String>) {
                            //usuarioDBHelper.addUsuario(nameUser!!.text.toString(),lastNameUser!!.text.toString(),emailUser!!.text.toString(),passUser!!.text.toString())

                            val item = response2.body()


                        }
                    })

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


}