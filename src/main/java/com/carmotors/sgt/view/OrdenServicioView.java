package com.carmotors.sgt.view;

import com.carmotors.sgt.controller.ClienteController;
import com.carmotors.sgt.controller.OrdenServicioController;
import com.carmotors.sgt.controller.ServicioTipoController;
import com.carmotors.sgt.exception.CarMotorsException;
import com.carmotors.sgt.model.Cliente;
import com.carmotors.sgt.model.ConsumoRepuesto;
import com.carmotors.sgt.model.ConsultaOrdenServicio;
import com.carmotors.sgt.model.DetalleRepuesto;
import com.carmotors.sgt.model.LineaOrdenSolicitada;
import com.carmotors.sgt.model.FiltroOrdenServicio;
import com.carmotors.sgt.model.ReservaLiberada;
import com.carmotors.sgt.model.ResultadoCancelarOrden;
import com.carmotors.sgt.model.ResultadoCompletarOrden;
import com.carmotors.sgt.model.ResumenOrdenServicio;
import com.carmotors.sgt.model.DetalleTecnico;
import com.carmotors.sgt.model.OrdenServicio;
import com.carmotors.sgt.model.OrdenServicioDetalle;
import com.carmotors.sgt.model.ServicioTipo;
import com.carmotors.sgt.model.Tecnico;
import com.carmotors.sgt.model.Vehiculo;
import com.carmotors.sgt.service.chain.SugerenciaTecnico;
import com.carmotors.sgt.util.ConsoleReader;
import com.carmotors.sgt.util.ConsoleTableFormatter;
import com.carmotors.sgt.util.InputValidator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Submenú de consola para las órdenes de servicio (RF-08, RF-10, RNF-03, RNF-24).
 * Solo captura datos e imprime resultados; las reglas residen en el servicio.
 */
public class OrdenServicioView implements SubmenuVista {

    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final OrdenServicioController ordenServicioController;
    private final ClienteController clienteController;
    private final ServicioTipoController servicioTipoController;

    public OrdenServicioView(OrdenServicioController ordenServicioController,
                             ClienteController clienteController,
                             ServicioTipoController servicioTipoController) {
        if (ordenServicioController == null) {
            throw new IllegalArgumentException("El controlador de órdenes de servicio es obligatorio.");
        }
        if (clienteController == null) {
            throw new IllegalArgumentException("El controlador de clientes y vehículos es obligatorio.");
        }
        if (servicioTipoController == null) {
            throw new IllegalArgumentException("El controlador de tipos de servicio es obligatorio.");
        }
        this.ordenServicioController = ordenServicioController;
        this.clienteController = clienteController;
        this.servicioTipoController = servicioTipoController;
    }

    @Override
    public String obtenerTitulo() {
        return "Órdenes de Servicio";
    }

    @Override
    public void mostrarMenu() {
        boolean ejecutando = true;
        while (ejecutando) {
            System.out.println("\n==================================================");
            System.out.println("              ÓRDENES DE SERVICIO                 ");
            System.out.println("==================================================");
            System.out.println("  -- Creación, Líneas, Técnicos y Repuestos --");
            System.out.println("  1. Crear orden de servicio");
            System.out.println("  2. Agregar línea de servicio");
            System.out.println("  3. Retirar línea de servicio");
            System.out.println("  4. Ajustar mano de obra de una línea");
            System.out.println("  5. Asignar técnicos a una línea");
            System.out.println("  6. Registrar horas trabajadas de un técnico");
            System.out.println("  7. Reservar repuestos en una línea");
            System.out.println("  8. Retirar repuestos de una línea");
            System.out.println("  -- Consultas --");
            System.out.println("  9. Listar órdenes de servicio");
            System.out.println(" 10. Ver detalle de una orden");
            System.out.println("  -- Transiciones de Estado --");
            System.out.println(" 11. Pasar orden a En Proceso");
            System.out.println(" 12. Completar Orden de Servicio");
            System.out.println(" 13. Cancelar orden");
            System.out.println(" 14. Entregar orden");
            System.out.println("  0. Volver al menú anterior");
            System.out.println("==================================================");

            int opcion = ConsoleReader.leerEnteroEnRango("Seleccione una opción", 0, 14);
            switch (opcion) {
                case 1:
                    crearOrden();
                    break;
                case 2:
                    agregarLinea();
                    break;
                case 3:
                    quitarLinea();
                    break;
                case 4:
                    ajustarManoObraLinea();
                    break;
                case 5:
                    asignarTecnicosALinea();
                    break;
                case 6:
                    registrarHorasTrabajadas();
                    break;
                case 7:
                    reservarRepuesto();
                    break;
                case 8:
                    retirarRepuesto();
                    break;
                case 9:
                    listarOrdenes();
                    break;
                case 10:
                    consultarDetalle();
                    break;
                case 11:
                    pasarAEnProceso();
                    break;
                case 12:
                    completarOrden();
                    break;
                case 13:
                    cancelarOrden();
                    break;
                case 14:
                    entregarOrden();
                    break;
                case 0:
                    ejecutando = false;
                    break;
                default:
                    break;
            }
        }
    }

