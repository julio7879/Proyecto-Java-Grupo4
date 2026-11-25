package com.carmotors.sgt.service.observer;

import com.carmotors.sgt.model.EventoServicioCompletado;


public interface Sujeto {


    void registrarObservador(Observador observador);


    @SuppressWarnings("unused")
    void removerObservador(Observador observador);


    void notificarObservadores(EventoServicioCompletado evento);
}
