package com.sigmavep.util;

import com.sigmavep.modelo.entidad.Alerta;
import com.sigmavep.modelo.entidad.Movil;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilidad con algoritmos de ordenación y búsqueda para el sistema SIGMAVEP.
 * Implementa algoritmo burbuja y búsqueda lineal. Clase estática; no puede instanciarse.
 *
 * @author Mateo German Ruiz Díaz
 */
public final class OrdenamientoUtil {

    private OrdenamientoUtil() {}

    public static List<Movil> ordenarMovilesPorKm(List<Movil> moviles) {
        List<Movil> lista = new ArrayList<>(moviles);
        int n = lista.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (lista.get(j).getKmActual() > lista.get(j + 1).getKmActual()) {
                    Movil temp = lista.get(j);
                    lista.set(j, lista.get(j + 1));
                    lista.set(j + 1, temp);
                }
            }
        }
        return lista;
    }

    public static List<Alerta> ordenarAlertasPorFecha(List<Alerta> alertas) {
        List<Alerta> lista = new ArrayList<>(alertas);
        int n = lista.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (Alerta.POR_FECHA.compare(lista.get(j), lista.get(j + 1)) > 0) {
                    Alerta temp = lista.get(j);
                    lista.set(j, lista.get(j + 1));
                    lista.set(j + 1, temp);
                }
            }
        }
        return lista;
    }

    public static Movil buscarMovilPorPatente(List<Movil> moviles, String patente) {
        for (Movil movil : moviles) {
            if (movil.getPatente().equalsIgnoreCase(patente)) {
                return movil;
            }
        }
        return null;
    }
}