    private void listarOrdenes() {
        try {
            System.out.println("\n  Estados de la orden");
            for (int estadoId = 1; estadoId <= 6; estadoId++) {
                System.out.println("    " + estadoId + ". " + ordenServicioController.obtenerNombreEstado(estadoId));
            }
            int estado = ConsoleReader.leerEnteroEnRango("Estado (0 para todos)", 0, 6);
            int tecnico = ConsoleReader.leerEnteroEnRango("ID del técnico (0 para todos)", 0, Integer.MAX_VALUE);
            String placa = ConsoleReader.leerTextoOpcional("Placa del vehículo (Enter para omitir)");
            int tipo = ConsoleReader.leerEnteroEnRango("ID del tipo de servicio (0 para todos)", 0, Integer.MAX_VALUE);
            LocalDate desde = ConsoleReader.leerFechaOpcional("Recepción desde (yyyy-MM-dd, Enter para omitir)");
            LocalDate hasta = ConsoleReader.leerFechaOpcional("Recepción hasta (yyyy-MM-dd, Enter para omitir)");
            FiltroOrdenServicio filtro = new FiltroOrdenServicio(
                    estado == 0 ? null : estado, tecnico == 0 ? null : tecnico, placa,
                    tipo == 0 ? null : tipo, desde, hasta);

            List<ResumenOrdenServicio> ordenes = ordenServicioController.consultarOrdenes(filtro);
            if (ordenes.isEmpty()) {
                System.out.println("\n  [i] No se encontraron órdenes con los filtros indicados.");
            } else {
                System.out.println("\n  [Órdenes de servicio: " + ordenes.size() + "]");
                List<String[]> filas = new ArrayList<>();
                for (ResumenOrdenServicio orden : ordenes) {
                    String tecnicos = orden.getTecnicos().isEmpty() ? "Sin asignar"
                            : String.join(", ", orden.getTecnicos().values());
                    filas.add(new String[]{
                            String.valueOf(orden.getId()),
                            orden.getPlaca(),
                            ordenServicioController.obtenerNombreEstado(orden.getEstadoId()),
                            orden.getFechaRecepcion().format(FORMATO_FECHA_HORA),
                            tecnicos
                    });
                }
                ConsoleTableFormatter.imprimirTabla(
                        new String[]{"Orden", "Placa", "Estado", "Fecha recepción", "Técnicos"},
                        filas,
                        new boolean[]{true, false, false, false, false});
            }
        } catch (CarMotorsException e) {
            System.out.println("\n  [!] " + e.getMessage());
        } finally {
            ConsoleReader.pausar();
        }
    }

    private void consultarDetalle() {
        int ordenId = ConsoleReader.leerEnteroPositivo("ID de la orden");
        try {
            imprimirDetalle(ordenServicioController.consultarDetalle(ordenId));
        } catch (CarMotorsException e) {
            System.out.println("  [!] " + e.getMessage());
        }
        ConsoleReader.pausar();
    }

    private void imprimirDetalle(ConsultaOrdenServicio consulta) {
        OrdenServicio orden = consulta.getOrden();
        Cliente cliente = consulta.getCliente();
        Vehiculo vehiculo = consulta.getVehiculo();
        System.out.println("\n  Orden de servicio " + orden.getId());
        System.out.println("    Estado: " + ordenServicioController.obtenerNombreEstado(orden.getEstadoId()));
        System.out.println("    Cliente: " + cliente.getNombre() + " | Documento: " + cliente.getDocumentoIdentidad());
        System.out.println("    Teléfono: " + textoDisponible(cliente.getTelefono())
                + " | Correo: " + textoDisponible(cliente.getEmail()));
        System.out.println("    Vehículo: " + vehiculo.getPlaca() + " | " + vehiculo.getMarca() + " " + vehiculo.getModelo());
        imprimirFecha("Recepción", orden.getFechaRecepcion());
        imprimirFecha("Finalización", orden.getFechaCompletado());
        imprimirFecha("Entrega", orden.getFechaEntrega());
        imprimirFecha("Cancelación", orden.getFechaCancelacion());
        System.out.println("    Problema: " + textoDisponible(orden.getDescripcionProblema()));

        for (ConsultaOrdenServicio.Linea linea : consulta.getLineas()) {
            OrdenServicioDetalle detalle = linea.getDetalle();
            System.out.println("\n    Línea " + detalle.getId() + " | " + detalle.getNombreServicioSnapshot());
            System.out.println("      Observaciones: " + textoDisponible(detalle.getObservaciones()));
            System.out.println("      Técnicos y horas:");
            if (linea.getHorasPorTecnico().isEmpty()) {
                System.out.println("        Sin técnicos asignados.");
            } else {
                List<String[]> filasTecnicos = new ArrayList<>();
                for (ConsultaOrdenServicio.HorasTecnico asignacion : linea.getHorasPorTecnico()) {
                    Tecnico tecnico = asignacion.getTecnico();
                    filasTecnicos.add(new String[]{
                            String.valueOf(tecnico.getId()),
                            tecnico.getNombre(),
                            asignacion.getHorasTrabajadas().toPlainString() + " h"
                    });
                }
                ConsoleTableFormatter.imprimirTabla(
                        new String[]{"ID", "Técnico", "Horas"},
                        filasTecnicos,
                        new boolean[]{true, false, true});
            }
            System.out.println("      Repuestos:");
            if (linea.getRepuestos().isEmpty()) {
                System.out.println("        Sin repuestos asignados.");
            } else {
                List<String[]> filasRepuestos = new ArrayList<>();
                for (DetalleRepuesto pieza : linea.getRepuestos()) {
                    filasRepuestos.add(new String[]{
                            pieza.getNombreRepuestoSnapshot(),
                            consulta.getEstadoRepuestos(),
                            String.valueOf(pieza.getCantidadAsignada()),
                            "$" + pieza.getPrecioUnitarioVenta().toPlainString()
                    });
                }
                ConsoleTableFormatter.imprimirTabla(
                        new String[]{"Repuesto", "Estado", "Cantidad", "Precio unit."},
                        filasRepuestos,
                        new boolean[]{false, false, true, true});
            }
            System.out.println("      Mano de obra: $" + detalle.getCostoManoObra().toPlainString());
            System.out.println("      Subtotal: $" + linea.getSubtotal().toPlainString());
        }
    }

