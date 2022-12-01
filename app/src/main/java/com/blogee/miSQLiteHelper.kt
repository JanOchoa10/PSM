package com.blogee

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class miSQLiteHelper(context: Context) : SQLiteOpenHelper(context, "Blogee.db",null,1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val ordenCreacion = "Create Table usuarios"+
                "(idUser Integer Primary Key Autoincrement, nameUser Text, lastNameUser Text ,emailUser Text, passUser Text)"

        db!!.execSQL(ordenCreacion)

    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        val ordenBorrado = "Drop Table If Exists usuarios"
        db!!.execSQL(ordenBorrado)
        onCreate(db)
    }

    fun addUsuario(nameUser: String, lastNameUser: String, emailUser:String, passUser: String){
        val data = ContentValues()
        data.put("nameUser",nameUser)
        data.put("lastNameUser",lastNameUser)
        data.put("emailUser",emailUser)
        data.put("passUser",passUser)

        val db = this.writableDatabase
        db.insert("usuarios",null,data)
        db.close()
    }


}