package com.gexcat.gex.jpa.misc;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gexcat.gex.cfg.GexContext;
import com.gexcat.gex.tool.FileMisc;
import com.gexcat.gex.tool.MiscBase;

import lombok.AllArgsConstructor;

public class GexCatMisc {

    /*
     * Constantes
     */
    public static final String JFD = "jfd/main/";

    public static final String JASPER = "/jasper/";

    public static final String BUNDLE = "bundle/GexCat";
    public static final String REPORT_BUNDLE = "bundle/Listados";

    public static final Color TABLE_COLOR = new Color(214, 244, 244);

    public static final String DERBY_CONNECTION = "jdbc:derby:";
    public static final String MARIADB_CONNECTION = "jdbc:mariadb://";

    public static final String FOR_BREAK = "\\n ";
    public static final String FOR_BREAK_EXPR = "\\Q\\n\\E";

    private static final String APPDATA = System.getenv("APPDATA");
    private static final String DVI_FILE = "/lib/dvisvgm.exe";
    private static final String LATEX_LOCALE = "utf8";
    private static ResourceBundle reportBundle;

    public static final String VIDEO_REGISTRO = "http://www.gexcat.com/videos.html#videoA";
    public static final String VIDEO_AUTENTICACION = "http://www.gexcat.com/videos.html#video2";
    public static final String VIDEO_IMPORTAR_EXPORTAR_PREGUNTAS = "http://www.gexcat.com/videos.html#video3";
    public static final String VIDEO_GENERAR_EXAMEN_TEST = "http://www.gexcat.com/videos.html#video4";

    public static final String CLAVE_GEXCAT = "innocan01";

    private static final String PASSWORD = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";
    // Entre 8 y 20 caracteres + dígito + letra mayúscula + letra minúscula + carácter especial

    private static final Pattern pattern = Pattern.compile(PASSWORD);

    // TODO false
    private static boolean validMiktexConfig = true;

    public static final String IDENTIFICADOR = "ID";
    public static final String CORRECCIONES = "correcciones";

    private static final String[] idiomas = {
        "ca",
        "en",
        "es",
        "gl",
        "it"
    };

    /*
     * Enum TipoProceso
     */
    public enum TipoProceso {
        TEST,
        DESARROLLO
    }

    /*
     * Enum Banda
     */
    @AllArgsConstructor
    public enum Banda {
        PREGUNTA_NORMAL(516),
        RESPUESTA_NORMAL(499),
        PREGUNTA_DOBLE(244),
        RESPUESTA_DOBLE(227);

        public final int ancho;
    }

    /*
     * Enum Formato
     */
    public enum Formato {
        CSV("csv"),
        DOCX("docx"),
        GIFT("gift", "txt"),
        TXT("txt"),
        XLS("xls"),
        XML("xml");

        public String[] extensiones;

        Formato(final String... extensiones) {
            this.extensiones = extensiones;
        }

        public String getExtension() {
            return name().toLowerCase(new Locale("es_ES"));
        }

        public String getText() {
            return FileMisc.getBundle("Formato." + name());
        }

        public static Formato[] values(final String clase) {

            switch (clase) {

                case "ImportarExportarAlumnos":
                case "AsignarIdentidades":
                    return new Formato[] { CSV, XLS };

                case "ImportarExportarPreguntas":
                    return new Formato[] { CSV, GIFT, TXT, XLS };

                default:
                    return Formato.values();
            }
        }

        public static Formato get(final String s) {

            for (final Formato p : values()) {

                if (p.getText().equals(s)) {
                    return p;
                }
            }

            return null;
        }

        public static LinkedHashMap<String, String[]> getFiltersMap(final String clase) {
            final var map = new LinkedHashMap<String, String[]>();

            for (final Formato aux : values(clase)) {
                map.put(aux.getText(), aux.extensiones);
            }

            return map;
        }
    }

    public static String getUrlConnection(String url) {

        if (UserData.getInstance().isJpa()) {
            return UserData.getInstance().getBaseDatos();
        }
        if (url == null) {
            url = GexContext.getProperties().getProperty("connection.url").substring(DERBY_CONNECTION.length());
        }

        if (url.indexOf("%APPDATA%") != -1) {
            return url.replace("%APPDATA%", APPDATA);
        }

        return url;
    }

