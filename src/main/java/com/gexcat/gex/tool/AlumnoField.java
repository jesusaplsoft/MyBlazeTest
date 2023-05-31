package com.gexcat.gex.tool;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum AlumnoField {
    DNI(0, "dni", "lblDni.text"),
    IDENTIFICADOR(1, "identificacion", "lblIdentificador.text"),
    NIU(2, "niu", "lblNiu.text"),
    NOMBRE(3, "nombre", "lblNombre.text"),
    APELLIDOS(4, "apellidos", "lblApellidos.text"),
    APELLIDO_1(5, "apellido1", null),
    APELLIDO_2(6, "apellido2", null),
    NOMBRE_COMPLETO(7, "nombreCompleto", null),
    CORREO(8, "correo", "lblCorreo.text"),
    SEXO(9, "sexo_aux", "Alumnos.lblSexo.text"),
    MOVIL(10, "movil", "Alumnos.lblMovil.text"),
    FECHA_NACIMIENTO(11, "fecha_nacimiento", null),
    NACIONALIDAD(12, "nacionalidad", null),
    INICIO_ESTUDIOS(13, "inicio_estudios", null),
    ESTUDIOS_ANTERIORES(14, "estudios_anteriores", null),
    TRABAJA(15, "trabaja_aux", null),
    EMPRESA(16, "empresa", null),
    HORARIO(17, "horario", null),
    OBSERVACIONES_ALUMNO(18, "observaciones_alumno", "lblObservaciones.text");

    private static final int VALORES = 19;

    private final int indice;
    private final String nombre;
    private final String texto;

    public int getIndice() {
        return indice;
    }

    public int getIndiceTabla() {
        return indice + 1;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTexto() {

        if (texto == null) {
            return FileMisc.getBundle("AlumnoField." + name());
        }

        return FileMisc.getBundle(texto).trim();
    }

    public static boolean[] getIndices(final String... nombres) {
        final var marcas = new boolean[VALORES];

        for (final String nombre : nombres) {

            for (final AlumnoField af : values()) {

                if (nombre.trim().equalsIgnoreCase(af.getNombre())) {
                    marcas[af.indice] = true;
                    break;
                }
            }
        }

        return marcas;
    }

    public static List<String> comprobarCampos(final String... nombres) {
        final List<String> lista = new ArrayList<>();
        var existe = false;
        var i = 0;

        for (final String nombre : nombres) {
            existe = false;

            for (final AlumnoField af : values()) {

                if (nombre.trim().equalsIgnoreCase(af.getTexto())
                    || nombre.trim().equalsIgnoreCase(af.getNombre())) {
                    nombres[i] = af.getNombre();
                    existe = true;
                    break;
                }
            }

            if (!existe) {
                lista.add(nombre);
            }

            i++;
        }

        return lista;
    }
}
