package com.example.inversorfrequenciaapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface InversorApi {

    @GET("leitura")
    public Call<String>listar();

    @POST("escrever")
    public Call<Integer>escrever(String login);

}
