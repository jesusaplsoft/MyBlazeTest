package com.gexcat.gex.repository.dto;

import java.io.Serializable;

//import com.gexcat.gex.jpa.misc.GexCatMisc;

import lombok.Value;

@Value
public class AlumnoCursoDto implements Serializable {

    private static final long serialVersionUID = -5628442486091525316L;

    Long id;
    String dni;
    Integer identificador;
    String nombre;
    String apellidos;
    boolean enviarCorreo;
    String correo;
    Long cursoId;

//    public String getNombreCompleto() {
//        return GexCatMisc.getTextoAlumno(apellidos, nombre);
//    }

    public String getCorreo() {
        return enviarCorreo ? correo : null;
    }

//    @Override
//    public String toString() {
//        return getNombreCompleto();
//    }
}
