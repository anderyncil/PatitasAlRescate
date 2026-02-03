package com.patitasalrescate.model;

public class Refugio {
    private String idRefugio;
    private String nombre;
    private String direccion;
    private double latitud;
    private double longitud;
    private String correo;
    private String numCelular;
    private long lastSync;  // Para sync con Supabase

    // Constructor vacío (para DAOs)
    public Refugio() {}

    // Constructor completo
    public Refugio(String idRefugio, String nombre, String direccion, double latitud, double longitud, String correo, String numCelular, long lastSync) {
        this.idRefugio = idRefugio;
        this.nombre = nombre;
        this.direccion = direccion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.correo = correo;
        this.numCelular = numCelular;
        this.lastSync = lastSync;
    }

    // Getters y Setters
    public String getIdRefugio() { return idRefugio; }
    public void setIdRefugio(String idRefugio) { this.idRefugio = idRefugio; }
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
    public String getNumCelular() { return numCelular; }
    public void setNumCelular(String numCelular) { this.numCelular = numCelular; }
    public long getLastSync() { return lastSync; }
    public void setLastSync(long lastSync) { this.lastSync = lastSync; }

    @Override
    public String toString() {
        return "Refugio{" + "idRefugio='" + idRefugio + "', nombre='" + nombre + "'}";
    }
}