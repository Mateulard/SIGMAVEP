package com.sigmavep.controlador;

import com.sigmavep.exepcion.SIGMAVEPException;

/**
 * Interfaz que define el contrato de todos los controladores del sistema.
 * Aplica polimorfismo por interfaces.
 *
 * @author Mateo German Ruiz Díaz
 */
public interface Controller {
    /**
     * Ejecuta la lógica principal del controlador.
     *
     * @throws SIGMAVEPException si ocurre un error de negocio o técnico.
     */
    void ejecutar() throws SIGMAVEPException;
}
