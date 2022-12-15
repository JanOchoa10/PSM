package com.blogee.activitys

import android.content.res.Resources
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.blogee.*
import com.blogee.models.Nota
import kotlinx.android.synthetic.main.activity_detalles_nota.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ImagenCompleta : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()
        setContentView(R.layout.activity_imagen_completa)

        val numeroNota = intent.getSerializableExtra("idDeMiNotaActualClave")


        val serviceNota: Service = RestEngine.getRestEngine().create(Service::class.java)
        val resultNota: Call<List<Nota>> = serviceNota.getNotas()

        resultNota.enqueue(object : Callback<List<Nota>> {
            override fun onFailure(call: Call<List<Nota>>, t: Throwable) {
//                Toast.makeText(this@ImagenCompleta, "Error", Toast.LENGTH_LONG).show()
                Dialogo.getInstance(this@ImagenCompleta)
                    .crearDialogoSinAccion(
                        this@ImagenCompleta,
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
//                            this@ImagenCompleta,
//                            "No tiene notas",
//                            Toast.LENGTH_LONG
//                        ).show()

                        Dialogo.getInstance(this@ImagenCompleta)
                            .crearDialogoSinAccion(
                                this@ImagenCompleta,
                                getString(R.string.dialog_no_tiene_notas),
                                getString(R.string.dialog_no_tiene_notas_text),
                                getString(R.string.dialog_aceptar)
                            )
                    } else {

                        for (itemNota in arrayPosts) {
                            if (itemNota.id_Nota == numeroNota) {


                                val miImagen = findViewById<ImageView>(R.id.imagenFullScreen)

                                var byteArray: ByteArray? = null

                                val strImage: String =
                                    itemNota.Image!!.replace("data:image/png;base64,", "")
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
                                    miImagen.setImageDrawable(roundedBitmapWrapper)
                                }

                            }
                        }

                    }
                } else {
//                    Toast.makeText(this@ImagenCompleta, "No hay notas", Toast.LENGTH_LONG).show()
                    Dialogo.getInstance(this@ImagenCompleta)
                        .crearDialogoSinAccion(
                            this@ImagenCompleta,
                            getString(R.string.dialog_no_tiene_notas),
                            getString(R.string.dialog_no_tiene_notas_text),
                            getString(R.string.dialog_aceptar)
                        )
                }
            }
        })

    }
}