package com.patitasalrescate.accesoADatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.patitasalrescate.model.Mascota;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DAOMascota {
    private BDConstruir dbHelper;

    public DAOMascota(Context context) {
        dbHelper = new BDConstruir(context);
    }

    // Insertar mascota
    public long insertar(Mascota mascota) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id_mascota", mascota.getIdMascota());
        values.put("id_refugio", mascota.getIdRefugio());
        values.put("especie", mascota.getEspecie());
        values.put("raza", mascota.getRaza());
        values.put("edad", mascota.getEdad());
        values.put("temperamento", mascota.getTemperamento());
        values.put("historia", mascota.getHistoria());
        // Guardar fotos como string separado por coma
        values.put("fotos", String.join(",", mascota.getFotos()));  // List a string coma-separado para SQLite
        values.put("es_adoptado", mascota.isEsAdoptado() ? 1 : 0);
        values.put("last_sync", mascota.getLastSync());
        return db.insert("mascotas", null, values);
    }

    // Listar todos
    public List<Mascota> listarTodos() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Mascota> mascotas = new ArrayList<>();
        Cursor cursor = db.query("mascotas", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Mascota masc = new Mascota();
                masc.setIdMascota(cursor.getString(cursor.getColumnIndexOrThrow("id_mascota")));
                masc.setIdRefugio(cursor.getString(cursor.getColumnIndexOrThrow("id_refugio")));
                masc.setEspecie(cursor.getString(cursor.getColumnIndexOrThrow("especie")));
                masc.setRaza(cursor.getString(cursor.getColumnIndexOrThrow("raza")));
                masc.setEdad(cursor.getInt(cursor.getColumnIndexOrThrow("edad")));
                masc.setTemperamento(cursor.getString(cursor.getColumnIndexOrThrow("temperamento")));
                masc.setHistoria(cursor.getString(cursor.getColumnIndexOrThrow("historia")));
                List<String> fotosList = new ArrayList<>();
                String fotosStr = cursor.getString(cursor.getColumnIndexOrThrow("fotos"));
                if (fotosStr != null && !fotosStr.isEmpty()) {
                    fotosList = Arrays.asList(fotosStr.split(","));
                }
                masc.setFotos(fotosList);
                masc.setEsAdoptado(cursor.getInt(cursor.getColumnIndexOrThrow("es_adoptado")) == 1);
                masc.setLastSync(cursor.getLong(cursor.getColumnIndexOrThrow("last_sync")));
                mascotas.add(masc);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return mascotas;
    }

    // Actualizar (por ID)
    public int actualizar(Mascota mascota) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("especie", mascota.getEspecie());
        // Agrega los demás fields
        values.put("es_adoptado", mascota.isEsAdoptado() ? 1 : 0);
        values.put("last_sync", mascota.getLastSync());
        return db.update("mascotas", values, "id_mascota = ?", new String[]{mascota.getIdMascota()});
    }

    // Eliminar
    public void eliminar(String idMascota) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("mascotas", "id_mascota = ?", new String[]{idMascota});
    }

    // Buscar por filtro (ej. especie, raza)
    public List<Mascota> buscarPorFiltro(String especie, String raza) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Mascota> mascotas = new ArrayList<>();
        String selection = "especie LIKE ? AND raza LIKE ?";
        String[] args = new String[]{"%" + especie + "%", "%" + raza + "%"};
        Cursor cursor = db.query("mascotas", null, selection, args, null, null, null);
        // Similar a listarTodos
        // ...
        cursor.close();
        return mascotas;
    }

    // Cambiar estado adoptado
    public void cambiarEstadoAdoptado(String idMascota, boolean adoptado) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("es_adoptado", adoptado ? 1 : 0);
        db.update("mascotas", values, "id_mascota = ?", new String[]{idMascota});
    }
}