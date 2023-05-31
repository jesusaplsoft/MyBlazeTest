package com.gexcat.gex.tool;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.logging.log4j.core.util.datetime.FastDateFormat;

import com.gexcat.gex.InfrastructureException;

/**
 * <p>
 * Esta clase ofrece varias utilidades que son utilizadas a lo durante todos los proyectos ya que que son operaciones
 * bastante comunes
 * </p>
 */
public class MiscBase {

    /* Ruta de los ficheros de idiomas */
    public static final String BUNDLE_PATH = "bundle/AplBase";

    /* Localización geofráfica de la aplicación */
    private static Locale locale;

    /* Clase que controla el acceso a los ficheros de idiomas */
    private static ResourceBundle bundle;

    /* Formato para la fecha */
    private static String dateFormat;
    /* Formato para la hora */
    private static String timeFormat;
    /* Formato para los valores numéricos */
    protected static NumberFormat numberFormat;

    /*
     * Paginación configurada en la búsquedas. Se utiliza cuando se piden consultas con paginación. El valor 0 indica
     * que no está activa la paginación
     */
    private static int paging;

    static {
        MiscBase.setLocale(Locale.getDefault());
        MiscBase.dateFormat = "dd/MM/yyyy";
        MiscBase.timeFormat = "HH:mm";
        MiscBase.numberFormat = NumberFormat.getInstance(MiscBase.getLocale());
        MiscBase.paging = 20;
    }

    /**
     * <p>
     * Compara si dos objetos son iguales, eliminado los errores de tipo {@code NullPointerException}
     * </p>
     *
     * @param a primer objeto a comparar
     * @param b segundo objeto a comparar
     *
     * @return {@code true} si se cumple el criterio que indica que los objetos son iguales
     *
     * @see Object#equals(Object)
     */
    public static boolean equals(final Object a, final Object b) {
        return a == b || a != null && b != null && a.equals(b);
    }

    /**
     * <p>
     * Compara un elemento contra otros.
     * </p>
     *
     * @param a   elemento principal
     * @param or  <b>or = {@code true}</b> indica que devuelve {@code true} desde que encuentre uno igual y <b>or =
     *            {@code false}</b> (and) devuelve {@code true} cuando todos don iguales
     * @param cmp otros objetos a comparar.Si no se pasan mas objetos devolverá {@code true} indicando que todos son
     *            iguales
     *
     * @return si son iguales o no
     */
    public static boolean equals(final Object a, final boolean or, final Object... cmp) {

        for (final Object b : cmp) {

            if (MiscBase.equals(a, b) == or) {
                return or;
            }
        }

        return !or || cmp.length == 0;
    }

    /**
     * Compara dos valores teniendo en cuenta los valores nulos.
     *
     * @param <T> con tipo
     * @param a   con campo1 a usar
     * @param b   con campo2 a usar
     *
     * @return -1, 0 o 1 dependiendo de la comparación
     */
    public static <T extends Comparable<T>> int compare(final T a, final T b) {
        return MiscBase.compare(a, b, true);
    }

    /**
     * <p>
     * Compara dos valores teniendo en cuenta los valores nulos.
     * </p>
     *
     * @param <T>      con tipo
     * @param a        primer campo a comparar
     * @param b        segundo campo a comparar
     * @param nullLast {@code true} si se desea poner los campos con valor {@code null} al final de la lista.
     *
     * @return -1, 0 o 1 dependiendo de la comparación
     */
    public static <T extends Comparable<T>> int compare(final T a, final T b, final boolean nullLast) {

        if (a == b) {
            return 0;
        }

        if (a == null) {
            return nullLast ? 1 : -1;
        }

        if (b == null) {
            return nullLast ? -1 : 1;
        }

        return a.compareTo(b);
    }

    /**
     * <p>
     * Compara dos objetos aunque no se pueda comprobar que sean instancia de la clase comparable y aunque no sean del
     * mismo tipo.
     * </p>
     *
     * <p>
     * Si ambos valores son numéricos, se comparará el valor segun el valor numérico de ellos. En caso contrario la
     * comparación se hará según el resultado del método {@code toString}
     * </p>
     *
     * @param a primer campo a comparar
     * @param b segundo campo a comparar
     *
     * @return -1, 0 o 1 dependiendo de la comparación
     */
    public static int compareObject(final Object a, final Object b) {

        if (a == b) {
            return 0;
        }

        if (a == null) {
            return -1;
        }

        if (b == null) {
            return 1;
        }

        if (a instanceof Number && b instanceof Number) {
            final var d = ((Number) a).doubleValue() - ((Number) b).doubleValue();
            return d < 0 ? -1 : d > 0 ? 1 : 0;
        }

        return a.toString().compareTo(b.toString());
    }

