package com.sigmavep.modelo.entidad;

/** Zona geográfica policial del sistema SIGMAVEP (ej: "Zona 1 - Rosario"). */
public class Zona extends BaseEntity {

    private String nombre;
    private String sede;

    public Zona() {}

    public Zona(String nombre, String sede) {
        super();
        this.nombre = nombre;
        this.sede = sede;
    }

    public Zona(int id, String nombre, String sede) {
        super(id);
        this.nombre = nombre;
        this.sede = sede;
    }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de zona no puede estar vacío.");
        }
        this.nombre = nombre.trim();
    }

    public String getSede() { return sede; }

    public void setSede(String sede) {
        if (sede == null || sede.trim().isEmpty()) {
            throw new IllegalArgumentException("La sede no puede estar vacía.");
        }
        this.sede = sede.trim();
    }

    @Override
    public String getDescripcionCorta() {
        return "Zona #" + id + " - " + nombre + " | Sede: " + sede;
    }
}
