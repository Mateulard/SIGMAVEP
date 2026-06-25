package com.sigmavep.util;

import com.sigmavep.exepcion.SIGMAVEPException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Utilidad para exportar datos a archivos CSV.
 * No puede instanciarse.
 *
 * @author Mateo German Ruiz Díaz
 */
public final class ArchivoUtil {

    private ArchivoUtil() {}

    /**
     * Exporta una lista de filas a un archivo CSV con separador de punto y coma.
     *
     * @param ruta        Ruta completa del archivo a crear.
     * @param datos       Filas de datos (cada fila es un arreglo de strings).
     * @param encabezados Nombres de las columnas.
     * @throws SIGMAVEPException si ocurre un error de I/O.
     */
    /**
     * Exporta a CSV. Parámetros: ruta, encabezados, filas de datos.
     */
    public static void exportarCSV(String ruta, String[] encabezados, List<String[]> datos)
            throws SIGMAVEPException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ruta))) {
            pw.println(String.join(";", encabezados));
            for (String[] fila : datos) {
                pw.println(String.join(";", fila));
            }
        } catch (IOException e) {
            throw new SIGMAVEPException("Error al exportar CSV: " + e.getMessage(), e);
        }
    }

    /**
     * Sobrecarga para compatibilidad: ruta, datos, encabezados (orden alternativo).
     */
    public static void exportarCSV(String ruta, List<String[]> datos, String[] encabezados)
            throws SIGMAVEPException {
        exportarCSV(ruta, encabezados, datos);
    }
}
