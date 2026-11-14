package com.carmotors.sgt.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utilidades de validación, normalización y conversión de formatos para entradas de consola.
 * Cumple con RNF-13 (validación de formatos) y RNF-15 (dinero con {@link BigDecimal} escala 2 y HALF_UP).
 */
public final class InputValidator {

    public static final int ESCALA_MONETARIA = 2;
    public static final RoundingMode REDONDEO_MONETARIO = RoundingMode.HALF_UP;
    public static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final String REGEX_EMAIL =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$";
    private static final String REGEX_PLACA = "^[A-Z0-9]{5,10}$";

    private InputValidator() {
    }

    /**
     * Recorta espacios al inicio y al final; retorna cadena vacía si la entrada es {@code null}.
     */
    public static String normalizarTexto(String entrada) {
        if (entrada == null) {
            return "";
        }
        return entrada.trim();
    }

    /**
     * Verifica si la entrada contiene al menos un carácter visible tras aplicar {@code trim()}.
     */
    public static boolean esTextoNoVacio(String entrada) {
        return !normalizarTexto(entrada).isEmpty();
    }

    /**
     * Verifica si la cadena representa un número entero válido.
     */
    public static boolean esEntero(String entrada) {
        String texto = normalizarTexto(entrada);
        if (texto.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(texto);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Convierte una cadena válida en entero.
     */
    public static int convertirAEntero(String entrada) {
        return Integer.parseInt(normalizarTexto(entrada));
    }

    /**
     * Verifica que el entero sea estrictamente positivo ({@code > 0}).
     */
    public static boolean esEnteroPositivo(int valor) {
        return valor > 0;
    }

    /**
     * Verifica que el entero sea mayor o igual a cero ({@code >= 0}).
     */
    public static boolean esEnteroNoNegativo(int valor) {
        return valor >= 0;
    }

    /**
     * Verifica que el entero se encuentre dentro del rango inclusivo {@code [min, max]}.
     */
    public static boolean esEnteroEnRango(int valor, int min, int max) {
        return valor >= min && valor <= max;
    }

    /**
     * Verifica si la cadena representa un número decimal válido para {@link BigDecimal}.
     */
    public static boolean esBigDecimalValido(String entrada) {
        String texto = normalizarTexto(entrada);
        if (texto.isEmpty()) {
            return false;
        }
        String normalizado = texto.replace(',', '.');
        try {
            new BigDecimal(normalizado).setScale(ESCALA_MONETARIA, REDONDEO_MONETARIO);
            return true;
        } catch (NumberFormatException | ArithmeticException e) {
            return false;
        }
    }

    /**
     * Convierte una cadena numérica válida a {@link BigDecimal} con escala 2 y {@link RoundingMode#HALF_UP}.
     */
    public static BigDecimal convertirABigDecimal(String entrada) {
        String normalizado = normalizarTexto(entrada).replace(',', '.');
        return new BigDecimal(normalizado).setScale(ESCALA_MONETARIA, REDONDEO_MONETARIO);
    }

    /**
     * Verifica que el monto sea no nulo y estrictamente positivo ({@code > 0}).
     */
    public static boolean esBigDecimalPositivo(BigDecimal valor) {
        return valor != null && valor.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Verifica que el monto sea no nulo y mayor o igual a cero ({@code >= 0}).
     */
    public static boolean esBigDecimalNoNegativo(BigDecimal valor) {
        return valor != null && valor.compareTo(BigDecimal.ZERO) >= 0;
    }

    /**
     * Valida que la fecha tenga formato {@code yyyy-MM-dd} y corresponda a una fecha real del calendario.
     */
    public static boolean esFechaValida(String entrada) {
        String texto = normalizarTexto(entrada);
        if (texto.isEmpty()) {
            return false;
        }
        try {
            LocalDate fecha = LocalDate.parse(texto, FORMATO_FECHA);
            return fecha.toString().equals(texto);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Convierte una cadena válida en formato {@code yyyy-MM-dd} a {@link LocalDate}.
     */
    public static LocalDate convertirAFecha(String entrada) {
        return LocalDate.parse(normalizarTexto(entrada), FORMATO_FECHA);
    }

    /**
     * Valida que la cadena corresponda a un formato estándar de correo electrónico.
     */
    public static boolean esEmailValido(String entrada) {
        String texto = normalizarTexto(entrada);
        return !texto.isEmpty() && texto.matches(REGEX_EMAIL);
    }

    /**
     * Normaliza la placa vehicular eliminando espacios y guiones y pasándola a mayúsculas (RN-13).
     */
    public static String normalizarPlaca(String entrada) {
        if (entrada == null) {
            return "";
        }
        return entrada.replace(" ", "").replace("-", "").toUpperCase();
    }

    /**
     * Verifica que la placa normalizada tenga entre 5 y 10 caracteres alfanuméricos.
     */
    public static boolean esPlacaValida(String entrada) {
        String placa = normalizarPlaca(entrada);
        return placa.matches(REGEX_PLACA);
    }

    /**
     * Verifica si la respuesta corresponde a una confirmación válida Sí/No.
     */
    public static boolean esConfirmacionValida(String entrada) {
        String valor = normalizarTexto(entrada).toUpperCase();
        return valor.equals("S") || valor.equals("SI") || valor.equals("SÍ")
                || valor.equals("Y") || valor.equals("YES")
                || valor.equals("N") || valor.equals("NO");
    }

    /**
     * Convierte una respuesta válida Sí/No en {@code true} (Sí) o {@code false} (No).
     */
    public static boolean convertirAConfirmacion(String entrada) {
        String valor = normalizarTexto(entrada).toUpperCase();
        return valor.equals("S") || valor.equals("SI") || valor.equals("SÍ")
                || valor.equals("Y") || valor.equals("YES");
    }
}
