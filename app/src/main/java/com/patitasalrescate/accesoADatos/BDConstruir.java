package com.patitasalrescate.accesoADatos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BDConstruir extends SQLiteOpenHelper {

    private static final String DB_NAME = "patitas_db";  // Nombre de la BD local
    private static final int DB_VERSION = 1;  // Versión inicial

    public BDConstruir(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tabla refugios (cache local)
        db.execSQL("CREATE TABLE refugios (" +
                "id_refugio TEXT PRIMARY KEY, " +  // UUID como String
                "nombre TEXT NOT NULL, " +
                "direccion TEXT, " +
                "latitud REAL, " +
                "longitud REAL, " +
                "correo TEXT, " +
                "num_celular TEXT, " +
                "last_sync INTEGER" +  // Timestamp para sync con Supabase
                ")");

        // Tabla mascotas
        db.execSQL("CREATE TABLE mascotas (" +
                "id_mascota TEXT PRIMARY KEY, " +
                "id_refugio TEXT, " +
                "especie TEXT NOT NULL, " +
                "raza TEXT, " +
                "edad INTEGER, " +
                "temperamento TEXT, " +
                "historia TEXT, " +
                "fotos TEXT, " +  // URLs separadas por coma o JSON
                "es_adoptado INTEGER DEFAULT 0, " +  // 0=false
                "last_sync INTEGER" +
                ")");

        // Tabla adoptantes (para usuario logueado o cache)
        db.execSQL("CREATE TABLE adoptantes (" +
                "id_adoptante TEXT PRIMARY KEY, " +
                "nombre TEXT, " +
                "correo TEXT, " +
                "num_celular TEXT, " +
                "edad INTEGER, " +
                "sexo TEXT, " +
                "last_sync INTEGER" +
                ")");

        // Tabla favoritos (corregida: columnas primero, constraint después)
        db.execSQL("CREATE TABLE favoritos (" +
                "id_adoptante TEXT, " +
                "id_mascota TEXT, " +
                "last_sync INTEGER, " +  // Movido aquí, antes de PRIMARY KEY
                "PRIMARY KEY (id_adoptante, id_mascota)" +  // Constraint al final
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Para desarrollo: Borra y recrea tablas
        db.execSQL("DROP TABLE IF EXISTS favoritos");
        db.execSQL("DROP TABLE IF EXISTS mascotas");
        db.execSQL("DROP TABLE IF EXISTS adoptantes");
        db.execSQL("DROP TABLE IF EXISTS refugios");
        onCreate(db);
    }
}