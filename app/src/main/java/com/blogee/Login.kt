package com.blogee

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.blogee.Models.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class Login : AppCompatActivity(),View.OnClickListener {
    lateinit var usuarioDBHelper: miSQLiteHelper
    var emailUser:TextView? = null
    var passUser:TextView? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()

        val btnLogin = findViewById<Button>(R.id.btn_login)
        btnLogin.setOnClickListener(this)
        emailUser = findViewById<TextView>(R.id.editTextTextEmailAddress)
        passUser = findViewById<TextView>(R.id.editTextTextPassword)

        usuarioDBHelper = miSQLiteHelper(this)


        //btnLogin.setOnClickListener{
            /* if(emailLogin.text.isNotBlank() && passLogin.text.isNotBlank() ){
                 val args = arrayOf(emailLogin.text.toString(),passLogin.text.toString())
                 val db : SQLiteDatabase = usuarioDBHelper.readableDatabase
                 val cursor = db.rawQuery("Select * From usuarios where emailUser = ? and passUser = ?", args)


                 if(cursor.moveToFirst()){
                     Toast.makeText(this,cursor.getString(1).toString() + cursor.getString(2).toString(),Toast.LENGTH_SHORT).show()
                     val emailUserLog = Bundle()
                     emailUserLog.putString("emailUserLog", emailLogin.text.toString())
                     val cambiarActivity = Intent(this, MainActivity::class.java)
                     cambiarActivity.putExtras(emailUserLog)
                     startActivity(cambiarActivity)
                     finish()
                 }else{
                     Toast.makeText(this,"Usuario no identificado",Toast.LENGTH_SHORT).show()
                 }

             }
             else{
                 Toast.makeText(this,"Comprueba tus datos",Toast.LENGTH_SHORT).show()
             }

             //Toast.makeText(this, "You clicked me.", Toast.LENGTH_SHORT).show()
             var txtEmail = findViewById<TextView>(R.id.editTextTextEmailAddress).text
             Toast.makeText(applicationContext,txtEmail,Toast.LENGTH_SHORT).show()*/
        //}

        // get reference to TextView
        val textSignUp = findViewById<TextView>(R.id.textView5)
        // set on-click listener
        textSignUp.setOnClickListener {
            //Toast.makeText(this@MainActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
            val cambiarActivity = Intent(this, SingUp::class.java)
            startActivity(cambiarActivity)
            finish()
        }

        val myPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val f: Int = myPreferences.getInt(getString(R.string.modo_oscuro), 0)
        if (f == 0) {
            //imageViewCM.setImageResource(R.drawable.ic_filter_hdr_white_24dp);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            if (f == 1) {
                // imageViewCM.setImageResource(R.drawable.ic_filter_hdr_black_24dp);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.btn_login-> login()

        }
    }

    private fun login(){


        if(emailUser!!.text.isNotBlank() && passUser!!.text.isNotBlank()){
            val cambiarActivity = Intent(this, MainActivity::class.java)
            //SE CONSTRUYE EL OBJECTO A ENVIAR,  ESTO DEPENDE DE COMO CONSTRUYAS EL SERVICIO
            // SI TU SERVICIO POST REQUIERE DOS PARAMETROS HACER UN OBJECTO CON ESOS DOS PARAMETROS
            val user =   Usuario(0,
                "",
                "",
                emailUser!!.text.toString(),
                passUser!!.text.toString(),
                "")


            val service: Service =  RestEngine.getRestEngine().create(Service::class.java)
            val result: Call<List<Usuario>> = service.getUserLogin( user)
            //Toast.makeText(this,"Hasta aquí bien",Toast.LENGTH_SHORT).show()
            result.enqueue(object: Callback<List<Usuario>> {
                override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
                    Toast.makeText(this@Login,"Sin conexión a Internet",Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<List<Usuario>>, response: Response<List<Usuario>>) {
                    //usuarioDBHelper.addUsuario(nameUser!!.text.toString(),lastNameUser!!.text.toString(),emailUser!!.text.toString(),passUser!!.text.toString())
                    //emailUser!!.text = ""
                    //passUser!!.text = ""
                    val item =  response.body()
                    if(item!=null){
                        if(item.isEmpty()){
                            Toast.makeText(this@Login,"Credenciales Incorrectas",Toast.LENGTH_LONG).show()
                          }else{
                            Toast.makeText(this@Login,"Bienvenido " + item[0].Name,Toast.LENGTH_LONG).show()
                            val idUserLog = Bundle()

                            idUserLog.putString("idUserLog", item[0].id_User.toString())

                            cambiarActivity.putExtras(idUserLog)
                            startActivity(cambiarActivity)
                            finish()
                        }
                    }else{
                        Toast.makeText(this@Login,"Incorrectas",Toast.LENGTH_LONG).show()
                    }


                }
            })
        }
        else{
            Toast.makeText(this,"Ingresa todos los datos",Toast.LENGTH_SHORT).show()
        }

    }





}