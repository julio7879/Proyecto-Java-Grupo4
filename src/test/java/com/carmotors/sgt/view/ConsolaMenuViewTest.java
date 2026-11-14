package com.carmotors.sgt.view;

import com.carmotors.sgt.util.ConsoleReader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConsolaMenuViewTest {

    @AfterEach
    void restaurarScanner() {
        ConsoleReader.reiniciarScannerSistema();
    }

    @Test
    void menuPrincipalPermiteSalirConOpcionCero() {
        ConsoleReader.setScanner(new Scanner("0\n"));
        ConsolaMenuView menu = new ConsolaMenuView();
        menu.mostrarMenuPrincipal();
    }

    @Test
    void menuPrincipalInvocaSubmenuConectadoYVuelveAlMenu() {
        ConsoleReader.setScanner(new Scanner("1\n0\n"));
        ConsolaMenuView menu = new ConsolaMenuView();

        final int[] llamadas = {0};
        menu.setSubmenuClientes(new SubmenuVista() {
            @Override
            public String obtenerTitulo() {
                return "Clientes y Vehículos";
            }

            @Override
            public void mostrarMenu() {
                llamadas[0] = llamadas[0] + 1;
            }
        });

        menu.mostrarMenuPrincipal();
        assertEquals(1, llamadas[0]);
    }

    @Test
    void menuPrincipalManejaModuloNoConectadoYExcepcionSinCerrarAplicacion() {
        ConsoleReader.setScanner(new Scanner("3\n\n2\n\n0\n"));
        ConsolaMenuView menu = new ConsolaMenuView();

        final int[] intentos = {0};
        menu.setSubmenuProveedores(new SubmenuVista() {
            @Override
            public String obtenerTitulo() {
                return "Proveedores";
            }

            @Override
            public void mostrarMenu() {
                intentos[0] = intentos[0] + 1;
                throw new RuntimeException("Error simulado");
            }
        });

        menu.mostrarMenuPrincipal();
        assertEquals(1, intentos[0]);
    }
}
