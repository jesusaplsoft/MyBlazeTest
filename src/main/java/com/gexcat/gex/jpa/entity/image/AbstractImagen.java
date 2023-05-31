package com.gexcat.gex.jpa.entity.image;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.swing.ImageIcon;

import com.gexcat.gex.InfrastructureException;
import com.gexcat.gex.tool.ImageUtilities;
import com.gexcat.gex.tool.MiscBase;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Imágenes de la aplicación.
 *
 * @author vaneMB
 */
@NoArgsConstructor
@Getter
@MappedSuperclass
@Setter
@ToString(callSuper = true)
public abstract class AbstractImagen
    implements Serializable, Cloneable {

    private static final long serialVersionUID = 855052768561469236L;

    @Column
    @Lob
    @ToString.Exclude
    private byte[] imagen;

    @Column
    @Lob
    @ToString.Exclude
    private byte[] original;

    public AbstractImagen(final byte[] imagen) {
        this.imagen = null;
        this.original = imagen;
    }

    public AbstractImagen(final byte[] imagen, final byte[] original) {
        this.original = original;
        this.imagen = Arrays.equals(imagen, original) ? null : imagen;
    }

    public AbstractImagen(final ImageIcon imagen) {
        this.imagen = null;
        setOriginalIcon(imagen);
    }

    public AbstractImagen(final ImageIcon imagen, final ImageIcon original) {
        setOriginalIcon(original);
        if (imagen == original) {
            this.imagen = null;
        } else {
            setImagenIcon(imagen);
        }
    }

    public AbstractImagen(final String filePath)
        throws IOException {

        if (filePath == null) {
            throw new IOException("Trayectoria a imagen está vacía");
        }
        setOriginalIcon(ImageUtilities.getImageIcon(filePath));
        this.imagen = null;
    }

    public AbstractImagen(final String filePath, final String originalFilePath)
        throws IOException {

        if (filePath != null) {
            setImagenIcon(ImageUtilities.getImageIcon(filePath), filePath);
        }

        if (originalFilePath != null) {
            setOriginalIcon(ImageUtilities.getImageIcon(originalFilePath), originalFilePath);
        }
        if (filePath == originalFilePath) {
            this.imagen = null;
        }
    }

    public byte[] getImagen() {

        if (this.imagen == null) {
            return this.getOriginal();
        }

        return this.imagen;
    }

    public void setImagen(final byte[] imagen) {
        this.imagen = Arrays.equals(imagen, original) ? null : imagen;
    }

    public ImageIcon getImagenIcon() {

        if (this.imagen == null) {
            return this.getOriginalIcon();
        }

        return new ImageIcon(this.imagen);
    }

    public final void setImagenIcon(final ImageIcon imagen, final String tipo) {
        imagen.setDescription(tipo);
        setImagenIcon(imagen);
    }

    public final void setImagenIcon(final ImageIcon imagen) {
        final String descripcion;

        if (imagen == null) {
            this.imagen = null;
        } else {
            descripcion = imagen.getDescription();

            /*
             * Si existe descripción entonces la imagen es nueva, si no la tiene, es la misma imagen que estaba guardada
             * previamente
             */
            if (descripcion != null) {

                try {
                    this.imagen = MiscBase.image2Array(imagen.getImage(),
                        descripcion.substring(descripcion.lastIndexOf(".") + 1));
                } catch (final IOException e) {
                    this.imagen = null;
                    throw new InfrastructureException(e);
                }
            }
        }
    }

    public ImageIcon getOriginalIcon() {

        if (this.original == null) {
            return null;
        }

        return new ImageIcon(this.original);
    }

    public final void setOriginalIcon(final ImageIcon imagen, final String tipo) {
        imagen.setDescription(tipo);
        setOriginalIcon(imagen);
    }

    public final void setOriginalIcon(final ImageIcon imagen) {

        if (imagen == null) {
            this.original = null;
        } else {

            try {
                this.original = MiscBase.image2Array(imagen.getImage(), "");
            } catch (final IOException e) {
                this.original = null;
                throw new InfrastructureException(e);
            }
        }
    }

    public void guardarCopia() {

        if (this.imagen == null) {
            this.original = null;
        } else {
            this.original = Arrays.copyOf(this.imagen, this.imagen.length);
        }
    }

    public void restaurar() {

        if (this.original == null) {
            this.imagen = null;
        } else {
            this.imagen = Arrays.copyOf(this.original, this.original.length);
        }
    }
}
