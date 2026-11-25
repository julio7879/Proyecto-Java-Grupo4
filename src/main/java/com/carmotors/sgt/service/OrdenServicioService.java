package com.carmotors.sgt.service;

import com.carmotors.sgt.service.observer.Observador;
import com.carmotors.sgt.service.observer.Sujeto;
import com.carmotors.sgt.config.ConnectionFactory;
import com.carmotors.sgt.exception.CarMotorsException;
import com.carmotors.sgt.exception.DatoDuplicadoException;
import com.carmotors.sgt.exception.EntidadNoEncontradaException;
import com.carmotors.sgt.exception.StockInsuficienteException;
import com.carmotors.sgt.exception.ValidacionException;
import com.carmotors.sgt.model.Cliente;
import com.carmotors.sgt.model.ConsultaOrdenServicio;
import com.carmotors.sgt.model.ConsumoRepuesto;
import com.carmotors.sgt.model.EventoServicioCompletado;
import com.carmotors.sgt.model.FiltroOrdenServicio;
import com.carmotors.sgt.model.ResumenOrdenServicio;
import com.carmotors.sgt.model.DetalleRepuesto;
import com.carmotors.sgt.model.DetalleTecnico;
import com.carmotors.sgt.model.Lote;
import com.carmotors.sgt.model.LineaOrdenSolicitada;
import com.carmotors.sgt.model.OrdenServicio;
import com.carmotors.sgt.model.OrdenServicioDetalle;
import com.carmotors.sgt.model.Repuesto;
import com.carmotors.sgt.model.ReservaLiberada;
import com.carmotors.sgt.model.ResultadoCancelarOrden;
import com.carmotors.sgt.model.ResultadoCompletarOrden;
import com.carmotors.sgt.model.ServicioTipo;
import com.carmotors.sgt.model.Vehiculo;
import com.carmotors.sgt.model.Tecnico;
import com.carmotors.sgt.dao.ClienteDAO;
import com.carmotors.sgt.dao.ConsultaOrdenServicioDAO;
import com.carmotors.sgt.dao.DetalleRepuestoDAO;
import com.carmotors.sgt.dao.LoteDAO;
import com.carmotors.sgt.dao.OrdenServicioDAO;
import com.carmotors.sgt.dao.OrdenServicioDetalleDAO;
import com.carmotors.sgt.dao.RepuestoDAO;
import com.carmotors.sgt.dao.ServicioTipoDAO;
import com.carmotors.sgt.dao.TecnicoDAO;
import com.carmotors.sgt.dao.VehiculoDAO;
import com.carmotors.sgt.dao.impl.ClienteDAOImpl;
import com.carmotors.sgt.dao.impl.ConsultaOrdenServicioDAOImpl;
import com.carmotors.sgt.dao.impl.DetalleRepuestoDAOImpl;
import com.carmotors.sgt.dao.impl.LoteDAOImpl;
import com.carmotors.sgt.dao.impl.VehiculoDAOImpl;
import com.carmotors.sgt.dao.impl.OrdenServicioDAOImpl;
import com.carmotors.sgt.dao.impl.OrdenServicioDetalleDAOImpl;
import com.carmotors.sgt.dao.impl.RepuestoDAOImpl;
import com.carmotors.sgt.dao.impl.ServicioTipoDAOImpl;
import com.carmotors.sgt.dao.impl.TecnicoDAOImpl;
import com.carmotors.sgt.service.chain.SugerenciaTecnico;
import com.carmotors.sgt.service.chain.SugerenciaTecnicoChain;
import com.carmotors.sgt.service.state.EstadoCancelada;
import com.carmotors.sgt.service.state.EstadoEnProceso;
import com.carmotors.sgt.service.state.EstadoOrden;
import com.carmotors.sgt.service.state.EstadoOrdenFactory;
import com.carmotors.sgt.service.state.EstadoPendiente;
import com.carmotors.sgt.util.InputValidator;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Reglas de negocio para crear órdenes, editar sus líneas, reservar repuestos en ellas
 * y completarlas descontando el inventario (RF-08, RF-10, RF-13, RF-35, RN-01 a RN-04, RN-08, RN-11).
 */
public class OrdenServicioService implements Sujeto {

    private static final Logger LOGGER = Logger.getLogger(OrdenServicioService.class.getName());
    private static final int MAX_LONGITUD_DESCRIPCION = 255;
    private static final int MAX_LONGITUD_OBSERVACIONES = 255;
    private static final BigDecimal MAX_HORAS_TRABAJADAS = new BigDecimal("999.99");

    private final OrdenServicioDAO ordenServicioDAO;
    private final OrdenServicioDetalleDAO ordenServicioDetalleDAO;
    private final VehiculoDAO vehiculoDAO;
    private final ClienteDAO clienteDAO;
    private final ServicioTipoDAO servicioTipoDAO;
    private final TecnicoDAO tecnicoDAO;
    private final RepuestoDAO repuestoDAO;
    private final SugerenciaTecnicoChain sugerenciaTecnicoChain;
    private final DetalleRepuestoDAO detalleRepuestoDAO;
    private final InventarioService inventarioService;
    private final boolean usarTransaccionJdbc;
    private final ConsultaOrdenServicioDAO consultaOrdenServicioDAO;
    private final LoteDAO loteDAO;
    private final ConsumoLotesFefo consumoLotesFefo = new ConsumoLotesFefo();
    private final List<Observador> observadores = new ArrayList<>();

    /**
     * Constructor de producción: usa DAOs JDBC y transacción con una única {@link Connection}.
     * Las dependencias de la reserva de repuestos se crean con sus implementaciones por defecto.
     */
    public OrdenServicioService(VehiculoDAO vehiculoDAO, ClienteDAO clienteDAO, ServicioTipoDAO servicioTipoDAO) {
        this(vehiculoDAO, clienteDAO, servicioTipoDAO, new RepuestoDAOImpl(), new DetalleRepuestoDAOImpl(),
                new InventarioService());
    }

    /**
     * Constructor de producción con las dependencias de la reserva de repuestos (RF-10) inyectadas.
     */
    public OrdenServicioService(VehiculoDAO vehiculoDAO, ClienteDAO clienteDAO, ServicioTipoDAO servicioTipoDAO,
                                RepuestoDAO repuestoDAO, DetalleRepuestoDAO detalleRepuestoDAO,
                                InventarioService inventarioService) {
        this(new OrdenServicioDAOImpl(), new OrdenServicioDetalleDAOImpl(), vehiculoDAO, clienteDAO,
                servicioTipoDAO, new TecnicoDAOImpl(), repuestoDAO, detalleRepuestoDAO, inventarioService,
                true, true);
    }

    /**
     * Constructor para pruebas con DAOs en memoria: guarda directamente con los DAOs recibidos.
     */
    public OrdenServicioService(OrdenServicioDAO ordenServicioDAO, OrdenServicioDetalleDAO ordenServicioDetalleDAO,
                                VehiculoDAO vehiculoDAO, ClienteDAO clienteDAO, ServicioTipoDAO servicioTipoDAO) {
        this(ordenServicioDAO, ordenServicioDetalleDAO, vehiculoDAO, clienteDAO,
                servicioTipoDAO, new TecnicoDAOImpl(), new RepuestoDAOImpl(), false);
    }

    public OrdenServicioService(OrdenServicioDAO ordenServicioDAO, OrdenServicioDetalleDAO ordenServicioDetalleDAO,
                                VehiculoDAO vehiculoDAO, ClienteDAO clienteDAO, ServicioTipoDAO servicioTipoDAO,
                                boolean usarTransaccionJdbc) {
        this(ordenServicioDAO, ordenServicioDetalleDAO, vehiculoDAO, clienteDAO,
                servicioTipoDAO, new TecnicoDAOImpl(), new RepuestoDAOImpl(), usarTransaccionJdbc);
    }

    public OrdenServicioService(OrdenServicioDAO ordenServicioDAO, OrdenServicioDetalleDAO ordenServicioDetalleDAO,
                                VehiculoDAO vehiculoDAO, ClienteDAO clienteDAO, ServicioTipoDAO servicioTipoDAO,
                                TecnicoDAO tecnicoDAO, RepuestoDAO repuestoDAO, boolean usarTransaccionJdbc) {
        this(ordenServicioDAO, ordenServicioDetalleDAO, vehiculoDAO, clienteDAO, servicioTipoDAO,
                tecnicoDAO, repuestoDAO, null, null, usarTransaccionJdbc, false);
    }

    /**
     * Constructor completo: con {@code usarTransaccionJdbc = false} permite probar la reserva
     * de repuestos con DAOs en memoria.
     */
    public OrdenServicioService(OrdenServicioDAO ordenServicioDAO, OrdenServicioDetalleDAO ordenServicioDetalleDAO,
                                VehiculoDAO vehiculoDAO, ClienteDAO clienteDAO, ServicioTipoDAO servicioTipoDAO,
                                RepuestoDAO repuestoDAO, DetalleRepuestoDAO detalleRepuestoDAO,
                                InventarioService inventarioService, boolean usarTransaccionJdbc) {
        this(ordenServicioDAO, ordenServicioDetalleDAO, vehiculoDAO, clienteDAO, servicioTipoDAO,
                new TecnicoDAOImpl(), repuestoDAO, detalleRepuestoDAO, inventarioService, usarTransaccionJdbc, true);
    }

    private OrdenServicioService(OrdenServicioDAO ordenServicioDAO, OrdenServicioDetalleDAO ordenServicioDetalleDAO,
                                 VehiculoDAO vehiculoDAO, ClienteDAO clienteDAO, ServicioTipoDAO servicioTipoDAO,
                                 TecnicoDAO tecnicoDAO, RepuestoDAO repuestoDAO,
                                 DetalleRepuestoDAO detalleRepuestoDAO, InventarioService inventarioService,
                                 boolean usarTransaccionJdbc, boolean exigirDependenciasReserva) {
        this(ordenServicioDAO, ordenServicioDetalleDAO, vehiculoDAO, clienteDAO, servicioTipoDAO,
                tecnicoDAO, repuestoDAO, detalleRepuestoDAO, inventarioService,
                usarTransaccionJdbc, exigirDependenciasReserva, new LoteDAOImpl(), null);
    }

    /**
     * Constructor para probar la finalización de órdenes (RF-13) con DAOs en memoria,
     * incluido el DAO de lotes.
     */
    public OrdenServicioService(OrdenServicioDAO ordenServicioDAO, OrdenServicioDetalleDAO ordenServicioDetalleDAO,
                                VehiculoDAO vehiculoDAO, ClienteDAO clienteDAO, ServicioTipoDAO servicioTipoDAO,
                                TecnicoDAO tecnicoDAO, RepuestoDAO repuestoDAO, DetalleRepuestoDAO detalleRepuestoDAO,
                                LoteDAO loteDAO, InventarioService inventarioService, boolean usarTransaccionJdbc) {
        this(ordenServicioDAO, ordenServicioDetalleDAO, vehiculoDAO, clienteDAO, servicioTipoDAO,
                tecnicoDAO, repuestoDAO, detalleRepuestoDAO, inventarioService,
                usarTransaccionJdbc, true, loteDAO, null);
    }

    /** Construye el servicio con repositorios en memoria para las consultas. */
    public OrdenServicioService(OrdenServicioDAO ordenServicioDAO, OrdenServicioDetalleDAO ordenServicioDetalleDAO,
                                VehiculoDAO vehiculoDAO, ClienteDAO clienteDAO, ServicioTipoDAO servicioTipoDAO,
                                TecnicoDAO tecnicoDAO, RepuestoDAO repuestoDAO, DetalleRepuestoDAO detalleRepuestoDAO,
                                ConsultaOrdenServicioDAO consultaOrdenServicioDAO) {
        this(ordenServicioDAO, ordenServicioDetalleDAO, vehiculoDAO, clienteDAO, servicioTipoDAO,
                tecnicoDAO, repuestoDAO, detalleRepuestoDAO, null, false, false, new LoteDAOImpl(),
                consultaOrdenServicioDAO);
    }

