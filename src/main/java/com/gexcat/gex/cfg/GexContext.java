package com.gexcat.gex.cfg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import com.gexcat.gex.InfrastructureException;
//import com.apl.base.exception.InfrastructureException;
//import com.apl.base.exception.RestrictionException;
//import com.apl.base.persistence.AplBasicContext;
//import com.apl.base.persistence.LoginContext;
//import com.apl.base.tool.FileMisc;
//import com.apl.base.tool.ProcessListener;
//import com.apl.base.type.Stateless;
//import com.apl.components.misc.Misc;
import com.gexcat.gex.jpa.misc.GexCatMisc;

/**
 * <p>
 * Contexto de la aplicación de GexCat
 * </p>
 *
 * @author jose
 */
public final class GexContext {

    private static final GexContext INSTANCE = new GexContext();
    private static final String CONFIG = "/db.properties";
    private static Properties PROPERTIES;

    private GexContext() {
    }

    public static void init() {
//        AplBasicContext.getInstance();
    }

    /**
     * <p>
     * Llamada a esta instancia = FileMisc.getInstance().[y el método que sea]
     * </p>
     *
     * @return esta instancia
     */
    public static GexContext getInstance() {
        return INSTANCE;
    }

    /**
     * Devuelve las propiedades cargadas del fichero <b>db.properties.</b>
     *
     * @return Propiedades del fichero db.properties
     */
    public static Properties getProperties() {
        final File f;

        if (PROPERTIES == null) {

            try {
                f = new File(getInstance()
                    .getClass()
                    .getResource(CONFIG)
                    .toURI());
                PROPERTIES = new Properties();
                PROPERTIES.load(new FileInputStream(f));
            } catch (final Exception e) {
                throw new InfrastructureException(e);
            }
        }

        return PROPERTIES;
    }

    /**
     * Guarda las propiedades en el fichero <b>db.properties</b>. Se utiliza principalmente para establecer la ruta de
     * la base de datos
     */
    public static void saveProperties() {

        try {
            final var f = new File(getInstance()
                .getClass()
                .getResource(CONFIG)
                .toURI());
            PROPERTIES.store(new FileOutputStream(f), new Date().toString());
        } catch (final Exception e) {
            throw new InfrastructureException(e);
        }

    }

    /**
     * <p>
     * Realiza una copia de la base de datos simplemente copiando los ficheros. La copia será consistente siempre que la
     * base de datos no esté en ejecución en el momento de hacer la copia. Esa es la razón por la que la opción no está
     * dentro de las utilidades de GexCat
     * </p>
     *
     * @param targetPath Lugar donde se colocará la copia de la base de datos
     *
     * @throws IOException Si sucedió un error con la base de datos
     */
    public static void backup(final String targetPath)
        throws IOException {

        final String source = GexCatMisc.getUrlConnection(null);
        final var targetFile = new File(targetPath, source.substring(source.lastIndexOf(File.separator) + 1));


    }
}
