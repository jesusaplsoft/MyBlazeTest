package com.gexcat.gex.tool;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.swing.ImageIcon;

/**
 * Métodos de utilidad para procesado de imágenes.
 *
 * @author <a href="mailto:ralf@terdic.de">Ralf Terdic</a>
 */
public class ImageUtilities {

    private static String IMG_BASE;

    /**
     * Establece la ruta base de las imágenes.
     *
     * @return ruta
     */
    public static String getImgBase() {
        return IMG_BASE;
    }

    /**
     * Devuelve la ruta base se las imágenes.
     *
     * @param imgBase
     */
    public static void setImgBase(final String imgBase) {
        ImageUtilities.IMG_BASE = imgBase;
    }

    /**
     * Devuelve el formato de una imagen (como cadena).
     *
     * @param stream flujo fuente de la imagen
     *
     * @return flujo formateado (p.ej. "JPEG")
     *
     * @throws IOException por error de E/S
     */
    public static String getFormat(final InputStream stream) throws IOException {
        final var iis = ImageIO.createImageInputStream(stream);
        final var iter = ImageIO.getImageReaders(iis);

        if (!iter.hasNext()) {
            throw new IOException("¡Formato de imagen no soportado!");
        }

        final var reader = iter.next();
        iis.close();
        return reader.getFormatName();
    }

    /**
     * Carga una imagen desde un flujo.
     *
     * @param stream flujo de entrada
     *
     * @return la imagen cargada
     *
     * @throws IOException por error de E/S
     */
    public static BufferedImage loadImage(final InputStream stream) throws IOException {
        return ImageIO.read(stream);
    }

    /**
     * Graba una imagen en un flujo de salida como fichero JPEG.
     *
     * @param image imagen a grabar
     * @param stream flujo a usar
     *
     * @throws IOException por error de E/S
     */
    public static void saveImageAsJPEG(final BufferedImage image, final OutputStream stream) throws IOException {
        ImageIO.write(image, "jpg", stream);
    }

    /**
     * Graba una imagen en un flujo de salida como fichero JPEG.
     *
     * <p>
     * La calidad del JPEG se puede especificar como porcentaje.
     * </p>
     *
     * @param image imagen a grabar
     * @param stream flujo a usar
     * @param qualityPercent porcentaje de la calidad de JPEG
     *
     * @throws IOException por error de E/S
     * @throws IllegalArgumentException si qualityPercent no está entre 0 y 100
     */
    public static void saveImageAsJPEG(final BufferedImage image, final OutputStream stream, final int qualityPercent)
        throws IOException {

        if (qualityPercent < 0 || qualityPercent > 100) {
            throw new IllegalArgumentException("¡Calidad fuera de límites!");
        }

        final var quality = qualityPercent / 100f;
        ImageWriter writer = null;
        final var iter = ImageIO.getImageWritersByFormatName("jpg");

        if (iter.hasNext()) {
            writer = iter.next();

            final var ios = ImageIO.createImageOutputStream(stream);
            writer.setOutput(ios);

            final ImageWriteParam iwparam = new JPEGImageWriteParam(Locale.getDefault());
            iwparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            iwparam.setCompressionQuality(quality);
            writer.write(null, new IIOImage(image, null, null), iwparam);
            ios.flush();
            writer.dispose();
            ios.close();
        }
    }

    /**
     * Graba una imagen en un flujo de salida como fichero PNG.
     *
     * @param image imagen a grabar
     * @param stream flujo de salida
     *
     * @throws IOException por error de E/S
     */
    public static void saveImageAsPNG(final BufferedImage image, final OutputStream stream) throws IOException {
        ImageIO.write(image, "png", stream);
    }

//    private static BufferedImage convertToARGB(final BufferedImage srcImage) {
//        final var newImage = new BufferedImage(srcImage.getWidth(null), srcImage.getHeight(null),
//                BufferedImage.TYPE_INT_ARGB);
//        final var bg = newImage.getGraphics();
//        bg.drawImage(srcImage, 0, 0, null);
//        bg.dispose();
//        return newImage;
//    }

    /**
     * Devuelve ImageIcon desde ruta pasada.
     *
     * @param filename con ruta de la imagen
     *
     * @return ImageIcon leída
     *
     * @throws IOException por error de E/S
     */
    public static ImageIcon getImageIcon(final String fileName) throws IOException {
        return new ImageIcon(getImage(fileName));
    }

    /**
     * Devuelve Image desde ruta pasada.
     *
     * @param filename con ruta de la imagen
     *
     * @return Image leída
     *
     * @throws IOException por error de E/S
     */
    public static Image getImage(final String fileName) throws IOException {
        return ImageIO.read(new File(fileName));

    }

    public static BufferedImage getBufferedImage(final String fileName) throws IOException {
        return ImageIO.read(new File(fileName));

    }

    public static byte[] getByteArrayFromImageFilename(final String fileName) throws IOException {
        final var bi = ImageIO.read(new File(fileName));
        final var baos = new ByteArrayOutputStream();
        ImageIO.write(bi, "jpg", baos);
        return baos.toByteArray();

    }

    public static boolean IsValidImage(final byte[] bytes) {
        try {
            final InputStream is = new ByteArrayInputStream(bytes);
            ImageIO.read(is);
        } catch (final IOException ioe) {
            System.out.println("IOException : " + ioe);
            return false;
        }
        return true;
    }
}