    private OrdenServicioService(OrdenServicioDAO ordenServicioDAO, OrdenServicioDetalleDAO ordenServicioDetalleDAO,
                                 VehiculoDAO vehiculoDAO, ClienteDAO clienteDAO, ServicioTipoDAO servicioTipoDAO,
                                 TecnicoDAO tecnicoDAO, RepuestoDAO repuestoDAO,
                                 DetalleRepuestoDAO detalleRepuestoDAO, InventarioService inventarioService,
                                 boolean usarTransaccionJdbc, boolean exigirDependenciasReserva,
                                 LoteDAO loteDAO, ConsultaOrdenServicioDAO consultaOrdenServicioDAO) {
        if (ordenServicioDAO == null) {
            throw new ValidacionException("El DAO de órdenes de servicio es obligatorio.");
        }
        if (ordenServicioDetalleDAO == null) {
            throw new ValidacionException("El DAO de líneas de servicio es obligatorio.");
        }
        if (vehiculoDAO == null) {
            throw new ValidacionException("El DAO de vehículos es obligatorio.");
        }
        if (clienteDAO == null) {
            throw new ValidacionException("El DAO de clientes es obligatorio.");
        }
        if (servicioTipoDAO == null) {
            throw new ValidacionException("El DAO de tipos de servicio es obligatorio.");
        }
        if (tecnicoDAO == null) {
            throw new ValidacionException("El DAO de técnicos es obligatorio.");
        }
        if (repuestoDAO == null) {
            throw new ValidacionException("El DAO de repuestos es obligatorio.");
        }
        if (loteDAO == null) {
            throw new ValidacionException("El DAO de lotes es obligatorio.");
        }
        this.ordenServicioDAO = ordenServicioDAO;
        this.ordenServicioDetalleDAO = ordenServicioDetalleDAO;
        this.vehiculoDAO = vehiculoDAO;
        this.clienteDAO = clienteDAO;
        this.servicioTipoDAO = servicioTipoDAO;
        this.tecnicoDAO = tecnicoDAO;
        if (exigirDependenciasReserva) {
            if (detalleRepuestoDAO == null) {
                throw new ValidacionException("El DAO de repuestos por línea es obligatorio.");
            }
            if (inventarioService == null) {
                throw new ValidacionException("El servicio de inventario es obligatorio.");
            }
        }
        this.repuestoDAO = repuestoDAO;
        this.sugerenciaTecnicoChain = new SugerenciaTecnicoChain();
        this.detalleRepuestoDAO = detalleRepuestoDAO;
        this.inventarioService = inventarioService;
        this.usarTransaccionJdbc = usarTransaccionJdbc;
        this.consultaOrdenServicioDAO = consultaOrdenServicioDAO;
        this.loteDAO = loteDAO;
    }

    /**
     * Crea una orden en estado Pendiente con sus líneas iniciales (RF-08, RN-18).
     *
     * @param vehiculoId          vehículo que ingresa al taller
     * @param descripcionProblema descripción del problema reportado (obligatoria, máx. 255)
     * @param lineas              líneas solicitadas; cada una con {@code servicioTipoId} y observaciones opcionales
     * @return la orden creada con su ID generado
     */
    public OrdenServicio crearOrden(int vehiculoId, String descripcionProblema, List<OrdenServicioDetalle> lineas) {
        return crearOrden(vehiculoId, descripcionProblema, lineas, List.of());
    }

    public OrdenServicio crearOrdenIntegral(int vehiculoId, String descripcionProblema,
                                            List<LineaOrdenSolicitada> solicitudes) {
        if (solicitudes == null || solicitudes.isEmpty()) {
            throw new ValidacionException("La orden debe tener al menos una línea de servicio.");
        }
        List<OrdenServicioDetalle> lineas = new ArrayList<>();
        List<List<DetalleRepuesto>> reservas = new ArrayList<>();
        for (LineaOrdenSolicitada solicitud : solicitudes) {
            if (solicitud == null || solicitud.getReservas() == null) {
                throw new ValidacionException("Cada línea debe indicar sus reservas, aunque la lista esté vacía.");
            }
            lineas.add(solicitud.getLinea());
            reservas.add(solicitud.getReservas());
        }
        return crearOrden(vehiculoId, descripcionProblema, lineas, reservas);
    }

    private OrdenServicio crearOrden(int vehiculoId, String descripcionProblema, List<OrdenServicioDetalle> lineas,
                                     List<List<DetalleRepuesto>> reservas) {
        validarVehiculoYCliente(vehiculoId);
        String descripcion = validarDescripcion(descripcionProblema);
        List<OrdenServicioDetalle> lineasPreparadas = prepararLineas(lineas);

        OrdenServicio orden = new OrdenServicio();
        orden.setVehiculoId(vehiculoId);
        orden.setEstadoId(new EstadoPendiente().getEstadoId());
        orden.setFechaRecepcion(LocalDateTime.now().withNano(0));
        orden.setDescripcionProblema(descripcion);

        boolean tieneReservas = reservas.stream().anyMatch(lista -> !lista.isEmpty());
        if (!usarTransaccionJdbc) {
            if (tieneReservas) {
                throw new CarMotorsException("El alta con reservas requiere una transacción JDBC.");
            }
            guardarOrdenYLineas(orden, lineasPreparadas, ordenServicioDAO, ordenServicioDetalleDAO);
            return orden;
        }

        try (Connection connection = ConnectionFactory.getInstance().getConnection()) {
            // Tras bloquear un repuesto, consultar el stock con los últimos datos confirmados.
            if (tieneReservas) {
                connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            }
            connection.setAutoCommit(false);
            try {
                guardarOrdenYLineas(orden, lineasPreparadas, new OrdenServicioDAOImpl(connection),
                        new OrdenServicioDetalleDAOImpl(connection));
                if (tieneReservas) {
                    guardarReservasIniciales(lineasPreparadas, reservas, connection);
                }
                connection.commit();
                return orden;
            } catch (SQLException | RuntimeException e) {
                try {
                    connection.rollback();
                } catch (SQLException errorRollback) {
                    e.addSuppressed(errorRollback);
                }
                throw e;
            }
        } catch (CarMotorsException e) {
            throw e;
        } catch (SQLException | RuntimeException e) {
            throw new CarMotorsException("No se pudo registrar la orden de servicio.", e);
        }
    }

    private void guardarReservasIniciales(List<OrdenServicioDetalle> lineas, List<List<DetalleRepuesto>> reservas,
                                         Connection connection) {
        RepuestoDAO repuestos = new RepuestoDAOImpl(connection);
        DetalleRepuestoDAO detalles = new DetalleRepuestoDAOImpl(connection);
        InventarioService inventario = new InventarioService(repuestos, new LoteDAOImpl(connection));
        for (int indice = 0; indice < lineas.size(); indice++) {
            int detalleId = lineas.get(indice).getId();
            for (DetalleRepuesto solicitud : reservas.get(indice)) {
                if (solicitud == null) {
                    throw new ValidacionException("La reserva solicitada no puede ser nula.");
                }
                int repuestoId = solicitud.getRepuestoId();
                int cantidad = solicitud.getCantidadAsignada();
                validarDatosReserva(detalleId, repuestoId, cantidad);
                Repuesto repuesto = repuestos.buscarPorIdParaActualizar(repuestoId);
                guardarReserva(prepararReserva(detalleId, repuestoId, cantidad, repuesto, detalles, inventario),
                        repuestos, detalles);
            }
        }
    }

    public List<ResumenOrdenServicio> consultarOrdenes(FiltroOrdenServicio filtro) {
        if (filtro == null) {
            throw new ValidacionException("Indique los filtros de búsqueda de la orden.");
        }
        if (filtro.getEstadoId() != null) {
            EstadoOrdenFactory.desdeId(filtro.getEstadoId());
        }
        if ((filtro.getTecnicoId() != null && filtro.getTecnicoId() <= 0)
                || (filtro.getServicioTipoId() != null && filtro.getServicioTipoId() <= 0)) {
            throw new ValidacionException("Los IDs de técnico y tipo de servicio deben ser positivos.");
        }
        String placa = InputValidator.normalizarPlaca(InputValidator.normalizarTexto(filtro.getPlaca()));
        if (!placa.isEmpty() && !InputValidator.esPlacaValida(placa)) {
            throw new ValidacionException("La placa debe tener entre 5 y 10 caracteres alfanuméricos.");
        }
        if (filtro.getDesde() != null && filtro.getHasta() != null && filtro.getDesde().isAfter(filtro.getHasta())) {
            throw new ValidacionException("La fecha inicial debe ser anterior o igual a la fecha final.");
        }

        FiltroOrdenServicio normalizado = new FiltroOrdenServicio(filtro.getEstadoId(), filtro.getTecnicoId(),
                placa, filtro.getServicioTipoId(), filtro.getDesde(), filtro.getHasta());
        if (!usarTransaccionJdbc) {
            return filtrarOrdenes(obtenerDaoConsultaEnMemoria(), normalizado);
        }
        try (Connection connection = ConnectionFactory.getInstance().getConnection()) {
            return filtrarOrdenes(new ConsultaOrdenServicioDAOImpl(connection), normalizado);
        } catch (SQLException e) {
            throw falloJdbcConsulta("No se pudieron listar las órdenes de servicio.", e);
        } catch (CarMotorsException e) {
            if (e.getCause() instanceof SQLException) {
                throw falloJdbcConsulta("No se pudieron listar las órdenes de servicio.", e);
            }
            throw e;
        }
    }

    private List<ResumenOrdenServicio> filtrarOrdenes(ConsultaOrdenServicioDAO consultas, FiltroOrdenServicio filtro) {
        return consultas.listar(filtro).stream()
                .filter(orden -> filtro.getTecnicoId() == null || orden.getTecnicos().containsKey(filtro.getTecnicoId()))
                .filter(orden -> filtro.getServicioTipoId() == null
                        || orden.getTiposServicio().contains(filtro.getServicioTipoId()))
                .collect(Collectors.toList());
    }

