package com.sigmavep.modelo.enumerado;

/**
 * Enumerado que representa los posibles estados de una alerta de mantenimiento.
 *
 * @author Mateo German Ruiz Díaz
 */
public enum EstadoAlertaEnum {

    PENDIENTE(1, "Pendiente"),
    PROCESADA(2, "Procesada"),
    POSTERGADA(3, "Postergada");

    private final int id;
    private final String nombre;

    EstadoAlertaEnum(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }

    @Override
    public String toString() { return nombre; }
}