    /**
     * <p>
     * Compara dos textos alfabéticamente. La comparación devolverá un resultado numérico donde el signo indicará la
     * posición del primer campo frente al segundo:
     * </p>
     *
     * <ul>
     * <li>-1 Cuando el primer elemento es anterior al segundo</li>
     * <li>1 Cuando el primer elemento es posterior al segundo</li>
     * <li>0 Cuando los textos son equivalentes</li>
     * </ul>
     *
     * <p>
     * En la comparación no se tendrán en cuenta las mayúsculas
     * </p>
     *
     * @param a primer campo a comparar
     * @param b segundo campo a comparar
     *
     * @return valor numérico que indica el orden
     *
     * @see String#compareToIgnoreCase(String)
     */
    public static int compareIgnoreCase(final String a, final String b) {

        if (a == null && b == null) {
            return 0;
        }

        if (a == null) {
            return -1;
        }

        if (b == null) {
            return 1;
        }

        if (a.equals(b)) {
            return 0;
        }

        return a.compareToIgnoreCase(b);
    }

    /**
     * <p>
     * Método que convierte la primera letra de un texto a mayúsculas
     * </p>
     *
     * @param text con texto a convertir
     *
     * @return resultado de la conversión
     */
    public static String firstToUpper(final String text) {

        if (text != null && text.length() > 1) {
            return new StringBuilder(text.substring(0, 1).toUpperCase()).append(text.substring(1)).toString();
        }

        return text;
    }

    /**
     * <p>
     * Transforma un valor cualquiera a texto mediante el siguiente criterio. El método es capaz de identificar valores
     * numéricos y fechas dando el formato adecuado en función del pais que tenga configurado
     * </p>
     *
     * <p>
     * Cualquier otro caso se tratará como texto
     * </p>
     *
     * @param value valor a formatear
     *
     * @return Texto con en formato de impresión
     */
    public static String format(final Object value) {

        if (value instanceof Number) {
            return MiscBase.numberFormat.format(value);
        }

        if (value instanceof Date) {
            return MiscBase.formatDate((Date) value);
        }

        if (value == null) {
            return "";
        }

        return value.toString();
    }

    /**
     * <p>
     * Convierte una fecha a texto con el formato establecido en el dateFormat
     * </p>
     *
     * @param date (Date) Fecha a convertir
     *
     * @return Devuelve la fecha con le formato especificado.
     */
    public static String formatDate(final Date date) {
        return MiscBase.formatDate(date, Locale.getDefault(), MiscBase.getDateFormat());
    }

    /**
     * <p>
     * Convierte una fecha a texto con un formato especifico
     * </p>
     *
     * @param date (Date) Fecha a convertir
     * @param mask (String) M�scara de la fecha, es decir, formato que tomar� la fecha seg�n los patrones de la clase
     *             <code>DateFormat</code>
     *
     * @return Devuelve la fecha con le formato especificado.
     */
    public static String formatDate(final Date date, final String mask) {
        return MiscBase.formatDate(date, Locale.getDefault(), mask);
    }

    /**
     * <p>
     * Convierte una fecha a texto con un formato especifico
     * </p>
     *
     * @param date   (Date) Fecha a convertir
     * @param locale (Locale) Ver definición de <i>locale</i>
     * @param mask   (String) Máscara de la fecha, es decir, formato que tomará la fecha según los patrones de la clase
     *               <code>DateFormat</code>
     *
     * @return Devuelve la fecha con le formato especificado.
     */
    public static String formatDate(final Date date, final Locale locale, final String mask) {

        if (date == null) {
            return null;
        }

        if (mask == null) {
            return date.toString();
        }

        return FastDateFormat.getInstance(mask, locale).format(date);
    }

