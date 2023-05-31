package com.gexcat.gex.type;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

import com.gexcat.gex.InfrastructureException;
import com.gexcat.gex.tool.MiscBase;

/**
 * <p>
 * Componente que le permite a Hibernate convertir los enumerados en campos de texto en la base de datos, que se realiza
 * a partir de la información extraída de la anotación {@code Type}.
 * </p>
 *
 * <p>
 * Actualmente Hibernate tiene dos métodos para guardar la información de los enumerados en la base de datos. El primero
 * es guardar un entero con el orden del parámetro en la lista que ha sido descartado porque no permite poder eliminar
 * elementos ni reordenarlos ya que el indice dentro del enum fallaría
 * </p>
 *
 * <p>
 * La otra forma sería guardar el texto del enumerado. Este método aunque está bien, no permite renombrar las constantes
 * del enum y ocupa un tamaño no definido en la base de datos mientras que con la clase EnumType el tamaño siempre va a
 * ser 1 carácter {@code CHAR(1)}
 * </p>
 *
 * <p>
 * A continuación se mostrará la forma de uso habitual. Aunque existe varias anotaciones para el campo {@code
 * permiso} del ejemplo. La única que afecta a esta clase es {@code @Type}. El resto de las anotaciones se han añadido
 * porque son parte del uso habitual
 * </p>
 *
 * <pre>
 * <code>
 *   &#64;Column(name = "PERMISO")
 *   &#64;Default(value = "'N'", groups = ByDefault.class)
 *   &#64;NotNull
 *   &#64;Type(type = EnumType.TYPE,
 *       parameters = {
 *           &#64;Parameter(name = EnumType.CLASS,
 *               value = "com.apl.base.type.Permiso")
 *       })
 *   private Permiso permiso;
 * </code>
 * </pre>
 *
 * @author JCalcines
 */
