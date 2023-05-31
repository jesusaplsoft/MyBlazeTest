package com.gexcat.gex.jpa.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.envers.NotAudited;

import com.gexcat.gex.jpa.entity.image.CorreccionImagen;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Información extendida sobre la corrección para exámenes de tipo test.
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
@Table(name = "correccion")
@ToString
@org.hibernate.annotations.Table(
    appliesTo = "correccion",
    comment = "Información extendida sobre la corrección para exámenes de tipo test"
)
public class Correccion
    implements Serializable, Cloneable {

    private static final long serialVersionUID = -1556520611763127408L;

    @Column(
        updatable = false,
        nullable = false
    )
    @Setter(AccessLevel.NONE)
    @Id
    private Long id;

    /**
     * Matriz de respuestas consignadas con el alumno. Esta matriz será comparada contra la matriz de corrección del
     * modelo para calcular el número de aciertos.
     */
    @Column(
        name = "resultados",
        nullable = false
    )
    @Lob
    @NotNull
    private Boolean[][] resultados;

    /**
     * Número de respuestas consignadas correctamente.
     */
    @Column(
        name = "aciertos",
        nullable = false,
        precision = 3
    )
    @ColumnDefault("0")
    @Digits(
        integer = 3,
        fraction = 0
    )
    @Max(999)
    @Min(0)
    @NotNull
    @Default
    private int aciertos = 0;

    /**
     * Número de respuestas sin consignar.
     */
    @Column(
        name = "blancos",
        nullable = false,
        precision = 3
    )
    @ColumnDefault("0")
    @Digits(
        integer = 3,
        fraction = 0
    )
    @Max(999)
    @Min(0)
    @NotNull
    @Default
    private int blancos = 0;

    /**
     * Número de respuestas consignadas erróneamente.
     */
    @Column(
        name = "fallos",
        nullable = false,
        precision = 3
    )
    @ColumnDefault("0")
    @Digits(
        integer = 3,
        fraction = 0
    )
    @Max(999)
    @Min(0)
    @NotNull
    @Default
    private int fallos = 0;

    /**
     * Modelo de examen.
     */
    @Column(
        name = "modelo",
        nullable = false,
        precision = 1
    )
    @ColumnDefault("1")
    @Digits(
        integer = 1,
        fraction = 0
    )
    @Max(9)
    @Min(1)
    @NotNull
    @Default
    private int modelo = 1;

    @OneToOne(
        mappedBy = "correccion",
        cascade = CascadeType.ALL,
        fetch = FetchType.LAZY,
        orphanRemoval = true,
        optional = false
    )
    @NotAudited
    @ToString.Exclude
    private CorreccionImagen imagen;

    /**
     * Información básica sobre la corrección.
     */
    @OneToOne
    @JoinColumn(
        name = "id",
        foreignKey = @ForeignKey(
            name = "cor_fk_nota"
        )
    )
    @MapsId
    @ToString.Exclude
    private Nota nota;

//    public static Boolean[][] getRespuestas(final ExTest test,
//        final Integer[] dim) {
//        final var FILAS = dim[0];
//        final var COLUMNAS = dim[1];
//        final var respuestas = new Boolean[FILAS][COLUMNAS];
//
//        for (var f = 0; f < FILAS; f++) {
//
//            for (var c = 0; c < COLUMNAS; c++) {
//                respuestas[f][c] = test.getTest()[f][c] == 1;
//            }
//        }
//
//        return respuestas;
//    }

    public void addImagen(final CorreccionImagen ci) {

        this.imagen = ci;
        ci.setCorreccion(this);
    }

    public void removeImagen(final CorreccionImagen ci) {
        this.imagen = null;

        if (ci != null) {
            ci.setCorreccion(null);
        }
    }

    @Override
    public int hashCode() {

        if (this.id != null) {
            return (int) (this.id * 12345);
        }

        return 31;
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final var that = (Correccion) o;

        return this.id != null && this.id.equals(that.getId());
    }
}
