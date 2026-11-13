package com.carmotors.sgt.config;

import com.carmotors.sgt.exception.ConfiguracionException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Fábrica de conexiones JDBC y punto único de acceso a la configuración.
 *
 * <p>Se implementa como Singleton porque la configuración ({@code config.properties})
 * debe leerse una sola vez y compartirse en toda la aplicación: así se evita
 * releer el archivo en cada operación y se garantiza que todas las capas usen
 * los mismos parámetros de conexión.</p>
 *
 * <p>Lo que es único es la fábrica, no la conexión: {@link #getConnection()}
 * entrega una conexión nueva en cada llamada para que cada transacción maneje
 * su propio commit/rollback y la cierre con try-with-resources.</p>
 */
public final class ConnectionFactory {

    private static final String ARCHIVO_CONFIG = "config.properties";

    private static ConnectionFactory instancia;

    private final Properties propiedades;

    // Constructor privado: nadie fuera de la clase puede crear otra instancia.
    private ConnectionFactory() {
        this.propiedades = cargarPropiedades();
    }

    /**
     * Devuelve la única instancia, creándola la primera vez que se solicita
     * (inicialización perezosa). Es synchronized para que, si dos hilos la
     * piden a la vez, no se creen dos instancias.
     */
    public static synchronized ConnectionFactory getInstance() {
        if (instancia == null) {
            instancia = new ConnectionFactory();
        }
        return instancia;
    }

    /**
     * Abre una conexión nueva con la base de datos. Quien la pide es
     * responsable de cerrarla (idealmente con try-with-resources).
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                obtenerRequerida("db.url"),
                obtenerRequerida("db.user"),
                propiedades.getProperty("db.password", ""));
    }

    /** Carpeta donde se guardan los archivos de texto de las facturas. */
    public String getCarpetaFacturas() {
        return obtenerRequerida("factura.carpeta");
    }

    private Properties cargarPropiedades() {
        InputStream entrada = getClass().getClassLoader().getResourceAsStream(ARCHIVO_CONFIG);
        if (entrada == null) {
            throw new ConfiguracionException(
                    "No se encontró '" + ARCHIVO_CONFIG + "' en el classpath. "
                    + "Copia src/main/resources/config.properties.example como "
                    + "src/main/resources/config.properties y ajusta tus datos.");
        }
        // Se lee como UTF-8 para admitir caracteres no ASCII (por ejemplo en la contraseña).
        try (Reader lector = new InputStreamReader(entrada, StandardCharsets.UTF_8)) {
            Properties props = new Properties();
            props.load(lector);
            return props;
        } catch (IOException e) {
            throw new ConfiguracionException("No se pudo leer '" + ARCHIVO_CONFIG + "'.", e);
        }
    }

    private String obtenerRequerida(String clave) {
        String valor = propiedades.getProperty(clave);
        if (valor == null || valor.isBlank()) {
            throw new ConfiguracionException(
                    "Falta la propiedad '" + clave + "' en " + ARCHIVO_CONFIG + ".");
        }
        return valor.trim();
    }
}