    private void imprimirFecha(String etiqueta, LocalDateTime fecha) {
        if (fecha != null) {
            System.out.println("    " + etiqueta + ": " + fecha.format(FORMATO_FECHA_HORA));
        }
    }

    private String textoDisponible(String texto) {
        return texto == null || texto.trim().isEmpty() ? "Sin registrar" : texto;
    }

    private void crearOrden() {
        try {
            String placa = ConsoleReader.leerPlaca("Placa del vehículo");
            Vehiculo vehiculo = clienteController.buscarVehiculoPorPlaca(placa);
            System.out.println("  Vehículo: " + vehiculo.getPlaca() + " | " + vehiculo.getMarca()
                    + " " + vehiculo.getModelo());

            String descripcion = ConsoleReader.leerTextoObligatorio("Descripción del problema");

            List<ServicioTipo> tipos = servicioTipoController.listarTodos();
            if (tipos.isEmpty()) {
                System.out.println("\n  [!] No hay tipos de servicio registrados; no se puede crear la orden.");
                ConsoleReader.pausar();
                return;
            }

            List<LineaOrdenSolicitada> lineas = new ArrayList<>();
            boolean agregarOtra = true;
            while (agregarOtra) {
                System.out.println("\n  --- Línea de servicio #" + (lineas.size() + 1) + " ---");
                ServicioTipo tipo = seleccionarTipoServicio(tipos);
                if (tipo == null) {
                    if (lineas.isEmpty()) {
                        System.out.println("\n  [i] Creación de orden cancelada.");
                        ConsoleReader.pausar();
                        return;
                    }
                    break;
                }
                String observaciones = ConsoleReader.leerTextoOpcional("Observaciones (Enter para omitir)");
                OrdenServicioDetalle linea = new OrdenServicioDetalle(tipo.getId(), observaciones);
                lineas.add(new LineaOrdenSolicitada(linea, leerReservasIniciales()));
                agregarOtra = ConsoleReader.leerConfirmacion("¿Desea agregar otra línea de servicio?");
            }

            OrdenServicio creada = ordenServicioController.crearOrdenIntegral(vehiculo.getId(), descripcion, lineas);
            System.out.println("\n  [✓] Orden de servicio creada exitosamente.");
            imprimirResumen(creada, vehiculo);
        } catch (CarMotorsException e) {
            System.out.println("\n  [!] Error al crear la orden de servicio: " + e.getMessage());
        }
        ConsoleReader.pausar();
    }

    private List<DetalleRepuesto> leerReservasIniciales() {
        List<DetalleRepuesto> reservas = new ArrayList<>();
        while (ConsoleReader.leerConfirmacion("¿Desea reservar un repuesto para esta línea?")) {
            DetalleRepuesto reserva = new DetalleRepuesto();
            reserva.setRepuestoId(ConsoleReader.leerEnteroPositivo("ID del repuesto"));
            reserva.setCantidadAsignada(ConsoleReader.leerEnteroPositivo("Cantidad a reservar"));
            reservas.add(reserva);
        }
        return reservas;
    }