    public static void setUrlConnection(String url) {

        if (UserData.getInstance().isJpa()) {
            final var aux = url.split(",");
            UserData.getInstance().setBaseDatos(aux[0] + ":" + aux[1] + "/" + aux[2]);
        } else {
            if (url.indexOf(APPDATA) != -1) {
                url = url.replace(APPDATA, "%APPDATA%");
            }

            GexContext.getProperties().put("connection.url", DERBY_CONNECTION + url);
            GexContext.saveProperties();
        }
    }

    private static String getMikTexPath(String miktex) {

        if (miktex == null) {
            miktex = UserData.getInstance().getDirectorioMiKTeX();
        }

        if (Files.exists(Paths.get(miktex, "/miktex/bin/x64/latex.exe"))) {
            return Paths.get(miktex, "/miktex/bin/x64").toString();
        }

        if (Files.exists(Paths.get(miktex, "/miktex/bin/latex.exe"))) {
            return Paths.get(miktex, "/miktex/bin").toString();
        }

        return null;

    }

    private static boolean isNewImageMagick(final String imageMagick) {
        return new File(imageMagick + "\\magick.exe").exists();
    }

    public static synchronized String execCommand(final List<String> lista, final File directory) {

        final var retorno = new StringBuilder();

        try {
            final var processBuilder = new ProcessBuilder(lista);
            processBuilder.directory(directory);

            final var proceso = processBuilder.start();

            final var br = new BufferedReader(new InputStreamReader(proceso.getErrorStream()));

            while (br.readLine() != null) {

            }

            br.close();

        } catch (final Exception e) {
            e.printStackTrace();
        }

        return retorno.toString();
    }

    public static boolean checkImageMagickPath(final String imageMagick) {
        return imageMagick == null ? false
            : new File(imageMagick + "\\convert.exe").exists() || new File(imageMagick + "\\magick.exe").exists();
    }

    public static boolean checkMiktexPath(final String miktex) {

        if (miktex == null) {
            return false;
        }

        final var path = getMikTexPath(miktex);
        return path == null ? false
            : Paths.get(path.toString(), "latex.exe").toFile().exists() && Paths.get(path.toString(), "dvips.exe")
                .toFile().exists();
    }

    public static boolean isValidMiktexConfig() {
        return validMiktexConfig;
    }

    public static void setValidMiktexConfig(final boolean validMiktexConfig) {
        GexCatMisc.validMiktexConfig = validMiktexConfig;
    }

    public static String chkMath(final String math, final Banda banda) {
        return chkMath(math, isValidMiktexConfig(), banda);
    }

    public static String chkMath(String math, final boolean valid, final Banda banda) {

        final String BEGIN;
        final String END;

        final var ancho = banda == null ? Banda.PREGUNTA_NORMAL.ancho : banda.ancho;

        math = math.trim().replaceAll("\\\\n[\\s]", "\\\\newline ");

        // Banda.PREGUNTA_DOBLE
        if (valid) {
            BEGIN = "\\documentclass[a4paper, 10pt]{article}"
                + "\\pagestyle{empty} "
                + "\\setlength{\\parindent}{0pt}"
                + "\\hoffset=0pt\\oddsidemargin=0pt"
                + "\\pretolerance=2000\\tolerance=3000"
                + "\\textwidth=" + ancho + "px" + MiscBase.buildParamString("\\usepackage[{0}]{inputenc}", LATEX_LOCALE)
                + "\\usepackage[spanish]{babel}"
                + "\\usepackage{amsmath, helvet}"
                + "\\renewcommand{\\familydefault}{\\sfdefault}"
                + "\\renewcommand{\\baselinestretch}{1.1}"
                + "\\begin{document} ";

            END = "\\end{document} ";

        } else {
            BEGIN = "\\begin{array}{lr}";
            END = "\\end{array}";
        }

        return BEGIN + math + END;
    }

    public static Font getFont(final boolean bold, final boolean italic, final String font, final int size) {
        var estilo = 0;

        if (bold) {
            estilo = Font.BOLD;
        }

        if (italic) {
            estilo = estilo + Font.ITALIC;
        }

        return new Font(font, estilo, size);
    }

    /**
     * <p>
     * Genera un código numérico entero a partir de un texto. Si la longitud del número es mayor de 8 cifras o el
     * resultado no es ningún número válido, devolverá un 0
     * </p>
     *
     * @param dni
     *
     * @return
     */
    public static Integer getIdentificador(final String dni) {
        var number = "";

        for (final char c : dni.toCharArray()) {

            if (Character.isDigit(c)) {
                number += c;
            }
        }

        if (number.length() <= 0) {
            return 0;
        }

        if (number.length() > 8) {
            number = number.substring(number.length() - 8);
        }

        return Integer.valueOf(number);
    }

