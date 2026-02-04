package com.patitasalrescate.model;

public class Refugio {
    private int id_refugio;
    private String nombre;
    private String direccion;
    private double latitud;
    private double longitud;
    private String correo;
    private String password;
    private String numCelular;
    private String fotoUrl;
    private long lastSync;

    public Refugio() {}

    // Constructor actualizado
    public Refugio(int id_refugio, String nombre, String direccion, double latitud, double longitud, String correo, String password, String numCelular, String fotoUrl,long lastSync) {
        this.id_refugio=id_refugio;
        this.nombre = nombre;
        this.direccion = direccion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.correo = correo;
        this.password = password;
        this.numCelular = numCelular;
        this.fotoUrl=fotoUrl;
        this.lastSync = lastSync;
    }

    // Getters y Setters

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }
    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNumCelular() { return numCelular; }
    public void setNumCelular(String numCelular) { this.numCelular = numCelular; }
    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
    public long getLastSync() { return lastSync; }

    public int getId_refugio() {
        return id_refugio;
    }

    public void setId_refugio(int id_refugio) {
        this.id_refugio = id_refugio;
    }

    public void setLastSync(long lastSync) { this.lastSync = lastSync; }
}