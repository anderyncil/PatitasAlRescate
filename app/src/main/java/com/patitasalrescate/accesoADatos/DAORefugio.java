package com.patitasalrescate.accesoADatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.patitasalrescate.model.Refugio;

import java.util.ArrayList;
import java.util.List;

public class DAORefugio {
    private BDConstruir dbHelper;

    public DAORefugio(Context context) {
        dbHelper = new BDConstruir(context);
    }

    // 1. INSERTAR
    public long insertar(Refugio refugio) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        // ID es autoincrement, NO se inserta manualmente
        values.put("nombre", refugio.getNombre());
        values.put("direccion", refugio.getDireccion());
        values.put("latitud", refugio.getLatitud());
        values.put("longitud", refugio.getLongitud());
        values.put("correo", refugio.getCorreo());
        values.put("password", refugio.getPassword());
        values.put("num_celular", refugio.getNumCelular());
        values.put("foto", refugio.getFotoUrl());
        values.put("last_sync", refugio.getLastSync());

        return db.insert("refugios", null, values);
    }

    // 2. LISTAR TODOS
    public List<Refugio> listarTodos() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Refugio> refugios = new ArrayList<>();
        Cursor cursor = db.query("refugios", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Refugio ref = new Refugio();
                // IMPORTANTE: Leer el ID como int
                ref.setId_refugio(cursor.getInt(cursor.getColumnIndexOrThrow("id_refugio")));

                ref.setNombre(cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
                ref.setDireccion(cursor.getString(cursor.getColumnIndexOrThrow("direccion")));
                ref.setLatitud(cursor.getDouble(cursor.getColumnIndexOrThrow("latitud")));
                ref.setLongitud(cursor.getDouble(cursor.getColumnIndexOrThrow("longitud")));
                ref.setCorreo(cursor.getString(cursor.getColumnIndexOrThrow("correo")));
                ref.setPassword(cursor.getString(cursor.getColumnIndexOrThrow("password")));
                ref.setNumCelular(cursor.getString(cursor.getColumnIndexOrThrow("num_celular")));
                ref.setFotoUrl(cursor.getString(cursor.getColumnIndexOrThrow("foto")));
                ref.setLastSync(cursor.getLong(cursor.getColumnIndexOrThrow("last_sync")));

                refugios.add(ref);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return refugios;
    }

    // 3. LOGIN
    public Refugio login(String correo, String passwordEncriptada) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("refugios", null, "correo = ? AND password = ?",
                new String[]{correo, passwordEncriptada}, null, null, null);

        if (cursor.moveToFirst()) {
            Refugio ref = new Refugio();
            // Leer ID como int
            ref.setId_refugio(cursor.getInt(cursor.getColumnIndexOrThrow("id_refugio")));
            ref.setNombre(cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
            ref.setCorreo(cursor.getString(cursor.getColumnIndexOrThrow("correo")));
            ref.setFotoUrl(cursor.getString(cursor.getColumnIndexOrThrow("foto")));

            cursor.close();
            return ref;
        }
        cursor.close();
        return null;
    }

    // 4. ACTUALIZAR
    public int actualizar(Refugio refugio) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", refugio.getNombre());
        values.put("direccion", refugio.getDireccion());
        values.put("num_celular", refugio.getNumCelular()); // Agregué celular por si acaso
        values.put("foto", refugio.getFotoUrl()); // Agregué foto por si la cambian
        values.put("last_sync", refugio.getLastSync());

        return db.update("refugios", values, "id_refugio = ?",
                new String[]{String.valueOf(refugio.getId_refugio())});
    }


    public void eliminar(int idRefugio) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("refugios", "id_refugio = ?", new String[]{String.valueOf(idRefugio)});
    }
}