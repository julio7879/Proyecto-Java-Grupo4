package com.carmotors.sgt.service.observer;

import com.carmotors.sgt.exception.ValidacionException;
import com.carmotors.sgt.model.EventoServicioCompletado;


public class ObservadorLogServicio implements Observador {

    @Override
    public void actualizar(EventoServicioCompletado evento) {

        System.out.println(formatearMensaje(evento));
    }

    public String formatearMensaje(EventoServicioCompletado evento) {
        if (evento == null) {
            throw new ValidacionException("El evento de servicio completado no puede ser nulo.");
        }
        return "LOG: El servicio # " + evento.getOrdenId()
                + " para el vehículo " + evento.getPlacaVehiculo()
                + " ha sido completado.";
    }
}
