package com.example.inversorfrequenciaapp;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    public static final String IP_SERVIDOR = "http://192.168.1.69:8080/";
    private static Retrofit retrofit = null;

    public static InversorApi getInversorApi(){
        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(IP_SERVIDOR)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(InversorApi.class);
    }
}
