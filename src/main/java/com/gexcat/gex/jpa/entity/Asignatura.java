package com.gexcat.gex.jpa.entity;

import static com.gexcat.gex.cfg.Constants.DIEZ;
import static com.gexcat.gex.cfg.Constants.SESENTA;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.envers.NotAudited;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Información de la/s asignatura/s que imparte un profesor.
 *
 * @author JMR + vaneMB
 */
@AllArgsConstructor
//@Audited
@Builder
@DynamicInsert
@DynamicUpdate
@Entity
@Getter
@NoArgsConstructor
@OptimisticLocking(type = OptimisticLockType.DIRTY)
@Setter
@Table(
    name = "asignatura",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "asi_unq_codigo",
            columnNames = { "codigo" }
        )
    },
    indexes = {
        @Index(
            name = "asi_idx_nombre",
            columnList = "nombre"
        )
    }
)
@ToString
@org.hibernate.annotations.Table(
    appliesTo = "asignatura",
    comment = "Información de la/s asignatura/s que imparte un profesor"
)
public class Asignatura
    implements Serializable, Cloneable {

    private static final long serialVersionUID = -1556520611763127408L;

    @Column(
        updatable = false,
        nullable = false
    )
    @SequenceGenerator(
        name = "asignaturaSequence",
        sequenceName = "asi_sequence",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "asignaturaSequence"
    )
    @ColumnDefault(value = "(next value for asi_sequence)")
    @Setter(AccessLevel.NONE)
    @Id
    private Long id;

    @Column(
        nullable = false,
        length = DIEZ
    )
    @NotNull
    @Size(max = DIEZ)
//    @NonNull
    private String codigo;

    @Column(
        nullable = false,
        length = SESENTA
    )
    @NotNull
    @Size(max = SESENTA)
    private String nombre;

    @OneToMany(
        mappedBy = "asignatura",
        cascade = { CascadeType.PERSIST, CascadeType.MERGE }
    )
    @NotAudited
    @ToString.Exclude
    @Default
    private final Set<Curso> cursos = new HashSet<>();

    // Relación propietaria
    @ManyToOne(
        fetch = FetchType.LAZY
    )
    @JoinColumn(
        name = "centro_id",
        referencedColumnName = "id",
        foreignKey = @ForeignKey(
            name = "asi_fk_centro"
        )
    )
    @NotAudited
    @ToString.Exclude
    private Centro centro;

    // Helper methods
    public void addCurso(final Curso curso) {
        this.cursos.add(curso);
        curso.setAsignatura(this);
    }

    public void removeCurso(final Curso curso) {
        this.cursos.remove(curso);
        curso.setAsignatura(null);
    }

    @Override
    public int hashCode() {

        return Objects.hash(codigo);
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final var that = (Asignatura) o;

        return this.codigo != null && this.codigo.equals(that.getCodigo());
    }
}