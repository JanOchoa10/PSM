package com.blogee.activitys


import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.util.PatternsCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.blogee.*
import com.blogee.UserApplication.Companion.prefs
import com.blogee.local.miSQLiteHelper
import com.blogee.models.Credenciales
import com.blogee.models.Usuario
import kotlinx.android.synthetic.main.activity_loading.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern


class Login : AppCompatActivity(), View.OnClickListener {
    lateinit var usuarioDBHelper: miSQLiteHelper
    var emailUser: TextView? = null
    var passUser: TextView? = null

    private val getCredenciales: Credenciales = prefs.getCredenciales()
    private val setCredenciales: Credenciales = Credenciales()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        var email: String = getCredenciales.emailGuardado
        var pass: String = getCredenciales.passGuardado

//        myEmailGlobal = email
//        myPassGlobal = pass

        if (email != "" && pass != "") {
            setContentView(R.layout.activity_loading)
            loginGuardado(email, pass)

            fab_new_post2.setOnClickListener {
                val cambiarActivity = Intent(
                    this,
                    Login::class.java
                )
                startActivity(cambiarActivity)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                finish()
            }
            var swipeRefreshLayout2: SwipeRefreshLayout = findViewById(R.id.swipe)
//        textView = findViewById(R.id.textView)
            swipeRefreshLayout2.setOnRefreshListener {
                //Ejecutamos código
//            number++
//            textView.text = " Total number = $number"

//                traerNotas()


                val cambiarActivity = Intent(
                    this,
                    Login::class.java
                )
                startActivity(cambiarActivity)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                finish()

                Handler().postDelayed(Runnable {
                    swipeRefreshLayout2.isRefreshing = false
                }, 200)
            }

        } else {


            setContentView(R.layout.activity_login)
            supportActionBar?.hide()

            val btnLogin = findViewById<Button>(R.id.btn_login)
            btnLogin.setOnClickListener(this)
            emailUser = findViewById<TextView>(R.id.editTextTextEmailAddress)
            passUser = findViewById<TextView>(R.id.editTextTextPassword)

            usuarioDBHelper = miSQLiteHelper(this)

            // get reference to TextView
            val textSignUp = findViewById<TextView>(R.id.tienesCuenta)
            // set on-click listener
            textSignUp.setOnClickListener {
                //Toast.makeText(this@MainActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
                val cambiarActivity = Intent(
                    this,
                    SingUp::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(cambiarActivity)
                overridePendingTransition(R.anim.to_left, R.anim.from_rigth)
                finish()
            }
        }


        val activo: Boolean = getCredenciales.getModoOscuro()

        if (activo) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_login -> validate()
        }
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


