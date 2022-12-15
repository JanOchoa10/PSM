package com.blogee.activitys


import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.*
import android.widget.SearchView.OnQueryTextListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.view.MenuItemCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.blogee.*
import com.blogee.adapters.PostsAdapter
import com.blogee.local.miSQLiteHelper
import com.blogee.models.Nota
import com.blogee.models.Usuario
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class MainActivity : AppCompatActivity(), OnQueryTextListener {
    lateinit var usuarioDBHelper: miSQLiteHelper
    var animando = false
    var abajo = false

    lateinit var swipeRefreshLayout: SwipeRefreshLayout
//    lateinit var textView: TextView
//    var number: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (intent.getStringExtra("idUserLog").toString() != null) {
            publicarNotas()
        }
//        textView.visibility = View.GONE

//        title = "KotlinApp"
        swipeRefreshLayout = findViewById(R.id.swipe)
//        textView = findViewById(R.id.textView)
        swipeRefreshLayout.setOnRefreshListener {
            //Ejecutamos código
//            number++
//            textView.text = " Total number = $number"

            traerNotas()

            Handler().postDelayed(Runnable {
                swipeRefreshLayout.isRefreshing = false
            }, 900)
        }
        usuarioDBHelper = miSQLiteHelper(this)
        val btnfavNewPost = findViewById<FloatingActionButton>(R.id.fab_new_post)

        btnfavNewPost.setOnClickListener {
            val idUserLog = Bundle()
            idUserLog.putString("idUserLog", intent.getStringExtra("idUserLog"))
            val emailUserLog = Bundle()
            emailUserLog.putString("emailUserLog", intent.getStringExtra("emailUserLog"))
            val cambiarActivity = Intent(
                this,
                Post2::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            cambiarActivity.putExtras(idUserLog)
            cambiarActivity.putExtras(emailUserLog)
            startActivity(cambiarActivity)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

        }


        traerNotas()

    }

    private fun publicarNotas() {
        val service: Service = RestEngine.getRestEngine().create(Service::class.java)
        val result: Call<List<Usuario>> =
            service.getUser(intent.getStringExtra("idUserLog").toString())

        result.enqueue(object : Callback<List<Usuario>> {
            override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
//                Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_LONG).show()
                Dialogo.getInstance(this@MainActivity)
                    .crearDialogoSinAccion(
                        this@MainActivity,
                        getString(R.string.dialog_error_de_usuario),
                        getString(R.string.dialog_error_de_usuario_text),
                        getString(R.string.dialog_aceptar)
                    )
            }

            override fun onResponse(
                call: Call<List<Usuario>>,
                response: Response<List<Usuario>>
            ) {
                val item = response.body()
                if (item != null) {
                    var email_User = intent.getStringExtra("emailUserLog")
                    val db = usuarioDBHelper.readableDatabase
                    val c = db.rawQuery(
                        "Select * from notas where emailUser ='" + email_User.toString() + "' and status = 1",
                        null
                    )
                    if (c.moveToFirst()) {

                        do {
                            var strimage = c.getString(4).toString()
                            if (strimage == null)
                                strimage = ""
                            val nota = Nota(
                                0,
                                c.getString(2).toString(),
                                c.getString(3).toString(),
                                item[0].id_User,
                                strimage
                            )
                            val service2: Service =
                                RestEngine.getRestEngine().create(Service::class.java)
                            val result2: Call<Int> = service2.saveNota(nota)

                            result2.enqueue(object : Callback<Int> {
                                override fun onFailure(call: Call<Int>, t: Throwable) {
                                    //Toast.makeText(this@Post2, "Error al publicar nota", Toast.LENGTH_LONG).show()
                                }

                                override fun onResponse(call: Call<Int>, response: Response<Int>) {
                                    //usuarioDBHelper.addUsuario(nameUser!!.text.toString(),lastNameUser!!.text.toString(),emailUser!!.text.toString(),passUser!!.text.toString())


//                                    Toast.makeText(
//                                        this@MainActivity,
//                                        "Publicado",
//                                        Toast.LENGTH_LONG
//                                    ).show()

                                    Dialogo.getInstance(this@MainActivity)
                                        .crearDialogoSinAccion(
                                            this@MainActivity,
                                            getString(R.string.dialog_publicado),
                                            getString(R.string.dialog_publicado_text),
                                            getString(R.string.dialog_aceptar)
                                        )

                                }
                            })


                        } while (c.moveToNext())

                    }
                    if (email_User != null) {
                        usuarioDBHelper.updateNotaPost(email_User)
                    }

                }

            }
        })


    }

    var listaPosts: MutableList<Nota> = mutableListOf()

    fun traerNotas() {

        val service: Service = RestEngine.getRestEngine().create(Service::class.java)
        val result: Call<List<Nota>> = service.getNotas()

        result.enqueue(object : Callback<List<Nota>> {
            override fun onFailure(call: Call<List<Nota>>, t: Throwable) {
//                Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_LONG).show()
                Dialogo.getInstance(this@MainActivity)
                    .crearDialogoSinAccion(
                        this@MainActivity,
                        getString(R.string.dialog_error_de_notas),
                        getString(R.string.dialog_error_de_notas_text),
                        getString(R.string.dialog_aceptar)
                    )
            }

            override fun onResponse(
                call: Call<List<Nota>>,
                response: Response<List<Nota>>
            ) {

                val arrayPosts = response.body()
                if (arrayPosts != null) {
                    if (arrayPosts.isEmpty()) {
//                        Toast.makeText(
//                            this@MainActivity,
//                            "No tiene notas",
//                            Toast.LENGTH_LONG
//                        ).show()

                        Dialogo.getInstance(this@MainActivity)
                            .crearDialogoSinAccion(
                                this@MainActivity,
                                getString(R.string.dialog_no_tiene_notas),
                                getString(R.string.dialog_no_tiene_notas_text),
                                getString(R.string.dialog_aceptar)
                            )
                    } else {
                        //      Visibilidad del texto cuando no hay publicaciones
                        val textoInicial = findViewById<TextView>(R.id.txtNoNotas)
                        textoInicial.visibility = View.GONE

                        listaPosts.clear()
                        for (item in arrayPosts) {
                            listaPosts.add(
                                Nota(
                                    item.id_Nota,
                                    item.Title,
                                    item.Description,
                                    item.id_User,
                                    item.Image
                                )
                            )
//                            getUnUsuario(item.id_User)
                        }


                        // Elementos dentro del listview
                        val lvPost = findViewById<ListView>(R.id.lvPosts)

                        val adaptador: PostsAdapter? = PostsAdapter(this@MainActivity, listaPosts)
                        lvPost.adapter = adaptador

                        lvPost.setOnItemClickListener { parent, view, position, id ->


                            val notaActual: Nota =
                                parent.getItemAtPosition(position) as Nota


                            val idUserLog = Bundle()
                            idUserLog.putString("idUserLog", intent.getStringExtra("idUserLog"))

                            val intent = Intent(
                                this@MainActivity,
                                DetallesNota::class.java
                            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

//                            val imgNota = notaActual.Image

//                            intent.putExtra("stringBlob", imgNota)
//                            notaActual.Image = ""
//                            intent.putExtra("verNota", notaActual)
                            intent.putExtras(idUserLog)
                            intent.putExtra("idDeMiNotaActualClave", notaActual.id_Nota)
                            intent.putExtra("idDeMiUsuarioDeNotaActualClave", notaActual.id_User)

                            startActivity(intent)
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                        }


                        lvPost.setOnScrollListener(object : AbsListView.OnScrollListener {
                            private var lastFirstVisibleItem = 0
                            override fun onScrollStateChanged(
                                view: AbsListView,
                                scrollState: Int
                            ) {
                            }

                            override fun onScroll(
                                view: AbsListView,
                                firstVisibleItem: Int,
                                visibleItemCount: Int,
                                totalItemCount: Int
                            ) {

                                if (firstVisibleItem == 0) {

                                    if (abajo) {
                                        fab_new_post.animate().translationY(0F)
                                            .setInterpolator(LinearInterpolator()).duration =
                                            200 // Cambiar al tiempo deseado
                                        abajo = false
                                    }

                                } else {

                                    if (lastFirstVisibleItem < firstVisibleItem) {
//                                    Toast.makeText(
//                                        applicationContext, "Scrolling down the listView",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
                                        if (!animando) {
                                            animando = true

                                            fab_new_post.animate().translationY(
                                                fab_new_post.height +
                                                        resources.getDimension(R.dimen.fab_margin)
                                            )
                                                .setInterpolator(LinearInterpolator()).duration =
                                                200 // Cambiar al tiempo deseado
                                            Handler().postDelayed({
                                                //doSomethingHere()
                                                animando = false
                                                abajo = true
                                            }, 200)
                                        }

                                    }
                                    if (lastFirstVisibleItem > firstVisibleItem) {
//                                    Toast.makeText(
//                                        applicationContext, "Scrolling up the listView",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
                                        if (!animando) {
                                            animando = true
                                            fab_new_post.animate().translationY(0F)
                                                .setInterpolator(LinearInterpolator()).duration =
                                                200 // Cambiar al tiempo deseado
                                            Handler().postDelayed({
                                                //doSomethingHere()
                                                animando = false
                                                abajo = false
                                            }, 200)
                                        }
                                    }
                                }
                                lastFirstVisibleItem = firstVisibleItem
                            }

                        })

                    }
                } else {
//                    Toast.makeText(this@MainActivity, "No hay notas", Toast.LENGTH_LONG).show()
                    Dialogo.getInstance(this@MainActivity)
                        .crearDialogoSinAccion(
                            this@MainActivity,
                            getString(R.string.dialog_no_tiene_notas),
                            getString(R.string.dialog_no_tiene_notas_text),
                            getString(R.string.dialog_aceptar)
                        )
                }
            }
        })
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
//                    Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_LONG).show()
                    Dialogo.getInstance(this@MainActivity)
                        .crearDialogoSinAccion(
                            this@MainActivity,
                            getString(R.string.dialog_error_de_usuario),
                            getString(R.string.dialog_error_de_usuario_text),
                            getString(R.string.dialog_aceptar)
                        )
                }

                override fun onResponse(
                    call: Call<List<Usuario>>,
                    response: Response<List<Usuario>>
                ) {
                    val item = response.body()
                    if (item != null) {
                        if (item.isEmpty()) {
//                            Toast.makeText(
//                                this@MainActivity,
//                                "No tiene información",
//                                Toast.LENGTH_LONG
//                            ).show()
                            Dialogo.getInstance(this@MainActivity)
                                .crearDialogoSinAccion(
                                    this@MainActivity,
                                    getString(R.string.dialog_error_de_usuario),
                                    getString(R.string.dialog_error_de_usuario_text),
                                    getString(R.string.dialog_aceptar)
                                )
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
                                    ImageUtilities.getBitMapFromByteArray(byteArray!!)
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
//                        Toast.makeText(this@MainActivity, "Incorrectas", Toast.LENGTH_LONG).show()
                        Dialogo.getInstance(this@MainActivity)
                            .crearDialogoSinAccion(
                                this@MainActivity,
                                getString(R.string.dialog_no_login),
                                getString(R.string.dialog_credenciales_incorrectas_text),
                                getString(R.string.dialog_aceptar)
                            )
                    }


                }
            })
        } else {
            var email_User = intent.getStringExtra("emailUserLog")
            val db = usuarioDBHelper.readableDatabase
            val c = db.rawQuery(
                "Select * from usuarios where emailUser ='" + email_User.toString() + "'",
                null
            )
            if (c.moveToFirst()) {
                var byteArray: ByteArray? = null
                val strImage: String =
                    c.getString(5).toString().replace("data:image/png;base64,", "")
                byteArray = Base64.getDecoder().decode(strImage)
                if (byteArray != null) {
                    //Bitmap redondo
                    val bitmap: Bitmap =
                        ImageUtilities.getBitMapFromByteArray(byteArray!!)
                    val roundedBitmapWrapper: RoundedBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(
                            Resources.getSystem(),
                            bitmap
                        )
                    roundedBitmapWrapper.setCircular(true)
                    miItem5.setIcon(roundedBitmapWrapper)

                }
            }

        }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.app_menu_main, menu)
        asignaFotoUsuario(menu)
        val searchItem = menu.findItem(R.id.app_bar_search)
        val searchView: SearchView = MenuItemCompat.getActionView(searchItem) as SearchView
        //permite modificar el hint que el EditText muestra por defecto
        searchView.queryHint = "Buscar"

        searchView.setOnQueryTextListener(this)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.user_profile -> {
                // Acción al presionar el botón
                val idUserLog = Bundle()
                idUserLog.putString("idUserLog", intent.getStringExtra("idUserLog"))
                val emailUserLog = Bundle()
                emailUserLog.putString("emailUserLog", intent.getStringExtra("emailUserLog"))
                val cambiarActivity = Intent(
                    this,
                    VerPerfil::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                cambiarActivity.putExtras(idUserLog)
                cambiarActivity.putExtras(emailUserLog)
                startActivity(cambiarActivity)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }


    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {

            Log.d("Filtro", newText.toString())

            val listaFiltrada = listaPosts.filter {
                it.Title.toString().lowercase(Locale.getDefault()).contains(
                    newText.toString()
                        .lowercase(Locale.getDefault())
                ) || it.Description.toString().lowercase(Locale.getDefault()).contains(
                    newText.toString()
                        .lowercase(Locale.getDefault())
                )
            }
//                Log.d("Filtro", listaFiltrada.toString())

            lvPosts.adapter = PostsAdapter(this@MainActivity, listaFiltrada)

            swipeRefreshLayout = findViewById(R.id.swipe)
//        textView = findViewById(R.id.textView)
            swipeRefreshLayout.setOnRefreshListener {
                //Ejecutamos código
//            number++
//            textView.text = " Total number = $number"

//                traerNotas()

                listaPosts.filter {
                    it.Title.toString().lowercase(Locale.getDefault()).contains(
                        newText.toString()
                            .lowercase(Locale.getDefault())
                    ) || it.Description.toString().lowercase(Locale.getDefault()).contains(
                        newText.toString()
                            .lowercase(Locale.getDefault())
                    )
                }

                lvPosts.adapter = PostsAdapter(this@MainActivity, listaFiltrada)

                Handler().postDelayed(Runnable {
                    swipeRefreshLayout.isRefreshing = false
                }, 200)
            }

        }
        return false
    }

//    override fun onBackPressed() {
////        super.onBackPressed()
//        finishAffinity()
//    }


}