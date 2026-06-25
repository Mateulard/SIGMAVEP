package com.sigmavep.exepcion;

/**
 * Excepción lanzada cuando no se encuentra un registro solicitado en el sistema.
 *
 * @author Mateo German Ruiz Díaz
 */
public class RegistroNoEncontradoException extends SIGMAVEPException {

    private static final long serialVersionUID = 1L;

    public RegistroNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    public RegistroNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
