package com.patitasalrescate.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Mascota {

    @SerializedName("id_mascota")
    @Expose
    private int idMascota;

    @SerializedName("id_refugio")
    @Expose
    private int idRefugio;

    @Expose
    private String especie;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Expose
    private String nombre;


    @Expose
    private String raza;

    @Expose
    private int edad;

    @Expose
    private String temperamento;

    @Expose
    private String historia;

    @Expose
    private List<String> fotos = new ArrayList<>();  // Lista para TEXT[] en Supabase

    @SerializedName("es_adoptado")
    @Expose
    private boolean esAdoptado;

    // Campos SOLO locales (no se envían a Supabase)
    private transient long lastSync;
    private String createdAt;
    private String updatedAt;

    public Mascota() {}

    public Mascota(int idMascota, int idRefugio, String especie, String raza, int edad,
                   String temperamento, String historia, List<String> fotos, boolean esAdoptado, String nombre, long lastSync) {
        this.idMascota = idMascota;
        this.idRefugio = idRefugio;
        this.especie = especie;
        this.raza = raza;
        this.edad = edad;
        this.temperamento = temperamento;
        this.historia = historia;
        this.fotos = fotos != null ? fotos : new ArrayList<>();
        this.esAdoptado = esAdoptado;
        this.lastSync = lastSync;
        this.nombre=nombre;
    }

    // Getters y Setters completos
    public int getIdMascota() { return idMascota; }
    public void setIdMascota(int idMascota) { this.idMascota = idMascota; }

    public int getIdRefugio() { return idRefugio; }
    public void setIdRefugio(int  idRefugio) { this.idRefugio = idRefugio; }

    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }

    public String getRaza() { return raza; }
    public void setRaza(String raza) { this.raza = raza; }

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }

    public String getTemperamento() { return temperamento; }
    public void setTemperamento(String temperamento) { this.temperamento = temperamento; }

    public String getHistoria() { return historia; }
    public void setHistoria(String historia) { this.historia = historia; }

    public List<String> getFotos() { return fotos; }
    public void setFotos(List<String> fotos) { this.fotos = fotos; }

    public boolean isEsAdoptado() { return esAdoptado; }
    public void setEsAdoptado(boolean esAdoptado) { this.esAdoptado = esAdoptado; }

    public long getLastSync() { return lastSync; }
    public void setLastSync(long lastSync) { this.lastSync = lastSync; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Mascota{" +
                "idMascota='" + idMascota + '\'' +
                ", especie='" + especie + '\'' +
                ", raza='" + raza + '\'' +
                ", esAdoptado=" + esAdoptado +
                '}';
    }
}