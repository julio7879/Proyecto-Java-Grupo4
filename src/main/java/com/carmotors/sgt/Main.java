package com.carmotors.sgt;

import com.carmotors.sgt.service.observer.ObservadorLogServicio;
import com.carmotors.sgt.service.observer.ObservadorNotificacionCliente;
import com.carmotors.sgt.controller.ClienteController;
import com.carmotors.sgt.controller.EvaluacionProveedorController;
import com.carmotors.sgt.controller.FacturaController;
import com.carmotors.sgt.controller.InventarioController;
import com.carmotors.sgt.controller.LoteController;
import com.carmotors.sgt.controller.OrdenServicioController;
import com.carmotors.sgt.controller.ProveedorController;
import com.carmotors.sgt.controller.ProveedorRepuestoController;
import com.carmotors.sgt.controller.RepuestoController;
import com.carmotors.sgt.controller.ReportesController;
import com.carmotors.sgt.controller.ServicioTipoController;
import com.carmotors.sgt.controller.TecnicoController;
import com.carmotors.sgt.dao.ClienteDAO;
import com.carmotors.sgt.dao.EspecialidadDAO;
import com.carmotors.sgt.dao.EvaluacionProveedorDAO;
import com.carmotors.sgt.dao.LoteDAO;
import com.carmotors.sgt.dao.MantenimientoTipoDAO;
import com.carmotors.sgt.dao.ProveedorDAO;
import com.carmotors.sgt.dao.ProveedorRepuestoDAO;
import com.carmotors.sgt.dao.ReporteDAO;
import com.carmotors.sgt.dao.RepuestoDAO;
import com.carmotors.sgt.dao.ServicioTipoDAO;
import com.carmotors.sgt.dao.TecnicoDAO;
import com.carmotors.sgt.dao.VehiculoDAO;
import com.carmotors.sgt.dao.impl.ClienteDAOImpl;
import com.carmotors.sgt.dao.impl.DetalleRepuestoDAOImpl;
import com.carmotors.sgt.dao.impl.EspecialidadDAOImpl;
import com.carmotors.sgt.dao.impl.EvaluacionProveedorDAOImpl;
import com.carmotors.sgt.dao.impl.FacturaDAOImpl;
import com.carmotors.sgt.dao.impl.FacturaDetalleDAOImpl;
import com.carmotors.sgt.dao.impl.FacturaSnapshotDAOImpl;
import com.carmotors.sgt.dao.impl.LoteDAOImpl;
import com.carmotors.sgt.dao.impl.MantenimientoTipoDAOImpl;
import com.carmotors.sgt.dao.impl.OrdenServicioDAOImpl;
import com.carmotors.sgt.dao.impl.OrdenServicioDetalleDAOImpl;
import com.carmotors.sgt.dao.impl.ProveedorDAOImpl;
import com.carmotors.sgt.dao.impl.ProveedorRepuestoDAOImpl;
import com.carmotors.sgt.dao.impl.ReporteDAOImpl;
import com.carmotors.sgt.dao.impl.RepuestoDAOImpl;
import com.carmotors.sgt.dao.impl.RepuestoTipoDAOImpl;
import com.carmotors.sgt.dao.impl.ServicioTipoDAOImpl;
import com.carmotors.sgt.dao.impl.TecnicoDAOImpl;
import com.carmotors.sgt.dao.impl.VehiculoDAOImpl;
import com.carmotors.sgt.service.ClienteService;
import com.carmotors.sgt.service.EvaluacionProveedorService;
import com.carmotors.sgt.service.FacturaService;
import com.carmotors.sgt.service.HistorialClienteService;
import com.carmotors.sgt.service.InventarioService;
import com.carmotors.sgt.service.LoteService;
import com.carmotors.sgt.service.OrdenServicioService;
import com.carmotors.sgt.service.ProveedorService;
import com.carmotors.sgt.service.ProveedorRepuestoService;
import com.carmotors.sgt.service.ReporteService;
import com.carmotors.sgt.service.RepuestoService;
import com.carmotors.sgt.service.ServicioTipoService;
import com.carmotors.sgt.service.TecnicoService;
import com.carmotors.sgt.service.facade.TallerFacade;
import com.carmotors.sgt.service.observer.AlertaStockBajoObserver;
import com.carmotors.sgt.service.VehiculoService;
import com.carmotors.sgt.view.CatalogoProveedorView;
import com.carmotors.sgt.view.ClienteView;
import com.carmotors.sgt.view.ConsolaMenuView;
import com.carmotors.sgt.view.EvaluacionProveedorView;
import com.carmotors.sgt.view.FacturacionView;
import com.carmotors.sgt.view.LoteView;
import com.carmotors.sgt.view.OrdenServicioView;
import com.carmotors.sgt.view.ProveedorView;
import com.carmotors.sgt.view.ReportesView;
import com.carmotors.sgt.view.RepuestoView;
import com.carmotors.sgt.view.ServicioTipoView;
import com.carmotors.sgt.view.ServiciosMenuView;
import com.carmotors.sgt.view.TecnicoView;

