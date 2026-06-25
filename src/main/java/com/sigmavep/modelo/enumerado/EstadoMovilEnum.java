package com.sigmavep.modelo.enumerado;

/**
 * Enumerado que representa los posibles estados operativos de un móvil policial.
 *
 * @author Mateo German Ruiz Díaz
 */
public enum EstadoMovilEnum {

    EN_SERVICIO(1, "En servicio"),
    FUERA_DE_SERVICIO(2, "Fuera de servicio"),
    EN_REPARACION(3, "En reparación");

    private final int id;
    private final String nombre;

    EstadoMovilEnum(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }

    @Override
    public String toString() { return nombre; }
}
