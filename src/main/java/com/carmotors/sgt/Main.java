package com.carmotors.sgt;

import com.carmotors.sgt.view.ConsolaMenuView;

/**
 * Punto de entrada de la aplicación CarMotors SGT.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("CarMotors - Sistema de Gestión de Taller");

        ConsolaMenuView menuPrincipal = new ConsolaMenuView();

        // Conexión de submenús por módulo (implementar SubmenuVista en com.carmotors.sgt.view):
        // menuPrincipal.setSubmenuClientes(new ClienteView(clienteController));
        // menuPrincipal.setSubmenuProveedores(new ProveedorView(proveedorController));
        // menuPrincipal.setSubmenuInventario(new InventarioView(inventarioController));
        // menuPrincipal.setSubmenuTecnicos(new TecnicoView(tecnicoController));
        // menuPrincipal.setSubmenuServicios(new OrdenServicioView(ordenServicioController));
        // menuPrincipal.setSubmenuFacturacion(new FacturacionView(facturacionController));
        // menuPrincipal.setSubmenuReportes(new ReportesView(reportesController));

        menuPrincipal.mostrarMenuPrincipal();
    }
}
