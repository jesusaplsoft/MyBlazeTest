package com.gexcat.gex.repository.dto;

import java.io.Serializable;

import com.gexcat.gex.type.Nodo;

import lombok.Value;

@Value
public class AlumnoTreeNodeDto implements Serializable {

    private static final long serialVersionUID = -7249761016197454582L;

    Long id;

    String nombre;

    String apellidos;

    String dni;

    Long cursoId;

    Nodo nodo = Nodo.ALUMNO;

    public String getCodigo() {
        return dni;
    }
}
