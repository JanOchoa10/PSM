package com.blogee.activitys

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.blogee.ImageUtilities
import com.blogee.RestEngine
import com.blogee.Service
import com.blogee.UserApplication
import com.blogee.local.miSQLiteHelper
import com.blogee.models.Credenciales
import com.blogee.models.Nota
import com.blogee.models.Usuario
import kotlinx.android.synthetic.main.item_publicacion.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

//  ABSTRACCIONES
abstract class ListItemView {
    val viewModel: IViewModel // <- ESTO ES EL BRIDGE!
    open val layout: View? = null

    constructor(viewModel: IViewModel) {
        this.viewModel = viewModel
    }

    open fun render(): View {
        Log.i("bridge", "default abstract render")

        return layout!!
    }
}

interface IViewModel {
    fun title(): String
    fun description(): String
    fun image(): String
    fun idNota(): Int
    fun idUserNota(): Int
    fun traerFotoPerfil(
        context: Context,
        getCredenciales: Credenciales,
        nombre: TextView,
        imgPerfil: ImageView
    )
}

// CONCRECIONES
class WithThumbnailListItemView(viewModel: IViewModel, override val layout: View) :
    ListItemView(viewModel) {

    private val getCredenciales: Credenciales = UserApplication.prefs.getCredenciales()
    private val setCredenciales: Credenciales = Credenciales()

    override fun render(): View {

        this.viewModel.traerFotoPerfil(
            layout.context,
            getCredenciales,
            layout.nombre,
            layout.imgPerfil
        )

        layout.titulo.text = this.viewModel.title()
        layout.descripcion.text = this.viewModel.description()

        val byteArray: ByteArray?
        val strImage: String =
            this.viewModel.image().replace("data:image/png;base64,", "")
        byteArray = Base64.getDecoder().decode(strImage)

        val bitmap: Bitmap?

        if (byteArray != null) {
            bitmap =
                ImageUtilities.getBitMapFromByteArray(byteArray)
            val roundedBitmapWrapper: RoundedBitmapDrawable =
                RoundedBitmapDrawableFactory.create(
                    Resources.getSystem(),
                    bitmap
                )
            layout.imgNota.setImageDrawable(roundedBitmapWrapper)
        }

        layout.imgNota.setOnClickListener {

            val intent = Intent(
                layout.context,
                ImagenCompleta::class.java
            ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            setCredenciales.idUserGuardado = getCredenciales.idUserGuardado
            setCredenciales.emailGuardado = getCredenciales.emailGuardado
            setCredenciales.passGuardado = getCredenciales.passGuardado

            setCredenciales.setIdNotaGuardado(this.viewModel.idNota())
            setCredenciales.setIdUserDeNota(this.viewModel.idUserNota())

            val activo: Boolean = getCredenciales.getModoOscuro()
            setCredenciales.setModoOscuro(activo)

            UserApplication.prefs.saveCredenciales(setCredenciales)

            startActivity(layout.context, intent, null)

        }

        return layout
    }
}

class JustTextListItemView(viewModel: IViewModel, override val layout: View) :
    ListItemView(viewModel) {

    private val getCredenciales: Credenciales = UserApplication.prefs.getCredenciales()

    override fun render(): View {

        this.viewModel.traerFotoPerfil(
            layout.context,
            getCredenciales,
            layout.nombre,
            layout.imgPerfil
        )

        layout.titulo.text = this.viewModel.title()
        layout.descripcion.text = this.viewModel.description()

        return layout
    }
}

class NotaViewModel : IViewModel {
    val nota: Nota

    constructor(nota: Nota) {
        this.nota = nota
    }

    override fun title(): String {
        return this.nota.Title.toString()
    }

    override fun description(): String {
        return this.nota.Description.toString()
    }

    override fun image(): String {
        return this.nota.Image.toString()
    }

    override fun idNota(): Int {
        return this.nota.id_Nota!!
    }

    override fun idUserNota(): Int {
        return this.nota.id_User!!
    }

    override fun traerFotoPerfil(
        context: Context,
        getCredenciales: Credenciales,
        nombre: TextView,
        imgPerfil: ImageView
    ) {
        lateinit var usuarioDBHelper: miSQLiteHelper
        val service: Service = RestEngine.getRestEngine().create(Service::class.java)
        val result: Call<List<Usuario>> = service.getUser(nota.id_User.toString())

        result.enqueue(object : Callback<List<Usuario>> {
            @SuppressLint("Recycle")
            override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {

                usuarioDBHelper = miSQLiteHelper(context)


                val emailUser = getCredenciales.emailGuardado


                val db = usuarioDBHelper.readableDatabase
                val c = db.rawQuery(
                    "Select * from usuarios where emailUser ='$emailUser'",
                    null
                )
                if (c.moveToFirst()) {
                    val byteArray5: ByteArray?
                    nombre.text = c.getString(1).toString()

                    val strImage: String =
                        c.getString(5).toString().replace("data:image/png;base64,", "")
                    byteArray5 = Base64.getDecoder().decode(strImage)
                    if (byteArray5 != null) {
                        //Bitmap redondo
                        val bitmap: Bitmap =
                            ImageUtilities.getBitMapFromByteArray(byteArray5)
                        val roundedBitmapWrapper: RoundedBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(
                                Resources.getSystem(),
                                bitmap
                            )
                        roundedBitmapWrapper.isCircular = true
                        imgPerfil.setImageDrawable(roundedBitmapWrapper)
                    }

                }


            }

            override fun onResponse(
                call: Call<List<Usuario>>,
                response: Response<List<Usuario>>
            ) {
                val item = response.body()
                if (item != null) {
                    if (item.isEmpty()) {
                        Toast.makeText(
                            context,
                            "No tiene información del adaptador",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {

                        val byteArray2: ByteArray?

                        nombre.text = item[0].Name

                        val strImage: String =
                            item[0].Image!!.replace("data:image/png;base64,", "")
                        byteArray2 = Base64.getDecoder().decode(strImage)
                        if (byteArray2 != null) {
                            //Bitmap redondo
                            val bitmap: Bitmap =
                                ImageUtilities.getBitMapFromByteArray(byteArray2)
                            val roundedBitmapWrapper: RoundedBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(
                                    Resources.getSystem(),
                                    bitmap
                                )
                            roundedBitmapWrapper.isCircular = true
                            imgPerfil.setImageDrawable(roundedBitmapWrapper)
                        }
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Credenciales del usuario incorrectas en el adaptador",
                        Toast.LENGTH_LONG
                    ).show()
                }


            }
        })
    }

}