package com.sigmavep.modelo.entidad;

/** Estado de una alerta de mantenimiento (catálogo). Valores: Pendiente, Procesada, Postergada. */
public class EstadoAlerta extends BaseEntity {

    private String nombre;

    public EstadoAlerta() {}

    public EstadoAlerta(String nombre) {
        super();
        this.nombre = nombre;
    }

    public EstadoAlerta(int id, String nombre) {
        super(id);
        this.nombre = nombre;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String getDescripcionCorta() {
        return nombre;
    }
}