    private void agregarLinea() {
        try {
            int ordenId = ConsoleReader.leerEnteroPositivo("ID de la orden");
            boolean requiereTecnico = ordenServicioController.requiereTecnicoParaAgregar(ordenId);
            List<ServicioTipo> tipos = servicioTipoController.listarTodos();
            if (tipos.isEmpty()) {
                System.out.println("  [!] No hay tipos de servicio registrados.");
                return;
            }

            ServicioTipo tipo = seleccionarTipoServicio(tipos);
            if (tipo == null) {
                System.out.println("  [i] Operación cancelada.");
                return;
            }
            String observaciones = ConsoleReader.leerTextoOpcional("Observaciones (Enter para omitir)");
            Integer tecnicoId = null;
            boolean confirmarOtraEspecialidad = false;
            if (requiereTecnico) {
                List<Tecnico> tecnicos = ordenServicioController.listarTecnicosActivos();
                if (tecnicos.isEmpty()) {
                    System.out.println("  [!] No hay técnicos activos para asignar.");
                    return;
                }
                SugerenciaTecnico sugerencia = ordenServicioController.sugerirTecnicoParaTipoServicio(ordenId, tipo.getId());
                mostrarTecnicosActivos(tecnicos);
                int seleccion = solicitarTecnicoParaAsignar(sugerencia);
                if (seleccion == 0) {
                    System.out.println("  [i] Operación cancelada.");
                    return;
                }
                Tecnico tecnico = ordenServicioController.buscarTecnico(seleccion);
                if (!ordenServicioController.especialidadCoincide(tipo, tecnico)) {
                    confirmarOtraEspecialidad = ConsoleReader.leerConfirmacion(
                            "La especialidad del técnico difiere de la requerida (RN-19). ¿Desea asignarlo?");
                    if (!confirmarOtraEspecialidad) {
                        return;
                    }
                }
                tecnicoId = seleccion;
            }
            OrdenServicioDetalle linea = new OrdenServicioDetalle(tipo.getId(), observaciones);
            System.out.println("  " + ordenServicioController.agregarLinea(
                    ordenId, linea, tecnicoId, confirmarOtraEspecialidad));
        } catch (CarMotorsException e) {
            System.out.println("  [!] " + e.getMessage());
        } finally {
            ConsoleReader.pausar();
        }
    }

    private void quitarLinea() {
        try {
            int ordenId = ConsoleReader.leerEnteroPositivo("ID de la orden");
            ordenServicioController.validarOrdenEditable(ordenId);
            List<OrdenServicioDetalle> lineas = ordenServicioController.listarLineas(ordenId);
            if (lineas.isEmpty()) {
                System.out.println("  [!] La orden no tiene líneas de servicio.");
                return;
            }
            int detalleId = seleccionarLinea(ordenId, lineas);
            if (detalleId == 0) {
                System.out.println("  [i] Operación cancelada.");
                return;
            }
            if (!ConsoleReader.leerConfirmacion("¿Confirma retirar la línea " + detalleId
                    + " de la orden " + ordenId + "?")) {
                System.out.println("  [i] Operación cancelada.");
                return;
            }
            System.out.println("  " + ordenServicioController.quitarLinea(ordenId, detalleId));
        } catch (CarMotorsException e) {
            System.out.println("  [!] " + e.getMessage());
        } finally {
            ConsoleReader.pausar();
        }
    }

    private void ajustarManoObraLinea() {
        try {
            int ordenId = ConsoleReader.leerEnteroPositivo("ID de la orden");
            ordenServicioController.validarOrdenEditable(ordenId);
            List<OrdenServicioDetalle> lineas = ordenServicioController.listarLineas(ordenId);
            if (lineas.isEmpty()) {
                System.out.println("  [!] La orden no tiene líneas de servicio.");
                return;
            }
            imprimirLineas(lineas);
            OrdenServicioDetalle seleccionada = null;
            while (seleccionada == null) {
                int detalleId = ConsoleReader.leerEnteroEnRango("ID de la línea (0 para cancelar)", 0, Integer.MAX_VALUE);
                if (detalleId == 0) {
                    System.out.println("  [i] Operación cancelada.");
                    return;
                }
                for (OrdenServicioDetalle linea : lineas) {
                    if (linea.getId() == detalleId) {
                        seleccionada = linea;
                        break;
                    }
                }
                if (seleccionada == null) {
                    System.out.println("  [!] Error: La línea no pertenece a la orden " + ordenId + ".");
                }
            }
            System.out.println("  Mano de obra actual: $" + seleccionada.getCostoManoObra().toPlainString());
            BigDecimal nuevoCosto = ConsoleReader.leerBigDecimalNoNegativo("Nuevo costo de mano de obra (COP)");
            System.out.println("  " + ordenServicioController.actualizarCostoManoObra(
                    ordenId, seleccionada.getId(), nuevoCosto));
        } catch (CarMotorsException e) {
            System.out.println("  [!] " + e.getMessage());
        } finally {
            ConsoleReader.pausar();
        }
    }

    private void asignarTecnicosALinea() {
        try {
            int ordenId = ConsoleReader.leerEnteroPositivo("ID de la orden");
            ordenServicioController.validarOrdenEditable(ordenId);
            imprimirLineas(ordenServicioController.listarLineas(ordenId));
            int detalleId = ConsoleReader.leerEnteroEnRango("ID de la línea (0 para cancelar)", 0, Integer.MAX_VALUE);
            if (detalleId == 0) {
                return;
            }

            boolean asignarOtro = true;
            while (asignarOtro) {
                List<DetalleTecnico> asignados = ordenServicioController.listarTecnicosDeLinea(ordenId, detalleId);
                if (!asignados.isEmpty()) {
                    System.out.println("\n  Técnicos actualmente asignados a la línea " + detalleId + ":");
                    mostrarTecnicosAsignados(asignados);
                }

                List<Tecnico> activos = ordenServicioController.listarTecnicosActivos();
                if (activos.isEmpty()) {
                    System.out.println("  [!] No hay técnicos activos disponibles para asignar.");
                    return;
                }

                SugerenciaTecnico sugerencia = ordenServicioController.sugerirTecnicoParaLinea(ordenId, detalleId);
                mostrarTecnicosActivos(activos);

                int tecnicoId = solicitarTecnicoParaAsignar(sugerencia);
                if (tecnicoId == 0) {
                    return;
                }

                procesarAsignacionTecnico(ordenId, detalleId, tecnicoId);
                asignarOtro = ConsoleReader.leerConfirmacion("¿Desea asignar otro técnico a esta línea?");
            }
        } catch (CarMotorsException e) {
            System.out.println("  [!] " + e.getMessage());
        } finally {
            ConsoleReader.pausar();
        }
    }

