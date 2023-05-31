package com.gexcat.gex.jpa.misc;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

//import com.apl.components.form.AbstractAplDialog;
//import com.apl.components.misc.Misc;
import com.gexcat.gex.AuditorAwareImpl;
//import com.gexcat.gex.exam.ProcessExam.FormatoPlantilla;
import com.gexcat.gex.jpa.entity.Grupo;
import com.gexcat.gex.jpa.entity.Usuario;
import com.gexcat.gex.tool.FileMisc;
import com.gexcat.gex.tool.MiscBase;
//import com.gexcat.gex.type.TipoIdiomaHoja;

import lombok.AllArgsConstructor;

public final class UserData {

    private static final UserData INSTANCE = new UserData();

    public static final String ALU_NOMBRE = "#n;";
    public static final String ALU_APELLIDOS = "#a;";

    public static final int DEFAULT_IMAGE_WIDTH = 617;
    public static final int DEFAULT_IMAGE_HEIGHT = 872;

    public static final int UMBRAL_SUPERIOR = 60;
    public static final int UMBRAL_INFERIOR = 4;
    public static final int NIVEL_PERMISIVIDAD = 4;
    public static final int UMBRAL_MARCAJE = 60;

    @AllArgsConstructor
    public enum FormatoAlumno {
        NOMBRE_APELLIDOS(ALU_NOMBRE + " " + ALU_APELLIDOS),
        APELLIDOS_NOMBRE(ALU_APELLIDOS + ", " + ALU_NOMBRE);

        private final String formato;

        public String getFormato() {
            return formato;
        }

        public String getText() {
            return FileMisc.getBundle("FormatoAlumno." + name());
        }
    }

    @AllArgsConstructor
    public enum TipoLicencia {
        BASE("b"),
        ENCUESTAS("e"),
        FORMULARIOS("f"),
        FORMULARIOS_AVANZADOS("x"),
        CORRECCION_CENTRALIZADA("c"),
        EXAMENES_CICLO("n");

        private final String tipo;

        public boolean isValido() {

            if (UserData.getInstance().getCorreo().endsWith(
                "@aplsoftware.com")) {
                return true;
            }
            return UserData.getInstance().getTipoLicencia().contains(tipo);
        }

        public static TipoLicencia get(final String s) {

            for (final TipoLicencia licencia : values()) {

                if (licencia.tipo.equals(s)) {
                    return licencia;
                }
            }

            return null;
        }
    }

    public enum TipoConexion {
        SSL_TLS,
        STARTTLS,
        OAUTH2,
        NINGUNO
    }

    private boolean correctorOrtografico;
    private boolean validarDni;
    private boolean reconocimientoClasico;
    private boolean latex;
    private boolean negrita;
    private boolean dobleColumna;
    private boolean instruccionesAparte;
    private boolean exportarRespuesta;
    private boolean corregirSinAlumno;
    private boolean correoFiable;
    private boolean acuseRecibo;
    private boolean mostrarLeyenda;
    private boolean jpa;
    private boolean esquema;

    private boolean modo = false;

    private FormatoAlumno formatoAlumno;

    private Integer ancho;
    private Integer alto;
    private Integer umbralSuperior;
    private Integer umbralInferior;
    private Integer nivelPermisividad;
    private Integer umbralMarcaje;
    private Integer resolucion;
    private Integer decimalesNotas;

    private Integer maximoAlumnosCurso;

    private Locale locale;

    private String idiomaDiccionario;
    private String profesor;
    private String directorioImageMagick;
    private String directorioMiKTeX;
    private String identificador;
    private String instrucciones;
    private String imagen;
    private String servidor;
    private String puerto;
    private String servidorApoderado;
    private String puertoApoderado;
    private String usuarioCorreo;
    private String clave;
    private String remitente;
    private String asunto;
    private String cuerpo;

    private String correo;
    private String version = "";

    private String tipoLicencia;

    private String baseDatos;

    private TipoConexion tipoConexion;

    private Usuario usuario;

    private Grupo grupo;

    private final Map<Long, Integer> limiteAlumnos = new HashMap<>();

    /**
     * <p>
     * Llamada a esta instancia = UserData.getInstance().[y el método que sea]
     * </p>
     *
     * @return esta instancia
     */
    public static UserData getInstance() {
        return INSTANCE;
    }

    public static String getDirectorio() {
//        return FileMisc.getDefaultUserFolder();
        return null;
    }

    public static void setDirectorio(final String directorio) {
//        FileMisc.setDefaultUserFolder(directorio);
    }

    public boolean isCorrectorOrtografico() {
        return correctorOrtografico;
    }

