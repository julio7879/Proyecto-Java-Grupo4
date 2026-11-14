package com.carmotors.sgt.util;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;

/**
 * Utilidad centralizada para lectura interactiva y validación por consola.
 * Permite a cualquier vista solicitar un dato validado en una sola llamada sin abortar el flujo (RNF-13, RNF-24).
 */
public final class ConsoleReader {

    private static Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

    private ConsoleReader() {
    }

    /**
     * Permite reemplazar el {@link Scanner} en pruebas unitarias sin cerrar {@code System.in}.
     */
    static void setScanner(Scanner nuevoScanner) {
        if (nuevoScanner == null) {
            throw new IllegalArgumentException("El Scanner no puede ser nulo.");
        }
        scanner = nuevoScanner;
    }

    /**
     * Restaura el lector por consola sobre {@code System.in} en UTF-8.
     */
    public static void reiniciarScannerSistema() {
        scanner = new Scanner(System.in, StandardCharsets.UTF_8);
    }

    /**
     * Solicita un texto obligatorio y repite la lectura si está vacío.
     */
    public static String leerTextoObligatorio(String mensaje) {
        while (true) {
            String valor = InputValidator.normalizarTexto(leerLineaSegura(mensaje));
            if (InputValidator.esTextoNoVacio(valor)) {
                return valor;
            }
            System.out.println("  [!] Error: Este campo es obligatorio y no puede estar vacío.");
        }
    }

    /**
     * Solicita un texto opcional; retorna cadena vacía si el usuario presiona Enter sin escribir.
     */
    public static String leerTextoOpcional(String mensaje) {
        return InputValidator.normalizarTexto(leerLineaSegura(mensaje));
    }

    /**
     * Solicita un número entero válido (acepta positivos, cero y negativos).
     */
    public static int leerEntero(String mensaje) {
        while (true) {
            Optional<Integer> valor = InputValidator.parsearEntero(leerLineaSegura(mensaje));
            if (valor.isPresent()) {
                return valor.get();
            }
            System.out.println("  [!] Error: Debe ingresar un número entero válido.");
        }
    }

    /**
     * Solicita un número entero estrictamente mayor que cero.
     */
    public static int leerEnteroPositivo(String mensaje) {
        while (true) {
            Optional<Integer> valor = InputValidator.parsearEntero(leerLineaSegura(mensaje));
            if (valor.isPresent() && InputValidator.esEnteroPositivo(valor.get())) {
                return valor.get();
            }
            System.out.println("  [!] Error: Debe ingresar un número entero positivo mayor que cero.");
        }
    }

    /**
     * Solicita un número entero mayor o igual a cero.
     */
    public static int leerEnteroNoNegativo(String mensaje) {
        while (true) {
            Optional<Integer> valor = InputValidator.parsearEntero(leerLineaSegura(mensaje));
            if (valor.isPresent() && InputValidator.esEnteroNoNegativo(valor.get())) {
                return valor.get();
            }
            System.out.println("  [!] Error: Debe ingresar un número entero mayor o igual a cero.");
        }
    }

    /**
     * Solicita un número entero dentro del rango inclusivo {@code [min, max]}.
     */
    public static int leerEnteroEnRango(String mensaje, int min, int max) {
        while (true) {
            Optional<Integer> valor = InputValidator.parsearEntero(leerLineaSegura(mensaje));
            if (valor.isPresent() && InputValidator.esEnteroEnRango(valor.get(), min, max)) {
                return valor.get();
            }
            System.out.println("  [!] Error: Debe ingresar un número entero entre " + min + " y " + max + ".");
        }
    }

    /**
     * Solicita un importe monetario positivo con escala 2 y {@code RoundingMode.HALF_UP}.
     */
    public static BigDecimal leerBigDecimalPositivo(String mensaje) {
        while (true) {
            Optional<BigDecimal> valor = InputValidator.parsearBigDecimal(leerLineaSegura(mensaje));
            if (valor.isPresent() && InputValidator.esBigDecimalPositivo(valor.get())) {
                return valor.get();
            }
            System.out.println("  [!] Error: Debe ingresar un importe numérico válido mayor que 0.00 (ej. 45000.00).");
        }
    }

    /**
     * Solicita un importe monetario mayor o igual a 0.00 con escala 2 y {@code RoundingMode.HALF_UP}.
     */
    public static BigDecimal leerBigDecimalNoNegativo(String mensaje) {
        while (true) {
            Optional<BigDecimal> valor = InputValidator.parsearBigDecimal(leerLineaSegura(mensaje));
            if (valor.isPresent() && InputValidator.esBigDecimalNoNegativo(valor.get())) {
                return valor.get();
            }
            System.out.println("  [!] Error: Debe ingresar un importe numérico válido mayor o igual a 0.00.");
        }
    }

