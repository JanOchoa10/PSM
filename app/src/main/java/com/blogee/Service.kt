package com.blogee

import com.blogee.Models.Nota
import com.blogee.Models.NotaG
import com.blogee.Models.Usuario
import retrofit2.Call
import retrofit2.http.*

//Retrofi usa una interface para hacer la petición hacia el servidor
interface Service{

    //Servicios para consumir
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

    @Headers("Content-Type: application/json")
    @POST("User/Update")
    fun saveEditUser(@Body userData: Usuario):Call<Int>




    //Notas
    @Headers("Content-Type: application/json")
    @POST("Nota/Save")
    fun saveNota(@Body userData: Nota):Call<Int>

    @Headers("Content-Type: application/json")
    @POST("Nota/SaveG")
    fun saveNotaG(@Body userData: NotaG):Call<Int>



    @GET("Nota/Notas")
    fun getNotas():Call<List<Nota>>

    @GET("Nota/Notas/{id}")
    fun getNotaUser(@Path("id") id: String?): Call<List<Nota>>

    @GET("Nota/DeleteNotaG/{id}")
    fun deleteNotaG(@Path("id") id: String?): Call<String>

    @GET("Nota/NotaG/{id}")
    fun getNotaGUser(@Path("id") id: String?): Call<List<NotaG>>

}