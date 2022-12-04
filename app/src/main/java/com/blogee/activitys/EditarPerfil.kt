package com.blogee.activitys

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.blogee.ImageUtilities
import com.blogee.R
import com.blogee.RestEngine
import com.blogee.Service
import com.blogee.models.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.util.*


class EditarPerfil : AppCompatActivity(), View.OnClickListener {
    @SuppressLint("MissingInflatedId")
    var nameUser: TextView? = null
    var lastNameUser: TextView? = null
    var emailUser: TextView? = null
    var passUser: TextView? = null
    var imageUI:ImageView? =  null
    var imgArray:ByteArray? =  null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_perfil)

        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)

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

        btnCancel.setOnClickListener{
            val idUserLog = Bundle()
            idUserLog.putString("idUserLog", intent.getStringExtra("idUserLog"))
            val cambiarActivity = Intent(this, VerPerfil::class.java)
            cambiarActivity.putExtras(idUserLog)
            startActivity(cambiarActivity)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

    }

    private fun infoUserEditar() {
        var id_User = intent.getStringExtra("idUserLog")
        if(id_User != null){

            val service: Service =  RestEngine.getRestEngine().create(Service::class.java)
            val result: Call<List<Usuario>> = service.getUser( id_User)
            //Toast.makeText(this,"Hasta aquí bien",Toast.LENGTH_SHORT).show()
            result.enqueue(object: Callback<List<Usuario>> {
                override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
                    Toast.makeText(this@EditarPerfil,"Error",Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<List<Usuario>>, response: Response<List<Usuario>>) {
                    val item =  response.body()
                    if(item!=null){
                        if(item.isEmpty()){
                            Toast.makeText(this@EditarPerfil,"No tiene información",Toast.LENGTH_LONG).show()
                        }else{
                            var byteArray:ByteArray? = null
                            nameUser!!.text = item[0].Name
                            lastNameUser!!.text = item[0].LastName
                            emailUser!!.text = item[0].Email
                            passUser!!.text = item[0].Password

                            val strImage:String =  item[0].Image!!.replace("data:image/png;base64,","")
                            byteArray =  Base64.getDecoder().decode(strImage)
                            if(byteArray != null){
                                //Bitmap redondo
                                val bitmap:Bitmap = ImageUtilities.getBitMapFromByteArray(byteArray)
                                val roundedBitmapWrapper: RoundedBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(Resources.getSystem(), bitmap)
                                roundedBitmapWrapper.setCircular(true)
                                imageUI!!.setImageDrawable(roundedBitmapWrapper)
                            }
                        }
                    }else{
                        Toast.makeText(this@EditarPerfil,"Incorrectas",Toast.LENGTH_LONG).show()
                    }


                }
            })
        }else{
            Toast.makeText(this,"Error de usuario", Toast.LENGTH_SHORT).show()
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
            R.id.btn_guardar_cambios -> GuardarCambios()
            R.id.btnCamera2 -> openCamera()
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



    private fun GuardarCambios() {
        if(nameUser!!.text.isNotBlank() && lastNameUser!!.text.isNotBlank() && emailUser!!.text.isNotBlank() && passUser!!.text.isNotBlank()){
            var id_User = intent.getStringExtra("idUserLog")?.toInt()
            val cambiarActivity = Intent(this, VerPerfil::class.java)
            val strEncodeImage:String
            if(this.imgArray != null){
                val encodedString:String =  Base64.getEncoder().encodeToString(this.imgArray)
                strEncodeImage= "data:image/png;base64," + encodedString
            }else{
                strEncodeImage=""
            }




            //nameUser!!.text=strEncodeImage

            //SE CONSTRUYE EL OBJECTO A ENVIAR,  ESTO DEPENDE DE COMO CONSTRUYAS EL SERVICIO
            // SI TU SERVICIO POST REQUIERE DOS PARAMETROS HACER UN OBJECTO CON ESOS DOS PARAMETROS
           val user =   Usuario(id_User,
                nameUser!!.text.toString(),
                lastNameUser!!.text.toString(),
                emailUser!!.text.toString(),
                passUser!!.text.toString(),
                strEncodeImage)
            val service: Service =  RestEngine.getRestEngine().create(Service::class.java)
            val result: Call<Int> = service.saveUser(user)

            result.enqueue(object: Callback<Int> {
                override fun onFailure(call: Call<Int>, t: Throwable) {
                    Toast.makeText(this@EditarPerfil,"Error", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<Int>, response: Response<Int>) {
                    //usuarioDBHelper.addUsuario(nameUser!!.text.toString(),lastNameUser!!.text.toString(),emailUser!!.text.toString(),passUser!!.text.toString())
                    nameUser!!.text = ""
                    lastNameUser!!.text = ""
                    emailUser!!.text = ""
                    passUser!!.text = ""
                    val idUserLog = Bundle()
                    idUserLog.putString("idUserLog", intent.getStringExtra("idUserLog"))
                    Toast.makeText(this@EditarPerfil,"Guardado", Toast.LENGTH_LONG).show()
                    cambiarActivity.putExtras(idUserLog)
                    startActivity(cambiarActivity)
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    finish()
                }
            })


        }
        else{
            Toast.makeText(this,"Ingresa todos los datos", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
//                onBackPressed()
        val idUserLog = Bundle()
        idUserLog.putString("idUserLog", intent.getStringExtra("idUserLog"))
        val cambiarActivity = Intent(this, VerPerfil::class.java)
        cambiarActivity.putExtras(idUserLog)
        startActivity(cambiarActivity)
        overridePendingTransition(R.anim.from_left, R.anim.to_right)
        return false
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.app_menu_editar_perfil, menu)

        val item: MenuItem = menu.findItem(R.id.dark_mode)

        val myPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val f = myPreferences.getInt(getString(R.string.modo_oscuro), 0)


        if (f == 0) {
            //Toast.makeText(this,"Modo oscuro activado", Toast.LENGTH_SHORT).show()
            item.setIcon(R.drawable.ic_baseline_dark_mode_24)
        } else {
            if (f == 1) {
                //Toast.makeText(this,"Modo oscuro desactivado", Toast.LENGTH_SHORT).show()
                item.setIcon(R.drawable.ic_baseline_light_mode_24)
            }
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

                val myPreferences = PreferenceManager.getDefaultSharedPreferences(this)
                val myEditor = myPreferences.edit()
                val f = myPreferences.getInt(getString(R.string.modo_oscuro), 0)



                if (f == 0) {
                    myEditor.putInt(getString(R.string.modo_oscuro), 1)
                    myEditor.apply()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

                } else {
                    if (f == 1) {
                        myEditor.putInt(getString(R.string.modo_oscuro), 0)
                        myEditor.apply()
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


                        //Toast.makeText(this,"Modo oscuro desactivado", Toast.LENGTH_SHORT).show()

                    }
                }


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