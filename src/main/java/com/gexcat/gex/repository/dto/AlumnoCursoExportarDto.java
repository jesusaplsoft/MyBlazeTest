package com.gexcat.gex.repository.dto;

import java.io.Serializable;

import com.gexcat.gex.type.TipoSexo;

import lombok.Value;

@Value
public class AlumnoCursoExportarDto implements Serializable {

    private static final long serialVersionUID = -4741854108020428233L;

    Long id;
    String dni;
    Integer identificador;
    String nombre;
    String apellidos;
    String movil;
    TipoSexo sexo;
    String niu;
    String correo;
    String observaciones;

    public String getApellido1() {
        final var aux = apellidos.split(" ");
        return aux[0];
    }

    public String getApellido2() {
        final var aux = apellidos.split(" ");

        if (aux.length > 2) {
            var aux2 = "";

            for (var i = 1; i < aux.length; i++) {
                aux2 += aux[i] + " ";
            }

            return aux2.trim();
        }

        return aux.length == 1 ? null : aux[1];
    }

    public String getNombreCompleto() {
        return apellidos + ", " + nombre;
    }

}
