package com.blogee.models

data class Nota(
    var id_Nota: Int? = null,
    var Title: String? = null,
    var Description: String? = null,
    var id_User: Int? = null,
    var Image: String? = null
) : java.io.Serializable
