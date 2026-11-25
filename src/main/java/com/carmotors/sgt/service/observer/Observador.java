package com.carmotors.sgt.service.observer;

import com.carmotors.sgt.model.EventoServicioCompletado;


public interface Observador {


    void actualizar(EventoServicioCompletado evento);
}