    /**
     * <p>
     * Da formato a un número en función de la máscara establecida en la aplicación
     * </p>
     *
     * @param num Valor a formatear
     *
     * @return Número con formato de impresión
     */
    public static String formatNumber(final Number num) {
        return MiscBase.numberFormat.format(num);
    }

    /**
     * <p>
     * Devuelve el formato establecido para valores numéricos
     * </p>
     *
     * @return Formato para números
     */
    public static NumberFormat getNumberFormat() {
        return MiscBase.numberFormat;
    }

    /**
     * <p>
     * Establece el formato establecido para valores numéricos
     * </p>
     *
     * @param numberFormat Formato para números
     */
    public static void setNumberFormat(final NumberFormat numberFormat) {
        MiscBase.numberFormat = numberFormat;
    }

    /**
     * <p>
     * Da formato a un número con un estilo de formato específico
     * </p>
     *
     * @param mask Estilo de formato
     * @param num  valor a formatear
     *
     * @return Número con formato de impresión
     */
    public static String formatNumber(final String mask, final Number num) {

        if (num == null) {
            return null;
        }

        return new DecimalFormat(mask).format(num);
    }

    /**
     * <p>
     * Da formato a un número con un estilo básico pero estableciendo el número de decimales a visualizar
     * </p>
     *
     * @param decimals Número de dígitos decimales que se mostraran
     * @param num      valor a formatear
     *
     * @return Número con formato de impresión
     */
    public static String formatNumber(final int decimals, final Number num) {
        var mask = "##0.";

        for (var i = 0; i < decimals; i++) {
            mask += "0";
        }

        return MiscBase.formatNumber(mask, num);
    }

    /**
     * <p>
     * Devuleve un texto especificado en los ficheros de idiomas dada una clave. El texto puede contener parámetros
     * especificado por su orden entre paréntesis (p.e. {@code {0}, {1},} ...)
     * </p>
     *
     * @param texto Clave del texto a extraer
     * @param param valores de los parámetros a sustituir
     *
     * @return Texto extraido.
     */
    public static String getBundle(final String texto, final String... param) {
        return MiscBase.getBundle(MiscBase.bundle, texto, param);
    }

    /**
     * <p>
     * Devuelve un Calendar con la fecha enviada. Evita que se repita este código con frecuencia, ya que a la Clase
     * <code>Calendar</code> no se ke puede pasar una fecha
     * </p>
     *
     * @param fecha : Fecha a convertir en Calendar
     *
     * @return Fecha convertida en Calendar.
     */
    public static Calendar getCalendar(final Date fecha) {

        if (fecha == null) {
            return null;
        }

        final var c = Calendar.getInstance();
        c.setTime(fecha);

        return c;
    }

    /**
     * <p>
     * Devuelve el formato de la fecha
     * </p>
     *
     * @return formato para las fechas
     */
    public static String getDateFormat() {
        return MiscBase.dateFormat;
    }

    /**
     * <p>
     * Establece el formato de la fecha
     * </p>
     *
     * @return Locale en curso
     */
    public static Locale getLocale() {
        return MiscBase.locale;
    }

    /**
     * <p>
     * Devuelve un número con formato numérico
     * </p>
     *
     * @param numero Este parámetro puede ser un <code>String</code> o un <code>Numeric</code> <i>(Byte, Short, Integer,
     *               Long, BigInteger, Double, Float, BigDecimal)</i>
     *
     * @return número convertido
     */
    public static Number getNumeric(final Object numero) {

        if (numero instanceof String) {

            try {
                return new BigDecimal(numero.toString());
            } catch (final NumberFormatException e) {
                return null;
            }
        }

        if (numero instanceof Number) {
            return (Number) numero;
        }

        return null;
    }

    /**
     * <p>
     * Número de líneas por página
     * </p>
     *
     * @return entero con líneas
     */
    public static int getPaging() {
        return MiscBase.paging;
    }

    // -----------------------------------------------------[ Otras Utilidades
    // ]-----------------------------------------

    /**
     * <p>
     * Devuleve el formato de la hora
     * </p>
     *
     * @return Formato horario
     */
    public static String getTimeFormat() {
        return MiscBase.timeFormat;
    }

    /**
     * Devuelve la fecha posterior.
     *
     * @param fecha1 a usar
     * @param fecha2 a usar
     *
     * @return fecha mayor o fecha2 si iguales
     */
    public static Date maxDate(final Date fecha1, final Date fecha2) {
        return fecha1.getTime() > fecha2.getTime() ? fecha1 : fecha2;
    }

