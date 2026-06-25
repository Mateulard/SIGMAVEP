package com.sigmavep.modelo.enumerado;

/**
 * Enumerado que representa los roles disponibles en el sistema SIGMAVEP.
 *
 * @author Mateo German Ruiz Díaz
 */
public enum RolEnum {

    ADMINISTRADOR(1, "Administrador"),
    FINANZAS(2, "Finanzas"),
    SUPERVISOR(3, "Supervisor");

    private final int id;
    private final String nombre;

    RolEnum(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }

    @Override
    public String toString() { return nombre; }
}
