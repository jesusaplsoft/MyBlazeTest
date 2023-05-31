package com.gexcat.gex;

/**
 * <p>
 * Este es tipo de excepción es bastante común en la aplicacicación y se lanza cuando se quiere lanzar un error que bajo
 * circunstancias normales no debe ocurrir y siempre que se da implica un error en la programación que debe ser
 * corregido o controlado de alguna otra forma
 * </p>
 *
 * <p>
 * Puesto que este error no debería darse. Por esa razón se ha decidido que este error debe extender del tipo {@code
 * RuntimeException} para que no sea necesario rodear al método con un bloque {@code try-catch}
 * </p>
 *
 * @author JCalcines
 */
public class InfrastructureException
    extends RuntimeException {

    private static final long serialVersionUID = 6641341254107239872L;

    /**
     * <p>
     * Crea una instancia de {@code InfrastructureException}
     * </p>
     *
     * @param message Mensaje de error
     */
    public InfrastructureException(final String message) {
        super(message);
    }

    /**
     * <p>
     * Crea una instancia de {@code InfrastructureException}
     * </p>
     *
     * @param cause Origen y tipo del error sucedido
     */
    public InfrastructureException(final Throwable cause) {
        super(cause.getLocalizedMessage(), cause.getCause());
    }

    /**
     * <p>
     * Crea una instancia de {@code InfrastructureException}
     * </p>
     *
     * @param message Mensaje de error
     * @param cause Origen y tipo del error sucedido
     */
    public InfrastructureException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