public class EnumType
    implements UserType, ParameterizedType {

    /**
     * <p>
     * Clase que se encarga de hacer la transformación de datos
     * </p>
     */
    public static final String TYPE = "com.gexcat.gex.type.EnumType"; // EnumType.class.getName();

    /**
     * <p>
     * Tipo de datos que se va a tramitar. Este parámetro puede que exista porque antiguamente había dos tipo que se
     * procesaban con esta clase: {@code Enum} y {@code Boolean}
     * </p>
     *
     * <p>
     * Puesto que actualmente solo se utiliza el tipo Enum, podría eliminarse la obligatoriedad de este parámetro
     * </p>
     *
     * <p>
     * TODO Quitar parámetro si se puede
     * </p>
     */
    public static final String CLASS = "enumClass";

    /**
     * <p>
     * Parámetro opcional para especifica cual será el método getter para convertir el valor del {@code Enum} en el
     * texto de la base de datos
     * </p>
     */
    public static final String TO_VALUE_METHOD = "to";

    /**
     * <p>
     * Parámetro opcional para especifica cual será el método setter para convertir el caracter de la base de datos en
     * una constante del {@code Enum}
     * </p>
     */

    public static final String FROM_VALUE_METHOD = "from";

    /**
     * <p>
     * Método getter por defecto en el caso de que no se especifique ninguno
     * </p>
     */
    public static final String DEFAULT_GETTER_METHOD = "toValue";

    /**
     * <p>
     * Método setter por defecto en el caso de que no se especifique ninguno
     * </p>
     */
    public static final String DEFAULT_SETTER_METHOD = "fromValue";

    /**
     * <p>
     * Tipos de dato que pueden ser devueltos o interpretados en la relación con la base de datos
     * </p>
     */
    private static final int[] SQL_TYPES = { Types.VARCHAR };

    // Clase del enum interpretado
    private Class<?> typeClass;

    // Método getter
    private Method toValue;

    // Método setter
    private Method fromValue;

    /**
     * <p>
     * Método que usa Hibernate para extraer los datos de la anotación {@code @Type} y ser capaz de devolver el valor
     * del enumerado que corresponde al valor de la base de datos.
     * </p>
     *
     * <p>
     * Por así decirlo, este método inicializa los métodos que sirven para extraer y guardar los valores en la base de
     * datos
     * </p>
     *
     * @param parameters Lista de parámetros especificados en la anotación {@code @Type} del campo a interpretar
     */
    @Override
    public void setParameterValues(final Properties parameters) {

        if (parameters.getProperty(EnumType.CLASS) == null) {
            throw new InfrastructureException(MiscBase.getBundle("type.EnumType.noParam"), new NullPointerException());
        }

        if (parameters.getProperty(EnumType.TO_VALUE_METHOD) == null) {
            parameters.setProperty(EnumType.TO_VALUE_METHOD, EnumType.DEFAULT_GETTER_METHOD);
        }

        if (parameters.getProperty(EnumType.FROM_VALUE_METHOD) == null) {
            parameters.setProperty(EnumType.FROM_VALUE_METHOD, EnumType.DEFAULT_SETTER_METHOD);
        }

        try {

            this.typeClass = Class.forName(parameters.getProperty(EnumType.CLASS)).asSubclass(Enum.class);

            this.toValue = this.typeClass.getMethod(parameters.getProperty(EnumType.TO_VALUE_METHOD));
            this.fromValue = this.typeClass.getMethod(parameters.getProperty(EnumType.FROM_VALUE_METHOD),
                this.toValue.getReturnType());

        } catch (final Exception e) {
            throw new InfrastructureException(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.hibernate.usertype.UserType#sqlTypes()
     */
    @Override
    public int[] sqlTypes() {
        return EnumType.SQL_TYPES.clone();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.hibernate.usertype.UserType#returnedClass()
     */
    @Override
    public Class<?> returnedClass() {
        return this.typeClass;
    }

    /**
     * <p>
     * Método necesario para la compatibilidad con Hibernate 3
     * </p>
     *
     * @param resultSet DOCUMENT ME!
     * @param names DOCUMENT ME!
     * @param owner DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws HibernateException DOCUMENT ME!
     * @throws SQLException DOCUMENT ME!
     */
    @Deprecated
    public Object nullSafeGet(final ResultSet resultSet, final String[] names, final Object owner)
        throws HibernateException, SQLException {

        try {
            return this.fromValue.invoke(this.typeClass, resultSet.getString(names[0]));
        } catch (final Exception e) {
            throw new HibernateException(MiscBase.getBundle("type.EnumType.invokingError", this.fromValue.getName(),
                this.typeClass.getName()), e);
        }
    }

    /**
     * <p>
     * Método necesario para la compatibilidad con Hibernate 3
     * </p>
     *
     * @param preparedStatement DOCUMENT ME!
     * @param value DOCUMENT ME!
     * @param index DOCUMENT ME!
     */
    @Deprecated
    public void nullSafeSet(final PreparedStatement preparedStatement, final Object value, final int index) {

        try {

            if (value == null) {
                preparedStatement.setNull(index, EnumType.SQL_TYPES[0]);
            } else {

                preparedStatement.setString(index, (String) this.toValue.invoke(value));

            }
        } catch (final Exception e) {
            throw new HibernateException(MiscBase.getBundle("type.EnumType.invokingError", this.toValue.getName(),
                this.typeClass.getName()), e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.hibernate.usertype.UserType#nullSafeGet(java.sql.ResultSet, java.lang.String[],
     * org.hibernate.engine.spi.SessionImplementor, java.lang.Object)
     */
    @Override
    public Object nullSafeGet(final ResultSet resultSet,
        final String[] names,
        final SharedSessionContractImplementor session,
        final Object owner)
        throws HibernateException, SQLException {

        try {
            return this.fromValue.invoke(this.typeClass, resultSet.getString(names[0]));
        } catch (final Exception e) {
            throw new HibernateException(MiscBase.getBundle("type.EnumType.invokingError", this.fromValue.getName(),
                this.typeClass.getName()), e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.hibernate.usertype.UserType#nullSafeSet(java.sql.PreparedStatement, java.lang.Object, int,
     * org.hibernate.engine.spi.SessionImplementor)
     */
    @Override
    public void nullSafeSet(final PreparedStatement preparedStatement,
        final Object value,
        final int index,
        final SharedSessionContractImplementor session) {

        try {

            if (value == null) {
                preparedStatement.setNull(index, EnumType.SQL_TYPES[0]);
            } else {

                preparedStatement.setString(index, (String) this.toValue.invoke(value));

            }
        } catch (final Exception e) {
            throw new HibernateException(MiscBase.getBundle("type.EnumType.invokingError", this.toValue.getName(),
                this.typeClass.getName()), e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.hibernate.usertype.UserType#deepCopy(java.lang.Object)
     */
    @Override
    public Object deepCopy(final Object value) {
        return value;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.hibernate.usertype.UserType#isMutable()
     */
    @Override
    public boolean isMutable() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.hibernate.usertype.UserType#assemble(java.io.Serializable, java.lang.Object)
     */
    @Override
    public Object assemble(final Serializable cached, final Object owner) {
        return cached;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.hibernate.usertype.UserType#disassemble(java.lang.Object)
     */
    @Override
    public Serializable disassemble(final Object value) {
        return (Serializable) value;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.hibernate.usertype.UserType#replace(java.lang.Object, java.lang.Object, java.lang.Object)
     */
    @Override
    public Object replace(final Object original, final Object target, final Object owner) {
        return original;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.hibernate.usertype.UserType#hashCode(java.lang.Object)
     */
    @Override
    public int hashCode(final Object x) {
        return x.hashCode();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.hibernate.usertype.UserType#equals(java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean equals(final Object x, final Object y) {
        return x == y || x != null && y != null && x.equals(y);
    }
}
