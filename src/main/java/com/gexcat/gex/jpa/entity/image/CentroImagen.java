package com.gexcat.gex.jpa.entity.image;

import java.io.IOException;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.swing.ImageIcon;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.envers.NotAudited;

import com.gexcat.gex.jpa.entity.Centro;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Imágenes de centros.
 *
 * @author vaneMB
 */
//@Audited
@DynamicInsert
@DynamicUpdate
@Entity
@Getter
@OptimisticLocking(type = OptimisticLockType.DIRTY)
@Setter
@Table(name = "cen_imagen")
@ToString(callSuper = true)
@org.hibernate.annotations.Table(
    appliesTo = "cen_imagen",
    comment = "Imágenes de centros"
)
public class CentroImagen
    extends AbstractImagen
    implements Serializable, Cloneable {

    private static final long serialVersionUID = 791425992196808673L;

    @Column(
        updatable = false,
        nullable = false
    )
    @Setter(AccessLevel.NONE)
    @Id
    private Long id;

    @OneToOne
    @JoinColumn(
        name = "id",
        foreignKey = @ForeignKey(
            name = "ceni_fk_centro"
        )
    )
    @MapsId
    @ToString.Exclude
    @NotAudited
    private Centro centro;

    public CentroImagen() {
    }

    public CentroImagen(final ImageIcon imagen) {
        super(imagen);
    }

    public CentroImagen(final ImageIcon imagen, final ImageIcon original) {
        super(imagen, original);
    }

    public CentroImagen(final byte[] imagen) {
        super(imagen);
    }

    public CentroImagen(final byte[] imagen, final byte[] original) {
        super(imagen, original);
    }

    public CentroImagen(final String imagenPath)
        throws IOException {
        super(imagenPath, imagenPath);
    }

    public CentroImagen(final String imagenPath, final String originalPath)
        throws IOException {
        super(imagenPath, originalPath);
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

        final var other = (CentroImagen) o;

        return this.id != null && this.id.equals(other.getId());
    }
}
