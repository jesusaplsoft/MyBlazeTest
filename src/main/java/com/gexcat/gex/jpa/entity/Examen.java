package com.gexcat.gex.jpa.entity;

import static com.gexcat.gex.cfg.Constants.CIEN;
import static com.gexcat.gex.cfg.Constants.UNO;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Check;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.envers.NotAudited;

import com.gexcat.gex.type.EnumType;
import com.gexcat.gex.type.EstadoExamen;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Examen realizado.
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
@OptimisticLocking
@Setter
@Table(
    name = "examen",
    indexes = {
        @Index(
            name = "exa_idx_curso",
            columnList = "curso_id"
        )
    }
)
@ToString
@org.hibernate.annotations.Table(
    appliesTo = "examen",
    comment = "Examen realizado"
)
@Check(
    constraints = "estado IN ('N', 'O', 'C', 'S', 'D', 'R', 'E')"
)
public class Examen
    implements Serializable, Cloneable {

    private static final long serialVersionUID = -1556520611763127408L;

    @Column(
        updatable = false,
        nullable = false
    )
    @SequenceGenerator(
        name = "examenSequence",
        sequenceName = "exa_sequence",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "examenSequence"
    )
    @ColumnDefault(value = "(next value for exa_sequence)")
    @Setter(AccessLevel.NONE)
    @Id
    private Long id;

    @Column(
        name = "descripcion",
        nullable = false,
        length = CIEN
    )
    @NotNull
    @Size(max = CIEN)
    private String descripcion;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(
        name = "estado",
        nullable = false,
        length = UNO
    )
    @ColumnDefault(EstadoExamen.DB_DEFAULT)
    @NotNull
    @Type(
        type = EnumType.TYPE,
        parameters = {
            @Parameter(
                name = EnumType.CLASS,
                value = "com.gexcat.gex.type.EstadoExamen"
            )
        }
    )
    @Default
    private EstadoExamen estado = EstadoExamen.NUEVO;

//    @OneToOne(
//        mappedBy = "examen",
//        cascade = CascadeType.ALL,
//        fetch = FetchType.LAZY,
//        orphanRemoval = true,
//        optional = false
//    )
//    @NotAudited
//    @ToString.Exclude
//    private Formula formula;

//    @OneToMany(
//        mappedBy = "examen",
//        cascade = { CascadeType.PERSIST, CascadeType.MERGE },
//        orphanRemoval = true
//    )
//    @OrderBy(value = "codigo")
//    @NotAudited
//    @ToString.Exclude
//    @Default
//    private final Set<ModeloExamen> modelos = new HashSet<>();

    @OneToMany(
        mappedBy = "examen",
        cascade = { CascadeType.PERSIST, CascadeType.MERGE },
        orphanRemoval = true
    )
    @NotAudited
    @ToString.Exclude
    @Default
    private final Set<Nota> notas = new HashSet<>();

//    @OneToMany(
//        mappedBy = "examen",
//        cascade = { CascadeType.PERSIST, CascadeType.MERGE },
//        orphanRemoval = true
//    )
//    @NotAudited
//    @ToString.Exclude
//    @Default
//    private final Set<PreguntaEnExamen> preguntasEnExamen = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "curso_id",
        referencedColumnName = "id",
        nullable = false,
        foreignKey = @ForeignKey(
            name = "exa_fk_curso"
        )
    )
    @NotNull
    @NotAudited
    @ToString.Exclude
    private Curso curso;

//    public void addFormula(final Formula formula) {
//        this.formula = formula;
//        formula.setExamen(this);
//    }
//
//    public void removeFormula(final Formula formula) {
//        this.formula = null;
//
//        if (formula != null) {
//            formula.setExamen(null);
//        }
//    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final var that = (Examen) o;
        return this.id != null && this.id.equals(that.getId());
    }
}
