package com.blogee.models

data class Usuario(
    var id_User: Int? = null,
    var Name: String? = null,
    var LastName: String? = null,
    var Email: String? = null,
    var Password: String? = null,
    var Image: String? = null
) : java.io.Serializable
