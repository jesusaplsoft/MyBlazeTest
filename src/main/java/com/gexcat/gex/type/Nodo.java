package com.gexcat.gex.type;

//import com.apl.components.misc.MiscComponent;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Nodo {
    CENTRO("centros16.png", null),
    ASIGNATURA("asignaturas16.png", CENTRO),
    CURSO("calendario16.png", ASIGNATURA),
    ALUMNO("alumnos16.png", CURSO),
    TEMA("temas16.png", ASIGNATURA);

    private final String imagen;
    private final Nodo padre;

    public Nodo getPadre() {
        return padre;
    }

//    public String getImagen() {
//        return Misc.getIconGroup().getRuta() + imagen;
//    }
}
