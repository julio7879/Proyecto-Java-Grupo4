/**
 * Vistas de consola: solo captura y muestra de datos usando {@code ConsoleReader},
 * sin lógica de negocio ni acceso a repositorios (RNF-03, RNF-24).
 *
 * <p>Cómo conectar un nuevo módulo al menú principal:</p>
 * <ol>
 *   <li>Crear la clase {@code <Modulo>View} en {@code com.carmotors.sgt.view} implementando {@link com.carmotors.sgt.view.SubmenuVista}.</li>
 *   <li>Recibir únicamente su controlador ({@code <Modulo>Controller}) por constructor y solicitar entradas con {@link com.carmotors.sgt.util.ConsoleReader}.</li>
 *   <li>Registrar la instancia en {@link com.carmotors.sgt.Main} usando el setter correspondiente en {@link com.carmotors.sgt.view.ConsolaMenuView} (ej. {@code menuPrincipal.setSubmenuClientes(...)}).</li>
 * </ol>
 */
package com.carmotors.sgt.view;