    /**
     * Solicita una fecha obligatoria en formato estricto {@code yyyy-MM-dd}.
     */
    public static LocalDate leerFecha(String mensaje) {
        while (true) {
            Optional<LocalDate> fecha = InputValidator.parsearFecha(leerLineaSegura(mensaje));
            if (fecha.isPresent()) {
                return fecha.get();
            }
            System.out.println("  [!] Error: Fecha inválida. Use el formato estricto yyyy-MM-dd (ej. 2026-05-20).");
        }
    }

    /**
     * Solicita una fecha opcional en formato {@code yyyy-MM-dd}; retorna {@code null} si se deja en blanco.
     */
    public static LocalDate leerFechaOpcional(String mensaje) {
        while (true) {
            String entrada = InputValidator.normalizarTexto(leerLineaSegura(mensaje));
            if (entrada.isEmpty()) {
                return null;
            }
            Optional<LocalDate> fecha = InputValidator.parsearFecha(entrada);
            if (fecha.isPresent()) {
                return fecha.get();
            }
            System.out.println("  [!] Error: Fecha inválida. Use el formato estricto yyyy-MM-dd o deje vacío para omitir.");
        }
    }

    /**
     * Solicita un correo electrónico obligatorio válido y lo retorna normalizado en minúsculas.
     */
    public static String leerEmail(String mensaje) {
        while (true) {
            String entrada = InputValidator.normalizarTexto(leerLineaSegura(mensaje));
            if (InputValidator.esEmailValido(entrada)) {
                return entrada.toLowerCase(Locale.ROOT);
            }
            System.out.println("  [!] Error: Formato de correo electrónico inválido (ej. usuario@dominio.com).");
        }
    }

    /**
     * Solicita un correo electrónico opcional; si se ingresa texto, exige un formato válido.
     */
    public static String leerEmailOpcional(String mensaje) {
        while (true) {
            String entrada = InputValidator.normalizarTexto(leerLineaSegura(mensaje));
            if (entrada.isEmpty()) {
                return "";
            }
            if (InputValidator.esEmailValido(entrada)) {
                return entrada.toLowerCase(Locale.ROOT);
            }
            System.out.println("  [!] Error: Formato de correo electrónico inválido (ej. usuario@dominio.com) o deje vacío.");
        }
    }

    /**
     * Solicita la placa de un vehículo y la retorna normalizada en mayúsculas sin espacios ni guiones (RN-13).
     */
    public static String leerPlaca(String mensaje) {
        while (true) {
            String entrada = leerLineaSegura(mensaje);
            if (InputValidator.esPlacaValida(entrada)) {
                return InputValidator.normalizarPlaca(entrada);
            }
            System.out.println("  [!] Error: Placa inválida. Ingrese entre 5 y 10 caracteres alfanuméricos (ej. ABC123).");
        }
    }

    /**
     * Solicita una confirmación interactiva Sí/No antes de operaciones sensibles (RNF-24).
     */
    public static boolean leerConfirmacion(String mensaje) {
        String base = mensaje == null ? "Confirmar" : mensaje.trim();
        String prompt = base.toUpperCase(Locale.ROOT).contains("(S/N)") ? base : base + " (S/N)";
        while (true) {
            Optional<Boolean> confirmacion = InputValidator.parsearConfirmacion(leerLineaSegura(prompt));
            if (confirmacion.isPresent()) {
                return confirmacion.get();
            }
            System.out.println("  [!] Error: Respuesta inválida. Ingrese 'S' para Sí o 'N' para No.");
        }
    }

    /**
     * Pausa la ejecución por consola hasta que el usuario presione Enter.
     */
    public static void pausar() {
        System.out.print("\nPresione ENTER para continuar...");
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }

    private static String leerLineaSegura(String mensaje) {
        if (mensaje != null && !mensaje.isBlank()) {
            String prompt;
            if (mensaje.endsWith(" ")) {
                prompt = mensaje;
            } else if (mensaje.endsWith(":")) {
                prompt = mensaje + " ";
            } else {
                prompt = mensaje + ": ";
            }
            System.out.print(prompt);
        }
        if (!scanner.hasNextLine()) {
            return "";
        }
        return scanner.nextLine();
    }
}
