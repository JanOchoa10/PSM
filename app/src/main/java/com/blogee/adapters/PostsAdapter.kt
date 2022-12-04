package com.blogee.adapters

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.blogee.ImageUtilities
import com.blogee.models.Nota
import com.blogee.models.Usuario
import com.blogee.R
import com.blogee.RestEngine
import com.blogee.Service
import kotlinx.android.synthetic.main.item_publicacion.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class PostsAdapter(
    private val mContext: Context,
    private val listaPosts: List<Nota>
) : ArrayAdapter<Nota>(mContext, 0, listaPosts) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layout = LayoutInflater.from(mContext).inflate(R.layout.item_publicacion, parent, false)

        val nota = listaPosts[position]

        val service: Service = RestEngine.getRestEngine().create(Service::class.java)
        val result: Call<List<Usuario>> = service.getUser(nota.id_User.toString())

        result.enqueue(object : Callback<List<Usuario>> {
            override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
                Toast.makeText(mContext, "Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<List<Usuario>>,
                response: Response<List<Usuario>>
            ) {
                val item = response.body()
                if (item != null) {
                    if (item.isEmpty()) {
                        Toast.makeText(
                            mContext,
                            "No tiene información",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {

                        var byteArray2: ByteArray? = null
//                            namePerfil!!.text = getString(R.string.name) + ": " + item[0].Name
//                            lastnamePerfil!!.text =
//                                getString(R.string.last_name) + ": " + item[0].LastName
//                            emailPerfil!!.text = getString(R.string.email) + ": " + item[0].Email

                        layout.nombre.text = item[0].Name

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
                            roundedBitmapWrapper.setCircular(true)
                            layout.imgPerfil.setImageDrawable(roundedBitmapWrapper)
                        }
                    }
                } else {
                    Toast.makeText(mContext, "Incorrectas", Toast.LENGTH_LONG).show()
                }


            }
        })


        if (nota.Image != "") {
            var byteArray: ByteArray? = null
//
            val strImage: String =
                nota.Image!!.replace("data:image/png;base64,", "")
            byteArray = Base64.getDecoder().decode(strImage)
//
            var bitmap: Bitmap? = null
//
            if (byteArray != null) {
//            //Bitmap redondo
                bitmap =
                    ImageUtilities.getBitMapFromByteArray(byteArray)
                val roundedBitmapWrapper: RoundedBitmapDrawable =
                    RoundedBitmapDrawableFactory.create(
                        Resources.getSystem(),
                        bitmap
                    )
//            roundedBitmapWrapper.setCircular(true)
                layout.imgNota.setImageDrawable(roundedBitmapWrapper)
//                layout.imgNota.minimumWidth
//                layout.imgNota.setImageBitmap(bitmap)
            }
        }

        layout.titulo.text = nota.Title
        layout.descripcion.text = nota.Description


        return layout
    }

}