    public ConsultaOrdenServicio consultarDetalle(int ordenId) {
        if (ordenId <= 0) {
            throw new ValidacionException("El ID de la orden debe ser un número positivo.");
        }
        if (!usarTransaccionJdbc) {
            return cargarDetalle(ordenId, ordenServicioDAO, vehiculoDAO, clienteDAO, obtenerDaoConsultaEnMemoria(),
                    ordenServicioDetalleDAO, detalleRepuestoDAO, tecnicoDAO);
        }
        try (Connection connection = ConnectionFactory.getInstance().getConnection()) {
            connection.setReadOnly(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            connection.setAutoCommit(false);
            Throwable fallo = null;
            boolean transaccionCerrada = false;
            try {
                ConsultaOrdenServicio detalle = cargarDetalle(ordenId, new OrdenServicioDAOImpl(connection),
                        new VehiculoDAOImpl(connection), new ClienteDAOImpl(connection),
                        new ConsultaOrdenServicioDAOImpl(connection), new OrdenServicioDetalleDAOImpl(connection),
                        new DetalleRepuestoDAOImpl(connection), new TecnicoDAOImpl(connection));
                connection.commit();
                transaccionCerrada = true;
                return detalle;
            } catch (SQLException | RuntimeException e) {
                fallo = e;
                try {
                    connection.rollback();
                    transaccionCerrada = true;
                } catch (SQLException falloRollback) {
                    e.addSuppressed(falloRollback);
                    LOGGER.warning("Falló el rollback de la consulta de órdenes (código JDBC "
                            + falloRollback.getErrorCode() + ").");
                }
                throw e;
            } finally {
                // Activar auto-commit puede cerrar una transacción cuyo rollback falló.
                if (transaccionCerrada) {
                    restaurarConexionConsulta(connection, fallo);
                }
            }
        } catch (SQLException e) {
            throw falloJdbcConsulta("No se pudo consultar el detalle de la orden.", e);
        } catch (CarMotorsException e) {
            if (e.getCause() instanceof SQLException) {
                throw falloJdbcConsulta("No se pudo consultar el detalle de la orden.", e);
            }
            throw e;
        }
    }

    private void restaurarConexionConsulta(Connection connection, Throwable fallo) throws SQLException {
        try {
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            if (fallo == null) {
                fallo = e;
                throw e;
            }
            fallo.addSuppressed(e);
        } finally {
            try {
                connection.setReadOnly(false);
            } catch (SQLException e) {
                if (fallo == null) {
                    throw e;
                }
                fallo.addSuppressed(e);
            }
        }
    }

    private CarMotorsException falloJdbcConsulta(String mensaje, Throwable fallo) {
        SQLException error = fallo instanceof SQLException ? (SQLException) fallo : (SQLException) fallo.getCause();
        LOGGER.warning("Falló una consulta de órdenes (código JDBC " + error.getErrorCode() + ").");
        return new CarMotorsException(mensaje, fallo);
    }

    private ConsultaOrdenServicioDAO obtenerDaoConsultaEnMemoria() {
        if (consultaOrdenServicioDAO == null) {
            throw new ValidacionException("El DAO de consultas de órdenes es obligatorio.");
        }
        return consultaOrdenServicioDAO;
    }

    private ConsultaOrdenServicio cargarDetalle(int ordenId, OrdenServicioDAO ordenes, VehiculoDAO vehiculos,
                                                ClienteDAO clientes, ConsultaOrdenServicioDAO consultas,
                                                OrdenServicioDetalleDAO detalles, DetalleRepuestoDAO repuestos,
                                                TecnicoDAO tecnicos) {
        OrdenServicio orden = ordenes.buscarPorId(ordenId);
        if (orden == null) {
            throw new EntidadNoEncontradaException("No existe una orden de servicio con el ID " + ordenId + ".");
        }
        if (repuestos == null) {
            throw new ValidacionException("El DAO de repuestos por línea es obligatorio.");
        }
        Vehiculo vehiculoActual = vehiculos.buscarPorId(orden.getVehiculoId());
        Cliente clienteActual = clientes.buscarPorId(vehiculoActual.getClienteId());
        Vehiculo vehiculo = new Vehiculo(vehiculoActual.getId(), vehiculoActual.getClienteId(),
                vehiculoActual.getPlaca(), vehiculoActual.getMarca(), vehiculoActual.getModelo());
        Cliente cliente = new Cliente(clienteActual.getId(), clienteActual.getNombre(),
                clienteActual.getDocumentoIdentidad(), clienteActual.getTelefono(), clienteActual.getEmail(),
                clienteActual.isActivo());
        consultas.cargarDatosFacturados(ordenId, cliente, vehiculo);

        Map<Integer, Tecnico> tecnicosPorId = new HashMap<>();
        List<ConsultaOrdenServicio.Linea> lineas = new ArrayList<>();
        for (OrdenServicioDetalle detalle : detalles.listarPorOrden(ordenId)) {
            List<DetalleRepuesto> piezas = repuestos.listarPorDetalle(detalle.getId());
            List<ConsultaOrdenServicio.HorasTecnico> horasPorTecnico = new ArrayList<>();
            for (DetalleTecnico asignacion : detalles.listarTecnicosAsignados(detalle.getId())) {
                int tecnicoId = asignacion.getTecnicoId();
                Tecnico tecnico = tecnicosPorId.get(tecnicoId);
                if (tecnico == null) {
                    tecnico = tecnicos.buscarPorId(tecnicoId);
                    tecnicosPorId.put(tecnicoId, tecnico);
                }
                horasPorTecnico.add(new ConsultaOrdenServicio.HorasTecnico(tecnico, asignacion.getHorasTrabajadas()));
            }
            BigDecimal subtotal = piezas.stream()
                    .map(pieza -> pieza.getPrecioUnitarioVenta().multiply(BigDecimal.valueOf(pieza.getCantidadAsignada())))
                    .reduce(detalle.getCostoManoObra(), BigDecimal::add)
                    .setScale(InputValidator.ESCALA_MONETARIA, InputValidator.REDONDEO_MONETARIO);
            lineas.add(new ConsultaOrdenServicio.Linea(detalle, horasPorTecnico, piezas, subtotal));
        }
        return new ConsultaOrdenServicio(orden, cliente, vehiculo, estadoRepuestos(orden), lineas);
    }

    private String estadoRepuestos(OrdenServicio orden) {
        EstadoOrden estado = EstadoOrdenFactory.desdeId(orden.getEstadoId());
        if (estado.permiteEdicion()) {
            return "Reservados";
        }
        if (estado instanceof EstadoCancelada) {
            return "Liberados";
        }
        return "Consumidos";
    }

    public boolean requiereTecnicoParaAgregar(int ordenId) {
        if (ordenId <= 0) {
            throw new ValidacionException("El ID de la orden debe ser un número positivo.");
        }
        return validarEstadoEditable(ordenServicioDAO.buscarPorId(ordenId), ordenId) instanceof EstadoEnProceso;
    }

    public boolean especialidadCoincide(ServicioTipo tipo, Tecnico tecnico) {
        if (tipo == null || tecnico == null) {
            return false;
        }
        return tipo.getEspecialidadId() == tecnico.getEspecialidadId();
    }

    public int agregarLinea(int ordenId, OrdenServicioDetalle solicitada, Integer tecnicoId,
                            boolean confirmarOtraEspecialidad) {
        if (ordenId <= 0) {
            throw new ValidacionException("El ID de la orden debe ser un número positivo.");
        }
        if (solicitada == null) {
            throw new ValidacionException("La línea de servicio es obligatoria.");
        }
        return editarLineas(ordenId, (detallesDao, tiposDao, tecnicosDao, repuestosDao, estado) -> {
            ServicioTipo tipo = tiposDao.buscarPorId(solicitada.getServicioTipoId());
            OrdenServicioDetalle linea = prepararLinea(solicitada, tipo, 1);
            Tecnico tecnico = null;
            if (estado instanceof EstadoEnProceso) {
                if (tecnicoId == null || tecnicoId <= 0) {
                    throw new ValidacionException("Una orden En Proceso exige asignar un técnico activo a la nueva línea.");
                }
                tecnico = tecnicosDao.buscarPorIdParaActualizar(tecnicoId);
                if (tecnico == null || !tecnico.isActivo()) {
                    throw new ValidacionException("Seleccione un técnico existente y activo.");
                }
                if (!especialidadCoincide(tipo, tecnico) && !confirmarOtraEspecialidad) {
                    throw new ValidacionException("Confirme la asignación del técnico de otra especialidad.");
                }
            }

            linea.setOrdenId(ordenId);
            int detalleId = detallesDao.guardar(linea);
            linea.setId(detalleId);
            if (tecnico != null) {
                detallesDao.asignarTecnico(detalleId, tecnico.getId());
            }
            return detalleId;
        });
    }

    public void quitarLinea(int ordenId, int detalleId) {
        if (ordenId <= 0) {
            throw new ValidacionException("El ID de la orden debe ser un número positivo.");
        }
        if (detalleId <= 0) {
            throw new ValidacionException("El ID de la línea debe ser un número positivo.");
        }
        editarLineas(ordenId, (detallesDao, tiposDao, tecnicosDao, repuestosDao, estado) -> {
            List<OrdenServicioDetalle> lineas = detallesDao.listarPorOrdenParaActualizar(ordenId);
            boolean pertenece = false;
            for (OrdenServicioDetalle linea : lineas) {
                if (linea.getId() == detalleId) {
                    pertenece = true;
                    break;
                }
            }
            if (!pertenece) {
                throw new EntidadNoEncontradaException("La línea indicada no pertenece a la orden.");
            }
            if (lineas.size() == 1) {
                throw new ValidacionException("La orden debe conservar al menos una línea de servicio.");
            }
            if (estado instanceof EstadoEnProceso && detallesDao.tieneHorasTrabajadas(detalleId)) {
                throw new ValidacionException("La línea tiene horas trabajadas registradas y no se puede retirar.");
            }

            for (Map.Entry<Integer, Integer> reserva : detallesDao.listarReservasParaActualizar(detalleId).entrySet()) {
                if (!repuestosDao.descontarReserva(reserva.getKey(), reserva.getValue())) {
                    throw new ValidacionException("La reserva del repuesto " + reserva.getKey() + " es inconsistente.");
                }
            }
            detallesDao.eliminar(ordenId, detalleId);
            return detalleId;
        });
    }

    /**
     * Ajusta el costo de mano de obra de una línea en una orden editable antes de completar (RN-08).
     *
     * @param ordenId    identificador de la orden editable
     * @param detalleId  identificador de la línea perteneciente a la orden
     * @param nuevoCosto nuevo importe de mano de obra mayor o igual a cero
     * @return la línea actualizada con su nuevo costo
     */
    public OrdenServicioDetalle actualizarCostoManoObra(int ordenId, int detalleId, BigDecimal nuevoCosto) {
        validarOrdenId(ordenId);
        validarDetalleId(detalleId);
        if (nuevoCosto == null) {
            throw new ValidacionException("El costo de mano de obra es obligatorio.");
        }
        if (nuevoCosto.signum() < 0) {
            throw new ValidacionException("El costo de mano de obra no puede ser negativo.");
        }
        BigDecimal costoNormalizado = nuevoCosto.setScale(
                InputValidator.ESCALA_MONETARIA, InputValidator.REDONDEO_MONETARIO);
        if (costoNormalizado.compareTo(InputValidator.MAX_MONTO_DECIMAL_12_2) > 0) {
            throw new ValidacionException("El costo de mano de obra supera el límite permitido.");
        }

        OrdenServicioDetalle[] resultado = new OrdenServicioDetalle[1];
        editarLineas(ordenId, (detallesDao, tiposDao, tecnicosDao, repuestosDao, estado) -> {
            OrdenServicioDetalle linea = buscarLineaDeOrden(detallesDao, ordenId, detalleId, true);
            if (!detallesDao.actualizarCostoManoObra(ordenId, detalleId, costoNormalizado)) {
                throw new CarMotorsException("No se pudo actualizar el costo de mano de obra de la línea "
                        + detalleId + ".");
            }
            linea.setCostoManoObra(costoNormalizado);
            resultado[0] = linea;
            return detalleId;
        });
        return resultado[0];
    }

    /**
     * Verifica que una orden exista y se encuentre en estado editable ({@code Pendiente} o {@code En Proceso}).
     *
     * @param ordenId identificador de la orden
     */
    public void validarOrdenEditable(int ordenId) {
        validarOrdenId(ordenId);
        validarEstadoEditable(ordenServicioDAO.buscarPorId(ordenId), ordenId);
    }

    /**
     * Retorna las asignaciones de técnicos de una línea de orden (RF-09).
     *
     * @param ordenId   identificador de la orden
     * @param detalleId identificador de la línea perteneciente a la orden
     * @return lista de asignaciones con sus horas trabajadas
     */
    public List<DetalleTecnico> listarTecnicosDeLinea(int ordenId, int detalleId) {
        validarOrdenId(ordenId);
        validarDetalleId(detalleId);
        OrdenServicio orden = ordenServicioDAO.buscarPorId(ordenId);
        if (orden == null) {
            throw new EntidadNoEncontradaException("No se encontró una orden de servicio con el ID: " + ordenId);
        }
        buscarLineaDeOrden(ordenServicioDetalleDAO, ordenId, detalleId, false);
        return ordenServicioDetalleDAO.listarTecnicosAsignados(detalleId);
    }

    /**
     * Sugiere el técnico óptimo para una línea usando la cadena {@code RN-20} / {@code RN-21},
     * excluyendo a los técnicos ya asignados a la misma línea (RF-09).
     *
     * @param ordenId   identificador de la orden editable
     * @param detalleId identificador de la línea
     * @return sugerencia encontrada o {@code null} si no quedan técnicos activos elegibles
     */
    public SugerenciaTecnico sugerirTecnicoParaLinea(int ordenId, int detalleId) {
        validarOrdenId(ordenId);
        validarDetalleId(detalleId);
        validarEstadoEditable(ordenServicioDAO.buscarPorId(ordenId), ordenId);
        OrdenServicioDetalle linea = buscarLineaDeOrden(ordenServicioDetalleDAO, ordenId, detalleId, false);
        ServicioTipo tipo = obtenerServicioTipoObligatorio(servicioTipoDAO, linea.getServicioTipoId());

        List<DetalleTecnico> yaAsignados = ordenServicioDetalleDAO.listarTecnicosAsignados(detalleId);
        Set<Integer> idsAsignados = new HashSet<>();
        for (DetalleTecnico asignacion : yaAsignados) {
            idsAsignados.add(asignacion.getTecnicoId());
        }
        return sugerirEntreActivos(tipo.getEspecialidadId(), idsAsignados);
    }

    /**
     * Sugiere el técnico óptimo antes de persistir una nueva línea según el tipo de servicio elegido (RF-09, RN-20).
     *
     * @param ordenId        identificador de la orden editable
     * @param servicioTipoId identificador del tipo de servicio de la nueva línea
     * @return sugerencia encontrada o {@code null} si no hay técnicos activos elegibles
     */
    public SugerenciaTecnico sugerirTecnicoParaTipoServicio(int ordenId, int servicioTipoId) {
        validarOrdenId(ordenId);
        if (servicioTipoId <= 0) {
            throw new ValidacionException("El ID del tipo de servicio debe ser un número positivo.");
        }
        validarEstadoEditable(ordenServicioDAO.buscarPorId(ordenId), ordenId);
        ServicioTipo tipo = obtenerServicioTipoObligatorio(servicioTipoDAO, servicioTipoId);
        return sugerirEntreActivos(tipo.getEspecialidadId(), new HashSet<>());
    }

    private SugerenciaTecnico sugerirEntreActivos(int especialidadRequeridaId, Set<Integer> idsExcluidos) {
        List<Tecnico> activos = tecnicoDAO.listarActivos();
        Map<Integer, Integer> cargas = tecnicoDAO.contarCargaActivaPorTecnico();
        List<Tecnico> candidatos = new ArrayList<>();
        if (activos != null) {
            for (Tecnico tecnico : activos) {
                if (tecnico != null && !idsExcluidos.contains(tecnico.getId())) {
                    candidatos.add(tecnico);
                }
            }
        }
        return sugerenciaTecnicoChain.sugerir(especialidadRequeridaId, candidatos, cargas);
    }

    /**
     * Valida precondiciones de asignación e indica si se requiere confirmación explícita porque
     * la especialidad del técnico no coincide con la requerida por la línea (RF-09, RN-19).
     *
     * @param ordenId   identificador de la orden editable
     * @param detalleId identificador de la línea
     * @param tecnicoId identificador del técnico candidato
     * @return {@code true} si la especialidad no coincide y debe pedirse confirmación
     */
    public boolean requiereConfirmacionEspecialidad(int ordenId, int detalleId, int tecnicoId) {
        validarOrdenId(ordenId);
        validarDetalleId(detalleId);
        validarTecnicoId(tecnicoId);
        validarEstadoEditable(ordenServicioDAO.buscarPorId(ordenId), ordenId);
        OrdenServicioDetalle linea = buscarLineaDeOrden(ordenServicioDetalleDAO, ordenId, detalleId, false);
        ServicioTipo tipo = obtenerServicioTipoObligatorio(servicioTipoDAO, linea.getServicioTipoId());
        Tecnico tecnico = validarTecnicoDisponible(tecnicoDAO, tecnicoId, false);

        if (ordenServicioDetalleDAO.existeTecnicoAsignado(detalleId, tecnicoId)) {
            throw new DatoDuplicadoException("El técnico '" + tecnico.getNombre()
                    + "' ya está asignado a la línea " + detalleId + ".");
        }
        return !especialidadCoincide(tipo, tecnico);
    }

    /**
     * Asigna un técnico activo a una línea de orden con sus horas trabajadas iniciales (RF-09, RN-07, RN-19).
     *
     * @param ordenId                   identificador de la orden editable
     * @param detalleId                 identificador de la línea
     * @param tecnicoId                 identificador del técnico activo
     * @param horasTrabajadas           horas iniciales (si es {@code null} se normaliza a {@code 0.00})
     * @param confirmarOtraEspecialidad confirmación explícita cuando la especialidad no coincide (RN-19)
     * @return registro {@link DetalleTecnico} persistido
     */
    public DetalleTecnico asignarTecnicoALinea(
            int ordenId,
            int detalleId,
            int tecnicoId,
            BigDecimal horasTrabajadas,
            boolean confirmarOtraEspecialidad) {
        validarOrdenId(ordenId);
        validarDetalleId(detalleId);
        validarTecnicoId(tecnicoId);
        BigDecimal horasNormalizadas = validarHorasTrabajadas(horasTrabajadas, true);

        editarLineas(ordenId, (detallesDao, tiposDao, tecnicosDao, repuestosDao, estado) -> {
            OrdenServicioDetalle linea = buscarLineaDeOrden(detallesDao, ordenId, detalleId, true);
            Tecnico tecnico = validarTecnicoDisponible(tecnicosDao, tecnicoId, true);
            if (detallesDao.existeTecnicoAsignado(detalleId, tecnicoId)) {
                throw new DatoDuplicadoException("El técnico '" + tecnico.getNombre()
                        + "' ya está asignado a la línea " + detalleId + ".");
            }
            ServicioTipo tipo = obtenerServicioTipoObligatorio(tiposDao, linea.getServicioTipoId());
            if (!especialidadCoincide(tipo, tecnico) && !confirmarOtraEspecialidad) {
                throw new ValidacionException(
                        "La especialidad del técnico no coincide con la requerida por el servicio; "
                                + "se requiere confirmación explícita (RN-19).");
            }
            detallesDao.asignarTecnico(detalleId, tecnicoId, horasNormalizadas);
            return detalleId;
        });

        return new DetalleTecnico(detalleId, tecnicoId, horasNormalizadas);
    }

    /**
     * Registra o actualiza las horas trabajadas de un técnico ya asignado a una línea (RF-09).
     *
     * @param ordenId         identificador de la orden editable
     * @param detalleId       identificador de la línea
     * @param tecnicoId       identificador del técnico asignado
     * @param horasTrabajadas total de horas trabajadas (obligatorio, no negativo)
     * @return registro {@link DetalleTecnico} actualizado
     */
    public DetalleTecnico registrarHorasTrabajadas(
            int ordenId,
            int detalleId,
            int tecnicoId,
            BigDecimal horasTrabajadas) {
        validarOrdenId(ordenId);
        validarDetalleId(detalleId);
        validarTecnicoId(tecnicoId);
        BigDecimal horasNormalizadas = validarHorasTrabajadas(horasTrabajadas, false);

        editarLineas(ordenId, (detallesDao, tiposDao, tecnicosDao, repuestosDao, estado) -> {
            buscarLineaDeOrden(detallesDao, ordenId, detalleId, true);
            if (!detallesDao.existeTecnicoAsignado(detalleId, tecnicoId)) {
                throw new EntidadNoEncontradaException("El técnico " + tecnicoId
                        + " no está asignado a la línea " + detalleId + ".");
            }
            if (!detallesDao.actualizarHorasTrabajadas(detalleId, tecnicoId, horasNormalizadas)) {
                throw new CarMotorsException("No se pudieron actualizar las horas trabajadas del técnico "
                        + tecnicoId + " en la línea " + detalleId + ".");
            }
            return detalleId;
        });

        return new DetalleTecnico(detalleId, tecnicoId, horasNormalizadas);
    }

    private void validarOrdenId(int ordenId) {
        if (ordenId <= 0) {
            throw new ValidacionException("El ID de la orden debe ser un número positivo.");
        }
    }

    private void validarDetalleId(int detalleId) {
        if (detalleId <= 0) {
            throw new ValidacionException("El ID de la línea debe ser un número positivo.");
        }
    }

    private void validarTecnicoId(int tecnicoId) {
        if (tecnicoId <= 0) {
            throw new ValidacionException("El ID del técnico debe ser un número positivo.");
        }
    }

    private OrdenServicioDetalle buscarLineaDeOrden(
            OrdenServicioDetalleDAO detalleDao,
            int ordenId,
            int detalleId,
            boolean bloquear) {
        List<OrdenServicioDetalle> lineas = bloquear
                ? detalleDao.listarPorOrdenParaActualizar(ordenId)
                : detalleDao.listarPorOrden(ordenId);
        for (OrdenServicioDetalle linea : lineas) {
            if (linea.getId() == detalleId) {
                return linea;
            }
        }
        throw new EntidadNoEncontradaException(
                "La línea " + detalleId + " no pertenece a la orden " + ordenId + ".");
    }

    private ServicioTipo obtenerServicioTipoObligatorio(ServicioTipoDAO tipoDao, int servicioTipoId) {
        ServicioTipo tipo = tipoDao.buscarPorId(servicioTipoId);
        if (tipo == null) {
            throw new EntidadNoEncontradaException(
                    "No se encontró el tipo de servicio con ID: " + servicioTipoId + ".");
        }
        return tipo;
    }

    private Tecnico validarTecnicoDisponible(TecnicoDAO daoTecnico, int tecnicoId, boolean bloquear) {
        Tecnico tecnico = bloquear
                ? daoTecnico.buscarPorIdParaActualizar(tecnicoId)
                : daoTecnico.buscarPorId(tecnicoId);
        if (tecnico == null) {
            throw new EntidadNoEncontradaException("No se encontró un técnico con el ID: " + tecnicoId + ".");
        }
        if (!tecnico.isActivo()) {
            throw new ValidacionException("El técnico '" + tecnico.getNombre()
                    + "' está inactivo y no puede ser asignado.");
        }
        return tecnico;
    }

    private BigDecimal validarHorasTrabajadas(BigDecimal horasTrabajadas, boolean permitirNulo) {
        if (horasTrabajadas == null) {
            if (permitirNulo) {
                return BigDecimal.ZERO.setScale(InputValidator.ESCALA_MONETARIA, InputValidator.REDONDEO_MONETARIO);
            }
            throw new ValidacionException("Las horas trabajadas son obligatorias.");
        }
        if (horasTrabajadas.signum() < 0) {
            throw new ValidacionException("Las horas trabajadas no pueden ser negativas.");
        }
        BigDecimal normalizadas = horasTrabajadas.setScale(
                InputValidator.ESCALA_MONETARIA, InputValidator.REDONDEO_MONETARIO);
        if (normalizadas.compareTo(MAX_HORAS_TRABAJADAS) > 0) {
            throw new ValidacionException(
                    "Las horas trabajadas no pueden superar " + MAX_HORAS_TRABAJADAS + ".");
        }
        return normalizadas;
    }

    private EstadoOrden validarEstadoEditable(OrdenServicio orden, int ordenId) {
        if (orden == null) {
            throw new EntidadNoEncontradaException("No se encontró una orden de servicio con el ID: " + ordenId);
        }
        EstadoOrden estado = EstadoOrdenFactory.desdeId(orden.getEstadoId());
        if (!estado.permiteEdicion()) {
            throw new ValidacionException("No se pueden editar las líneas de una orden en estado " + estado.getNombre() + ".");
        }
        return estado;
    }

    private int editarLineas(int ordenId, EdicionLineas edicion) {
        if (!usarTransaccionJdbc) {
            OrdenServicio orden = ordenServicioDAO.buscarPorIdParaActualizar(ordenId);
            EstadoOrden estado = validarEstadoEditable(orden, ordenId);
            return edicion.ejecutar(ordenServicioDetalleDAO, servicioTipoDAO, tecnicoDAO, repuestoDAO, estado);
        }

        try (Connection connection = ConnectionFactory.getInstance().getConnection()) {
            connection.setAutoCommit(false);
            try {
                OrdenServicioDAO ordenDao = new OrdenServicioDAOImpl(connection);
                OrdenServicioDetalleDAO detalleDao = new OrdenServicioDetalleDAOImpl(connection);
                ServicioTipoDAO tipoDao = new ServicioTipoDAOImpl(connection);
                TecnicoDAO tecnicoDaoTransaccional = new TecnicoDAOImpl(connection);
                RepuestoDAO repuestoDaoTransaccional = new RepuestoDAOImpl(connection);

                OrdenServicio orden = ordenDao.buscarPorIdParaActualizar(ordenId);
                EstadoOrden estado = validarEstadoEditable(orden, ordenId);
                int detalleId = edicion.ejecutar(
                        detalleDao, tipoDao, tecnicoDaoTransaccional, repuestoDaoTransaccional, estado);
                connection.commit();
                return detalleId;
            } catch (RuntimeException e) {
                try {
                    connection.rollback();
                } catch (SQLException errorRollback) {
                    e.addSuppressed(errorRollback);
                }
                throw e;
            }
        } catch (SQLException e) {
            throw new CarMotorsException("No se pudo editar la orden de servicio: " + e.getMessage(), e);
        }
    }

    private interface EdicionLineas {
        int ejecutar(OrdenServicioDetalleDAO detalleDao,
                     ServicioTipoDAO servicioTipoDao,
                     TecnicoDAO tecnicoDao,
                     RepuestoDAO repuestoDao,
                     EstadoOrden estado);
    }

    /**
     * Retorna las líneas de una orden existente.
     *
     * @param ordenId identificador de la orden
     * @return líneas de la orden ordenadas por ID
     */
    public List<OrdenServicioDetalle> listarLineas(int ordenId) {
        if (ordenId <= 0) {
            throw new ValidacionException("El ID de la orden debe ser un número positivo.");
        }
        OrdenServicio orden = ordenServicioDAO.buscarPorId(ordenId);
        if (orden == null) {
            throw new EntidadNoEncontradaException("No se encontró una orden de servicio con el ID: " + ordenId);
        }
        return ordenServicioDetalleDAO.listarPorOrden(ordenId);
    }

    /**
     * Reserva unidades de un repuesto en una línea de servicio (RF-10, RN-01, RN-02, RN-11).
     *
     * <p>Valida todo antes de abrir la transacción. En una única unidad de trabajo JDBC aumenta
     * {@code repuestos.cantidad_reservada} y guarda la reserva en {@code detalle_repuestos} con el
     * nombre y el precio de venta vigentes como snapshot. El stock físico de los lotes no cambia.</p>
     *
     * @param detalleId  línea de servicio que recibe el repuesto
     * @param repuestoId repuesto a reservar
     * @param cantidad   unidades a reservar (mayor que cero)
     * @return la reserva guardada
     */
    public DetalleRepuesto reservarRepuesto(int detalleId, int repuestoId, int cantidad) {
        verificarDependenciasReserva();
        validarDatosReserva(detalleId, repuestoId, cantidad);
        if (!usarTransaccionJdbc) {
            validarLineaEditableParaReserva(detalleId, ordenServicioDAO, ordenServicioDetalleDAO, false);
            DetalleRepuesto reserva = prepararReserva(detalleId, repuestoId, cantidad,
                    repuestoDAO.buscarPorId(repuestoId), detalleRepuestoDAO, inventarioService);
            guardarReserva(reserva, repuestoDAO, detalleRepuestoDAO);
            return reserva;
        }

        try (Connection connection = ConnectionFactory.getInstance().getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);
            try {
                validarLineaEditableParaReserva(detalleId, new OrdenServicioDAOImpl(connection),
                        new OrdenServicioDetalleDAOImpl(connection), true);
                RepuestoDAO repuestos = new RepuestoDAOImpl(connection);
                DetalleRepuestoDAO detalles = new DetalleRepuestoDAOImpl(connection);
                InventarioService inventario = new InventarioService(repuestos, new LoteDAOImpl(connection));
                DetalleRepuesto reserva = prepararReserva(detalleId, repuestoId, cantidad,
                        repuestos.buscarPorIdParaActualizar(repuestoId), detalles, inventario);
                guardarReserva(reserva, repuestos, detalles);
                connection.commit();
                return reserva;
            } catch (SQLException | RuntimeException e) {
                try {
                    connection.rollback();
                } catch (SQLException errorRollback) {
                    e.addSuppressed(errorRollback);
                }
                throw e;
            }
        } catch (CarMotorsException e) {
            throw e;
        } catch (SQLException | RuntimeException e) {
            throw new CarMotorsException("No se pudo reservar el repuesto.", e);
        }
    }

