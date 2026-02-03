package com.patitasalrescate.accesoADatos;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.patitasalrescate.model.Mascota;
import com.patitasalrescate.model.Refugio;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SupabaseService {

    private static final String SUPABASE_URL = "https://sjbuliztalqmsquunnsv.supabase.co"; // ← Cambia por tu URL real
    private static final String ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InNqYnVsaXp0YWxxbXNxdXVubnN2Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzAwODMwOTEsImV4cCI6MjA4NTY1OTA5MX0.SmTZWaSdO0OFTmHgM4VeBZyErc1O_MQO1be8pKJahoI"; // ← De Settings > API > anon/public

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    private Request.Builder baseRequest(String endpoint) {
        return new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/" + endpoint)
                .addHeader("apikey", ANON_KEY)
                .addHeader("Authorization", "Bearer " + ANON_KEY)
                .addHeader("Content-Type", "application/json");
    }

    // Ejemplo: Obtener todas las mascotas (PULL)
    public List<Mascota> getMascotas() throws IOException {
        Request request = baseRequest("mascotas?select=*").build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                Type listType = new TypeToken<List<Mascota>>(){}.getType();
                return gson.fromJson(response.body().string(), listType);
            }
        }
        return null;
    }

    // En SupabaseService.java
    public boolean insertarMascota(Mascota mascota) throws IOException {
        String json = gson.toJson(mascota);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));

        Request request = baseRequest("mascotas")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No body";
                android.util.Log.e("SupabaseInsert", "Error " + response.code() + ": " + errorBody);
                android.util.Log.e("SupabaseInsert", "Request JSON enviado: " + json);  // para debug
                return false;
            }
            android.util.Log.d("SupabaseInsert", "Insert OK: " + mascota.getIdMascota());
            return true;
        }
    }


    // Puedes agregar más: update, delete, getRefugios, etc.
}