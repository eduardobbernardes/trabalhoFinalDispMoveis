package com.example.inversorfrequenciaapp;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public final class RetrofitClient {
    private RetrofitClient() { }

    public static InversorApi criarApi(String enderecoBase) {
        String base = enderecoBase.endsWith("/")
                ? enderecoBase : enderecoBase + "/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        return retrofit.create(InversorApi.class);
    }
}