    private void validarDatosReserva(int detalleId, int repuestoId, int cantidad) {
        if (cantidad <= 0) {
            throw new ValidacionException("La cantidad a reservar debe ser mayor que cero.");
        }
        if (detalleId <= 0) {
            throw new ValidacionException("El ID de la línea de servicio debe ser un número positivo.");
        }
        if (repuestoId <= 0) {
            throw new ValidacionException("El ID del repuesto debe ser un número positivo.");
        }

    }

    private void validarLineaEditableParaReserva(int detalleId, OrdenServicioDAO ordenes,
                                                 OrdenServicioDetalleDAO detalles, boolean bloquear) {
        OrdenServicioDetalle linea = detalles.buscarPorId(detalleId);
        if (linea == null) {
            throw new EntidadNoEncontradaException("No se encontró una línea de servicio con el ID: " + detalleId);
        }
        OrdenServicio orden = bloquear
                ? ordenes.buscarPorIdParaActualizar(linea.getOrdenId()) : ordenes.buscarPorId(linea.getOrdenId());
        if (orden == null) {
            throw new EntidadNoEncontradaException("No se encontró la orden de servicio con el ID: "
                    + linea.getOrdenId());
        }
        EstadoOrden estado = EstadoOrdenFactory.desdeId(orden.getEstadoId());
        if (!estado.permiteEdicion()) {
            throw new ValidacionException("La orden " + orden.getId() + " está en estado '" + estado.getNombre()
                    + "' y no permite cambios; no se pueden reservar repuestos.");
        }

        if (bloquear && detalles.buscarPorId(detalleId) == null) {
            throw new EntidadNoEncontradaException("No se encontró una línea de servicio con el ID: " + detalleId);
        }
    }

