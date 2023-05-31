package com.gexcat.gex.jpa.entity;

import static com.gexcat.gex.cfg.Constants.QUINCE;
import static com.gexcat.gex.cfg.Constants.UNO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Check;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.envers.NotAudited;

import com.gexcat.gex.tool.MiscBase;
import com.gexcat.gex.type.EnumType;
import com.gexcat.gex.type.TipoCuatrimestre;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Asignaturas de cada curso.
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
    name = "curso",
    uniqueConstraints = { @UniqueConstraint(
        name = "cur_unq_codigo_asignatura",
        columnNames = {
            "codigo",
            "asignatura_id" }
    )
    },
    indexes = @Index(
        name = "cur_idx_asignatura",
        columnList = "asignatura_id"
    )
)
@ToString
@org.hibernate.annotations.Table(
    appliesTo = "curso",
    comment = "Información del curso"
)
@Check(
    constraints = "cuatrimestre IN ('0', '1', '2')"
)
public class Curso
    implements Serializable, Cloneable {

    @Transient
    private static final long serialVersionUID = -1556520611763127408L;

    @Column(
        updatable = false,
        nullable = false
    )
    @SequenceGenerator(
        name = "cursoSequence",
        sequenceName = "cur_sequence",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "cursoSequence"
    )
    @ColumnDefault(value = "(next value for cur_sequence)")
    @Setter(AccessLevel.NONE)
    @Id
    private Long id;

    @Column(
        name = "codigo",
        nullable = false,
        length = QUINCE
    )
    @NotNull
    @Size(max = QUINCE)
    private String codigo;

    @Column(
        name = "cuatrimestre",
        nullable = false,
        length = UNO
    )
    @ColumnDefault(TipoCuatrimestre.DB_DEFAULT)
    @NotNull
    @Type(
        type = EnumType.TYPE,
        parameters = {
            @Parameter(
                name = EnumType.CLASS,
                value = "com.gexcat.gex.type.TipoCuatrimestre"
            )
        }
    )
    @Default
    private TipoCuatrimestre cuatrimestre = TipoCuatrimestre.ANUAL;

    @OneToMany(
        mappedBy = "curso",
        cascade = { CascadeType.PERSIST, CascadeType.MERGE }
    )
    @NotAudited
    @ToString.Exclude
    @Default
    private final Set<Examen> examenes = new HashSet<>();

    @OneToMany(
        mappedBy = "curso",
        cascade = { CascadeType.PERSIST, CascadeType.MERGE },
        orphanRemoval = true
    )
    @NotAudited
    @ToString.Exclude
    @Default
    private final Set<Tema> temas = new HashSet<>();

    @ManyToOne(
        fetch = FetchType.LAZY
    )
    @JoinColumn(
        name = "asignatura_id",
        referencedColumnName = "id",
        foreignKey = @ForeignKey(
            name = "cur_fk_asignatura"
        )
    )
    @NotAudited
    private Asignatura asignatura;

    @ManyToOne(
        fetch = FetchType.LAZY
    )
    @JoinColumn(
        name = "grupo_id",
        referencedColumnName = "id",
        foreignKey = @ForeignKey(
            name = "cur_fk_grupo"
        )
    )
    @NotAudited
    private Grupo grupo;

    // Helper methods
    public void addExamen(final Examen examen) {
        this.examenes.add(examen);
        examen.setCurso(this);
    }

    public void removeExamen(final Examen examen) {
        this.examenes.remove(examen);
        examen.setCurso(null);
    }

    public void addTema(final Tema tema) {
        this.temas.add(tema);
        tema.setCurso(this);
    }

    public void removeTema(final Tema tema) {
        this.temas.remove(tema);
        tema.setCurso(null);
    }

    public List<Tema> getTemasOrdenados() {

        if (temas.isEmpty()) {
            return new ArrayList<>();
        }

        final List<Tema> temasOrdenados = new ArrayList<>(temas);
        Collections.sort(temasOrdenados, (t1, t2) -> MiscBase.compare(t1.getCapitulo(), t2.getCapitulo()));
        return temasOrdenados;

    }

    @Override
    public int hashCode() {

        return Objects.hash(asignatura, codigo);
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final var that = (Curso) o;

        return Objects.equals(asignatura, that.getAsignatura()) && Objects.equals(codigo, that.getCodigo());
    }
}