package com.blogee

import android.content.Context
import androidx.appcompat.app.AlertDialog

//import android.app.Application

open class SingletonDialog<out T, in A>(private val constructor: (A) -> T) {

    @Volatile
    private var instance: T? = null

    fun getInstance(arg: A): T {
        return when {
            instance != null -> instance!!
            else -> synchronized(this) {
                if (instance == null) instance = constructor(arg)
                instance!!
            }
        }
    }
}

class Dialogo private constructor(context: Context) {

    fun crearDialogoSinAccion(
        mContext: Context,
        title: String,
        message: String,
        textPositiveButton: String
    ) {
        val builder = AlertDialog.Builder(mContext)
        builder.setIcon(R.drawable.bluebird)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(textPositiveButton, null)
        builder.show()
    }

    fun crearDialogoConAccion(
        mContext: Context,
        title: String,
        message: String,
        textPositiveButton: String,
        textNeutralButton: String
    ) {
        val builder = AlertDialog.Builder(mContext)
        builder.setIcon(R.drawable.bluebird)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(textPositiveButton) { dialog, which ->

        }
        builder.setNeutralButton(textNeutralButton, null)
        builder.show()
    }

    fun crearDialogoConDobleAccion(
        mContext: Context,
        title: String,
        message: String,
        textPositiveButton: String,
        textNeutralButton: String,
        textNegativeButton: String,
        accion1: Unit,
        accion2: Unit,
    ) {
        val builder = AlertDialog.Builder(mContext)
        builder.setIcon(R.drawable.bluebird)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(textPositiveButton) { dialog, which ->
//            accion1
        }
        builder.setNeutralButton(textNeutralButton, null)
        builder.setNegativeButton(textNegativeButton) { dialog, which ->
//            accion2
        }
        builder.show()
    }

    companion object : SingletonDialog<Dialogo, Context>(::Dialogo)
}