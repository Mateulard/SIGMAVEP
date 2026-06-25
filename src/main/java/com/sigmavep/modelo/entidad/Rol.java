package com.sigmavep.modelo.entidad;

/** Rol de usuario del sistema (catálogo). Roles disponibles: Administrador, Finanzas, Supervisor. */
public class Rol extends BaseEntity {

    private String nombre;
    private String descripcion;

    public Rol() {}

    public Rol(String nombre, String descripcion) {
        super();
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Rol(int id, String nombre, String descripcion) {
        super(id);
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public String getDescripcionCorta() {
        return nombre;
    }
}
