package com.sigmavep.modelo.entidad;

/** Estado operativo de un móvil policial (catálogo). Valores: En servicio, Fuera de servicio, En reparación. */
public class EstadoMovil extends BaseEntity {

    private String nombre;

    public EstadoMovil() {}

    public EstadoMovil(String nombre) {
        super();
        this.nombre = nombre;
    }

    public EstadoMovil(int id, String nombre) {
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