    private fun login() {


        if (emailUser!!.text.isNotBlank() && passUser!!.text.isNotBlank()) {
            val cambiarActivity = Intent(
                this,
                MainActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            //SE CONSTRUYE EL OBJECTO A ENVIAR,  ESTO DEPENDE DE COMO CONSTRUYAS EL SERVICIO
            // SI TU SERVICIO POST REQUIERE DOS PARAMETROS HACER UN OBJECTO CON ESOS DOS PARAMETROS
            val user = Usuario(
                0,
                "",
                "",
                emailUser!!.text.toString(),
                passUser!!.text.toString(),
                ""
            )


            val service: Service = RestEngine.getRestEngine().create(Service::class.java)
            val result: Call<List<Usuario>> = service.getUserLogin(user)
            //Toast.makeText(this,"Hasta aquí bien",Toast.LENGTH_SHORT).show()
            result.enqueue(object : Callback<List<Usuario>> {
                override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
                    //Toast.makeText(this@Login, "Sin conexión a Internet", Toast.LENGTH_LONG).show()

                    // Se ejecuta cada 5 segundos o 5000 milisegundos
                    // Vuelve a intentar conectarse
                    /*Handler().postDelayed(Runnable {
                        login()
                    }, 5000)*/
//                    if (usuarioDBHelper.getUsuario(
//                            emailUser!!.text.toString(),
//                            passUser!!.text.toString()
//                        ) == 1
//                    ) {
////                        val emailUserLog = Bundle()
////                        emailUserLog.putString("emailUserLog", emailUser!!.text.toString())
////                        cambiarActivity.putExtras(emailUserLog)
//                        startActivity(cambiarActivity)
//                        overridePendingTransition(R.anim.to_left, R.anim.from_rigth)
//                        finish()
//                    } else {

                    if (isConnectedWifi(this@Login) || isConnectedMobile(this@Login)) {
                        Dialogo.getInstance(this@Login)
                            .crearDialogoSinAccion(
                                this@Login,
                                getString(R.string.dialog_no_login),
                                getString(R.string.dialog_credenciales_incorrectas_text),
                                getString(R.string.dialog_aceptar)
                            )
                    } else {
                        Dialogo.getInstance(this@Login)
                            .crearDialogoSinAccion(
                                this@Login,
                                getString(R.string.dialog_sin_internet),
                                getString(R.string.dialog_sin_internet_text),
                                getString(R.string.dialog_aceptar)
                            )
                    }
//                    }

                }

                override fun onResponse(
                    call: Call<List<Usuario>>,
                    response: Response<List<Usuario>>
                ) {


                    val item = response.body()
                    if (item != null) {
                        if (item.isEmpty()) {
//                            Toast.makeText(
//                                this@Login,
//                                "Credenciales Incorrectas",
//                                Toast.LENGTH_LONG
//                            ).show()
                            Dialogo.getInstance(this@Login)
                                .crearDialogoSinAccion(
                                    this@Login,
                                    getString(R.string.dialog_no_login),
                                    getString(R.string.dialog_credenciales_incorrectas_text),
                                    getString(R.string.dialog_aceptar)
                                )
                        } else {
                            Toast.makeText(
                                this@Login,
                                getString(R.string.dialog_welcome) + " " + item[0].Name,
                                Toast.LENGTH_LONG
                            ).show()


                            if (usuarioDBHelper.getUsuario(
                                    emailUser!!.text.toString(),
                                    passUser!!.text.toString()
                                ) == 0
                            )
                                usuarioDBHelper.addUsuario(
                                    item[0].Name.toString(),
                                    item[0].LastName.toString(),
                                    emailUser!!.text.toString(),
                                    passUser!!.text.toString(),
                                    item[0].Image.toString()
                                )

//                            val idUserLog = Bundle()

//                            idUserLog.putString("idUserLog", item[0].id_User.toString())
//                            val emailUserLog = Bundle()
//                            emailUserLog.putString("emailUserLog", emailUser!!.text.toString())


                            setCredenciales.idUserGuardado = item[0].id_User!!
                            setCredenciales.emailGuardado = emailUser!!.text.toString()
                            setCredenciales.passGuardado = passUser!!.text.toString()
                            val activo: Boolean = getCredenciales.getModoOscuro()
                            setCredenciales.setModoOscuro(activo)
                            //ESTAMOS GRABANDO
                            prefs.saveCredenciales(setCredenciales)


//                            myEditor.putString("idUserLogeado", item[0].id_User.toString())
//                            myEditor.putString("emailLogged", emailUser!!.text.toString())
//                            myEditor.putString("passLogged", passUser!!.text.toString())
//
//                            myEditor.apply()


                            emailUser!!.text = ""
                            passUser!!.text = ""

//                            cambiarActivity.putExtras(idUserLog)
//                            cambiarActivity.putExtras(emailUserLog)
                            startActivity(cambiarActivity)
                            overridePendingTransition(R.anim.to_left, R.anim.from_rigth)
                            finish()

                        }
                    } else {
//                        Toast.makeText(this@Login, "Incorrectas", Toast.LENGTH_LONG).show()
                        Dialogo.getInstance(this@Login)
                            .crearDialogoSinAccion(
                                this@Login,
                                getString(R.string.dialog_no_login),
                                getString(R.string.dialog_credenciales_incorrectas_text),
                                getString(R.string.dialog_aceptar)
                            )
                    }


                }
            })
        } else {
//            Toast.makeText(this, "Ingresa todos los datos", Toast.LENGTH_SHORT).show()
            Dialogo.getInstance(this@Login)
                .crearDialogoSinAccion(
                    this@Login,
                    getString(R.string.dialog_datos_faltantes),
                    getString(R.string.dialog_datos_faltantes_text),
                    getString(R.string.dialog_aceptar)
                )
        }

    }

