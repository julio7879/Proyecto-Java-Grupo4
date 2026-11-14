package com.carmotors.sgt.view;

import com.carmotors.sgt.util.ConsoleReader;

/**
 * Esqueleto del menú principal por consola de CarMotors SGT (T1.3).
 * Orquesta únicamente la navegación de alto nivel hacia los submenús de cada módulo
 * usando {@link ConsoleReader}, sin lógica de negocio ni acceso directo a datos (RNF-03, RNF-24).
 */
public class ConsolaMenuView {

    private SubmenuVista submenuClientes;
    private SubmenuVista submenuProveedores;
    private SubmenuVista submenuInventario;
    private SubmenuVista submenuTecnicos;
    private SubmenuVista submenuServicios;
    private SubmenuVista submenuFacturacion;
    private SubmenuVista submenuReportes;

    public void setSubmenuClientes(SubmenuVista submenuClientes) {
        this.submenuClientes = submenuClientes;
    }

    public void setSubmenuProveedores(SubmenuVista submenuProveedores) {
        this.submenuProveedores = submenuProveedores;
    }

    public void setSubmenuInventario(SubmenuVista submenuInventario) {
        this.submenuInventario = submenuInventario;
    }

    public void setSubmenuTecnicos(SubmenuVista submenuTecnicos) {
        this.submenuTecnicos = submenuTecnicos;
    }

    public void setSubmenuServicios(SubmenuVista submenuServicios) {
        this.submenuServicios = submenuServicios;
    }

    public void setSubmenuFacturacion(SubmenuVista submenuFacturacion) {
        this.submenuFacturacion = submenuFacturacion;
    }

    public void setSubmenuReportes(SubmenuVista submenuReportes) {
        this.submenuReportes = submenuReportes;
    }

    /**
     * Inicia el ciclo principal de navegación por consola hasta que el usuario elija la opción {@code 0}.
     */
    public void mostrarMenuPrincipal() {
        boolean ejecutando = true;
        while (ejecutando) {
            imprimirOpciones();
            int opcion = ConsoleReader.leerEnteroEnRango("Seleccione una opción", 0, 7);

            switch (opcion) {
                case 1:
                    ejecutarSubmenu(submenuClientes, "Clientes y Vehículos");
                    break;
                case 2:
                    ejecutarSubmenu(submenuProveedores, "Proveedores");
                    break;
                case 3:
                    ejecutarSubmenu(submenuInventario, "Inventario y Lotes");
                    break;
                case 4:
                    ejecutarSubmenu(submenuTecnicos, "Técnicos y Especialidades");
                    break;
                case 5:
                    ejecutarSubmenu(submenuServicios, "Servicios y Órdenes de Trabajo");
                    break;
                case 6:
                    ejecutarSubmenu(submenuFacturacion, "Facturación");
                    break;
                case 7:
                    ejecutarSubmenu(submenuReportes, "Reportes Gerenciales");
                    break;
                case 0:
                    System.out.println("\nSaliendo de CarMotors SGT. ¡Hasta pronto!");
                    ejecutando = false;
                    break;
                default:
                    break;
            }
        }
    }

    private void imprimirOpciones() {
        System.out.println("\n==================================================");
        System.out.println("     CARMOTORS SGT - MENÚ PRINCIPAL DEL TALLER    ");
        System.out.println("==================================================");
        System.out.println("  1. Clientes y Vehículos");
        System.out.println("  2. Proveedores");
        System.out.println("  3. Inventario y Lotes");
        System.out.println("  4. Técnicos y Especialidades");
        System.out.println("  5. Servicios y Órdenes de Trabajo");
        System.out.println("  6. Facturación");
        System.out.println("  7. Reportes Gerenciales");
        System.out.println("  0. Salir");
        System.out.println("==================================================");
    }

    private void ejecutarSubmenu(SubmenuVista submenu, String nombrePorDefecto) {
        if (submenu == null) {
            System.out.println("\n  [i] El módulo '" + nombrePorDefecto + "' aún no está conectado.");
            ConsoleReader.pausar();
            return;
        }
        try {
            submenu.mostrarMenu();
        } catch (Exception e) {
            System.out.println("\n  [!] Error al ejecutar el módulo '" + submenu.obtenerTitulo() + "': " + e.getMessage());
            ConsoleReader.pausar();
        }
    }
}
