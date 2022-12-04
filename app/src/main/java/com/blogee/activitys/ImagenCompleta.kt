package com.blogee.activitys

import android.content.res.Resources
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.blogee.ImageUtilities
import com.blogee.R
import com.blogee.models.Nota
import kotlinx.android.synthetic.main.activity_detalles_nota.*
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


        val nota = intent.getSerializableExtra("verNota") as Nota

        val miImagen = findViewById<ImageView>(R.id.imagenFullScreen)

        var byteArray: ByteArray? = null

        val strImage: String =
            nota.Image!!.replace("data:image/png;base64,", "")
        byteArray = Base64.getDecoder().decode(strImage)

        var bitmap: Bitmap? = null

        if (byteArray != null) {
//            //Bitmap redondo
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