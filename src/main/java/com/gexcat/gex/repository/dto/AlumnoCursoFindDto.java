package com.gexcat.gex.repository.dto;

import java.io.Serializable;

import com.gexcat.gex.jpa.misc.GexCatMisc;

import lombok.Value;

@Value
public class AlumnoCursoFindDto implements Serializable {

    private static final long serialVersionUID = -3686269191352675415L;

    String dni;
    Integer identificador;
    String nombre;
    String apellidos;
    String codigoCentro;
    String codigoAsignatura;
    Long idCurso;
    String codigoCurso;

    public String getNombreCompleto() {
        return GexCatMisc.getTextoAlumno(apellidos, nombre);
    }
}
