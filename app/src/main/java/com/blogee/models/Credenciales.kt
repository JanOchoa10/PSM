package com.blogee.models

class Credenciales : ModoOscuro() {
    var idUserGuardado: Int = 0
    var emailGuardado: String = ""
    var passGuardado: String = ""
}

open class ModoOscuro : NotaInfo() {
    private var modoOscuroActivo: Boolean = false

    fun getModoOscuro(): Boolean {
        return modoOscuroActivo
    }

    fun setModoOscuro(Activo: Boolean) {
        this.modoOscuroActivo = Activo
    }

}

open class NotaInfo : NotasLocal() {
    private var idNotaGuardado: Int = 0
    private var idUserDeNota: Int = 0

    fun getIdNotaGuardado(): Int {
        return idNotaGuardado
    }

    fun setIdNotaGuardado(ID: Int) {
        this.idNotaGuardado = ID
    }

    fun getIdUserDeNota(): Int {
        return idUserDeNota
    }

    fun setIdUserDeNota(IDUser: Int) {
        this.idUserDeNota = IDUser
    }

}

open class NotasLocal : Filtro() {
    private var primeraVez: Boolean = false

    fun getNotasLocal(): Boolean {
        return primeraVez
    }

    fun setNotasLocal(Activo: Boolean) {
        this.primeraVez = Activo
    }

}

open class Filtro {
    private var filtro: Int = 0

    fun getFiltro(): Int {
        return filtro
    }

    fun setFiltro(filtro: Int) {
        this.filtro = filtro
    }

}