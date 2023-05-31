package com.gexcat.gex.repository.dto;

import java.io.Serializable;

import com.gexcat.gex.jpa.misc.GexCatMisc;

import lombok.Value;

public record AlumnoCursoFindRecord(
    String dni,
    Integer identificador,
    String nombre,
    String apellidos,
    String codigoCentro,
    String codigoAsignatura,
    Long idCurso,
    String codigoCurso) implements Serializable {

    public String getNombreCompleto() {
        return GexCatMisc.getTextoAlumno(apellidos, nombre);
    }
}
