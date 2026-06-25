package com.sigmavep.util;

/**
 * Utilidad con validaciones generales del sistema SIGMAVEP.
 * Clase estática; no puede instanciarse.
 *
 * @author Mateo German Ruiz Díaz
 */
public final class ValidadorUtil {

    private ValidadorUtil() {}

    public static boolean esVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    public static boolean esAnioValido(int anio) {
        int anioActual = java.time.LocalDate.now().getYear();
        return anio >= 1950 && anio <= anioActual + 1;
    }

    public static boolean esPatente(String patente) {
        if (esVacio(patente)) return false;
        String p = patente.trim().toUpperCase();
        return p.matches("^[A-Z]{3}\\d{3}$") || p.matches("^[A-Z]{2}\\d{3}[A-Z]{2}$");
    }

    public static boolean esUsernameValido(String username) {
        if (esVacio(username)) return false;
        String u = username.trim();
        return u.length() >= 4 && u.length() <= 50 && !u.contains(" ");
    }

    public static boolean esPasswordValido(String password) {
        return !esVacio(password) && password.length() >= 6;
    }
}