    /**
     * Devuelve la fecha anterior.
     *
     * @param fecha1 a usar
     * @param fecha2 a usar
     *
     * @return fecha menor o fecha2 si iguales
     */
    public static Date minDate(final Date fecha1, final Date fecha2) {
        return fecha1.getTime() < fecha2.getTime() ? fecha1 : fecha2;
    }

    /**
     * <p>
     * Devuelve la fecha del día posterior
     * </p>
     *
     * @param date a usar
     *
     * @return fecha hallada
     */
    public static Date nextDay(final Date date) {
        return MiscBase.nextDay(date, false);
    }

    /**
     * Método que devuelve la fecha del día posterior con la hora inicial o la fecha especificada con la hora de
     * finalización del día.
     *
     * @param date    fecha a utilizar
     * @param endTime indica si se devolverá la fecha con la hora de finalización del día
     *
     * @return fecha modificada
     */
    public static Date nextDay(final Date date, final boolean endTime) {
        final var calendar = MiscBase.getCalendar(date);
        calendar.set(Calendar.HOUR_OF_DAY, endTime ? 23 : 0);
        calendar.set(Calendar.MINUTE, endTime ? 59 : 0);
        calendar.set(Calendar.SECOND, endTime ? 59 : 0);
        calendar.set(Calendar.MILLISECOND, endTime ? 999 : 0);

        if (!endTime) {
            calendar.add(Calendar.DATE, 1);
        }

        return calendar.getTime();
    }

