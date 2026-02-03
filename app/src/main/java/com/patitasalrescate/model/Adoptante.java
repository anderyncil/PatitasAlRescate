package com.patitasalrescate.model;

public class Adoptante {
    private String idAdoptante;
    private String nombre;
    private String correo;
    private String numCelular;
    private int edad;
    private String sexo;
    private long lastSync;

    public Adoptante() {}

    public Adoptante(String idAdoptante, String nombre, String correo, String numCelular, int edad, String sexo, long lastSync) {
        this.idAdoptante = idAdoptante;
        this.nombre = nombre;
        this.correo = correo;
        this.numCelular = numCelular;
        this.edad = edad;
        this.sexo = sexo;
        this.lastSync = lastSync;
    }

    // Getters y Setters
    public String getIdAdoptante() { return idAdoptante; }
    public void setIdAdoptante(String idAdoptante) { this.idAdoptante = idAdoptante; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getNumCelular() { return numCelular; }
    public void setNumCelular(String numCelular) { this.numCelular = numCelular; }
    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }
    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    public long getLastSync() { return lastSync; }
    public void setLastSync(long lastSync) { this.lastSync = lastSync; }

    @Override
    public String toString() {
        return "Adoptante{" + "idAdoptante='" + idAdoptante + "', nombre='" + nombre + "'}";
    }
}