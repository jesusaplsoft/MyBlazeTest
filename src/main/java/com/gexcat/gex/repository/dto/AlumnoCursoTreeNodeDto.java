package com.gexcat.gex.repository.dto;

import java.io.Serializable;

import lombok.Value;

@Value
public class AlumnoCursoTreeNodeDto implements Serializable {

    private static final long serialVersionUID = -4430168950015693300L;

    private String nombre;
    private String apellidos;
    private String dni;
    private Long cursoId;
}
