package com.sigmavep.modelo.entidad;

/** Tipo de novedad que puede afectar a un móvil (catálogo). Ej: Falla mecánica, Accidente. */
public class TipoNovedad extends BaseEntity {

    private String nombre;
    private String descripcion;

    public TipoNovedad() {}

    public TipoNovedad(String nombre, String descripcion) {
        super();
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public TipoNovedad(int id, String nombre, String descripcion) {
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
