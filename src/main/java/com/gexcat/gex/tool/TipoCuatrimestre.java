package com.gexcat.gex.tool;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum TipoCuatrimestre {
    ANUAL("0"),
    PRIMERO("1"),
    SEGUNDO("2");

    public static final String DB_DEFAULT = "'0'";

    private String tipo;

    public String toValue() {
        return tipo;
    }

    public String getText() {
        return FileMisc.getBundle("TipoCuatrimestre." + name());
    }

    public static TipoCuatrimestre fromValue(final String tipo) {

        for (final TipoCuatrimestre frm : TipoCuatrimestre.values()) {

            if (frm.toValue().equals(tipo)) {
                return frm;
            }
        }

        return null;
    }

    public static TipoCuatrimestre defaultValue() {
        return ANUAL;
    }
}
