package com.sigmavep.dao;

import com.sigmavep.exepcion.RegistroNoEncontradoException;
import com.sigmavep.exepcion.SIGMAVEPException;

import java.util.List;

/**
 * Interfaz genérica CRUD para todos los DAOs del sistema SIGMAVEP.
 * Aplica polimorfismo paramétrico (genéricos) para reutilizar la definición.
 *
 * @param <T> Tipo de entidad manejada por el DAO.
 * @author Mateo German Ruiz Díaz
 */
public interface BaseDAO<T> {
    T insertar(T entidad) throws SIGMAVEPException;
    T actualizar(T entidad) throws SIGMAVEPException;
    boolean eliminar(int id) throws SIGMAVEPException;
    T buscarPorId(int id) throws SIGMAVEPException, RegistroNoEncontradoException;
    List<T> listarTodos() throws SIGMAVEPException;
}