    private DetalleRepuesto prepararReserva(int detalleId, int repuestoId, int cantidad, Repuesto repuesto,
                                           DetalleRepuestoDAO detalles, InventarioService inventario) {
        if (repuesto == null) {
            throw new EntidadNoEncontradaException("No se encontró un repuesto con el ID: " + repuestoId);
        }
        if (!repuesto.isActivo()) {
            throw new ValidacionException("El repuesto '" + repuesto.getNombre()
                    + "' está inactivo; no se puede reservar.");
        }
        if (detalles.buscar(detalleId, repuestoId) != null) {
            throw new ValidacionException("El repuesto '" + repuesto.getNombre()
                    + "' ya está asignado a la línea " + detalleId + ".");
        }

        int disponible = inventario.calcularStock(repuestoId).getDisponible();
        if (cantidad > disponible) {
            throw new StockInsuficienteException("Stock insuficiente para '" + repuesto.getNombre()
                    + "': solicitado " + cantidad + ", disponible " + disponible + ".");
        }

        DetalleRepuesto reserva = new DetalleRepuesto();
        reserva.setDetalleId(detalleId);
        reserva.setRepuestoId(repuestoId);
        reserva.setCantidadAsignada(cantidad);
        reserva.setNombreRepuestoSnapshot(repuesto.getNombre());
        reserva.setPrecioUnitarioVenta(repuesto.getPrecioVenta()
                .setScale(InputValidator.ESCALA_MONETARIA, InputValidator.REDONDEO_MONETARIO));

        return reserva;
    }

    /**
     * Retorna los repuestos reservados en una línea de servicio (RF-11).
     *
     * @param detalleId identificador de la línea
     * @return reservas de la línea ordenadas por ID de repuesto
     */
    public List<DetalleRepuesto> listarRepuestosDeLinea(int detalleId) {
        verificarDependenciasReserva();
        if (detalleId <= 0) {
            throw new ValidacionException("El ID de la línea de servicio debe ser un número positivo.");
        }
        OrdenServicioDetalle linea = ordenServicioDetalleDAO.buscarPorId(detalleId);
        if (linea == null) {
            throw new EntidadNoEncontradaException("No se encontró una línea de servicio con el ID: " + detalleId);
        }
        return detalleRepuestoDAO.listarPorDetalle(detalleId);
    }

    /**
     * Retira un repuesto reservado de una línea de servicio y libera exactamente la cantidad
     * reservada (RF-11, RN-05, HU-06). Solo se permite en órdenes {@code Pendiente} o
     * {@code En Proceso}; la línea de servicio y el stock físico de los lotes no se modifican.
     *
     * <p>La liberación de la reserva y la eliminación de {@code detalle_repuestos} se ejecutan en
     * una única unidad de trabajo JDBC con la misma {@link Connection}.</p>
     *
     * @param detalleId  línea de servicio que pierde el repuesto
     * @param repuestoId repuesto reservado que se retira
     * @return la reserva retirada, con su cantidad y su snapshot de nombre y precio
     */
    public DetalleRepuesto retirarRepuesto(int detalleId, int repuestoId) {
        verificarDependenciasReserva();
        if (detalleId <= 0) {
            throw new ValidacionException("El ID de la línea de servicio debe ser un número positivo.");
        }
        if (repuestoId <= 0) {
            throw new ValidacionException("El ID del repuesto debe ser un número positivo.");
        }

        if (!usarTransaccionJdbc) {
            return ejecutarRetirarRepuesto(
                    detalleId, repuestoId, ordenServicioDAO, ordenServicioDetalleDAO,
                    repuestoDAO, detalleRepuestoDAO, false);
        }

        try (Connection connection = ConnectionFactory.getInstance().getConnection()) {
            connection.setAutoCommit(false);
            try {
                OrdenServicioDAO ordenDaoTransaccional = new OrdenServicioDAOImpl(connection);
                OrdenServicioDetalleDAO detalleDaoTransaccional = new OrdenServicioDetalleDAOImpl(connection);
                RepuestoDAO repuestoDaoTransaccional = new RepuestoDAOImpl(connection);
                DetalleRepuestoDAO detalleRepuestoDaoTransaccional = new DetalleRepuestoDAOImpl(connection);

                DetalleRepuesto retirada = ejecutarRetirarRepuesto(
                        detalleId, repuestoId, ordenDaoTransaccional, detalleDaoTransaccional,
                        repuestoDaoTransaccional, detalleRepuestoDaoTransaccional, true);
                connection.commit();
                return retirada;
            } catch (CarMotorsException e) {
                hacerRollbackSeguro(connection, e);
                throw e;
            } catch (SQLException e) {
                hacerRollbackSeguro(connection, e);
                throw new CarMotorsException("No se pudo retirar el repuesto de la línea: " + e.getMessage(), e);
            } catch (RuntimeException e) {
                hacerRollbackSeguro(connection, e);
                throw new CarMotorsException("Error inesperado al retirar el repuesto de la línea: "
                        + e.getMessage(), e);
            } finally {
                restaurarAutoCommitSeguro(connection);
            }
        } catch (SQLException e) {
            throw new CarMotorsException("No se pudo completar la transacción del retiro del repuesto: "
                    + e.getMessage(), e);
        }
    }

