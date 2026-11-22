package com.example.inversorfrequenciaapp;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface InversorApi {
    @GET("./")
    Call<String> listar();

    @POST("./")
    Call<ResponseBody> escrever(@Body String texto);
}