    public void setCorrectorOrtografico(final boolean correctorOrtografico) {
        this.correctorOrtografico = correctorOrtografico;
//        AbstractAplDialog.setCorrector(correctorOrtografico);
    }

    public boolean isValidarDni() {
        return validarDni;
    }

    public void setValidarDni(final boolean validarDni) {
        this.validarDni = validarDni;
    }

    public boolean isReconocimientoClasico() {
        return reconocimientoClasico;
    }

    public String getReconocimientoClasico() {
        return reconocimientoClasico ? "0" : "1";
    }

    public void setReconocimientoClasico(final boolean reconocimientoClasico) {
        this.reconocimientoClasico = reconocimientoClasico;
    }

    public boolean isLatex() {
        return latex;
    }

    public void setLatex(final boolean latex) {
        this.latex = latex;
    }

    public boolean isNegrita() {
        return negrita;
    }

    public void setNegrita(final boolean negrita) {
        this.negrita = negrita;
    }

    public boolean isDobleColumna() {
        return dobleColumna;
    }

    public void setDobleColumna(final boolean dobleColumna) {
        this.dobleColumna = dobleColumna;
    }

    public boolean isInstruccionesAparte() {
        return instruccionesAparte;
    }

    public void setInstruccionesAparte(final boolean instruccionesAparte) {
        this.instruccionesAparte = instruccionesAparte;
    }

    public boolean isExportarRespuesta() {
        return exportarRespuesta;
    }

    public void setExportarRespuesta(final boolean exportarRespuesta) {
        this.exportarRespuesta = exportarRespuesta;
    }

    public boolean isCorregirSinAlumno() {
        return corregirSinAlumno;
    }

    public void setCorregirSinAlumno(final boolean corregirSinAlumno) {
        this.corregirSinAlumno = corregirSinAlumno;
    }

    public TipoConexion getTipoConexion() {
        return tipoConexion;
    }

    public void setTipoConexion(final TipoConexion tipoConexion) {
        this.tipoConexion = tipoConexion;
    }

    public boolean isCorreoFiable() {
        return correoFiable;
    }

    public void setCorreoFiable(final boolean correoFiable) {
        this.correoFiable = correoFiable;
    }

    public boolean isAcuseRecibo() {
        return acuseRecibo;
    }

    public void setAcuseRecibo(final boolean acuseRecibo) {
        this.acuseRecibo = acuseRecibo;
    }

    public boolean isMostrarLeyenda() {
        return mostrarLeyenda;
    }

    public void setMostrarLeyenda(final boolean mostrarLeyenda) {
        this.mostrarLeyenda = mostrarLeyenda;
    }

    public boolean isModoEvaluacion() {
        return modo;
    }

    public void setModoEvaluacion() {
        modo = true;
    }

    public FormatoAlumno getFormatoAlumno() {
        return formatoAlumno;
    }

    public void setFormatoAlumno(final FormatoAlumno formatoAlumno) {
        this.formatoAlumno = formatoAlumno;
    }

    public Integer getAncho() {
        return ancho;
    }

    public void setAncho(final Integer ancho) {
        this.ancho = ancho;
    }

    public Integer getAlto() {
        return alto;
    }

    public void setAlto(final Integer alto) {
        this.alto = alto;
    }

    public Integer getUmbralSuperior() {
        return umbralSuperior;
    }

    public void setUmbralSuperior(final Integer umbralSuperior) {
        this.umbralSuperior = umbralSuperior;
    }

    public Integer getUmbralInferior() {
        return umbralInferior;
    }

    public void setUmbralInferior(final Integer umbralInferior) {
        this.umbralInferior = umbralInferior;
    }

    public Integer getNivelPermisividad() {
        return nivelPermisividad;
    }

    public void setNivelPermisividad(final Integer nivelPermisividad) {
        this.nivelPermisividad = nivelPermisividad;
    }

    public Integer getUmbralMarcaje() {
        return umbralMarcaje;
    }

    public void setUmbralMarcaje(final Integer umbralMarcaje) {
        this.umbralMarcaje = umbralMarcaje;
    }

    public Integer getResolucion() {
        return resolucion;
    }

    public void setResolucion(final Integer resolucion) {
        this.resolucion = resolucion;
    }

    public Integer getDecimalesNotas() {
        return decimalesNotas;
    }

    public void setDecimalesNotas(final Integer decimalesNotas) {
        this.decimalesNotas = decimalesNotas;
    }

    public Integer getMaximoAlumnosCurso() {
        return maximoAlumnosCurso;
    }

