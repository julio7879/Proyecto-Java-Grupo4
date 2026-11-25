package com.carmotors.sgt.view;

import com.carmotors.sgt.util.ConsoleReader;

/**
 * Submenú de la opción "Servicios y Órdenes de Trabajo": delega en las vistas
 * del catálogo de tipos de servicio y de órdenes de servicio sin modificarlas.
 */
public class ServiciosMenuView implements SubmenuVista {

    private final SubmenuVista tiposServicioView;
    private final SubmenuVista ordenesServicioView;

    public ServiciosMenuView(SubmenuVista tiposServicioView, SubmenuVista ordenesServicioView) {
        if (tiposServicioView == null) {
            throw new IllegalArgumentException("La vista de tipos de servicio es obligatoria.");
        }
        if (ordenesServicioView == null) {
            throw new IllegalArgumentException("La vista de órdenes de servicio es obligatoria.");
        }
        this.tiposServicioView = tiposServicioView;
        this.ordenesServicioView = ordenesServicioView;
    }

    @Override
    public String obtenerTitulo() {
        return "Servicios y Órdenes de Trabajo";
    }

    @Override
    public void mostrarMenu() {
        boolean ejecutando = true;
        while (ejecutando) {
            System.out.println("\n==================================================");
            System.out.println("         SERVICIOS Y ÓRDENES DE TRABAJO           ");
            System.out.println("==================================================");
            System.out.println("  1. Catálogo de tipos de servicio");
            System.out.println("  2. Órdenes de servicio");
            System.out.println("  0. Volver al menú principal");
            System.out.println("==================================================");

            int opcion = ConsoleReader.leerEnteroEnRango("Seleccione una opción", 0, 2);
            switch (opcion) {
                case 1:
                    tiposServicioView.mostrarMenu();
                    break;
                case 2:
                    ordenesServicioView.mostrarMenu();
                    break;
                case 0:
                    ejecutando = false;
                    break;
                default:
                    break;
            }
        }
    }
}
