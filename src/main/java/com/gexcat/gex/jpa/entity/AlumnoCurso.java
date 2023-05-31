package com.gexcat.gex.jpa.entity;

import static com.gexcat.gex.cfg.Constants.QUINIENTOS_DOCE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.NotAudited;

import com.gexcat.gex.tool.MiscBase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Información de los alumnos y cursos de cada uno.
 *
 * <p>
 * Información de qué cursos tiene cada alumno.
 * </p>
 *
 * @author jesusmarin
 */
@AllArgsConstructor
//@Audited
@Entity
@Getter
@Builder
@NoArgsConstructor
@Setter
@Table(name = "alumno_curso")
@ToString
@org.hibernate.annotations.Table(
    appliesTo = "alumno_curso",
    comment = "Alumno/s que hay en el Sistema +"
        + " Curso/s a los que pertenece cada uno"
)
public class AlumnoCurso
    implements Serializable {

    private static final long serialVersionUID = 6817322916844408094L;

    @EmbeddedId
    private AlumnoCursoId id;

    // Relación inversa
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "alumno_id",
        updatable = false,
        nullable = false,
        foreignKey = @ForeignKey(
            name = "aluc_fk_alumno"
        )
    )
    @MapsId("alumnoId")
    @NotNull
    @ToString.Exclude
    private Alumno alumno;

    // Relación inversa
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "curso_id",
        updatable = false,
        nullable = false,
        foreignKey = @ForeignKey(
            name = "aluc_fk_curso"
        )
    )
    @MapsId("cursoId")
    @NotNull
    @ToString.Exclude
    private Curso curso;

    @OneToMany(
        mappedBy = "alumnoCurso",
        cascade = { CascadeType.PERSIST, CascadeType.MERGE }
    )
    @NotAudited
    @ToString.Exclude
    @Default
    private Set<Nota> notas = new HashSet<>();

    @Column(
        name = "observaciones",
        length = QUINIENTOS_DOCE
    )
    @Size(max = QUINIENTOS_DOCE)
    private String observaciones;

    public AlumnoCurso(final Alumno alumno, final Curso curso) {
        this.alumno = alumno;
        this.curso = curso;
        this.id = new AlumnoCursoId(alumno.getId(), curso.getId());
    }

    // Helper methods
    public void addNota(final Nota nota) {

        if (notas == null || notas.isEmpty()) {
            notas = new HashSet<>();
        }

        notas.add(nota);
        nota.setAlumnoCurso(this);
    }

    public void removeNota(final Nota nota) {
        this.notas.remove(nota);
        nota.setAlumnoCurso(null);
    }

    public List<Nota> getNotasOrdenadas() {

        if (notas.isEmpty()) {
            return new ArrayList<>();
        }

        final List<Nota> notasOrdenadas = new ArrayList<>(notas);

        Collections.sort(notasOrdenadas, (n1, n2) -> {
            int aux;

            if (n1 == n2) {
                return 0;
            }
            if (n1.getExamen() != null && n2.getExamen() != null) {
                aux = MiscBase.compare(n1.getExamen().getFecha(), n2.getExamen().getFecha());
                return aux == 0 ? MiscBase.compare(n1.getId(), n2.getId()) : aux;
            }
            return MiscBase.compareObject(n1.getExamen(), n2.getExamen());
        });

        return notasOrdenadas;
    }

    public void setNotas(final Set<Nota> set) {

        if (notas == null || notas.isEmpty()) {
            notas = new HashSet<>(set);
        } else {
            notas.addAll(set);
            notas.retainAll(set);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(alumno, curso);
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final var that = (AlumnoCurso) o;
        return Objects.equals(alumno, that.getAlumno()) && Objects.equals(curso, that.getCurso());
    }
}
