package com.carmotors.sgt.service.observer;

import com.carmotors.sgt.exception.CarMotorsException;
import com.carmotors.sgt.exception.ValidacionException;
import com.carmotors.sgt.model.EventoServicioCompletado;
import com.carmotors.sgt.util.InputValidator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;


public class ObservadorNotificacionCliente implements Observador {

    public static final String ARCHIVO_POR_DEFECTO = "notificaciones_clientes.txt";

    private static final DateTimeFormatter FORMATO_FECHA_HORA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String rutaArchivo;


    public ObservadorNotificacionCliente() {
        this(ARCHIVO_POR_DEFECTO);
    }


    public ObservadorNotificacionCliente(String rutaArchivo) {
        if (!InputValidator.esTextoNoVacio(rutaArchivo)) {
            throw new ValidacionException("La ruta del archivo de notificaciones no puede estar vacía.");
        }
        this.rutaArchivo = rutaArchivo.trim();
    }

    @Override
    public void actualizar(EventoServicioCompletado evento) {
        String linea = formatearLinea(evento);
        File archivo = new File(rutaArchivo);
        File directorioPadre = archivo.getParentFile();
        if (directorioPadre != null && !directorioPadre.exists() && !directorioPadre.mkdirs()) {
            throw new CarMotorsException("No se pudo crear el directorio para '" + rutaArchivo + "'.");
        }

        try (BufferedWriter escritor = new BufferedWriter(
                new FileWriter(archivo, StandardCharsets.UTF_8, true))) {
            escritor.write(linea);
            escritor.newLine();
        } catch (IOException e) {
            throw new CarMotorsException(
                    "No se pudo escribir la notificación del cliente en '" + rutaArchivo + "': " + e.getMessage(),
                    e);
        }
    }

    public String formatearLinea(EventoServicioCompletado evento) {
        if (evento == null || evento.getFechaEvento() == null) {
            throw new ValidacionException("El evento de servicio completado y su fecha son obligatorios.");
        }
        String fechaHora = evento.getFechaEvento().format(FORMATO_FECHA_HORA);
        return "[" + fechaHora + "] - NOTIFICACIÓN PARA " + evento.getNombreCliente()
                + ": Su vehículo con placa " + evento.getPlacaVehiculo()
                + " está listo para ser recogido.";
    }
}