    private void registrarHorasTrabajadas() {
        try {
            int ordenId = ConsoleReader.leerEnteroPositivo("ID de la orden");
            ordenServicioController.validarOrdenEditable(ordenId);
            imprimirLineas(ordenServicioController.listarLineas(ordenId));
            int detalleId = ConsoleReader.leerEnteroEnRango("ID de la línea (0 para cancelar)", 0, Integer.MAX_VALUE);
            if (detalleId == 0) {
                return;
            }

            List<DetalleTecnico> asignados = ordenServicioController.listarTecnicosDeLinea(ordenId, detalleId);
            if (asignados.isEmpty()) {
                System.out.println("  [i] La línea " + detalleId + " no tiene técnicos asignados todavía.");
                return;
            }

            System.out.println("\n  Técnicos asignados a la línea " + detalleId + ":");
            mostrarTecnicosAsignados(asignados);

            int tecnicoId = ConsoleReader.leerEnteroEnRango("ID del técnico (0 para cancelar)", 0, Integer.MAX_VALUE);
            if (tecnicoId == 0) {
                return;
            }

            BigDecimal horas = ConsoleReader.leerHorasNoNegativas("Horas trabajadas totales del técnico en esta línea");
            System.out.println("  " + ordenServicioController.registrarHorasTrabajadas(ordenId, detalleId, tecnicoId, horas));
        } catch (CarMotorsException e) {
            System.out.println("  [!] " + e.getMessage());
        } finally {
            ConsoleReader.pausar();
        }
    }

    private void procesarAsignacionTecnico(int ordenId, int detalleId, int tecnicoId) {
        try {
            boolean requiereConfirmacion = ordenServicioController.requiereConfirmacionEspecialidad(
                    ordenId, detalleId, tecnicoId);
            boolean confirmarOtraEspecialidad = false;
            if (requiereConfirmacion) {
                confirmarOtraEspecialidad = ConsoleReader.leerConfirmacion(
                        "La especialidad del técnico no coincide con la requerida (RN-19). ¿Desea asignarlo?");
                if (!confirmarOtraEspecialidad) {
                    System.out.println("  [i] Asignación de técnico cancelada.");
                    return;
                }
            }
            BigDecimal horas = ConsoleReader.leerHorasNoNegativas("Horas trabajadas (0 si aún no inicia)");
            System.out.println("  " + ordenServicioController.asignarTecnicoALinea(
                    ordenId, detalleId, tecnicoId, horas, confirmarOtraEspecialidad));
        } catch (CarMotorsException e) {
            System.out.println("  [!] " + e.getMessage());
        }
    }

    private int solicitarTecnicoParaAsignar(SugerenciaTecnico sugerencia) {
        if (sugerencia == null) {
            System.out.println("  [i] No hay técnico sugerido disponible para esta línea.");
            return ConsoleReader.leerEnteroEnRango("ID del técnico (0 para cancelar)", 0, Integer.MAX_VALUE);
        }

        Tecnico recomendado = sugerencia.getTecnico();
        String especialidad = ordenServicioController.obtenerNombreEspecialidad(recomendado.getEspecialidadId());
        System.out.println("\n  --- Técnico Sugerido ---");
        System.out.println("    ID:           " + recomendado.getId());
        System.out.println("    Nombre:       " + recomendado.getNombre());
        System.out.println("    Especialidad: " + especialidad);
        System.out.println("    Carga actual: " + sugerencia.getCargaActual() + " línea(s) activa(s)");
        System.out.println("    Criterio:     " + sugerencia.getCriterioAplicado());
        if (!sugerencia.isEspecialidadCoincide()) {
            System.out.println("  [!] Advertencia (RN-19): El técnico sugerido pertenece a otra especialidad.");
        }

        String prompt = "Presione ENTER para aceptar al sugerido [" + recomendado.getId()
                + " - " + recomendado.getNombre() + "], ingrese otro ID o 0 para cancelar";
        while (true) {
            String entrada = ConsoleReader.leerTextoOpcional(prompt);
            if (entrada.isEmpty()) {
                return recomendado.getId();
            }
            if (InputValidator.esEntero(entrada)) {
                int valor = InputValidator.convertirAEntero(entrada);
                if (valor >= 0) {
                    return valor;
                }
            }
            System.out.println("  [!] Error: Ingrese un ID entero válido (0 para cancelar) o presione Enter.");
        }
    }

