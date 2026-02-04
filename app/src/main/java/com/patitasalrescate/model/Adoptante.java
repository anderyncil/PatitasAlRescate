package com.patitasalrescate.model;

public class Adoptante {
    private int idAdoptante;
    private String nombre;
    private String correo;
    private String password;
    private String numCelular;
    private int edad;
    private String sexo;
    private long lastSync;

    public Adoptante() {}

    public Adoptante(int idAdoptante, String nombre, String correo, String password, String numCelular, int edad, String sexo, long lastSync) {
        this.idAdoptante = idAdoptante;
        this.nombre = nombre;
        this.correo = correo;
        this.password = password;
        this.numCelular = numCelular;
        this.edad = edad;
        this.sexo = sexo;
        this.lastSync = lastSync;
    }

    // Getters y Setters
    public int getIdAdoptante() { return idAdoptante; }
    public void setIdAdoptante(int idAdoptante) { this.idAdoptante = idAdoptante; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNumCelular() { return numCelular; }
    public void setNumCelular(String numCelular) { this.numCelular = numCelular; }
    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }
    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    public long getLastSync() { return lastSync; }
    public void setLastSync(long lastSync) { this.lastSync = lastSync; }
}