/**
 * Punto de entrada de la aplicación CarMotors SGT.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("CarMotors - Sistema de Gestión de Taller");
        try {
            ConsolaMenuView menuPrincipal = new ConsolaMenuView();

            ClienteDAO clienteDAO = new ClienteDAOImpl();
            ClienteService clienteService = new ClienteService(clienteDAO);
            VehiculoDAO vehiculoDAO = new VehiculoDAOImpl();
            VehiculoService vehiculoService = new VehiculoService(vehiculoDAO, clienteDAO);
            ClienteController clienteController = new ClienteController(clienteService, vehiculoService,
                    new HistorialClienteService());
            menuPrincipal.setSubmenuClientes(new ClienteView(clienteController));

            ProveedorDAO proveedorDAO = new ProveedorDAOImpl();
            ProveedorService proveedorService = new ProveedorService(proveedorDAO);
            ProveedorController proveedorController = new ProveedorController(proveedorService);

            EvaluacionProveedorDAO evaluacionProveedorDAO = new EvaluacionProveedorDAOImpl();
            EvaluacionProveedorService evaluacionProveedorService = new EvaluacionProveedorService(evaluacionProveedorDAO, proveedorDAO);
            EvaluacionProveedorController evaluacionProveedorController = new EvaluacionProveedorController(evaluacionProveedorService);
            EvaluacionProveedorView evaluacionProveedorView = new EvaluacionProveedorView(evaluacionProveedorController, proveedorController);

            RepuestoDAO repuestoDAO = new RepuestoDAOImpl();
            ProveedorRepuestoDAO proveedorRepuestoDAO = new ProveedorRepuestoDAOImpl();
            RepuestoService repuestoService = new RepuestoService(
                    repuestoDAO, new RepuestoTipoDAOImpl(), proveedorRepuestoDAO, proveedorDAO);
            RepuestoController repuestoController = new RepuestoController(repuestoService);

            ProveedorRepuestoService proveedorRepuestoService =
                    new ProveedorRepuestoService(proveedorRepuestoDAO, proveedorDAO, repuestoDAO, evaluacionProveedorDAO);
            ProveedorRepuestoController proveedorRepuestoController = new ProveedorRepuestoController(proveedorRepuestoService);
            CatalogoProveedorView catalogoProveedorView =
                    new CatalogoProveedorView(proveedorRepuestoController, proveedorController, repuestoController);

            LoteDAO loteDAO = new LoteDAOImpl();
            LoteService loteService = new LoteService(loteDAO, proveedorDAO, proveedorRepuestoDAO, repuestoDAO, true);
            LoteController loteController = new LoteController(loteService);
            LoteView loteView = new LoteView(loteController, proveedorController, repuestoController);

            InventarioService inventarioService = new InventarioService(
                    repuestoDAO, loteDAO, proveedorRepuestoDAO, proveedorDAO);
            inventarioService.agregarObserver(new AlertaStockBajoObserver());
            InventarioController inventarioController = new InventarioController(inventarioService);

            menuPrincipal.setSubmenuProveedores(new ProveedorView(proveedorController, evaluacionProveedorView, catalogoProveedorView));
            menuPrincipal.setSubmenuInventario(new RepuestoView(repuestoController, catalogoProveedorView, loteView, inventarioController));

            EspecialidadDAO especialidadDAO = new EspecialidadDAOImpl();
            MantenimientoTipoDAO mantenimientoTipoDAO = new MantenimientoTipoDAOImpl();

            TecnicoDAO tecnicoDAO = new TecnicoDAOImpl();
            TecnicoService tecnicoService = new TecnicoService(tecnicoDAO, especialidadDAO);
            TecnicoController tecnicoController = new TecnicoController(tecnicoService);
            menuPrincipal.setSubmenuTecnicos(new TecnicoView(tecnicoController));

            ServicioTipoDAO servicioTipoDAO = new ServicioTipoDAOImpl();
            ServicioTipoService servicioTipoService = new ServicioTipoService(
                    servicioTipoDAO, mantenimientoTipoDAO, especialidadDAO);
            ServicioTipoController servicioTipoController = new ServicioTipoController(servicioTipoService);
            ServicioTipoView servicioTipoView = new ServicioTipoView(servicioTipoController);

            OrdenServicioService ordenServicioService = new OrdenServicioService(vehiculoDAO, clienteDAO, servicioTipoDAO,
                    repuestoDAO, new DetalleRepuestoDAOImpl(), inventarioService);
            ordenServicioService.registrarObservador(new ObservadorLogServicio());
            ordenServicioService.registrarObservador(new ObservadorNotificacionCliente());
            TallerFacade tallerFacade = new TallerFacade(ordenServicioService, inventarioService, tecnicoService);
            OrdenServicioController ordenServicioController = new OrdenServicioController(ordenServicioService, tallerFacade);
            OrdenServicioView ordenServicioView =
                    new OrdenServicioView(ordenServicioController, clienteController, servicioTipoController);
            menuPrincipal.setSubmenuServicios(new ServiciosMenuView(servicioTipoView, ordenServicioView));

            ReporteDAO reporteDAO = new ReporteDAOImpl();
            ReporteService reporteService = new ReporteService(reporteDAO, tecnicoDAO);
            ReportesController reportesController = new ReportesController(reporteService);
            menuPrincipal.setSubmenuReportes(new ReportesView(reportesController));

            FacturaService facturaService = new FacturaService(new FacturaDAOImpl(), new FacturaSnapshotDAOImpl(),
                    new FacturaDetalleDAOImpl(), new OrdenServicioDAOImpl(), new OrdenServicioDetalleDAOImpl(),
                    new DetalleRepuestoDAOImpl(), vehiculoDAO, clienteDAO);
            FacturaController facturaController = new FacturaController(facturaService);
            menuPrincipal.setSubmenuFacturacion(new FacturacionView(facturaController));

            menuPrincipal.mostrarMenuPrincipal();
        } catch (Exception e) {
            System.out.println("\n  [!] Error crítico al ejecutar CarMotors SGT: " + e.getMessage());
        }
    }
}
