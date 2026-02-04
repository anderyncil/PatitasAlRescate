package com.patitasalrescate.accesoADatos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BDConstruir extends SQLiteOpenHelper {

    private static final String DB_NAME = "patitas_db";
    private static final int DB_VERSION = 3;  // Subimos versión por cambio en tabla mascotas

    public BDConstruir(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tabla refugios
        db.execSQL("CREATE TABLE refugios (" +
                "id_refugio INTEGER PRIMARY KEY AUTOINCREMENT, " + // ID numérico
                "nombre TEXT NOT NULL, " +
                "direccion TEXT, " +
                "latitud REAL, " +
                "longitud REAL, " +
                "correo TEXT, " +
                "password TEXT, " +
                "num_celular TEXT, " +
                "foto TEXT,"+
                "last_sync INTEGER" +
                ")");

        // Tabla mascotas (Agregado campo 'nombre')
        db.execSQL("CREATE TABLE mascotas (" +
                "id_mascota INTEGER PRIMARY KEY AUTOINCREMENT, " + // ID numérico
                "id_refugio INTEGER, " + // Relación numérica
                "nombre TEXT, " +        // NUEVO CAMPO NOMBRE
                "especie TEXT NOT NULL, " +
                "raza TEXT, " +
                "edad INTEGER, " +
                "temperamento TEXT, " +
                "historia TEXT, " +
                "fotos TEXT, " +
                "es_adoptado INTEGER DEFAULT 0, " +
                "last_sync INTEGER" +
                ")");

        // Tabla adoptantes
        db.execSQL("CREATE TABLE adoptantes (" +
                "id_adoptante INTEGER PRIMARY KEY AUTOINCREMENT, " + // ID numérico
                "nombre TEXT, " +
                "correo TEXT, " +
                "password TEXT, " +
                "num_celular TEXT, " +
                "edad INTEGER, " +
                "sexo TEXT, " +
                "last_sync INTEGER" +
                ")");

        // Tabla favoritos
        db.execSQL("CREATE TABLE favoritos (" +
                "id_adoptante INTEGER, " +
                "id_mascota INTEGER, " +
                "last_sync INTEGER, " +
                "PRIMARY KEY (id_adoptante, id_mascota)" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS favoritos");
        db.execSQL("DROP TABLE IF EXISTS mascotas");
        db.execSQL("DROP TABLE IF EXISTS adoptantes");
        db.execSQL("DROP TABLE IF EXISTS refugios");
        onCreate(db);
    }
}