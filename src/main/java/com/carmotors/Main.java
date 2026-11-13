package com.carmotors;

import com.carmotors.config.ConexionFactory;
import com.carmotors.excepcion.ConfiguracionException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Punto de entrada de la aplicación.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println(" CarMotors - Sistema de Gestión de Taller");
        System.out.println("==========================================");

        try (Connection conexion = ConexionFactory.getInstance().getConnection()) {
            System.out.println("Conexión a la base de datos establecida correctamente.");
        } catch (SQLException | ConfiguracionException e) {
            System.out.println("No se pudo conectar a la base de datos: " + e.getMessage());
        }
    }
}
