package com.example.mytv2
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("your_endpoint_here") // Замените на нужный эндпоинт
    fun getMovieDetails(@Query("id") movieId: String): Call<Movie>
}
