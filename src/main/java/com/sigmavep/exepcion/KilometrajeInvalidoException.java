package com.sigmavep.exepcion;

/**
 * Excepción lanzada cuando el kilometraje ingresado es inválido (menor al actual).
 *
 * @author Mateo German Ruiz Díaz
 */
public class KilometrajeInvalidoException extends SIGMAVEPException {

    private static final long serialVersionUID = 1L;

    public KilometrajeInvalidoException(String mensaje) {
        super(mensaje);
    }

    public KilometrajeInvalidoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
