package com.carmotors.sgt.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Utilidades puras de validación, normalización y conversión de formatos para entradas de consola.
 * Cumple con RNF-13 (validación de formatos) y RNF-15 (dinero con {@link BigDecimal} escala 2 y HALF_UP).
 */
public final class InputValidator {

    public static final int ESCALA_MONETARIA = 2;
    public static final RoundingMode REDONDEO_MONETARIO = RoundingMode.HALF_UP;
    public static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);

    private static final Pattern PATRON_EMAIL =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$");
    private static final Pattern PATRON_PLACA = Pattern.compile("^[A-Z0-9]{5,10}$");

    private InputValidator() {
    }

    /**
     * Recorta espacios al inicio y al final; retorna cadena vacía si la entrada es {@code null}.
     */
    public static String normalizarTexto(String entrada) {
        return entrada == null ? "" : entrada.trim();
    }

    /**
     * Verifica si la entrada contiene al menos un carácter visible tras aplicar {@code trim()}.
     */
    public static boolean esTextoNoVacio(String entrada) {
        return !normalizarTexto(entrada).isEmpty();
    }

    /**
     * Intenta convertir una cadena en un entero válido.
     */
    public static Optional<Integer> parsearEntero(String entrada) {
        String texto = normalizarTexto(entrada);
        if (texto.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Integer.parseInt(texto));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
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
     * Convierte una cadena numérica a {@link BigDecimal} con escala 2 y {@link RoundingMode#HALF_UP}.
     * Tolera coma decimal convirtiéndola a punto antes de construir el valor exacto.
     */
    public static Optional<BigDecimal> parsearBigDecimal(String entrada) {
        String texto = normalizarTexto(entrada);
        if (texto.isEmpty()) {
            return Optional.empty();
        }
        String normalizado = texto.replace(',', '.');
        try {
            BigDecimal monto = new BigDecimal(normalizado)
                    .setScale(ESCALA_MONETARIA, REDONDEO_MONETARIO);
            return Optional.of(monto);
        } catch (NumberFormatException | ArithmeticException e) {
            return Optional.empty();
        }
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
     * Parsea una fecha en formato estricto {@code yyyy-MM-dd} (ej. rechaza 2026-02-30).
     */
    public static Optional<LocalDate> parsearFecha(String entrada) {
        String texto = normalizarTexto(entrada);
        if (texto.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(LocalDate.parse(texto, FORMATO_FECHA));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    /**
     * Valida que la cadena corresponda a un formato estándar de correo electrónico.
     */
    public static boolean esEmailValido(String entrada) {
        String texto = normalizarTexto(entrada);
        return !texto.isEmpty() && PATRON_EMAIL.matcher(texto).matches();
    }

    /**
     * Normaliza la placa vehicular eliminando espacios y guiones y pasándola a mayúsculas (RN-13).
     */
    public static String normalizarPlaca(String entrada) {
        if (entrada == null) {
            return "";
        }
        return entrada.replaceAll("[\\s-]+", "").toUpperCase(Locale.ROOT);
    }

    /**
     * Verifica que la placa normalizada tenga entre 5 y 10 caracteres alfanuméricos.
     */
    public static boolean esPlacaValida(String entrada) {
        String placa = normalizarPlaca(entrada);
        return PATRON_PLACA.matcher(placa).matches();
    }

    /**
     * Interpreta confirmaciones de consola Sí/No ({@code S}, {@code SI}, {@code SÍ}, {@code Y}, {@code YES}, {@code N}, {@code NO}).
     */
    public static Optional<Boolean> parsearConfirmacion(String entrada) {
        String valor = normalizarTexto(entrada).toUpperCase(Locale.ROOT);
        return switch (valor) {
            case "S", "SI", "SÍ", "Y", "YES" -> Optional.of(Boolean.TRUE);
            case "N", "NO" -> Optional.of(Boolean.FALSE);
            default -> Optional.empty();
        };
    }
}
