package com.patitasalrescate.accesoADatos;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.patitasalrescate.model.Adoptante;
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

    private static final String SUPABASE_URL = "https://sjbuliztalqmsquunnsv.supabase.co";
    private static final String ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InNqYnVsaXp0YWxxbXNxdXVubnN2Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzAwODMwOTEsImV4cCI6MjA4NTY1OTA5MX0.SmTZWaSdO0OFTmHgM4VeBZyErc1O_MQO1be8pKJahoI";

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    private Request.Builder baseRequest(String endpoint) {
        return new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/" + endpoint)
                .addHeader("apikey", ANON_KEY)
                .addHeader("Authorization", "Bearer " + ANON_KEY)
                .addHeader("Content-Type", "application/json");
    }

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

    // Método para registrar adoptante en Supabase
    public boolean insertarAdoptante(Adoptante adoptante) throws IOException {
        // Convertimos el objeto a JSON
        String json = gson.toJson(adoptante);

        // Creamos el cuerpo de la petición
        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));

        // Construimos la petición POST a la tabla 'adoptantes'
        Request request = baseRequest("adoptantes")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No body";
                android.util.Log.e("SupabaseAdoptante", "Error " + response.code() + ": " + errorBody);
                android.util.Log.e("SupabaseAdoptante", "JSON enviado: " + json);
                return false;
            }

            android.util.Log.d("SupabaseAdoptante", "Adoptante insertado OK: " + adoptante.getIdAdoptante());
            return true;
        }
    }

    public String subirFoto(byte[] imagenBytes, String nombreArchivo) {
        // 1. URL específica del Storage
        String urlStorage = SUPABASE_URL + "/storage/v1/object/imagenes-refugio/" + nombreArchivo;

        // 2. Preparamos el cuerpo del archivo
        RequestBody requestBody = RequestBody.create(imagenBytes, MediaType.parse("image/jpeg"));

        // 3. Petición POST
        Request request = new Request.Builder()
                .url(urlStorage)
                .addHeader("apikey", ANON_KEY)
                .addHeader("Authorization", "Bearer " + ANON_KEY)
                .addHeader("Content-Type", "image/jpeg")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // Si subió bien, construimos la URL pública manualmente
                return SUPABASE_URL + "/storage/v1/object/public/imagenes-refugio/" + nombreArchivo;
            } else {
                android.util.Log.e("Supabase", "Error subiendo: " + response.code() + " - " + response.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // Retorna null si falló
    }




    public boolean insertarRefugio(Refugio refugio) throws IOException {
        String json = gson.toJson(refugio);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));

        Request request = baseRequest("refugios")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No body";
                android.util.Log.e("SupabaseRefugio", "Error " + response.code() + ": " + errorBody);
                return false;
            }
            android.util.Log.d("SupabaseRefugio", "Refugio insertado OK");
            return true;
        }
    }
}
