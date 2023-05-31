package com.gexcat.gex.jpa.entity;

import static com.gexcat.gex.cfg.Constants.TRECE;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
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
 * Nota obtenida en un examen.
 *
 * @author JMR + vaneMB
 */
@AllArgsConstructor
@NoArgsConstructor
//@Audited
@Builder
@DynamicInsert
@DynamicUpdate
@Entity
@Getter
@OptimisticLocking(type = OptimisticLockType.DIRTY)
@Setter
@Table(
    name = "nota",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "not_unq_ace",
            columnNames = {
                "alumnoId",
                "cursoId",
                "examen_id"
            }
        )
    }
)
@ToString
@org.hibernate.annotations.Table(
    appliesTo = "nota",
    comment = "Nota obtenida en un examen"
)
public class Nota
    implements Serializable, Cloneable {

    private static final long serialVersionUID = -1556520611763127408L;

    @Column(
        updatable = false,
        nullable = false
    )
    @SequenceGenerator(
        name = "notaSequence",
        sequenceName = "not_sequence",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "notaSequence"
    )
    @ColumnDefault(value = "(next value for not_sequence)")
    @Setter(AccessLevel.NONE)
    @Id
    private Long id;

    @Column(
        name = "codigo_barras",
        length = TRECE
    )
    @Size(max = TRECE)
    private String codigoBarras;

    /**
     * Calificación que obtuvo el alumno. Normalmente, se genera a partir de la fórmula especificada en el examen.
     */
    @Column(
        name = "nota",
        nullable = false,
        precision = 9,
        scale = 6
    )
    @ColumnDefault("0.000000")
    @DecimalMax("100.000000")
    @DecimalMin("-100.000000")
    @Digits(
        integer = 3,
        fraction = 6
    )
    @NotNull
    @Default
    private BigDecimal nota = BigDecimal.ZERO;

    /**
     * Extensión de la información sobre la calificación para exámenes de tipo test.
     */
    @OneToOne(
        mappedBy = "nota",
        cascade = CascadeType.ALL,
        fetch = FetchType.LAZY,
        orphanRemoval = true
    )
    @NotAudited
    @ToString.Exclude
    private Correccion correccion;

    // Relación propietaria
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns(
        foreignKey = @ForeignKey(
            name = "not_fk_alumno_curso"
        ),
        value = {
            @JoinColumn(
                name = "alumnoId",
                referencedColumnName = "alumno_id",
                nullable = false
            ),
            @JoinColumn(
                name = "cursoId",
                referencedColumnName = "curso_id",
                nullable = false
            )
        }
    )
    @NotNull
    @NotAudited
    @ToString.Exclude
//    @NonNull
    private AlumnoCurso alumnoCurso;

    // Relación propietaria
    @ManyToOne(
        fetch = FetchType.LAZY
    )
    @JoinColumn(
        name = "examen_id",
        referencedColumnName = "id",
        nullable = false,
        foreignKey = @ForeignKey(
            name = "not_fk_examen"
        )
    )
    @NotNull
    @NotAudited
    @ToString.Exclude
//    @NonNull
    private Examen examen;

    public void addCorreccion(final Correccion correccion) {
        this.correccion = correccion;
        correccion.setNota(this);
    }

    public void removeCorreccion(final Correccion correccion) {
        this.correccion = null;

        if (correccion != null) {
            correccion.setNota(null);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(alumnoCurso, examen);
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final var that = (Nota) o;
        return Objects.equals(alumnoCurso, that.getAlumnoCurso()) && Objects.equals(examen, that.getExamen());
    }

}