    private fun loginGuardado(email: String, pass: String) {


        val cambiarActivity = Intent(
            this,
            MainActivity::class.java
        ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        //SE CONSTRUYE EL OBJECTO A ENVIAR,  ESTO DEPENDE DE COMO CONSTRUYAS EL SERVICIO
        // SI TU SERVICIO POST REQUIERE DOS PARAMETROS HACER UN OBJECTO CON ESOS DOS PARAMETROS
        val user = Usuario(
            0,
            "",
            "",
            email,
            pass,
            ""
        )


        val service: Service = RestEngine.getRestEngine().create(Service::class.java)
        val result: Call<List<Usuario>> = service.getUserLogin(user)
        //Toast.makeText(this,"Hasta aquí bien",Toast.LENGTH_SHORT).show()
        result.enqueue(object : Callback<List<Usuario>> {
            override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
//                    setContentView(R.layout.activity_login)
//                Toast.makeText(this@Login, "Sin conexión a Internet", Toast.LENGTH_LONG).show()

                val builder = AlertDialog.Builder(this@Login)
                builder.setIcon(R.drawable.bluebird)
                builder.setTitle(getString(R.string.dialog_sin_internet))
                builder.setMessage(getString(R.string.dialog_sin_internet_text))
                builder.setPositiveButton(getString(R.string.dialog_reintentar)) { dialog, which ->
                    // Se ejecuta cada 5 segundos o 5000 milisegundos
                    // Vuelve a intentar conectarse
                    Handler().postDelayed(Runnable {
                        loginGuardado(email, pass)
                    }, 5000)
                }
                builder.setNeutralButton(getString(R.string.dialog_sin_conexion)) { dialog, which ->
                    val intent = Intent(this@Login, MainActivity::class.java)

//                    val emailUserLog = Bundle()
//                    emailUserLog.putString("emailUserLog", email)
//                    intent.putExtras(emailUserLog)

//                    intent.putExtra("soloPerfil", true)

                    startActivity(intent)

                }
                builder.show()


            }

            override fun onResponse(
                call: Call<List<Usuario>>,
                response: Response<List<Usuario>>
            ) {
                //usuarioDBHelper.addUsuario(nameUser!!.text.toString(),lastNameUser!!.text.toString(),emailUser!!.text.toString(),passUser!!.text.toString())

                val item = response.body()
                if (item != null) {
                    if (item.isEmpty()) {
//                        Toast.makeText(
//                            this@Login,
//                            "Credenciales Incorrectas",
//                            Toast.LENGTH_LONG
//                        ).show()

                        Dialogo.getInstance(this@Login)
                            .crearDialogoSinAccion(
                                this@Login,
                                getString(R.string.dialog_no_login),
                                getString(R.string.dialog_credenciales_incorrectas_text),
                                getString(R.string.dialog_aceptar)
                            )
                    } else {
                        Toast.makeText(
                            this@Login,
                            getString(R.string.dialog_welcome) + " " + item[0].Name,
                            Toast.LENGTH_LONG
                        ).show()

//                        val idUserLog = Bundle()
//                        idUserLog.putString("idUserLog", item[0].id_User.toString())
//                        val emailUserLog = Bundle()
//                        emailUserLog.putString("emailUserLog", item[0].Email.toString())
//                        cambiarActivity.putExtras(idUserLog)
//                        cambiarActivity.putExtras(emailUserLog)
                        startActivity(cambiarActivity)
//                            overridePendingTransition(R.anim.to_left, R.anim.from_rigth)
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                        finish()
                    }
                } else {
//                    Toast.makeText(this@Login, "Incorrectas", Toast.LENGTH_LONG).show()
                    Dialogo.getInstance(this@Login)
                        .crearDialogoSinAccion(
                            this@Login,
                            getString(R.string.dialog_no_login),
                            getString(R.string.dialog_credenciales_incorrectas_text),
                            getString(R.string.dialog_aceptar)
                        )
                }


            }
        })


    }


//    override fun onBackPressed() {
//        finishAffinity()
//    }

    val caracteresEspeciales = "[:punct:]"

    private fun validateEmail(): Boolean {
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
                    + ".{8,}"               // Al menos 8 caracteres
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

    private fun validate() {

        if (!validateEmail()) {
            return
        }

        login()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.app_menu_main, menu)

        val searchItem = menu.findItem(R.id.app_bar_search)
        searchItem.isVisible = false

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.user_profile -> {
                // Acción al presionar el botón

                val cambiarActivity = Intent(
                    this,
                    Login::class.java
                )
                startActivity(cambiarActivity)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}