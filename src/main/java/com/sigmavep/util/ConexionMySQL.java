package com.sigmavep.util;

import java.io.*;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase utilitaria para gestionar la conexión a la base de datos MySQL.
 *
 * Estrategia de búsqueda de configuración (en orden de prioridad):
 *   1. Archivo "db.properties" en el directorio donde se ejecuta la aplicación (externo, editable).
 *   2. Archivo "db.properties" en el classpath (dentro del JAR, configuración por defecto).
 *
 * Esto permite que el profesor ejecute la app en cualquier PC y configure
 * sus propias credenciales sin recompilar.
 *
 * @author Mateo German Ruiz Díaz
 */
public final class ConexionMySQL {

    /** Nombre del archivo de configuración. */
    public static final String NOMBRE_ARCHIVO = "db.properties";

    /** Ruta del directorio de ejecución (junto al JAR). */
    private static final Path RUTA_EXTERNA = Paths.get(System.getProperty("user.dir"), NOMBRE_ARCHIVO);

    private static String url;
    private static String user;
    private static String password;
    private static String driver;
    private static boolean configurado = false;

    static {
        try {
            cargarConfiguracion();
        } catch (Exception e) {
            // Si falla la carga inicial, los valores quedan null.
            // SetupFrame se encargará de inicializarlos.
            System.err.println("[ConexionMySQL] No se pudo cargar db.properties: " + e.getMessage());
        }
    }

    private ConexionMySQL() {}

    /**
     * Carga la configuración desde archivo externo o interno (classpath).
     */
    private static void cargarConfiguracion() throws Exception {
        Properties props = new Properties();

        if (Files.exists(RUTA_EXTERNA)) {
            // Prioridad 1: archivo externo junto al JAR (editable por el usuario)
            try (InputStream is = Files.newInputStream(RUTA_EXTERNA)) {
                props.load(is);
            }
        } else {
            // Prioridad 2: classpath (dentro del JAR)
            InputStream is = ConexionMySQL.class.getClassLoader().getResourceAsStream(NOMBRE_ARCHIVO);
            if (is == null) throw new RuntimeException("db.properties no encontrado en classpath ni en directorio de ejecución.");
            try (is) {
                props.load(is);
            }
        }

        driver   = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
        url      = props.getProperty("db.url");
        user     = props.getProperty("db.user");
        password = props.getProperty("db.password");

        Class.forName(driver);
        configurado = true;
    }

    /**
     * Reconfigura la conexión con nuevos parámetros y guarda el archivo externo.
     *
     * @param host     Servidor MySQL (ej: "localhost")
     * @param port     Puerto (ej: 3306)
     * @param database Nombre de la base de datos (ej: "sigmavep")
     * @param usuario  Usuario MySQL
     * @param clave    Contraseña MySQL
     * @throws Exception si no se puede guardar o conectar.
     */
    public static void reconfigurar(String host, int port, String database,
                                    String usuario, String clave) throws Exception {
        String nuevaUrl = String.format(
            "jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=America/Argentina/Buenos_Aires&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8",
            host, port, database
        );

        // Verificar que conecta antes de guardar
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection conn = DriverManager.getConnection(nuevaUrl, usuario, clave)) {
            if (conn == null || conn.isClosed()) throw new Exception("Conexión fallida.");
        }

        // Actualizar variables estáticas
        url      = nuevaUrl;
        user     = usuario;
        password = clave;
        driver   = "com.mysql.cj.jdbc.Driver";
        configurado = true;

        // Guardar archivo externo
        Properties props = new Properties();
        props.setProperty("db.driver", driver);
        props.setProperty("db.url",    url);
        props.setProperty("db.user",   user);
        props.setProperty("db.password", password);
        try (OutputStream os = Files.newOutputStream(RUTA_EXTERNA)) {
            props.store(os, "SIGMAVEP v2.0 - Configuracion de Base de Datos (generado automaticamente)");
        }
    }

    /**
     * Obtiene una nueva conexión a la base de datos.
     *
     * @return Conexión JDBC activa.
     * @throws SQLException si no se puede conectar.
     * @throws IllegalStateException si la conexión no fue configurada.
     */
    public static Connection getConnection() throws SQLException {
        if (!configurado || url == null) {
            throw new IllegalStateException("La conexión a la base de datos no está configurada. Use el Asistente de Configuración.");
        }
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Verifica que la conexión a la base de datos funcione.
     *
     * @throws Exception si la conexión falla.
     */
    public static void verificarConexion() throws Exception {
        if (!configurado || url == null) {
            throw new Exception("Configuración no cargada.");
        }
        try (Connection conn = getConnection()) {
            if (conn == null || conn.isClosed()) {
                throw new Exception("Conexión nula o cerrada.");
            }
        }
    }

    /**
     * Verifica una conexión con parámetros específicos (sin modificar la config actual).
     */
    public static void verificarConexionConParametros(String host, int port, String database,
                                                       String usuario, String clave) throws Exception {
        String testUrl = String.format(
            "jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=America/Argentina/Buenos_Aires&allowPublicKeyRetrieval=true",
            host, port, database
        );
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection conn = DriverManager.getConnection(testUrl, usuario, clave)) {
            if (conn == null || conn.isClosed()) throw new Exception("Conexión cerrada.");
        }
    }

    /**
     * Verifica conexión al servidor MySQL sin seleccionar una base de datos.
     * Útil para crear la BD si no existe aún.
     */
    public static Connection getConnectionSinDB(String host, int port,
                                                  String usuario, String clave) throws Exception {
        String urlServidor = String.format(
            "jdbc:mysql://%s:%d?useSSL=false&serverTimezone=America/Argentina/Buenos_Aires&allowPublicKeyRetrieval=true",
            host, port
        );
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(urlServidor, usuario, clave);
    }

    public static boolean isConfigurado() { return configurado; }
    public static String getUrl()         { return url; }
    public static String getUser()        { return user; }
}
