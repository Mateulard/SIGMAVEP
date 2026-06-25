package com.sigmavep.util;

import com.sigmavep.modelo.entidad.Usuario;
import com.sigmavep.modelo.enumerado.RolEnum;

/**
 * Gestiona la sesión del usuario actualmente autenticado en el sistema.
 *
 * Implementa el concepto de sesión mediante un atributo estático que persiste
 * durante la ejecución del programa y es accesible desde cualquier punto del
 * sistema.
 *
 * Es una clase de utilidad con métodos estáticos; no puede instanciarse.
 *
 * @author Mateo German Ruiz Díaz
 */
public final class Session {

    /** Usuario actualmente autenticado. Null si no hay sesión activa. */
    private static Usuario usuarioActual = null;

    /** Constructor privado: impide instanciación. */
    private Session() {
    }

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public static void setUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static boolean estaAutenticado() {
        return usuarioActual != null;
    }

    public static void cerrar() {
        usuarioActual = null;
    }

    public static boolean esAdministrador() {
        return usuarioActual != null && usuarioActual.getRol() != null
                && usuarioActual.getRol().getId() == RolEnum.ADMINISTRADOR.getId();
    }

    public static boolean esSupervisor() {
        return usuarioActual != null && usuarioActual.getRol() != null
                && usuarioActual.getRol().getId() == RolEnum.SUPERVISOR.getId();
    }

    public static boolean esFinanzas() {
        return usuarioActual != null && usuarioActual.getRol() != null
                && usuarioActual.getRol().getId() == RolEnum.FINANZAS.getId();
    }

    public static boolean esSupervisorOAdmin() {
        return esAdministrador() || esSupervisor();
    }
}
