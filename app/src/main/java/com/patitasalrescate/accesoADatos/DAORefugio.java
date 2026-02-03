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

    // Insertar refugio
    public long insertar(Refugio refugio) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id_refugio", refugio.getIdRefugio());
        values.put("nombre", refugio.getNombre());
        values.put("direccion", refugio.getDireccion());
        values.put("latitud", refugio.getLatitud());
        values.put("longitud", refugio.getLongitud());
        values.put("correo", refugio.getCorreo());
        values.put("num_celular", refugio.getNumCelular());
        values.put("last_sync", refugio.getLastSync());
        return db.insert("refugios", null, values);
    }

    // Listar todos
    public List<Refugio> listarTodos() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Refugio> refugios = new ArrayList<>();
        Cursor cursor = db.query("refugios", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Refugio ref = new Refugio();
                ref.setIdRefugio(cursor.getString(cursor.getColumnIndexOrThrow("id_refugio")));
                ref.setNombre(cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
                ref.setDireccion(cursor.getString(cursor.getColumnIndexOrThrow("direccion")));
                ref.setLatitud(cursor.getDouble(cursor.getColumnIndexOrThrow("latitud")));
                ref.setLongitud(cursor.getDouble(cursor.getColumnIndexOrThrow("longitud")));
                ref.setCorreo(cursor.getString(cursor.getColumnIndexOrThrow("correo")));
                ref.setNumCelular(cursor.getString(cursor.getColumnIndexOrThrow("num_celular")));
                ref.setLastSync(cursor.getLong(cursor.getColumnIndexOrThrow("last_sync")));
                refugios.add(ref);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return refugios;
    }

    // Actualizar (por ID)
    public int actualizar(Refugio refugio) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        // Pon todos los fields como en insertar, excepto ID
        values.put("nombre", refugio.getNombre());
        // ... (agrega los demás)
        values.put("last_sync", refugio.getLastSync());
        return db.update("refugios", values, "id_refugio = ?", new String[]{refugio.getIdRefugio()});
    }

    // Eliminar
    public void eliminar(String idRefugio) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("refugios", "id_refugio = ?", new String[]{idRefugio});
    }

    // Buscar por ID
    public Refugio buscarPorId(String idRefugio) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("refugios", null, "id_refugio = ?", new String[]{idRefugio}, null, null, null);
        if (cursor.moveToFirst()) {
            Refugio ref = new Refugio();
            // Asigna como en listarTodos
            // ...
            cursor.close();
            return ref;
        }
        cursor.close();
        return null;
    }
}