    private void mostrarTecnicosActivos(List<Tecnico> activos) {
        System.out.println("\n  Técnicos activos disponibles:");
        List<String[]> filas = new ArrayList<>();
        for (Tecnico tecnico : activos) {
            String nombreEsp = ordenServicioController.obtenerNombreEspecialidad(tecnico.getEspecialidadId());
            filas.add(new String[]{
                    String.valueOf(tecnico.getId()),
                    tecnico.getNombre(),
                    nombreEsp
            });
        }
        ConsoleTableFormatter.imprimirTabla(
                new String[]{"ID", "Técnico", "Especialidad"},
                filas,
                new boolean[]{true, false, false});
    }

    private void mostrarTecnicosAsignados(List<DetalleTecnico> asignados) {
        List<String[]> filas = new ArrayList<>();
        for (DetalleTecnico asignacion : asignados) {
            Tecnico tecnico = ordenServicioController.buscarTecnico(asignacion.getTecnicoId());
            String nombre = tecnico != null ? tecnico.getNombre() : "ID " + asignacion.getTecnicoId();
            filas.add(new String[]{
                    String.valueOf(asignacion.getTecnicoId()),
                    nombre,
                    asignacion.getHorasTrabajadas() + " h"
            });
        }
        ConsoleTableFormatter.imprimirTabla(
                new String[]{"ID", "Técnico", "Horas"},
                filas,
                new boolean[]{true, false, true});
    }

    private void reservarRepuesto() {
        try {
            int ordenId = ConsoleReader.leerEnteroPositivo("ID de la orden de servicio");
            ordenServicioController.validarOrdenEditable(ordenId);
            List<OrdenServicioDetalle> lineas = ordenServicioController.listarLineas(ordenId);
            if (lineas.isEmpty()) {
                System.out.println("\n  [!] La orden no tiene líneas de servicio.");
                return;
            }
            int detalleId = seleccionarLinea(ordenId, lineas);
            if (detalleId == 0) {
                System.out.println("  [i] Operación cancelada.");
                return;
            }
            int repuestoId = ConsoleReader.leerEnteroPositivo("ID del repuesto");
            int cantidad = ConsoleReader.leerEnteroPositivo("Cantidad a reservar");

            DetalleRepuesto reserva = ordenServicioController.reservarRepuesto(detalleId, repuestoId, cantidad);
            System.out.println("\n  [✓] Repuesto reservado exitosamente.");
            System.out.println("  Línea:             " + reserva.getDetalleId());
            System.out.println("  Repuesto:          " + reserva.getRepuestoId() + " | " + reserva.getNombreRepuestoSnapshot());
            System.out.println("  Cantidad asignada: " + reserva.getCantidadAsignada());
            System.out.println("  Precio unitario:   $" + reserva.getPrecioUnitarioVenta());
            System.out.println("  Disponible restante: " + ordenServicioController.consultarDisponible(repuestoId));
        } catch (CarMotorsException e) {
            System.out.println("\n  [!] Error al reservar el repuesto: " + e.getMessage());
        } finally {
            ConsoleReader.pausar();
        }
    }

    private void pasarAEnProceso() {
        try {
            int ordenId = ConsoleReader.leerEnteroPositivo("ID de la orden de servicio");
            if (!ConsoleReader.leerConfirmacion("¿Confirma pasar la orden " + ordenId
                    + " a estado En Proceso?")) {
                System.out.println("\n  [i] Operación cancelada; la orden no cambió.");
                return;
            }
            OrdenServicio orden = ordenServicioController.pasarAEnProceso(ordenId);
            System.out.println("\n  [✓] Orden " + orden.getId() + " pasada a estado "
                    + ordenServicioController.obtenerNombreEstado(orden.getEstadoId()) + ".");
        } catch (CarMotorsException e) {
            System.out.println("\n  [!] No se pudo pasar la orden a En Proceso: " + e.getMessage());
        } finally {
            ConsoleReader.pausar();
        }
    }

    private void completarOrden() {
        try {
            int ordenId = ConsoleReader.leerEnteroPositivo("ID de la orden de servicio");
            boolean confirmar = ConsoleReader.leerConfirmacion("Completar la orden " + ordenId
                    + " descuenta el inventario y no se puede deshacer. ¿Desea continuar?");
            if (!confirmar) {
                System.out.println("\n  [i] Operación cancelada; la orden no cambió.");
                return;
            }

            ResultadoCompletarOrden resultado = ordenServicioController.completarOrden(ordenId);
            OrdenServicio orden = resultado.getOrden();
            System.out.println("\n  [✓] Orden " + orden.getId() + " completada.");
            System.out.println("  Estado:            " + ordenServicioController.obtenerNombreEstado(orden.getEstadoId()));
            System.out.println("  Fecha completado:  " + orden.getFechaCompletado().format(FORMATO_FECHA_HORA));
            if (resultado.getConsumos().isEmpty()) {
                System.out.println("  Repuestos:         la orden no tenía repuestos; no se descontó inventario.");
            } else {
                System.out.println("  Repuestos descontados del inventario:");
                List<String[]> filas = new ArrayList<>();
                for (ConsumoRepuesto consumo : resultado.getConsumos()) {
                    filas.add(new String[]{
                            String.valueOf(consumo.getRepuestoId()),
                            consumo.getNombreRepuesto(),
                            String.valueOf(consumo.getUnidadesDescontadas())
                    });
                }
                ConsoleTableFormatter.imprimirTabla(
                        new String[]{"ID Rep.", "Repuesto", "Unidades descontadas"},
                        filas,
                        new boolean[]{true, false, true});
            }
        } catch (CarMotorsException e) {
            System.out.println("\n  [!] No se pudo completar la orden: " + e.getMessage());
        } finally {
            ConsoleReader.pausar();
        }
    }

