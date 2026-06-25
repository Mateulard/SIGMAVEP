package com.sigmavep.exepcion;

/**
 * Excepción lanzada cuando se intenta registrar una patente ya existente en el sistema.
 *
 * @author Mateo German Ruiz Díaz
 */
public class PatenteDuplicadaException extends SIGMAVEPException {

    private static final long serialVersionUID = 1L;

    public PatenteDuplicadaException(String mensaje) {
        super(mensaje);
    }

    public PatenteDuplicadaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
