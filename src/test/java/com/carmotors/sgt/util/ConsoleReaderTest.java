package com.carmotors.sgt.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleReaderTest {

    private void simularEntrada(String lineas) {
        ConsoleReader.setScanner(new Scanner(lineas));
    }

    @AfterEach
    void restaurarScanner() {
        ConsoleReader.reiniciarScannerSistema();
    }

    @Test
    void leerTextoObligatorioReintentaHastaRecibirTextoValido() {
        simularEntrada("\n   \n  Taller CarMotors  \n");
        String resultado = ConsoleReader.leerTextoObligatorio("Nombre");
        assertEquals("Taller CarMotors", resultado);
    }

    @Test
    void leerTextoOpcionalPermiteVacioYNormalizaEspacios() {
        simularEntrada("   \n");
        assertEquals("", ConsoleReader.leerTextoOpcional("Observaciones"));

        simularEntrada("  Cambio de aceite  \n");
        assertEquals("Cambio de aceite", ConsoleReader.leerTextoOpcional("Observaciones"));
    }

    @Test
    void leerEnteroPositivoYEnRangoReintentanEntradasInvalidas() {
        simularEntrada("abc\n0\n-3\n4\n");
        assertEquals(4, ConsoleReader.leerEnteroPositivo("Cantidad"));

        simularEntrada("texto\n0\n6\n5\n");
        assertEquals(5, ConsoleReader.leerEnteroEnRango("Calificación", 1, 5));
    }

    @Test
    void leerBigDecimalPositivoUsaEscalaDosYHalfUp() {
        simularEntrada("no-numero\n0\n-10.50\n12500.556\n");
        BigDecimal resultado = ConsoleReader.leerBigDecimalPositivo("Precio");
        assertEquals(new BigDecimal("12500.56"), resultado);
        assertEquals(2, resultado.scale());

        simularEntrada("49,994\n");
        BigDecimal conComa = ConsoleReader.leerBigDecimalNoNegativo("Descuento");
        assertEquals(new BigDecimal("49.99"), conComa);
    }

    @Test
    void leerFechaRechazaFormatoIncorrectoYFechaInexistente() {
        simularEntrada("14/11/2026\n2026-02-30\n2026-11-14\n");
        LocalDate fecha = ConsoleReader.leerFecha("Fecha ingreso");
        assertEquals(LocalDate.of(2026, 11, 14), fecha);
    }

    @Test
    void leerFechaOpcionalRetornaNullSiEstaVacio() {
        simularEntrada("   \n");
        assertNull(ConsoleReader.leerFechaOpcional("Fecha caducidad"));

        simularEntrada("fecha-mala\n2027-01-15\n");
        assertEquals(LocalDate.of(2027, 1, 15), ConsoleReader.leerFechaOpcional("Fecha caducidad"));
    }

    @Test
    void leerEmailYEmailOpcionalValidanYNormalizanAMinusculas() {
        simularEntrada("correo-invalido\nusuario@dominio\nCliente@CarMotors.COM\n");
        assertEquals("cliente@carmotors.com", ConsoleReader.leerEmail("Email"));

        simularEntrada("\n");
        assertEquals("", ConsoleReader.leerEmailOpcional("Email proveedor"));

        simularEntrada("mal@\nVentas@Proveedor.co\n");
        assertEquals("ventas@proveedor.co", ConsoleReader.leerEmailOpcional("Email proveedor"));
    }

    @Test
    void leerPlacaNormalizaAMayusculasSinEspaciosNiGuiones() {
        simularEntrada("\nabc!\n  abc - 123  \n");
        assertEquals("ABC123", ConsoleReader.leerPlaca("Placa"));
    }

    @Test
    void leerConfirmacionReintentaYAceptaVariantesSiNo() {
        simularEntrada("talvez\nsí\n");
        assertTrue(ConsoleReader.leerConfirmacion("¿Desea cancelar la orden?"));

        simularEntrada("x\nNo\n");
        assertFalse(ConsoleReader.leerConfirmacion("¿Confirmar desactivación?"));
    }
}
