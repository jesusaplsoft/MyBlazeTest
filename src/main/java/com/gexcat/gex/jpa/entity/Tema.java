package com.gexcat.gex.jpa.entity;

import static com.gexcat.gex.cfg.Constants.CIEN;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;

import com.gexcat.gex.tool.MiscBase;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Temas de la asignatura.
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
    name = "tema",
    indexes = {
        @Index(
            name = "tem_idx_curso",
            columnList = "curso_id"
        )
    }
)
@ToString
@org.hibernate.annotations.Table(
    appliesTo = "tema",
    comment = "Temas de la asignatura"
)
public class Tema
    implements Serializable, Cloneable {

    private static final long serialVersionUID = -1556520611763127408L;

    @Transient
    public static final Comparator<Tema> COMPARATOR;

    static {
        COMPARATOR = new TemaComparator();
    }

    @Column(
        updatable = false,
        nullable = false
    )
    @SequenceGenerator(
        name = "temaSequence",
        sequenceName = "tem_sequence",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "temaSequence"
    )
    @ColumnDefault(value = "(next value for tem_sequence)")
    @Setter(AccessLevel.NONE)
    @Id
    private Long id;

    @Column(
        name = "capitulo",
        nullable = false,
        precision = 3
    )
    @ColumnDefault("1")
    @Digits(
        integer = 3,
        fraction = 0
    )
    @Max(999)
    @Min(1)
    @NotNull
    @Default
    private int capitulo = 1;

    @Column(
        name = "titulo",
        nullable = false,
        length = CIEN
    )
    @NotNull
    @Size(max = CIEN)
    private String titulo;

    // Relación inversa
    @ManyToOne(
        fetch = FetchType.LAZY
    )
    @JoinColumn(
        name = "curso_id",
        referencedColumnName = "id",
        nullable = false,
        foreignKey = @ForeignKey(
            name = "tem_fk_curso"
        )
    )
    @NotNull
    private Curso curso;

    public static class TemaComparator
        implements Comparator<Tema>, Serializable {

        private static final long serialVersionUID = 3091442067010725669L;

        @Override
        public int compare(final Tema t1, final Tema t2) {
            int aux;

            if ((aux = MiscBase.compare(t1.getCurso().getAsignatura().getCentro().getCodigo(), t2.getCurso()
                .getAsignatura().getCentro().getCodigo())) != 0) {
                return aux;
            }
            if (((aux = MiscBase.compare(t1.getCurso().getAsignatura().getCodigo(), t2.getCurso().getAsignatura()
                .getCodigo())) != 0) || ((aux = MiscBase.compare(t1.getCurso().getCodigo(), t2.getCurso().getCodigo())) != 0)) {
                return aux;
            } else {
                return MiscBase.compare(t1.getCapitulo(), t2.getCapitulo());
            }
        }
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, curso, capitulo);
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final var that = (Tema) o;

        return Objects.equals(
            id, that.getId())
            && Objects.equals(curso, that.getCurso())
            && Objects.equals(capitulo, that.getCapitulo());
    }
}
