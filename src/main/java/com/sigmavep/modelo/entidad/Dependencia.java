package com.sigmavep.modelo.entidad;

/** Dependencia policial (comisaría, destacamento, etc.) asociada a una zona. */
public class Dependencia extends BaseEntity {

    private String nombre;
    private Zona zona;

    public Dependencia() {}

    public Dependencia(String nombre, Zona zona) {
        super();
        this.nombre = nombre;
        this.zona = zona;
    }

    public Dependencia(int id, String nombre, Zona zona) {
        super(id);
        this.nombre = nombre;
        this.zona = zona;
    }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de dependencia no puede estar vacío.");
        }
        this.nombre = nombre.trim();
    }

    public Zona getZona() { return zona; }
    public void setZona(Zona zona) { this.zona = zona; }

    @Override
    public String getDescripcionCorta() {
        return nombre + " | Zona: " + (zona != null ? zona.getNombre() : "N/A");
    }
}