    /**
     * mogrify -strip -colorspace sRGB exif.jpg.
     *
     * @param filename
     */
    public static void fixImage(final String filename) {

        final var newImageMagick = isNewImageMagick(UserData.getInstance().getDirectorioImageMagick());
        final var MOGRIFY = Paths.get(UserData.getInstance().getDirectorioImageMagick(),
            newImageMagick ? "/magick.exe" : "/mogrify.exe").toString();
        final var file = new File(filename);

        final List<String> command = new ArrayList<>();

        String[] comando;

        if (newImageMagick) {
            comando = new String[] {
                MOGRIFY,
                "mogrify",
                "-strip",
                "-colorspace",
                "sRGB",
                file.getAbsolutePath()
            };
        } else {
            comando = new String[] {
                MOGRIFY,
                "-strip",
                "-colorspace",
                "sRGB",
                file.getAbsolutePath()
            };
        }

        Collections.addAll(command, comando);

        execCommand(command, file.getParentFile());
    }

    public static void setReportBundle(final Locale locale) {
        GexCatMisc.reportBundle = ResourceBundle.getBundle(REPORT_BUNDLE, locale);
    }

    /**
     * <p>
     * Comprueba el número de caracteres '<' y lo compara con los caracteres '>'. Si el número es el mismo, se supone
     * que texto contiene HTML.
     * </p>
     *
     * <p>
     * En el caso de ser menor la cantidad de caracteres '<' los textos de los exámenes se visualizarán de forma
     * incorrecta. La función de este método es detectar los casos en los que sucede esto.
     * </p>
     *
     * @param texto
     *
     * @return
     */
    public static boolean comprobarTextoExamen(final String texto) {
        var value = 0;

        if (texto == null) {
            return true;
        }

        for (final char c : texto.toCharArray()) {

            if (c == '<') {
                value++;
            } else if (c == '>') {
                value--;
            }
        }

        return value <= 0;
    }

    public static String getTextoAlumno(final String apellidos, final String nombre) {
        return UserData.getInstance().getFormatoAlumno().getFormato().replace(UserData.ALU_NOMBRE, nombre)
            .replace(UserData.ALU_APELLIDOS, apellidos);
    }

    public static List<String> getIdiomas() {
        return Arrays.asList(idiomas);
    }

    public static String aumentarFuente(final String texto) {
        final var aux = texto;
        return aux.replace("<div>", "<div><font size= \"10\">")
            .replace("<div align=\"left\">", "<div align=\"left\"><font size= \"10\">")
            .replace("<div align=\"center\">", "<div align=\"center\"><font size= \"10\">")
            .replace("<div align=\"right\">", "<div align=\"right\"><font size= \"10\">")
            .replace("<div align=\"justify\">", "<div align=\"justify\"><font size= \"10\">")
            .replace("</div>", "</font></div>").replace("<p>", "<p><font size= \"10\">")
            .replace("<p align=\"left\">", "<p align=\"left\"><font size= \"10\">")
            .replace("<p align=\"center\">", "<p align=\"center\"><font size= \"10\">")
            .replace("<p align=\"right\">", "<p align=\"right\"><font size= \"10\">")
            .replace("<p align=\"justify\">", "<p align=\"justify\"><font size= \"10\">")
            .replace("</p>", "</font></p>");
    }

    public static LocalDate toLocalDate(final Date date) {
        return date == null ? null : date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static Date toDate(final LocalDate date) {
        return date == null ? null : Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static String formatTimestampToTime(final Timestamp value) {
        final var timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
        return timeFormatter.format(value.toLocalDateTime());
    }

    public static String borrarLetra(final String dni) {
        var aux = dni;

        if (Character.isLetter(aux.charAt(aux.length() - 1))) {
            aux = aux.substring(0, aux.length() - 1);
        }

        if (aux.charAt(aux.length() - 1) == '-') {
            aux = aux.substring(0, aux.length() - 1);
        }

        return aux.replaceFirst("^0+(?!$)", "");
    }

    public static boolean isClaveValida(final String clave) {
        final Matcher matcher = pattern.matcher(clave);
        return matcher.matches();
    }
}
