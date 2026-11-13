package com.carmotors.excepcion;

/**
 * Error de configuración de la aplicación (archivo faltante, propiedad
 * ausente o con formato inválido). Es unchecked porque la aplicación no puede
 * recuperarse de ella: hay que corregir la configuración y volver a ejecutar.
 */
public class ConfiguracionException extends RuntimeException {

    public ConfiguracionException(String mensaje) {
        super(mensaje);
    }

    public ConfiguracionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