    private DetalleRepuesto ejecutarRetirarRepuesto(
            int detalleId,
            int repuestoId,
            OrdenServicioDAO ordenDao,
            OrdenServicioDetalleDAO detalleDao,
            RepuestoDAO repuestoDao,
            DetalleRepuestoDAO detalleRepuestoDao,
            boolean bloquear) {
        OrdenServicioDetalle linea = detalleDao.buscarPorId(detalleId);
        if (linea == null) {
            throw new EntidadNoEncontradaException("No se encontró una línea de servicio con el ID: " + detalleId);
        }
        OrdenServicio orden = bloquear
                ? ordenDao.buscarPorIdParaActualizar(linea.getOrdenId())
                : ordenDao.buscarPorId(linea.getOrdenId());
        if (orden == null) {
            throw new EntidadNoEncontradaException("No se encontró la orden de servicio con el ID: "
                    + linea.getOrdenId());
        }
        EstadoOrden estado = EstadoOrdenFactory.desdeId(orden.getEstadoId());
        if (!estado.permiteEdicion()) {
            throw new ValidacionException("La orden " + orden.getId() + " está en estado '" + estado.getNombre()
                    + "' y no permite cambios; no se pueden retirar repuestos (RN-05).");
        }

        DetalleRepuesto reserva = detalleRepuestoDao.buscar(detalleId, repuestoId);
        if (reserva == null) {
            throw new EntidadNoEncontradaException("El repuesto " + repuestoId
                    + " no está reservado en la línea " + detalleId + ".");
        }

        liberarReserva(reserva, repuestoDao, detalleRepuestoDao);
        return reserva;
    }

    private void hacerRollbackSeguro(Connection connection, Throwable causaOriginal) {
        try {
            connection.rollback();
        } catch (SQLException errorRollback) {
            causaOriginal.addSuppressed(errorRollback);
        }
    }

    private void restaurarAutoCommitSeguro(Connection connection) {
        try {
            connection.setAutoCommit(true);
        } catch (SQLException errorAutoCommit) {
            LOGGER.log(Level.WARNING, "No se pudo restaurar autoCommit al cerrar conexión.", errorAutoCommit);
        }
    }

