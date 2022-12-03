package com.blogee

import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.view.MenuItemCompat
import com.blogee.Models.Nota
import com.blogee.Models.Usuario
import com.blogee.adapters.PostsAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val btnfavNewPost = findViewById<FloatingActionButton>(R.id.fab_new_post)

        btnfavNewPost.setOnClickListener {
            val idUserLog = Bundle()
            idUserLog.putString("idUserLog", intent.getStringExtra("idUserLog"))
            val cambiarActivity = Intent(this, Post2::class.java)
            cambiarActivity.putExtras(idUserLog)
            startActivity(cambiarActivity)

        }


        traerNotas()


    }

    fun traerNotas() {
        var listaPosts: MutableList<Nota> = mutableListOf()

        val service: Service = RestEngine.getRestEngine().create(Service::class.java)
        val result: Call<List<Nota>> = service.getNotas()

        result.enqueue(object : Callback<List<Nota>> {
            override fun onFailure(call: Call<List<Nota>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<List<Nota>>,
                response: Response<List<Nota>>
            ) {
                val arrayPosts = response.body()
                if (arrayPosts != null) {
                    if (arrayPosts.isEmpty()) {
                        Toast.makeText(
                            this@MainActivity,
                            "No tiene notas",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        //      Visibilidad del texto cuando no hay publicaciones
                        val textoInicial = findViewById<TextView>(R.id.txtNoNotas)
                        textoInicial.visibility = View.GONE

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

                        val adaptador = PostsAdapter(this@MainActivity, listaPosts)

                        // Elementos dentro del listview
                        val lvPost = findViewById<ListView>(R.id.lvPosts)

                        lvPost.adapter = adaptador

                        lvPost.setOnItemClickListener { parent, view, position, id ->

                            val notaActual: Nota =
                                parent.getItemAtPosition(position) as Nota

                            Toast.makeText(
                                applicationContext,
                                notaActual.Title
                                        + "\n\n" + notaActual.Description
                                        + "\n\n" + notaActual.id_User,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                    }
                } else {
                    Toast.makeText(this@MainActivity, "No hay notas", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    fun asignaFotoUsuario(menu: Menu) {

        var miItem5: MenuItem = menu.findItem(R.id.user_profile)

        var id_User = intent.getStringExtra("idUserLog")
        if(id_User != null){

            val service: Service =  RestEngine.getRestEngine().create(Service::class.java)
            val result: Call<List<Usuario>> = service.getUser( id_User)
            //Toast.makeText(this,"Hasta aquí bien",Toast.LENGTH_SHORT).show()
            result.enqueue(object: Callback<List<Usuario>> {
                override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
                    Toast.makeText(this@MainActivity,"Error",Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<List<Usuario>>, response: Response<List<Usuario>>) {
                    val item =  response.body()
                    if(item!=null){
                        if(item.isEmpty()){
                            Toast.makeText(this@MainActivity,"No tiene información",Toast.LENGTH_LONG).show()
                        }else{
                            var byteArray:ByteArray? = null
//                            nameUser!!.text = item[0].Name
//                            lastNameUser!!.text = item[0].LastName
//                            emailUser!!.text = item[0].Email
//                            passUser!!.text = item[0].Password

                            val strImage:String =  item[0].Image!!.replace("data:image/png;base64,","")
                            byteArray =  Base64.getDecoder().decode(strImage)
                            if(byteArray != null){
                                //Bitmap redondo
                                val bitmap: Bitmap =ImageUtilities.getBitMapFromByteArray(byteArray)
                                val roundedBitmapWrapper: RoundedBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(Resources.getSystem(), bitmap)
                                roundedBitmapWrapper.setCircular(true)
//                                imageUI!!.setImageDrawable(roundedBitmapWrapper)
                                miItem5.setIcon(roundedBitmapWrapper)

                            }
                        }
                    }else{
                        Toast.makeText(this@MainActivity,"Incorrectas",Toast.LENGTH_LONG).show()
                    }


                }
            })
        }else{
            Toast.makeText(this,"Error de usuario", Toast.LENGTH_SHORT).show()
        }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.app_menu_main, menu)

        asignaFotoUsuario(menu)

        val searchItem = menu.findItem(R.id.app_bar_search)
        val searchView: SearchView = MenuItemCompat.getActionView(searchItem) as SearchView
        //permite modificar el hint que el EditText muestra por defecto
        //permite modificar el hint que el EditText muestra por defecto
        searchView.setQueryHint("Buscar")
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener() {
//            fun onQueryTextSubmit(query: String?): Boolean {
//                Toast.makeText(this@MainActivity, R.string.submitted, Toast.LENGTH_SHORT).show()
//                //se oculta el EditText
//                searchView.setQuery("", false)
//                searchView.setIconified(true)
//                return true
//            }
//
//            fun onQueryTextChange(newText: String?): Boolean {
//                textView.setText(newText)
//                return true
//            }
//        })

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
            else -> super.onOptionsItemSelected(item)
        }
    }

}