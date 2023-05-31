package com.gexcat.gex.jpa.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Información de curso/s que procesa un(os) alumno/s.
 *
 * @author jesusmarin
 */
@AllArgsConstructor
@Embeddable
@Getter
@NoArgsConstructor
@ToString
public class AlumnoCursoId
    implements Serializable {

    private static final long serialVersionUID = -1556520611763127408L;

    @NotNull
    private Long alumnoId;

    @NotNull
    private Long cursoId;

    @Override
    public int hashCode() {
        return Objects.hash(alumnoId, cursoId);
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final var that = (AlumnoCursoId) o;
        return Objects.equals(alumnoId, that.getAlumnoId()) && Objects.equals(cursoId, that.getCursoId());
    }
}
