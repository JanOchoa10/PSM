package com.blogee.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.blogee.models.Usuario

class miSQLiteHelper(context: Context) : SQLiteOpenHelper(context, "Blogee.db", null, 5) {
    override fun onCreate(db: SQLiteDatabase?) {
        val ordenCreacion = "Create Table usuarios" +
                "(idUser Integer Primary Key Autoincrement, nameUser Text, lastNameUser Text ,emailUser Text, passUser Text, image Blob)"
        db!!.execSQL(ordenCreacion)
        val ordenCreacion2 = "Create Table notas" +
                "(idNota Integer Primary Key Autoincrement, emailUser Text, title Text ,description Text, image Blob, status Integer)"
        db.execSQL(ordenCreacion2)

    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        val ordenBorrado = "Drop Table If Exists usuarios"
        db!!.execSQL(ordenBorrado)
        val ordenBorrado2 = "Drop Table If Exists notas"
        db.execSQL(ordenBorrado2)
        onCreate(db)
    }

    fun addUsuario(
        nameUser: String,
        lastNameUser: String,
        emailUser: String,
        passUser: String,
        imageUser: String
    ): Long {
        val db = this.writableDatabase
        val data = ContentValues()
        data.put("nameUser", nameUser)
        data.put("lastNameUser", lastNameUser)
        data.put("emailUser", emailUser)
        data.put("passUser", passUser)
        data.put("image", imageUser)
        val success = db.insert("usuarios", null, data)
        db.close()
        return success
    }

    fun getUsuario(emailU: String, passU: String): Int {
        var r = 0
        val db = this.readableDatabase
        val c = db.rawQuery(
            "Select * from usuarios where emailUser ='$emailU' and passUser= '$passU'",
            null
        )
        if (c.moveToFirst())
            r = 1

        return r
    }

    fun updateUser(user: Usuario, emailUser: String): Int {
        val db = this.writableDatabase
        val data = ContentValues()
        data.put("nameUser", user.Name)
        data.put("lastNameUser", user.LastName)
        data.put("emailUser", user.Email)
        data.put("passUser", user.Password)
        data.put("image", user.Image)
        val data2 = ContentValues()
        data2.put("emailUser", user.Email)
        db.update("notas", data2, "emailUser= '$emailUser' ", null)
        val success =
            db.update("usuarios", data, "emailUser= '$emailUser' ", null)
        db.close()
        return success
    }

    fun addNota(
        title: String,
        description: String,
        image: String,
        emailUser: String,
        status: Int
    ): Long {
        val db = this.writableDatabase
        val data = ContentValues()
        data.put("emailUser", emailUser)
        data.put("title", title)
        data.put("description", description)
        data.put("image", image)
        data.put("status", status.toString())
        val success = db.insert("notas", null, data)
        db.close()
        return success
    }

    fun deleteNotaSaved(email: String) {
        val db = this.writableDatabase
        db.delete("notas", "emailUser='$email' and status = 2", null)
        db.close()
    }

    fun updateNotaPost(emailUser: String): Int {
        val db = this.writableDatabase
        val data = ContentValues()
        data.put("status", 0)
        //val success = db.rawQuery("Update notas set status = 0 where emailUser = '"+emailUser+"' and status = 1",null)
        val success =
            db.update("notas", data, "emailUser= '$emailUser' and status = 1", null)
        db.close()
        return success
    }

    fun deleteTablaNotas() {
        val db = this.writableDatabase
        db.delete("notas", "status != 2", null)
        db.close()
    }


}