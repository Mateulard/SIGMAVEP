package com.sigmavep.exepcion;

/**
 * Excepción lanzada cuando falla el proceso de autenticación.
 *
 * @author Mateo German Ruiz Díaz
 */
public class AutenticacionException extends SIGMAVEPException {

    private static final long serialVersionUID = 1L;

    public AutenticacionException(String mensaje) {
        super(mensaje);
    }

    public AutenticacionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