    public void setMaximoAlumnosCurso(final Integer maximoAlumnosCurso) {
        this.maximoAlumnosCurso = maximoAlumnosCurso;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(final Locale locale) {
        final var first = this.locale == null;

        if (locale == null) {
            this.locale = Locale.ENGLISH;
        } else {
            this.locale = locale;
        }

        if (first) {
            MiscBase.setLocale(this.locale);
//            FileMisc.setLocale(this.locale);
        }
    }

    public String getIdiomaDiccionario() {
        return idiomaDiccionario;
    }

    public void setIdiomaDiccionario(final String idiomaDiccionario) {
        this.idiomaDiccionario = idiomaDiccionario;
    }

    public String getProfesor() {
        return profesor;
    }

    public void setProfesor(final String profesor) {
        this.profesor = profesor;
    }

    public String getDirectorioImageMagick() {
        return directorioImageMagick;
    }

    public void setDirectorioImageMagick(final String directorioImageMagick) {
        this.directorioImageMagick = directorioImageMagick == null || directorioImageMagick.length() == 0 ? null
            : directorioImageMagick;
    }

    public String getDirectorioMiKTeX() {
        return directorioMiKTeX;
    }

    public void setDirectorioMiKTeX(final String directorioMiKTeX) {
        this.directorioMiKTeX = directorioMiKTeX == null || directorioMiKTeX.length() == 0 ? null
            : directorioMiKTeX;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(final String identificador) {
        this.identificador = identificador;
    }

    public String getInstrucciones() {
        return instrucciones;
    }

    public void setInstrucciones(final String instrucciones) {
        this.instrucciones = instrucciones;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(final String imagen) {
        this.imagen = imagen;
    }

    public String getServidor() {
        return servidor;
    }

    public void setServidor(final String servidor) {
        this.servidor = servidor;
    }

    public String getPuerto() {
        return puerto;
    }

    public void setPuerto(final String puerto) {
        this.puerto = puerto;
    }

    public String getServidorApoderado() {
        return servidorApoderado;
    }

    public void setServidorApoderado(final String servidorApoderado) {
        this.servidorApoderado = servidorApoderado;
    }

    public String getPuertoApoderado() {
        return puertoApoderado;
    }

    public void setPuertoApoderado(final String puertoApoderado) {
        this.puertoApoderado = puertoApoderado;
    }

    public String getUsuarioCorreo() {
        return usuarioCorreo;
    }

    public void setUsuarioCorreo(final String usuarioCorreo) {
        this.usuarioCorreo = usuarioCorreo;
    }

    public String getClave() {

        if (clave != null && clave.length() == 0) {
            return null;
        }

        return clave;
    }

    public void setClave(final String clave) {
        this.clave = clave;
    }

    public String getRemitente() {
        return remitente;
    }

    public void setRemitente(final String remitente) {
        this.remitente = remitente;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(final String asunto) {
        this.asunto = asunto;
    }

    public String getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(final String cuerpo) {
        this.cuerpo = cuerpo;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(final String correo) {
        this.correo = correo;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public String getTipoLicencia() {
        return tipoLicencia;
    }

    public void setTipoLicencia(final String tipoLicencia) {
        this.tipoLicencia = tipoLicencia;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public boolean isAdminOrCoordinador() {
        return usuario.isAdmin() || usuario.isCoordinador();
    }

    public void setUsuario(final Usuario usuario) {
        this.usuario = usuario;
        AuditorAwareImpl.USER = usuario.getCodigo();
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(final Grupo grupo) {
        this.grupo = grupo;
    }

    public boolean isJpa() {
        return jpa;
    }

    public void setJpa(final boolean jpa) {
        this.jpa = jpa;
    }

    public boolean isEsquema() {
        return esquema;
    }

    public void setEsquema(final boolean esquema) {
        this.esquema = esquema;
    }

    public String getBaseDatos() {
        return baseDatos;
    }

    public void setBaseDatos(final String baseDatos) {
        this.baseDatos = baseDatos;
    }

    public Integer getLimiteAlumnos(final Long cursoId) {
        final var aux = limiteAlumnos.get(cursoId);

        if (aux != null) {
            return aux;
        }
        setLimiteAlumnos(cursoId, getMaximoAlumnosCurso());
        return getMaximoAlumnosCurso();
    }

    public void setLimiteAlumnos(final Long cursoId, final Integer maximo) {
        limiteAlumnos.put(cursoId, maximo);
    }

    public String getFormatoFecha() {
        return MiscBase.getDateFormat();
    }

    public void setFormatoFecha(final String dateFormat) {
        MiscBase.setDateFormat(dateFormat);
    }
}