    private void cancelarOrden() {
        try {
            int ordenId = ConsoleReader.leerEnteroPositivo("ID de la orden de servicio");
            boolean confirmar = ConsoleReader.leerConfirmacion("Cancelar la orden " + ordenId
                    + " liberará todas sus reservas y no se puede deshacer. ¿Desea continuar?");
            if (!confirmar) {
                System.out.println("\n  [i] Operación cancelada; la orden no cambió.");
                return;
            }

            ResultadoCancelarOrden resultado = ordenServicioController.cancelarOrden(ordenId);
            OrdenServicio orden = resultado.getOrden();
            System.out.println("\n  [✓] Orden " + orden.getId() + " cancelada.");
            System.out.println("  Estado:             " + ordenServicioController.obtenerNombreEstado(orden.getEstadoId()));
            System.out.println("  Fecha cancelación:  " + orden.getFechaCancelacion().format(FORMATO_FECHA_HORA));
            if (resultado.getReservasLiberadas().isEmpty()) {
                System.out.println("  Reservas:           la orden no tenía repuestos reservados.");
            } else {
                System.out.println("  Reservas de repuestos liberadas:");
                List<String[]> filas = new ArrayList<>();
                for (ReservaLiberada reserva : resultado.getReservasLiberadas()) {
                    filas.add(new String[]{
                            String.valueOf(reserva.getRepuestoId()),
                            reserva.getNombreRepuesto(),
                            String.valueOf(reserva.getUnidadesLiberadas())
                    });
                }
                ConsoleTableFormatter.imprimirTabla(
                        new String[]{"ID Rep.", "Repuesto", "Unidades liberadas"},
                        filas,
                        new boolean[]{true, false, true});
            }
        } catch (CarMotorsException e) {
            System.out.println("\n  [!] No se pudo cancelar la orden: " + e.getMessage());
        } finally {
            ConsoleReader.pausar();
        }
    }

    private void entregarOrden() {
        try {
            int ordenId = ConsoleReader.leerEnteroPositivo("ID de la orden de servicio");
            boolean confirmar = ConsoleReader.leerConfirmacion("Entregar la orden " + ordenId
                    + " solo es posible si está facturada. ¿Desea continuar?");
            if (!confirmar) {
                System.out.println("\n  [i] Operación cancelada; la orden no cambió.");
                return;
            }

            OrdenServicio orden = ordenServicioController.entregarOrden(ordenId);
            System.out.println("\n  [✓] Orden " + orden.getId() + " entregada.");
            System.out.println("  Estado:         " + ordenServicioController.obtenerNombreEstado(orden.getEstadoId()));
            System.out.println("  Fecha entrega:  " + orden.getFechaEntrega().format(FORMATO_FECHA_HORA));
        } catch (CarMotorsException e) {
            System.out.println("\n  [!] No se pudo entregar la orden: " + e.getMessage());
        } finally {
            ConsoleReader.pausar();
        }
    }

