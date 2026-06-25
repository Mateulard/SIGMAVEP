package com.sigmavep.exepcion;

/**
 * Excepción base de la aplicación SIGMAVEP.
 * Todas las excepciones del sistema heredan de esta clase.
 *
 * @author Mateo German Ruiz Díaz
 */
public class SIGMAVEPException extends Exception {

    private static final long serialVersionUID = 1L;

    public SIGMAVEPException(String mensaje) {
        super(mensaje);
    }

    public SIGMAVEPException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
