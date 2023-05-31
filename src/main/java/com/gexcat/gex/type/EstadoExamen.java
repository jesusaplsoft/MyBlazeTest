package com.gexcat.gex.type;

import com.gexcat.gex.tool.FileMisc;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum EstadoExamen {
    NUEVO("N"),
    CORREGIDO("O"),
    CERRADO("C"),
    CERRADO_SIN_CALCULAR("S"),
    DESARROLLO_NUEVO("D"),
    DESARROLLO_CORREGIDO("R"),
    DESARROLLO_CERRADO("E");

    public static final String DB_DEFAULT = "'N'";

    private String tipo;

    public String toValue() {
        return tipo;
    }

    @Override
    public String toString() {
        return FileMisc.getBundle("EstadoExamen." + name());
    }

    public static EstadoExamen fromValue(final String tipo) {

        for (final EstadoExamen frm : EstadoExamen.values()) {

            if (frm.toValue().equals(tipo)) {
                return frm;
            }
        }

        return null;
    }

    public static EstadoExamen defaultValue() {
        return NUEVO;
    }
}
