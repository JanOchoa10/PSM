package com.blogee

import com.blogee.Models.Usuario
import retrofit2.Call
import retrofit2.http.*

//Retrofi usa una interface para hacer la petición hacia el servidor
interface Service{

    //Servicios para consumir el Album
    @GET("User/Users")
    fun getUsers():Call<List<Usuario>>

    @GET("User/Users/{id}")
    fun getUser(@Path("id") id: String?): Call<List<Usuario>>

    @Headers("Content-Type: application/json")
    @POST("User/Login")
    fun getUserLogin(@Body userData: Usuario):Call<List<Usuario>>


    @Headers("Content-Type: application/json")
    @POST("User/Save")
    fun saveUser(@Body userData: Usuario):Call<Int>

}