package com.sigmavep.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilidad para generación y verificación de hashes SHA-256.
 * Se utiliza para almacenar y verificar contraseñas de forma segura:
 * nunca se guarda la contraseña en texto plano, sino su hash.
 *
 * Es una clase de utilidad con métodos estáticos; no puede instanciarse.
 *
 * @author Mateo German Ruiz Díaz
 */
public final class HashUtil {

    /** Constructor privado: impide instanciación de clase utilitaria. */
    private HashUtil() {
    }

    /**
     * Genera el hash SHA-256 de un texto plano.
     * Se aplica para proteger las contraseñas antes de almacenarlas.
     *
     * @param texto El texto (contraseña) a hashear.
     * @return Cadena hexadecimal de 64 caracteres representando el hash SHA-256.
     * @throws RuntimeException si el algoritmo SHA-256 no está disponible (no
     *                          ocurre en JVM estándar).
     */
    public static String sha256(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error crítico: algoritmo SHA-256 no disponible.", e);
        }
    }

    /**
     * Verifica si una contraseña en texto plano coincide con un hash SHA-256
     * almacenado.
     *
     * @param textoPlano La contraseña ingresada por el usuario.
     * @param hash       El hash SHA-256 almacenado en el sistema.
     * @return {@code true} si la contraseña corresponde al hash.
     */
    public static boolean verificar(String textoPlano, String hash) {
        if (textoPlano == null || hash == null)
            return false;
        return sha256(textoPlano).equals(hash);
    }
}
