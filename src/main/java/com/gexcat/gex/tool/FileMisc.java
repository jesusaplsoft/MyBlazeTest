package com.gexcat.gex.tool;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * <p>
 * Clave con utilidades varias relacionadas con ficheros o acceso a base de datos
 * </p>
 */
public final class FileMisc {
    
    private static ResourceBundle projectBundle;
    private static ResourceBundle auxiliarBundle;

    private FileMisc() {
    }

    public static List<String> scanResources(final String path, final String ext, final boolean absolutePath)
        throws URISyntaxException {
        String aux;

        if (absolutePath) {
            return FileMisc.scanFolder(new File(new FileMisc().getClass().getResource(path).toURI()), ext);
        }

        final List<String> paths = new ArrayList<>();

        for (final String str : FileMisc.scanFolder(new File(new FileMisc().getClass().getResource(path).toURI()),
            ext)) {
            aux = new File(str).toURI().getPath();
            paths.add(aux.substring(aux.lastIndexOf(path)));
        }

        return paths;
    }

    @Deprecated
    private static List<String> scanFolder(final File folder, final String ext) {
        final List<String> list = new ArrayList<>();

        if (folder != null) {
            final var entries = folder.list();

            if (entries != null) {

                for (final String s : entries) {
                    final var currentFile = new File(folder.getPath(), s);

                    if (currentFile.isDirectory()) {
                        list.addAll(FileMisc.scanFolder(currentFile, ext));
                    } else if (currentFile.getName().endsWith(ext)) {
                        list.add(currentFile.getAbsolutePath());
                    }
                }
            }
        }

        return list;

    }
    public static String getBundle(final String texto, final String... param) {
        final var bundle = auxiliarBundle == null ? projectBundle : auxiliarBundle;

        if (bundle.containsKey(texto)) {
            return getBundle(bundle, texto, param);
        }
        if (auxiliarBundle != null && projectBundle.containsKey(texto)) {
            return getBundle(projectBundle, texto, param);
        }

        return texto;
    }

    /**
     * Devuelve un texto del fichero de idiomas.
     *
     * @param bundle con fichero de properties
     * @param texto  con clave a buscar
     * @param param  parámetros que se usan en la clave del texto
     *
     * @return cadena compuesta
     */
    public static String getBundle(final ResourceBundle bundle, final String texto, final String... param) {

        if (bundle == null) {
            return texto;
        }

        var msg = bundle.getString(texto).replace("\\\\n", "{n}");
        msg = msg.replace("\\n", "\n").replace("{n}", "\\n");

        for (Integer i = 0; i < param.length; i++) {
            msg = msg.replace("{i}".replace("i", i.toString()), param[i]);
        }

        return msg;
    }
}
