package com.gexcat.gex.jpa.entity;

import static com.gexcat.gex.cfg.Constants.DIEZ;
import static com.gexcat.gex.cfg.Constants.NOVENTA;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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

import com.gexcat.gex.jpa.entity.image.CentroImagen;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Información del centro.
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
    name = "centro",
    uniqueConstraints = { @UniqueConstraint(
        name = "cen_unq_codigo",
        columnNames = { "codigo" }
    ) },
    indexes = { @Index(
        name = "cen_idx_nombre",
        columnList = "nombre"
    ) }
)
@ToString
@org.hibernate.annotations.Table(
    appliesTo = "centro",
    comment = "Información del centro"
)
public class Centro
//    extends AbstractPropertyChange
    implements Serializable, Cloneable {

    private static final long serialVersionUID = -4397424443446100208L;

    @Column(
        updatable = false,
        nullable = false
    )
    @SequenceGenerator(
        name = "centroSequence",
        sequenceName = "cen_sequence",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "centroSequence"
    )
    @ColumnDefault(value = "(next value for cen_sequence)")
    @Setter(AccessLevel.NONE)
    @Id
    private Long id;

    // Código del centro, que normalmente se consigna con las siglas, como por
    // ejemplo ULL, UNED, etc.
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
        length = NOVENTA
    )
    @NotNull
    @Size(max = NOVENTA)
    private String nombre;

    @OneToOne(
        mappedBy = "centro",
        cascade = CascadeType.ALL,
        fetch = FetchType.LAZY,
        orphanRemoval = true
    )
    @NotAudited
    @ToString.Exclude
    private CentroImagen imagen;

    @OneToMany(
        mappedBy = "centro",
        cascade = { CascadeType.PERSIST, CascadeType.MERGE }
    )
    @NotAudited
    @ToString.Exclude
    @Default
    private final Set<Asignatura> asignaturas = new HashSet<>();

    // Helper methods
    public void addAsignatura(final Asignatura asignatura) {
        this.asignaturas.add(asignatura);
        asignatura.setCentro(this);
    }

    public void removeAsignatura(final Asignatura asignatura) {
        this.asignaturas.remove(asignatura);
        asignatura.setCentro(null);
    }

    public void addImagen(final CentroImagen ci) {

        if (this.imagen == null) {
            this.imagen = ci;
        } else {
            this.imagen.setImagen(ci.getImagen());
            this.imagen.setOriginal(ci.getOriginal());
        }

        ci.setCentro(this);
    }

    public void removeImagen(final CentroImagen ci) {
        this.imagen = null;

        if (ci != null) {
            ci.setCentro(null);
        }
    }

//    public void setCodigo(final String codigo) {
//        final String old = this.codigo;
//        this.codigo = codigo;
//        pcs.firePropertyChange("codigo", old, this.codigo);
//    }
//
//    public void setNombre(final String nombre) {
//        final String old = this.nombre;
//        this.nombre = nombre;
//        pcs.firePropertyChange("nombre", old, this.nombre);
//    }

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

        final var that = (Centro) o;

        return this.codigo != null && this.codigo.equals(that.getCodigo());
    }
}