    private void retirarRepuesto() {
        try {
            int ordenId = ConsoleReader.leerEnteroPositivo("ID de la orden de servicio");
            ordenServicioController.validarOrdenEditable(ordenId);
            List<OrdenServicioDetalle> lineas = ordenServicioController.listarLineas(ordenId);
            if (lineas.isEmpty()) {
                System.out.println("\n  [!] La orden no tiene líneas de servicio.");
                return;
            }
            int detalleId = seleccionarLinea(ordenId, lineas);
            if (detalleId == 0) {
                System.out.println("  [i] Operación cancelada.");
                return;
            }

            List<DetalleRepuesto> reservas = ordenServicioController.listarRepuestosDeLinea(detalleId);
            if (reservas.isEmpty()) {
                System.out.println("\n  [i] La línea " + detalleId + " no tiene repuestos reservados.");
                return;
            }

            System.out.println("\n  Repuestos reservados en la línea " + detalleId + ":");
            List<String[]> filas = new ArrayList<>();
            for (DetalleRepuesto reserva : reservas) {
                filas.add(new String[]{
                        String.valueOf(reserva.getRepuestoId()),
                        reserva.getNombreRepuestoSnapshot(),
                        String.valueOf(reserva.getCantidadAsignada()),
                        "$" + reserva.getPrecioUnitarioVenta().toPlainString()
                });
            }
            ConsoleTableFormatter.imprimirTabla(
                    new String[]{"ID Rep.", "Repuesto", "Cantidad", "Precio unit."},
                    filas,
                    new boolean[]{true, false, true, true});

            DetalleRepuesto seleccionada = seleccionarRepuestoReservado(reservas);
            if (seleccionada == null) {
                System.out.println("  [i] Retiro de repuesto cancelado.");
                return;
            }
            System.out.println("  Reservado actual en la línea: "
                    + seleccionada.getCantidadAsignada() + " unidad(es) a liberar.");

            boolean confirmar = ConsoleReader.leerConfirmacion("¿Confirma retirar '"
                    + seleccionada.getNombreRepuestoSnapshot() + "' de la línea " + detalleId + "?");
            if (!confirmar) {
                System.out.println("  [i] Retiro de repuesto cancelado.");
                return;
            }

            String resultado = ordenServicioController.retirarRepuesto(detalleId, seleccionada.getRepuestoId());
            System.out.println("  " + resultado);
            if (!resultado.startsWith("No se pudo")) {
                System.out.println("  Disponible del repuesto tras liberar la reserva: "
                        + ordenServicioController.consultarDisponible(seleccionada.getRepuestoId()));
            }
        } catch (CarMotorsException e) {
            System.out.println("\n  [!] Error al retirar el repuesto: " + e.getMessage());
        } finally {
            ConsoleReader.pausar();
        }
    }

    private DetalleRepuesto seleccionarRepuestoReservado(List<DetalleRepuesto> reservas) {
        while (true) {
            int idElegido = ConsoleReader.leerEnteroEnRango("ID del repuesto a retirar (0 para cancelar)",
                    0, Integer.MAX_VALUE);
            if (idElegido == 0) {
                return null;
            }
            for (DetalleRepuesto reserva : reservas) {
                if (reserva.getRepuestoId() == idElegido) {
                    return reserva;
                }
            }
            System.out.println("  [!] Error: El repuesto indicado no está reservado en esta línea.");
        }
    }

    private int seleccionarLinea(int ordenId, List<OrdenServicioDetalle> lineas) {
        System.out.println("  Líneas de la orden " + ordenId + ":");
        for (OrdenServicioDetalle linea : lineas) {
            System.out.println("    - Línea " + linea.getId() + " | " + linea.getNombreServicioSnapshot());
        }
        while (true) {
            int idElegido = ConsoleReader.leerEnteroEnRango("ID de la línea (0 para cancelar)",
                    0, Integer.MAX_VALUE);
            if (idElegido == 0) {
                return 0;
            }
            for (OrdenServicioDetalle linea : lineas) {
                if (linea.getId() == idElegido) {
                    return idElegido;
                }
            }
            System.out.println("  [!] Error: La línea no pertenece a la orden " + ordenId + ".");
        }
    }

    private ServicioTipo seleccionarTipoServicio(List<ServicioTipo> tipos) {
        System.out.println("  Tipos de servicio disponibles:");
        for (ServicioTipo tipo : tipos) {
            System.out.println("    " + tipo.getId() + ". " + tipo.getNombre()
                    + " | Mano de obra base: $" + tipo.getPrecioManoObraBase());
        }
        while (true) {
            int idElegido = ConsoleReader.leerEnteroEnRango(
                    "ID del tipo de servicio (0 para cancelar)", 0, Integer.MAX_VALUE);
            if (idElegido == 0) {
                return null;
            }
            for (ServicioTipo tipo : tipos) {
                if (tipo.getId() == idElegido) {
                    return tipo;
                }
            }
            System.out.println("  [!] Error: El ID no pertenece al catálogo de tipos de servicio.");
        }
    }

    private void imprimirResumen(OrdenServicio orden, Vehiculo vehiculo) {
        System.out.println("\n--- Resumen de la Orden ---");
        System.out.println("  Orden ID:          " + orden.getId());
        System.out.println("  Placa:             " + vehiculo.getPlaca());
        System.out.println("  Estado:            " + ordenServicioController.obtenerNombreEstado(orden.getEstadoId()));
        System.out.println("  Fecha recepción:   " + orden.getFechaRecepcion().format(FORMATO_FECHA_HORA));
        System.out.println("  Problema:          " + orden.getDescripcionProblema());
        System.out.println("  Líneas de servicio:");
        imprimirLineas(ordenServicioController.listarLineas(orden.getId()));
    }

    private void imprimirLineas(List<OrdenServicioDetalle> lineas) {
        List<String[]> filas = new ArrayList<>();
        for (OrdenServicioDetalle linea : lineas) {
            String observaciones = linea.getObservaciones() == null ? "N/A" : linea.getObservaciones();
            filas.add(new String[]{
                    String.valueOf(linea.getId()),
                    linea.getNombreServicioSnapshot(),
                    "$" + linea.getCostoManoObra(),
                    observaciones
            });
        }
        ConsoleTableFormatter.imprimirTabla(
                new String[]{"Línea", "Servicio", "Mano de obra", "Observaciones"},
                filas,
                new boolean[]{true, false, true, false});
    }
}
