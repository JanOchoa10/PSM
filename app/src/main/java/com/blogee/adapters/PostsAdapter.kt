package com.blogee.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.blogee.*
import com.blogee.activitys.JustTextListItemView
import com.blogee.activitys.NotaViewModel
import com.blogee.activitys.WithThumbnailListItemView
import com.blogee.models.Nota
import kotlinx.android.synthetic.main.item_publicacion.view.*
import java.util.*


class PostsAdapter(
    private val mContext: Context,
    private var listaPosts: List<Nota>
) : ArrayAdapter<Nota>(mContext, 0, listaPosts) {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layout = LayoutInflater.from(mContext).inflate(R.layout.item_publicacion, parent, false)

        val nota = listaPosts[position]

        return if (nota.Image != "") {
            WithThumbnailListItemView(NotaViewModel(nota), layout).render()
        } else {
            JustTextListItemView(NotaViewModel(nota), layout).render()
        }
    }

}