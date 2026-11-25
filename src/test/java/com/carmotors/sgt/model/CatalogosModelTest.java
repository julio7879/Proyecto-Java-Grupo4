package com.carmotors.sgt.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias para las 4 entidades de tablas de consulta / catálogos fijos.
 */
class CatalogosModelTest {

    @Test
    @DisplayName("RepuestoTipo POJO debe soportar constructores, getters, setters y toString")
    void testRepuestoTipo() {
        RepuestoTipo tipo = new RepuestoTipo();
        tipo.setId(1);
        tipo.setNombre("Mecánico");

        assertEquals(1, tipo.getId());
        assertEquals("Mecánico", tipo.getNombre());
        assertTrue(tipo.toString().contains("1") && tipo.toString().contains("Mecánico"));

        RepuestoTipo conParams = new RepuestoTipo(2, "Eléctrico");
        assertEquals(2, conParams.getId());
        assertEquals("Eléctrico", conParams.getNombre());
    }

    @Test
    @DisplayName("Especialidad POJO debe soportar constructores, getters, setters y toString")
    void testEspecialidad() {
        Especialidad esp = new Especialidad();
        esp.setId(1);
        esp.setNombre("Mecánica general");

        assertEquals(1, esp.getId());
        assertEquals("Mecánica general", esp.getNombre());
        assertTrue(esp.toString().contains("1") && esp.toString().contains("Mecánica general"));

        Especialidad conParams = new Especialidad(2, "Motor");
        assertEquals(2, conParams.getId());
        assertEquals("Motor", conParams.getNombre());
    }

    @Test
    @DisplayName("MantenimientoTipo POJO debe soportar constructores, getters, setters y toString")
    void testMantenimientoTipo() {
        MantenimientoTipo mt = new MantenimientoTipo();
        mt.setId(1);
        mt.setNombre("Preventivo");

        assertEquals(1, mt.getId());
        assertEquals("Preventivo", mt.getNombre());
        assertTrue(mt.toString().contains("1") && mt.toString().contains("Preventivo"));

        MantenimientoTipo conParams = new MantenimientoTipo(2, "Correctivo");
        assertEquals(2, conParams.getId());
        assertEquals("Correctivo", conParams.getNombre());
    }

    @Test
    @DisplayName("ServicioEstado POJO debe soportar constructores, getters, setters y toString")
    void testServicioEstado() {
        ServicioEstado se = new ServicioEstado();
        se.setId(1);
        se.setNombre("Pendiente");

        assertEquals(1, se.getId());
        assertEquals("Pendiente", se.getNombre());
        assertTrue(se.toString().contains("1") && se.toString().contains("Pendiente"));

        ServicioEstado conParams = new ServicioEstado(6, "Cancelada");
        assertEquals(6, conParams.getId());
        assertEquals("Cancelada", conParams.getNombre());
    }
}
