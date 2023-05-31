package com.gexcat.gex.type;

import com.gexcat.gex.tool.FileMisc;

import lombok.AllArgsConstructor;

/**
 * <p>
 * Tipos de Sexo
 * </p>
 */
@AllArgsConstructor
public enum TipoSexo {
    HOMBRE("H"),
    MUJER("M"),
    INDEFINIDO("");

    private String tipo;

    public String toValue() {
        return tipo;
    }

    public String getText() {

        if (tipo.length() == 0) {
            return " ";
        }

        return FileMisc.getBundle("TipoSexo." + name());
    }

    public String getTipo() {

        if (tipo == null) {
            return "";
        }

        return String.valueOf(toString().charAt(0));
    }

    public static TipoSexo defaultValue() {
        return TipoSexo.INDEFINIDO;
    }

    public static TipoSexo fromValue(final String tipo) {

        if (tipo == null) {
            return INDEFINIDO;
        }
        return HOMBRE.getTipo().equalsIgnoreCase(tipo) ? HOMBRE
            : MUJER.getTipo().equalsIgnoreCase(tipo) ? MUJER : INDEFINIDO;
    }
}
