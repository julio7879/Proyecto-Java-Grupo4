package com.carmotors.sgt.model;

import java.time.LocalDateTime;

/**
 * DTO de solo transporte con los datos de una orden completada
 * (orden, vehículo, cliente y fecha del evento) para los observadores.
 */
public class EventoServicioCompletado {

    private final OrdenServicio orden;
    private final Vehiculo vehiculo;
    private final Cliente cliente;
    private final LocalDateTime fechaEvento;

    public EventoServicioCompletado(OrdenServicio orden, Vehiculo vehiculo, Cliente cliente) {
        this.orden = orden;
        this.vehiculo = vehiculo;
        this.cliente = cliente;
        this.fechaEvento = orden != null && orden.getFechaCompletado() != null
                ? orden.getFechaCompletado()
                : LocalDateTime.now().withNano(0);
    }

    public int getOrdenId() {
        return orden != null ? orden.getId() : 0;
    }

    public String getPlacaVehiculo() {
        return vehiculo != null && vehiculo.getPlaca() != null ? vehiculo.getPlaca().trim() : "";
    }

    public String getNombreCliente() {
        return cliente != null && cliente.getNombre() != null ? cliente.getNombre().trim() : "";
    }

    public LocalDateTime getFechaEvento() {
        return fechaEvento;
    }

    @Override
    public String toString() {
        return "EventoServicioCompletado{" +
                "ordenId=" + getOrdenId() +
                ", placa='" + getPlacaVehiculo() + '\'' +
                ", cliente='" + getNombreCliente() + '\'' +
                ", fechaEvento=" + fechaEvento +
                '}';
    }
}