    /**
     * Pasa una orden de servicio de {@code Pendiente} a {@code En Proceso} (RF-12, RN-06, RN-07).
     *
     * <p>Exige que cada línea tenga al menos un técnico activo asignado; si una línea no lo tiene,
     * la transición se bloquea indicando la línea (HU-05). El cambio de estado se ejecuta en una
     * única unidad de trabajo JDBC que bloquea la cabecera con {@code FOR UPDATE}.</p>
     *
     * @param ordenId identificador de la orden
     * @return la orden actualizada con su nuevo estado
     */
    public OrdenServicio pasarAEnProceso(int ordenId) {
        if (ordenId <= 0) {
            throw new ValidacionException("El ID de la orden debe ser un número positivo.");
        }

        if (!usarTransaccionJdbc) {
            OrdenServicio orden = ordenServicioDAO.buscarPorIdParaActualizar(ordenId);
            EstadoOrden nuevoEstado = validarTransicionAEnProceso(orden, ordenId,
                    ordenServicioDetalleDAO, tecnicoDAO);
            if (!ordenServicioDAO.actualizarEstado(ordenId, nuevoEstado.getEstadoId())) {
                throw new CarMotorsException("No se pudo actualizar el estado de la orden " + ordenId + ".");
            }
            orden.setEstadoId(nuevoEstado.getEstadoId());
            return orden;
        }

        try (Connection connection = ConnectionFactory.getInstance().getConnection()) {
            connection.setAutoCommit(false);
            try {
                OrdenServicioDAO ordenDaoTransaccional = new OrdenServicioDAOImpl(connection);
                OrdenServicioDetalleDAO detalleDaoTransaccional = new OrdenServicioDetalleDAOImpl(connection);
                TecnicoDAO tecnicoDaoTransaccional = new TecnicoDAOImpl(connection);

                OrdenServicio orden = ordenDaoTransaccional.buscarPorIdParaActualizar(ordenId);
                EstadoOrden nuevoEstado = validarTransicionAEnProceso(orden, ordenId,
                        detalleDaoTransaccional, tecnicoDaoTransaccional);
                if (!ordenDaoTransaccional.actualizarEstado(ordenId, nuevoEstado.getEstadoId())) {
                    throw new CarMotorsException("No se pudo actualizar el estado de la orden " + ordenId + ".");
                }

                connection.commit();
                orden.setEstadoId(nuevoEstado.getEstadoId());
                return orden;
            } catch (CarMotorsException e) {
                connection.rollback();
                throw e;
            } catch (SQLException e) {
                connection.rollback();
                throw new CarMotorsException("No se pudo pasar la orden a En Proceso: " + e.getMessage(), e);
            } catch (RuntimeException e) {
                connection.rollback();
                throw new CarMotorsException("Error inesperado al pasar la orden a En Proceso: "
                        + e.getMessage(), e);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new CarMotorsException("No se pudo completar la transición de la orden a En Proceso: "
                    + e.getMessage(), e);
        }
    }

    /**
     * Registra la entrega de una orden facturada y pasa su estado a {@code Entregado} (RF-12, RN-10).
     *
     * <p>Solo se permite desde {@code Facturado}; cualquier otro estado lanza
     * {@link com.carmotors.sgt.exception.TransicionEstadoInvalidaException}. El estado y la
     * {@code fecha_entrega} se persisten en una única unidad de trabajo JDBC que bloquea la
     * cabecera con {@code FOR UPDATE}.</p>
     *
     * @param ordenId identificador de la orden facturada
     * @return la orden actualizada con su estado y fecha de entrega
     */
    public OrdenServicio entregarOrden(int ordenId) {
        if (ordenId <= 0) {
            throw new ValidacionException("El ID de la orden debe ser un número positivo.");
        }
        LocalDateTime fechaEntrega = LocalDateTime.now().withNano(0);

        if (!usarTransaccionJdbc) {
            return ejecutarEntregarOrden(ordenId, fechaEntrega, ordenServicioDAO);
        }
        return entregarOrdenEnTransaccion(ordenId, fechaEntrega);
    }

    private OrdenServicio entregarOrdenEnTransaccion(int ordenId, LocalDateTime fechaEntrega) {
        try (Connection connection = ConnectionFactory.getInstance().getConnection()) {
            connection.setAutoCommit(false);
            try {
                OrdenServicioDAO ordenDaoTransaccional = new OrdenServicioDAOImpl(connection);
                OrdenServicio orden = ejecutarEntregarOrden(ordenId, fechaEntrega, ordenDaoTransaccional);
                connection.commit();
                return orden;
            } catch (CarMotorsException e) {
                connection.rollback();
                throw e;
            } catch (SQLException e) {
                connection.rollback();
                throw new CarMotorsException("No se pudo entregar la orden: " + e.getMessage(), e);
            } catch (RuntimeException e) {
                connection.rollback();
                throw new CarMotorsException("Error inesperado al entregar la orden: " + e.getMessage(), e);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new CarMotorsException("No se pudo completar la transacción de entrega de la orden " + ordenId
                    + ": " + e.getMessage(), e);
        }
    }

    private OrdenServicio ejecutarEntregarOrden(int ordenId, LocalDateTime fechaEntrega, OrdenServicioDAO ordenDao) {
        // 1. La orden existe y el patrón State permite pasar a Entregado (solo desde Facturado, RN-10).
        OrdenServicio orden = ordenDao.buscarPorIdParaActualizar(ordenId);
        if (orden == null) {
            throw new EntidadNoEncontradaException("No se encontró una orden de servicio con el ID: " + ordenId);
        }
        EstadoOrden nuevoEstado = EstadoOrdenFactory.desdeId(orden.getEstadoId()).entregar();

        // 2. Persiste estado_id y fecha_entrega; si no se actualiza la fila, no hay entrega.
        if (!ordenDao.marcarEntregada(ordenId, nuevoEstado.getEstadoId(), fechaEntrega)) {
            throw new CarMotorsException("No se pudo registrar la entrega de la orden " + ordenId + ".");
        }
        orden.setEstadoId(nuevoEstado.getEstadoId());
        orden.setFechaEntrega(fechaEntrega);
        return orden;
    }

    /**
     * Completa una orden En Proceso y descuenta del inventario los repuestos reservados en
     * todas sus líneas (RF-13, RN-03, RN-04, RN-08, sección 2.6).
     *
     * <p>En una única unidad de trabajo JDBC: bloquea la cabecera, valida la transición con el
     * patrón State, valida las líneas, comprueba que el stock operativo cubra cada repuesto antes
     * de descontar nada, descuenta los lotes con FEFO, reduce el reservado y marca la orden como
     * Completada con su fecha. Si algo falla, nada cambia. No se registra el vínculo lote-orden
     * (límite de trazabilidad adoptado). Después del commit se evalúa la alerta de stock bajo
     * (RN-22).</p>
     *
     * @param ordenId identificador de la orden
     * @return la orden completada y las unidades descontadas por repuesto
     */
    public ResultadoCompletarOrden completarOrden(int ordenId) {
        ResultadoCompletarOrden resultado = completarOrdenSinNotificar(ordenId);
        inventarioService.notificarStockBajoTrasCompletar(resultado);
        return resultado;
    }

    /** Confirma la orden y devuelve los consumos para que la fachada coordine las alertas. */
    public ResultadoCompletarOrden completarOrdenSinNotificar(int ordenId) {
        if (ordenId <= 0) {
            throw new ValidacionException("El ID de la orden debe ser un número positivo.");
        }
        if (detalleRepuestoDAO == null || inventarioService == null) {
            throw new CarMotorsException("La finalización de órdenes no está configurada en este servicio.");
        }
        LocalDateTime fechaCompletado = LocalDateTime.now().withNano(0);

        ResultadoCompletarOrden resultado;
        if (!usarTransaccionJdbc) {
            resultado = ejecutarCompletarOrden(ordenId, fechaCompletado, ordenServicioDAO,
                    ordenServicioDetalleDAO, detalleRepuestoDAO, repuestoDAO, loteDAO);
        } else {
            resultado = completarOrdenEnTransaccion(ordenId, fechaCompletado);
        }

        notificarOrdenCompletadaSiHayObservadores(resultado.getOrden());
        return resultado;
    }

    @Override
    public void registrarObservador(Observador observador) {
        if (observador == null) {
            throw new ValidacionException("El observador a registrar no puede ser nulo.");
        }
        if (!observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    @Override
    public void removerObservador(Observador observador) {
        if (observador == null) {
            throw new ValidacionException("El observador a remover no puede ser nulo.");
        }
        observadores.remove(observador);
    }

    @Override
    public void notificarObservadores(EventoServicioCompletado evento) {
        if (evento == null) {
            throw new ValidacionException("El evento de servicio completado no puede ser nulo.");
        }
        for (Observador observador : new ArrayList<>(observadores)) {
            try {
                observador.actualizar(evento);
            } catch (RuntimeException e) {
                LOGGER.log(Level.WARNING,
                        "No se pudo notificar al observador " + observador.getClass().getSimpleName()
                                + " para la orden #" + evento.getOrdenId() + ": " + e.getMessage(),
                        e);
            }
        }
    }

    private void notificarOrdenCompletadaSiHayObservadores(OrdenServicio orden) {
        if (orden == null || observadores.isEmpty()) {
            return;
        }
        try {
            Vehiculo vehiculo = vehiculoDAO.buscarPorId(orden.getVehiculoId());
            if (vehiculo == null) {
                LOGGER.warning("No se encontró el vehículo " + orden.getVehiculoId()
                        + " para notificar la orden completada #" + orden.getId() + ".");
                return;
            }
            Cliente cliente = clienteDAO.buscarPorId(vehiculo.getClienteId());
            if (cliente == null) {
                LOGGER.warning("No se encontró el cliente " + vehiculo.getClienteId()
                        + " para notificar la orden completada #" + orden.getId() + ".");
                return;
            }
            EventoServicioCompletado evento = new EventoServicioCompletado(orden, vehiculo, cliente);
            notificarObservadores(evento);
        } catch (RuntimeException e) {
            LOGGER.log(Level.WARNING,
                    "No se pudo construir la notificación de la orden completada #"
                            + orden.getId() + ": " + e.getMessage(),
                    e);
        }
    }

    private ResultadoCompletarOrden completarOrdenEnTransaccion(int ordenId, LocalDateTime fechaCompletado) {
        try (Connection connection = ConnectionFactory.getInstance().getConnection()) {
            connection.setAutoCommit(false);
            try {
                OrdenServicioDAO ordenDaoTransaccional = new OrdenServicioDAOImpl(connection);
                OrdenServicioDetalleDAO detalleDaoTransaccional = new OrdenServicioDetalleDAOImpl(connection);
                DetalleRepuestoDAO detalleRepuestoDaoTransaccional = new DetalleRepuestoDAOImpl(connection);
                RepuestoDAO repuestoDaoTransaccional = new RepuestoDAOImpl(connection);
                LoteDAO loteDaoTransaccional = new LoteDAOImpl(connection);

                ResultadoCompletarOrden resultado = ejecutarCompletarOrden(ordenId, fechaCompletado,
                        ordenDaoTransaccional, detalleDaoTransaccional, detalleRepuestoDaoTransaccional,
                        repuestoDaoTransaccional, loteDaoTransaccional);
                connection.commit();
                return resultado;
            } catch (CarMotorsException e) {
                connection.rollback();
                throw e;
            } catch (SQLException e) {
                connection.rollback();
                throw new CarMotorsException("No se pudo completar la orden: " + e.getMessage(), e);
            } catch (RuntimeException e) {
                connection.rollback();
                throw new CarMotorsException("Error inesperado al completar la orden: " + e.getMessage(), e);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new CarMotorsException("No se pudo completar la transacción de la orden " + ordenId + ": "
                    + e.getMessage(), e);
        }
    }

    private ResultadoCompletarOrden ejecutarCompletarOrden(int ordenId, LocalDateTime fechaCompletado,
                                                           OrdenServicioDAO ordenDao,
                                                           OrdenServicioDetalleDAO detalleDao,
                                                           DetalleRepuestoDAO detalleRepuestoDao,
                                                           RepuestoDAO repuestoDao, LoteDAO loteDao) {
        // 1. La orden existe y el patrón State permite pasar a Completado (solo desde En Proceso).
        OrdenServicio orden = ordenDao.buscarPorIdParaActualizar(ordenId);
        if (orden == null) {
            throw new EntidadNoEncontradaException("No se encontró una orden de servicio con el ID: " + ordenId);
        }
        EstadoOrden nuevoEstado = EstadoOrdenFactory.desdeId(orden.getEstadoId()).completar();

        // 2. Al menos una línea y mano de obra >= 0 en cada una (RN-08).
        List<OrdenServicioDetalle> lineas = detalleDao.listarPorOrdenParaActualizar(ordenId);
        if (lineas.isEmpty()) {
            throw new ValidacionException("La orden " + ordenId + " no tiene líneas de servicio (RN-18).");
        }
        for (OrdenServicioDetalle linea : lineas) {
            if (linea.getCostoManoObra() == null || linea.getCostoManoObra().compareTo(BigDecimal.ZERO) < 0) {
                throw new ValidacionException("La línea " + linea.getId() + " ('" + linea.getNombreServicioSnapshot()
                        + "') tiene un costo de mano de obra inválido; debe ser mayor o igual a cero (RN-08).");
            }
        }

        // 3. Cantidades asignadas agrupadas por repuesto en todas las líneas.
        Map<Integer, Integer> requeridoPorRepuesto = new LinkedHashMap<>();
        Map<Integer, String> nombrePorRepuesto = new HashMap<>();
        for (OrdenServicioDetalle linea : lineas) {
            for (DetalleRepuesto reserva : detalleRepuestoDao.listarPorDetalle(linea.getId())) {
                int acumulado = requeridoPorRepuesto.getOrDefault(reserva.getRepuestoId(), 0);
                requeridoPorRepuesto.put(reserva.getRepuestoId(), acumulado + reserva.getCantidadAsignada());
                nombrePorRepuesto.put(reserva.getRepuestoId(), reserva.getNombreRepuestoSnapshot());
            }
        }

        // 4. Cobertura del stock operativo ANTES de descontar nada (sección 2.6, puntos 4 y 7).
        LocalDate hoy = fechaCompletado.toLocalDate();
        Map<Integer, List<Lote>> lotesPorRepuesto = new HashMap<>();
        for (Map.Entry<Integer, Integer> requerido : requeridoPorRepuesto.entrySet()) {
            int repuestoId = requerido.getKey();
            Repuesto repuesto = repuestoDao.buscarPorId(repuestoId);
            List<Lote> lotes = loteDao.listarPorRepuesto(repuestoId);
            int operativo = consumoLotesFefo.calcularStockOperativo(lotes, hoy);
            if (operativo < requerido.getValue()) {
                throw new StockInsuficienteException("No se puede completar la orden " + ordenId + ": el repuesto '"
                        + nombrePorRepuesto.get(repuestoId) + "' (ID " + repuestoId + ") requiere "
                        + requerido.getValue() + " unidad(es) y su stock operativo es " + operativo + ".");
            }
            if (repuesto != null && operativo < repuesto.getCantidadReservada()) {
                int deficit = repuesto.getCantidadReservada() - operativo;
                throw new StockInsuficienteException("No se puede completar la orden " + ordenId
                        + ": el repuesto '" + nombrePorRepuesto.get(repuestoId) + "' (ID " + repuestoId
                        + ") presenta un déficit global de " + deficit + " unidad(es) (stock operativo: "
                        + operativo + ", reservas vigentes: " + repuesto.getCantidadReservada()
                        + "). Ajuste las reservas afectadas o ingrese lotes válidos antes de completar.");
            }
            lotesPorRepuesto.put(repuestoId, lotes);
        }

        // 5. Descuento por FEFO y reducción del reservado (RN-03).
        List<ConsumoRepuesto> consumos = new ArrayList<>();
        for (Map.Entry<Integer, Integer> requerido : requeridoPorRepuesto.entrySet()) {
            int repuestoId = requerido.getKey();
            int cantidad = requerido.getValue();
            Map<Integer, Integer> distribucion = consumoLotesFefo.calcularConsumo(
                    lotesPorRepuesto.get(repuestoId), hoy, cantidad);
            for (Map.Entry<Integer, Integer> porLote : distribucion.entrySet()) {
                if (!loteDao.descontarUnidades(porLote.getKey(), porLote.getValue())) {
                    throw new CarMotorsException("No se pudieron descontar " + porLote.getValue()
                            + " unidad(es) del lote " + porLote.getKey() + ".");
                }
            }
            if (!repuestoDao.descontarReserva(repuestoId, cantidad)) {
                throw new CarMotorsException("La reserva del repuesto " + repuestoId
                        + " es inconsistente: no se pudo reducir en " + cantidad + " unidad(es).");
            }
            consumos.add(new ConsumoRepuesto(repuestoId, nombrePorRepuesto.get(repuestoId), cantidad));
        }

        // 6. Estado Completado y fecha de finalización.
        if (!ordenDao.marcarCompletada(ordenId, nuevoEstado.getEstadoId(), fechaCompletado)) {
            throw new CarMotorsException("No se pudo marcar como completada la orden " + ordenId + ".");
        }
        orden.setEstadoId(nuevoEstado.getEstadoId());
        orden.setFechaCompletado(fechaCompletado);
        return new ResultadoCompletarOrden(orden, consumos);
    }

    /**
     * Cancela una orden Pendiente o En Proceso liberando todas las reservas de repuestos de sus
     * líneas y registrando la fecha de cancelación (RF-14, RN-04, RN-05, RN-06, HU-08).
     *
     * <p>En una única unidad de trabajo JDBC: bloquea la cabecera con {@code FOR UPDATE}, valida
     * la transición mediante el patrón State (solo desde Pendiente o En Proceso), consolida y
     * libera todas las reservas en {@code repuestos} sin modificar el stock físico de lotes,
     * conserva líneas, horas y repuestos como historial y marca la orden como Cancelada con su
     * fecha. Si ocurre cualquier fallo, revierte la transacción completa.</p>
     *
     * @param ordenId identificador de la orden a cancelar
     * @return la orden cancelada y el detalle de las reservas liberadas por repuesto
     */
    public ResultadoCancelarOrden cancelarOrden(int ordenId) {
        if (ordenId <= 0) {
            throw new ValidacionException("El ID de la orden debe ser un número positivo.");
        }
        if (detalleRepuestoDAO == null) {
            throw new CarMotorsException("La cancelación de órdenes no está configurada en este servicio.");
        }
        LocalDateTime fechaCancelacion = LocalDateTime.now().withNano(0);

        if (!usarTransaccionJdbc) {
            return ejecutarCancelarOrden(ordenId, fechaCancelacion, ordenServicioDAO,
                    ordenServicioDetalleDAO, detalleRepuestoDAO, repuestoDAO);
        }
        return cancelarOrdenEnTransaccion(ordenId, fechaCancelacion);
    }

    private ResultadoCancelarOrden cancelarOrdenEnTransaccion(int ordenId, LocalDateTime fechaCancelacion) {
        try (Connection connection = ConnectionFactory.getInstance().getConnection()) {
            connection.setAutoCommit(false);
            try {
                OrdenServicioDAO ordenDaoTransaccional = new OrdenServicioDAOImpl(connection);
                OrdenServicioDetalleDAO detalleDaoTransaccional = new OrdenServicioDetalleDAOImpl(connection);
                DetalleRepuestoDAO detalleRepuestoDaoTransaccional = new DetalleRepuestoDAOImpl(connection);
                RepuestoDAO repuestoDaoTransaccional = new RepuestoDAOImpl(connection);

                ResultadoCancelarOrden resultado = ejecutarCancelarOrden(ordenId, fechaCancelacion,
                        ordenDaoTransaccional, detalleDaoTransaccional,
                        detalleRepuestoDaoTransaccional, repuestoDaoTransaccional);
                connection.commit();
                return resultado;
            } catch (CarMotorsException e) {
                connection.rollback();
                throw e;
            } catch (SQLException e) {
                connection.rollback();
                throw new CarMotorsException("No se pudo cancelar la orden: " + e.getMessage(), e);
            } catch (RuntimeException e) {
                connection.rollback();
                throw new CarMotorsException("Error inesperado al cancelar la orden: " + e.getMessage(), e);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new CarMotorsException("No se pudo completar la transacción de cancelación de la orden " + ordenId
                    + ": " + e.getMessage(), e);
        }
    }

    private ResultadoCancelarOrden ejecutarCancelarOrden(int ordenId, LocalDateTime fechaCancelacion,
                                                         OrdenServicioDAO ordenDao,
                                                         OrdenServicioDetalleDAO detalleDao,
                                                         DetalleRepuestoDAO detalleRepuestoDao,
                                                         RepuestoDAO repuestoDao) {
        // 1. La orden existe y el patrón State permite pasar a Cancelada (solo Pendiente o En Proceso).
        OrdenServicio orden = ordenDao.buscarPorIdParaActualizar(ordenId);
        if (orden == null) {
            throw new EntidadNoEncontradaException("No se encontró una orden de servicio con el ID: " + ordenId);
        }
        EstadoOrden nuevoEstado = EstadoOrdenFactory.desdeId(orden.getEstadoId()).cancelar();

        // 2. Consolida reservas de todas las líneas conservando líneas, horas y repuestos como historial (RF-14).
        List<OrdenServicioDetalle> lineas = detalleDao.listarPorOrdenParaActualizar(ordenId);
        Map<Integer, Integer> reservadoPorRepuesto = new LinkedHashMap<>();
        Map<Integer, String> nombrePorRepuesto = new HashMap<>();
        for (OrdenServicioDetalle linea : lineas) {
            for (DetalleRepuesto reserva : detalleRepuestoDao.listarPorDetalle(linea.getId())) {
                int acumulado = reservadoPorRepuesto.getOrDefault(reserva.getRepuestoId(), 0);
                reservadoPorRepuesto.put(reserva.getRepuestoId(), acumulado + reserva.getCantidadAsignada());
                nombrePorRepuesto.put(reserva.getRepuestoId(), reserva.getNombreRepuestoSnapshot());
            }
        }

        // 3. Libera exactamente las reservas asociadas sin tocar lotes físicos (RN-05).
        List<ReservaLiberada> reservasLiberadas = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entrada : reservadoPorRepuesto.entrySet()) {
            int repuestoId = entrada.getKey();
            int cantidad = entrada.getValue();
            if (!repuestoDao.descontarReserva(repuestoId, cantidad)) {
                throw new CarMotorsException("La reserva del repuesto " + repuestoId
                        + " es inconsistente: no se pudo liberar " + cantidad + " unidad(es).");
            }
            reservasLiberadas.add(new ReservaLiberada(repuestoId, nombrePorRepuesto.get(repuestoId), cantidad));
        }

        // 4. Estado Cancelada y fecha de cancelación (RN-06).
        if (!ordenDao.marcarCancelada(ordenId, nuevoEstado.getEstadoId(), fechaCancelacion)) {
            throw new CarMotorsException("No se pudo marcar como cancelada la orden " + ordenId + ".");
        }
        orden.setEstadoId(nuevoEstado.getEstadoId());
        orden.setFechaCancelacion(fechaCancelacion);
        return new ResultadoCancelarOrden(orden, reservasLiberadas);
    }

    /**
     * Retorna el disponible para nuevas reservas de un repuesto (sección 2.5).
     *
     * @param repuestoId identificador del repuesto
     * @return unidades disponibles
     */
    public int consultarDisponible(int repuestoId) {
        verificarDependenciasReserva();
        return inventarioService.calcularStock(repuestoId).getDisponible();
    }

    /**
     * Traduce el ID de estado de una orden a su nombre usando el patrón State.
     *
     * @param estadoId identificador de {@code servicio_estados}
     * @return nombre del estado
     */
    public String obtenerNombreEstado(int estadoId) {
        return EstadoOrdenFactory.desdeId(estadoId).getNombre();
    }

    private void verificarDependenciasReserva() {
        if (repuestoDAO == null || detalleRepuestoDAO == null || inventarioService == null) {
            throw new CarMotorsException("La reserva de repuestos no está configurada en este servicio.");
        }
    }

    private void guardarReserva(DetalleRepuesto reserva, RepuestoDAO repuestoDao, DetalleRepuestoDAO detalleDao) {
        boolean actualizado = repuestoDao.aumentarReservado(reserva.getRepuestoId(), reserva.getCantidadAsignada());
        if (!actualizado) {
            throw new CarMotorsException("No se pudo actualizar el reservado del repuesto "
                    + reserva.getRepuestoId() + ".");
        }
        boolean guardado = detalleDao.guardar(reserva);
        if (!guardado) {
            throw new CarMotorsException("No se pudo registrar la reserva del repuesto en la línea "
                    + reserva.getDetalleId() + ".");
        }
    }

    private void liberarReserva(DetalleRepuesto reserva, RepuestoDAO repuestoDao, DetalleRepuestoDAO detalleDao) {
        boolean actualizado = repuestoDao.descontarReserva(reserva.getRepuestoId(), reserva.getCantidadAsignada());
        if (!actualizado) {
            throw new CarMotorsException("La reserva del repuesto " + reserva.getRepuestoId()
                    + " en la línea " + reserva.getDetalleId() + " es inconsistente: no se pudo liberar "
                    + reserva.getCantidadAsignada() + " unidad(es).");
        }
        boolean eliminado = detalleDao.eliminar(reserva.getDetalleId(), reserva.getRepuestoId());
        if (!eliminado) {
            throw new CarMotorsException("No se pudo eliminar la reserva del repuesto "
                    + reserva.getRepuestoId() + " de la línea " + reserva.getDetalleId() + ".");
        }
    }

    private EstadoOrden validarTransicionAEnProceso(OrdenServicio orden, int ordenId,
                                                    OrdenServicioDetalleDAO detalleDao, TecnicoDAO tecnicoDao) {
        if (orden == null) {
            throw new EntidadNoEncontradaException("No se encontró una orden de servicio con el ID: " + ordenId);
        }
        EstadoOrden estadoActual = EstadoOrdenFactory.desdeId(orden.getEstadoId());
        EstadoOrden nuevoEstado = estadoActual.iniciarProceso();

        List<OrdenServicioDetalle> lineas = detalleDao.listarPorOrdenParaActualizar(ordenId);
        if (lineas.isEmpty()) {
            throw new ValidacionException("La orden " + ordenId + " no tiene líneas de servicio (RN-18).");
        }
        for (OrdenServicioDetalle linea : lineas) {
            if (!tieneTecnicoActivo(detalleDao, tecnicoDao, linea.getId())) {
                throw new ValidacionException("La línea " + linea.getId() + " ('"
                        + linea.getNombreServicioSnapshot() + "') no tiene un técnico activo asignado; "
                        + "no se puede pasar la orden a En Proceso (RN-07).");
            }
        }
        return nuevoEstado;
    }

    private boolean tieneTecnicoActivo(OrdenServicioDetalleDAO detalleDao, TecnicoDAO tecnicoDao, int detalleId) {
        for (DetalleTecnico asignacion : detalleDao.listarTecnicosAsignados(detalleId)) {
            Tecnico tecnico = tecnicoDao.buscarPorId(asignacion.getTecnicoId());
            if (tecnico != null && tecnico.isActivo()) {
                return true;
            }
        }
        return false;
    }

    private void validarVehiculoYCliente(int vehiculoId) {
        if (vehiculoId <= 0) {
            throw new ValidacionException("El ID del vehículo debe ser un número positivo.");
        }
        Vehiculo vehiculo = vehiculoDAO.buscarPorId(vehiculoId);
        if (vehiculo == null) {
            throw new EntidadNoEncontradaException(
                    "No se encontró un vehículo con el ID: " + vehiculoId + ". Primero debe registrarse el vehículo.");
        }
        Cliente cliente = clienteDAO.buscarPorId(vehiculo.getClienteId());
        if (cliente == null) {
            throw new EntidadNoEncontradaException(
                    "No se encontró el cliente dueño del vehículo con placa " + vehiculo.getPlaca() + ".");
        }
        if (!cliente.isActivo()) {
            throw new ValidacionException("El cliente '" + cliente.getNombre()
                    + "' dueño del vehículo está inactivo; no se pueden crear órdenes para su vehículo.");
        }
    }

    private String validarDescripcion(String descripcionProblema) {
        String descripcion = InputValidator.normalizarEspacios(descripcionProblema);
        if (descripcion.isEmpty()) {
            throw new ValidacionException("La descripción del problema es obligatoria.");
        }
        if (descripcion.length() < 3 || !InputValidator.contieneLetra(descripcion)) {
            throw new ValidacionException(
                    "La descripción del problema debe tener al menos 3 caracteres e incluir texto descriptivo.");
        }
        if (descripcion.length() > MAX_LONGITUD_DESCRIPCION) {
            throw new ValidacionException("La descripción del problema no puede superar los "
                    + MAX_LONGITUD_DESCRIPCION + " caracteres.");
        }
        return descripcion;
    }

    private List<OrdenServicioDetalle> prepararLineas(List<OrdenServicioDetalle> lineas) {
        if (lineas == null || lineas.isEmpty()) {
            throw new ValidacionException("La orden debe tener al menos una línea de servicio (RN-18).");
        }

        List<OrdenServicioDetalle> preparadas = new ArrayList<>();
        int numero = 1;
        for (OrdenServicioDetalle solicitada : lineas) {
            if (solicitada == null) {
                throw new ValidacionException("La línea " + numero + " de la orden no puede ser nula.");
            }
            ServicioTipo tipo = servicioTipoDAO.buscarPorId(solicitada.getServicioTipoId());
            preparadas.add(prepararLinea(solicitada, tipo, numero));
            numero++;
        }
        return preparadas;
    }

    private OrdenServicioDetalle prepararLinea(OrdenServicioDetalle solicitada, ServicioTipo tipo, int numero) {
        if (tipo == null) {
            throw new EntidadNoEncontradaException("Línea " + numero + ": no existe un tipo de servicio con el ID: "
                    + solicitada.getServicioTipoId());
        }
        if (tipo.getPrecioManoObraBase() == null) {
            throw new ValidacionException("El tipo de servicio '" + tipo.getNombre()
                    + "' no tiene precio base de mano de obra.");
        }

        String observaciones = InputValidator.normalizarTexto(solicitada.getObservaciones());
        if (observaciones.length() > MAX_LONGITUD_OBSERVACIONES) {
            throw new ValidacionException("Línea " + numero + ": las observaciones no pueden superar los "
                    + MAX_LONGITUD_OBSERVACIONES + " caracteres.");
        }

        OrdenServicioDetalle linea = new OrdenServicioDetalle();
        linea.setServicioTipoId(tipo.getId());
        linea.setNombreServicioSnapshot(tipo.getNombre());
        linea.setCostoManoObra(tipo.getPrecioManoObraBase()
                .setScale(InputValidator.ESCALA_MONETARIA, InputValidator.REDONDEO_MONETARIO));
        linea.setObservaciones(observaciones.isEmpty() ? null : observaciones);
        return linea;
    }

    private void guardarOrdenYLineas(OrdenServicio orden, List<OrdenServicioDetalle> lineas,
                                     OrdenServicioDAO ordenDao, OrdenServicioDetalleDAO detalleDao) {
        int ordenId = ordenDao.guardar(orden);
        orden.setId(ordenId);
        for (OrdenServicioDetalle linea : lineas) {
            linea.setOrdenId(ordenId);
            int detalleId = detalleDao.guardar(linea);
            linea.setId(detalleId);
        }
    }
}
