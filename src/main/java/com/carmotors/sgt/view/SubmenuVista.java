package com.carmotors.sgt.view;

/**
 * Contrato común para los submenús de consola de cada módulo del sistema.
 * Cada módulo implementa esta interfaz y se conecta en {@link ConsolaMenuView}.
 */
public interface SubmenuVista {

    /**
     * Retorna el título visible del módulo en el menú principal.
     */
    String obtenerTitulo();

    /**
     * Inicia el ciclo de interacción por consola propio del módulo.
     */
    void mostrarMenu();
}
