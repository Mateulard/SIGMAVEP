package com.sigmavep.exepcion;

/**
 * Excepción lanzada cuando un móvil tiene alertas de mantenimiento pendientes.
 *
 * @author Mateo German Ruiz Díaz
 */
public class AlertaPendienteException extends SIGMAVEPException {

    private static final long serialVersionUID = 1L;

    public AlertaPendienteException(String mensaje) {
        super(mensaje);
    }

    public AlertaPendienteException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
