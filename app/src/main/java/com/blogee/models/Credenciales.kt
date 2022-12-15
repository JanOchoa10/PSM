package com.blogee.models

class Credenciales: ModoOscuro() {
    var idUserGuardado: Int = 0
    var emailGuardado: String = ""
    var passGuardado: String = ""
}

open class ModoOscuro {
    private var modoOscuroActivo: Boolean = false

    fun getModoOscuro(): Boolean {
        return modoOscuroActivo
    }

    fun setModoOscuro(Activo: Boolean) {
        this.modoOscuroActivo = Activo
    }

}