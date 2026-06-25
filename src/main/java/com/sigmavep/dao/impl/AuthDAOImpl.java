package com.sigmavep.dao.impl;

import com.sigmavep.exepcion.AutenticacionException;
import com.sigmavep.exepcion.RegistroNoEncontradoException;
import com.sigmavep.exepcion.SIGMAVEPException;
import com.sigmavep.modelo.entidad.Usuario;
import com.sigmavep.util.HashUtil;

/**
 * DAO especializado en la autenticación de usuarios.
 * Verifica credenciales contra MySQL usando hash SHA-256.
 *
 * @author Mateo German Ruiz Díaz
 */
public class AuthDAOImpl {

    private final UsuarioDAOImpl usuarioDAO;

    public AuthDAOImpl() {
        this.usuarioDAO = new UsuarioDAOImpl();
    }

    /**
     * Autentica a un usuario verificando username y contraseña (hash SHA-256).
     *
     * @param username Username ingresado.
     * @param password Contraseña en texto plano.
     * @return El {@link Usuario} autenticado.
     * @throws AutenticacionException Si las credenciales son incorrectas o la cuenta está inactiva.
     * @throws SIGMAVEPException      Si ocurre un error técnico.
     */
    public Usuario autenticar(String username, String password)
            throws AutenticacionException, SIGMAVEPException {
        try {
            Usuario usuario = usuarioDAO.buscarPorUsername(username);

            if (!usuario.isActivo()) {
                throw new AutenticacionException(
                    "La cuenta de usuario '" + username + "' está inactiva. Contacte al administrador.");
            }

            if (!HashUtil.verificar(password, usuario.getPasswordHash())) {
                throw new AutenticacionException(
                    "Contraseña incorrecta para el usuario '" + username + "'.");
            }

            return usuario;

        } catch (AutenticacionException e) {
            throw e;
        } catch (RegistroNoEncontradoException e) {
            throw new AutenticacionException("Usuario '" + username + "' no encontrado en el sistema.");
        } catch (Exception e) {
            throw new SIGMAVEPException("Error técnico durante la autenticación: " + e.getMessage(), e);
        }
    }
}
