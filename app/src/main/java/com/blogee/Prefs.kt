package com.blogee

import android.content.Context
import com.blogee.models.Credenciales

class Prefs(val context: Context) {

    //Constantes
    private val SHARED_NAME = "USERPREFERENCESBLOGEE" //<--PONER EL NOMBRE QUE MEJOR LES PARESCA


    //EL MANEJADOR DE LAS SHARED PREFERENCES
    //VAMOS A DEFINIR EL NOMBRE ARCHIVO XML QUE SE VA A GUARDAR EN NUESTRA CARPETA SHARED_PREFS
    //Y TAMBIEN DEFINIMOS QUIEN PUEDE TENER ACCESO A ES XML EN ESTA CASO ES PRIVADO
    private val managerPrefs = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE)!!

    //FUNCION QUE NOS PERMITE GUARDAR LAS CREDENCIALES
    fun saveCredenciales(credenciales: Credenciales) {
        val editor = managerPrefs.edit()
        editor.putInt("idUserGuardado", credenciales.idUserGuardado)
        editor.putString("emailLogged", credenciales.emailGuardado)
        editor.putString("passLogged", credenciales.passGuardado)
        editor.putBoolean("userModoOscuro", credenciales.getModoOscuro())
        editor.putInt("idNotaActual", credenciales.getIdNotaGuardado())
        editor.putInt("idUserDeNotaActual", credenciales.getIdUserDeNota())
        editor.putBoolean("mensajeLocal", credenciales.getNotasLocal())
        editor.apply()
    }

    //FUNCION QUE PERMITE RECUPERAR LAS CREDENCIALES
    fun getCredenciales(): Credenciales {

        val credential: Credenciales = Credenciales()
        //ENCASO DE QUE NO HAYA DATOS REGRESA UN VALOR POR DEFAULT
        val idUserGuardado: Int = managerPrefs.getInt("idUserGuardado", 0)
        val emailGuardado: String? = managerPrefs.getString(
            "emailLogged",
            ""
        )
        val passGuardado: String? = managerPrefs.getString(
            "passLogged",
            ""
        )
        val userModoOscuro: Boolean = managerPrefs.getBoolean("userModoOscuro", false)
        val idNotaActual: Int = managerPrefs.getInt("idNotaActual", 0)
        val idUserDeNotaActual: Int = managerPrefs.getInt("idUserDeNotaActual", 0)
        val mensajeLocal: Boolean = managerPrefs.getBoolean("mensajeLocal", false)

        credential.idUserGuardado = idUserGuardado
        credential.emailGuardado = emailGuardado!!
        credential.passGuardado = passGuardado!!
        credential.setModoOscuro(userModoOscuro)
        credential.setIdNotaGuardado(idNotaActual)
        credential.setIdUserDeNota(idUserDeNotaActual)
        credential.setNotasLocal(mensajeLocal)

        return credential
    }
}