    /**
     * Método que devuelve la fecha especificada con la hora de inicio del día.
     *
     * @param date fecha a utilizar
     *
     * @return fecha modificada
     */
    public static Date startDate(final Date date) {
        final var c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * <p>
     * Devuelve la letra del DNI
     * </p>
     *
     * @param number a usar
     *
     * @return letra del dni
     */
    public static Character getDniLetter(final String number) {

        try {
            return "TRWAGMYFPDXBNJZSQVHLCKE".charAt(Integer.valueOf(number) % 23);
        } catch (final Exception e) {
            return null;
        }
    }

    /**
     * Validar email.
     *
     * @param correo a validar
     *
     * @return true si es bueno o false si no
     */
    public static boolean isEmail(final String correo) {

        if (correo != null) {
            return Pattern.compile(

// "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]"
// + "+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$").matcher(correo).find();
        }

        return false;
    }

    /**
     * Comprueba si la Dirección MAC es correcta.
     *
     * @param mac a comprobar
     *
     * @return si es correcta o no
     */
    public static boolean isMAC(final String mac) {

        // Mezcla de separadores
        if (mac.contains(":") && mac.contains("-") || mac.endsWith(":") && mac.endsWith("-")) {
            return false;
        }

        final var pattern = Pattern.compile("^([0-9A-F]{2}([:-]|$)){6}$|([0-9A-F]{4}([.]|$)){3}$");
        final var matcher = pattern.matcher(mac);

        if (matcher.matches()) {
            return true;
        }

        return false;
    }

    /**
     * Convert the specified object into a Date. Si luego se quiere formatear a su Locale, pasarla por formatDate.
     *
     * @param value  the value to convert
     * @param format the DateFormat pattern to parse String values
     *
     * @return the converted value
     *
     * @throws ClassCastException thrown if the value cannot be converted to a Calendar
     */
    public static Date parseDate(final Object value, final String format)
        throws ClassCastException {

        if (value instanceof Date) {
            return (Date) value;
        }

        if (value instanceof Calendar) {
            return ((Calendar) value).getTime();
        }

        if (!(value instanceof String)) {
            throw new ClassCastException("The value " + value + " can't be converted to a Date");
        }

        try {
            return new SimpleDateFormat(format).parse((String) value);
        } catch (final ParseException e) {
            throw new ClassCastException("The value " + value + " can't be converted to a Date");
        }
    }

    /**
     * <p>
     * Pasa un <code>String</code> con un número a <code>Integer</code>
     * </p>
     *
     * @param value a analizar
     *
     * @return entero resultante
     */
    public static Integer parseInteger(final String value) {

        if (value == null || value.isEmpty()) {
            return null;
        }

        try {
            return MiscBase.numberFormat.parse(value).intValue();
        } catch (final ParseException e) {
            throw new InfrastructureException(e.getLocalizedMessage(), e);
        }
    }

    /**
     * <p>
     * Comprueba si un texto se corresponda a un valor numérico formateado
     * </p>
     *
     * @param value Texto a comprobar
     *
     * @return {@code true} si el texto se corresponde con un valor numérico
     */
    public static boolean isNumeric(final String value) {

        if (value == null || value.isEmpty()) {
            return false;
        }

        try {
            MiscBase.numberFormat.parse(value).intValue();
            return true;
        } catch (final Exception e) {
            return false;
        }
    }

    /**
     * <p>
     * Devuelve la fecha del día anterior
     * </p>
     *
     * @param fecha a usar
     *
     * @return fecha hallada
     */
    public static Date previousDay(final Date fecha) {
        final var date = MiscBase.getCalendar(fecha);
        date.add(Calendar.DATE, -1);

        return date.getTime();
    }

    /**
     * Establece el formato de la fecha.
     *
     * @param dateFormat a poner
     */
    public static void setDateFormat(final String dateFormat) {
        MiscBase.dateFormat = dateFormat;
    }

    /**
     * Establece el idioma y la región y cambiamos el fichero de idiomas.
     *
     * @param loc : Localización geográfica
     */
    public static void setLocale(final Locale loc) {
        MiscBase.locale = loc;
        MiscBase.bundle = ResourceBundle.getBundle(MiscBase.BUNDLE_PATH, loc);
        MiscBase.numberFormat = NumberFormat.getInstance(loc);

    }

    public static void setPaging(final int paging) {
        MiscBase.paging = paging;
    }

    /**
     * Formato de la hora.
     *
     * @param timeFormat a poner
     */
    public static void setTimeFormat(final String timeFormat) {
        MiscBase.timeFormat = timeFormat;
    }

    /**
     * Devuelve un texto del fichero de idiomas.
     *
     * @param bundle con fihero de properties
     * @param texto  con clave a buscar
     * @param param  parámetros que se usan en la clave del texto
     *
     * @return cadena compuesta
     */
    protected static final String getBundle(final ResourceBundle bundle, final String texto, final String... param) {

        if (bundle == null || !bundle.containsKey(texto)) {
            return texto;
        }

        var msg = bundle.getString(texto).replace("\\\\n", "{n}");
        msg = msg.replace("\\n", "\n").replace("{n}", "\\n");

        for (Integer i = 0; i < param.length; i++) {
            msg = msg.replace("{i}".replace("i", i.toString()), param[i]);
        }

        return msg;
    } // end method getBundle

    /**
     * Obtener la clase de un parámetro.
     *
     * @param <E>              con Entidad
     * @param var              a revisar
     * @param parametizedClass clase a usar
     * @param arg              con número del parámetro
     *
     * @return clase encontrada
     */
    @SuppressWarnings("unchecked")
    public static <E> Class<E> getParameterClass(final E var, final Class<?> parametizedClass, final int arg) {

        Class<?> c = parametizedClass;

        while (c != null) {

            if (c.getGenericSuperclass() instanceof ParameterizedType) {
                return (Class<E>) ((ParameterizedType) c.getGenericSuperclass()).getActualTypeArguments()[arg];
            }

            c = c.getSuperclass();

        }

        return null;
    }

    public static byte[] image2Array(final Image img, final String type)
        throws IOException {
        return image2Array(img, type, -1, -1);
    }

    /**
     * <p>
     * Descompone una imagen en un vector de bytes
     * </p>
     *
     * @param img  Imagen a descomponer
     * @param type tipo de formato de la imagen
     *
     * @return arreglo resultante
     *
     * @throws IOException por error
     */
    public static byte[] image2Array(final Image img, final String type, final int width, final int height)
        throws IOException {
        final var bas = new ByteArrayOutputStream();
        final BufferedImage bu;
        final int bit;

        if ("jpg".equals(type.toLowerCase()) || "jpeg".equals(type.toLowerCase())) {
            bit = BufferedImage.TYPE_INT_RGB;
        } else {
            bit = BufferedImage.TYPE_INT_ARGB;
        }

        // Inicializa la imagen
        if (width == -1 && height == -1) {
            bu = new BufferedImage(img.getWidth(null), img.getHeight(null), bit);
            bu.createGraphics().drawImage(img, 0, 0, null);
        } else {
            bu = new BufferedImage(width, height, bit);
            bu.createGraphics().drawImage(img, 0, 0, width, height, null);
        }

        if (!ImageIO.write(bu, type, bas)) {
            ImageIO.write(bu, "png", bas);

            if (bas.size() == 0) {
                throw new InfrastructureException("This format is not allowed");
            }
        }

        return bas.toByteArray();
    }

    /**
     * <p>
     * Dado las dimensiones de un objeto (de una imagen normalmente) y estableciendo las dimensiones de un marco donde
     * tiene que caber este objeto, se calculan las dimesiones que tendrá que tener el objeto para que quepa dentro del
     * marco sin que el objeto sufra ninguna deformación. Si el tamaño del objeto es inferior al tamaño del marco, el
     * objeto no se verá modificado por lo que las dimensiones devueltas serán las originales
     * </p>
     *
     * @param rec       Tamaño del objeto a redimensionar
     * @param maxWidth  Ancho del marco
     * @param maxHeight Alto del marco
     *
     * @return Nuevas dimensiones para el objeto.
     */
    public static Rectangle scaleDimension(final Rectangle rec, final int maxWidth, final int maxHeight) {
        final double factor;

        if (rec.width > maxWidth && rec.height > maxHeight) {

            if (maxWidth / (rec.width * 1.) < maxHeight / (rec.height * 1.)) {
                factor = maxWidth / (rec.width * 1.);
            } else {
                factor = maxHeight / (rec.height * 1.);
            }
        } else if (rec.width > maxWidth) {
            factor = maxWidth / (rec.width * 1.);
        } else if (rec.height > maxHeight) {
            factor = maxHeight / (rec.height * 1.);
        } else {
            factor = 1.;
        }

        return new Rectangle(Double.valueOf(rec.width * factor).intValue(),
            Double.valueOf(rec.height * factor).intValue());
    }

    /**
     * <p>
     * Compone una imagen desde una vector de bytes
     * </p>
     *
     * @param img Vector de bytes
     *
     * @return icono con imagen
     */
    public static ImageIcon array2Image(final byte[] img) {
        return new ImageIcon(img);
    }

    /**
     * <p>
     * Compone un texto a partir de un conjunto de valores. Nótese que si se desea separar los valores ha de añadirse
     * entre los parámetros los espacios
     * </p>
     *
     * @param param Conjunto de valores que compondrán el texto
     *
     * @return texto compuesto
     */
    public static String buildString(final Object... param) {
        final var s = new StringBuilder();

        for (final Object element : param) {
            s.append(element);
        }

        return s.toString();
    }

    /**
     * <p>
     * Compone una cadena de texto con parámetros. Los parámetros serán establecidos por su orden de aparición
     * estableciendo este valor entre llaves. (p.e. {@code {0}, {1},} ...)
     * </p>
     *
     * @param base  Texto base
     * @param param valores de los parámetros que serán sustituidos
     *
     * @return Texto compuesto
     */
    public static String buildParamString(final String base, final Object... param) {

        var msg = base;

        for (Integer i = 0; i < param.length; i++) {

            if (param[i] == null) {
                msg = msg.replace("{i}".replace("i", i.toString()), "null");
            } else {
                msg = msg.replace("{i}".replace("i", i.toString()), param[i].toString());
            }
        }

        return msg;
    }

    /**
     * Método utilizado para que dado una cantidad de registros por páginas y un número de regitros devuelva el número
     * de páginas que dará lugar.
     *
     * @param registros Número de líneas que devuelve las cosultas.Es de tipo String porque en algunas consultas
     *                  funciona bien como <code>int</code> y en otras devuelve un <code>long</code>
     * @param regXpag   Número de registros que caben en una página.
     *
     * @return Número de páginas que puede devolver listado
     */
    public static int calculaPaginas(final Number registros, final Integer regXpag) {
        final var numRegs = registros.intValue();

        int retorno;

        if (regXpag == 0) {
            retorno = 1;
        } else if (numRegs % regXpag == 0) {
            retorno = numRegs / regXpag;
        } else {
            retorno = 1 + numRegs / regXpag;
        }

        return retorno;
    }

}
