package com.patitasalrescate.accesoADatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.patitasalrescate.model.Mascota;

import java.util.ArrayList;
import java.util.List;

public class DAOFavoritos {
    private BDConstruir dbHelper;
    private DAOMascota daoMascota;  // Para join

    public DAOFavoritos(Context context) {
        dbHelper = new BDConstruir(context);
        daoMascota = new DAOMascota(context);
    }

    // Agregar favorito
    public long addFavorito(String idAdoptante, String idMascota) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id_adoptante", idAdoptante);
        values.put("id_mascota", idMascota);
        values.put("last_sync", System.currentTimeMillis());
        return db.insert("favoritos", null, values);
    }

    // Eliminar favorito
    public void removeFavorito(String idAdoptante, String idMascota) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("favoritos", "id_adoptante = ? AND id_mascota = ?", new String[]{idAdoptante, idMascota});
    }

    // Listar favoritos de un adoptante (devuelve List<Mascota>)
    public List<Mascota> getFavoritosPorAdoptante(String idAdoptante) {
        List<Mascota> favoritos = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT m.* FROM mascotas m JOIN favoritos f ON m.id_mascota = f.id_mascota WHERE f.id_adoptante = ?", new String[]{idAdoptante});
        if (cursor.moveToFirst()) {
            do {
                Mascota masc = new Mascota();
                // Asigna fields como en DAOMascota.listarTodos
                // ...
                favoritos.add(masc);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return favoritos;
    }
}