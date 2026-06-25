package com.sigmavep.modelo.entidad;

/** Tipo de mantenimiento preventivo (catálogo). Define el límite de km que dispara la alerta. */
public class TipoMantenimiento extends BaseEntity {

    private String nombre;
    private int limiteKm;
    private String descripcion;

    public TipoMantenimiento() {}

    public TipoMantenimiento(String nombre, int limiteKm, String descripcion) {
        super();
        this.nombre = nombre;
        this.limiteKm = limiteKm;
        this.descripcion = descripcion;
    }

    public TipoMantenimiento(int id, String nombre, int limiteKm, String descripcion) {
        super(id);
        this.nombre = nombre;
        this.limiteKm = limiteKm;
        this.descripcion = descripcion;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getLimiteKm() { return limiteKm; }

    public void setLimiteKm(int limiteKm) {
        if (limiteKm <= 0) {
            throw new IllegalArgumentException("El límite de km debe ser mayor a 0.");
        }
        this.limiteKm = limiteKm;
    }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public String getDescripcionCorta() {
        return nombre + " (cada " + limiteKm + " km)";
    }
}
