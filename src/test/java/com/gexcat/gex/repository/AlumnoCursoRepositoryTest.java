package com.gexcat.gex.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.gexcat.gex.AuditorAwareImpl;
import com.gexcat.gex.cfg.GexCatTestConfiguration;
import com.gexcat.gex.jpa.entity.Alumno;
import com.gexcat.gex.jpa.entity.Asignatura;
import com.gexcat.gex.jpa.entity.Centro;
import com.gexcat.gex.jpa.entity.Curso;
import com.gexcat.gex.jpa.entity.Grupo;
import com.gexcat.gex.jpa.entity.image.AlumnoImagen;
import com.gexcat.gex.jpa.entity.image.CentroImagen;
import com.gexcat.gex.jpa.misc.UserData;
import com.gexcat.gex.type.TipoCuatrimestre;
import com.gexcat.gex.type.TipoSexo;

import lombok.extern.log4j.Log4j2;

/**
 * Test de AlumnoCursoRepository
 *
 * @author vaneMB
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = GexCatTestConfiguration.class)
@Log4j2
@DisplayName("Al ejecutar AlumnoCursoRepository")
class AlumnoCursoRepositoryTest {

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private CentroRepository centroRepository;

    @Autowired
    private AsignaturaRepository asignaturaRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Autowired
    private AlumnoCursoRepository alumnoCursoRepository;

    @BeforeAll
    void setUpBeforeClass()
        throws IOException {
        AuditorAwareImpl.USER = "jesus";

        try {
            newData();
        } catch (final DataIntegrityViolationException e) {
            log.error(">>>> " + e.getMostSpecificCause());
        }
    }

    public void newData()
        throws IOException {
        log.warn(">>>> DATOS");

        transactionTemplate.setName("org.springframework.data.jpa.repository.support.SimpleJpaRepository.newData");
        transactionTemplate.execute((TransactionCallback<Void>) transactionStatus -> {

            CentroImagen centroImagen1 = null;
            try {
                centroImagen1 = new CentroImagen("src/test/resources/imagenes/networking.jpg");
            } catch (final IOException e) {
                log.error(e.getLocalizedMessage());
            }
            final var centro1 = Centro.builder()
                .codigo("cen1")
                .nombre("Centro1")
                .imagen(centroImagen1)
                .build();
            if (centroImagen1 != null)
                centroImagen1.setCentro(centro1);

            CentroImagen centroImagen2 = null;
            try {
                centroImagen2 = new CentroImagen("src/test/resources/imagenes/networking.jpg");
            } catch (final IOException e) {
                log.error(e.getLocalizedMessage());
            }
            final var centro2 = Centro.builder()
                .codigo("cen2")
                .nombre("Centro2")
                .imagen(centroImagen2)
                .build();
            if (centroImagen2 != null)
                centroImagen2.setCentro(centro2);

            centroRepository.save(centro1);
            centroRepository.save(centro2);

            final var grupo = Grupo.builder()
                .codigo("grupo1")
                .nombre("Grupo 1 para probar")
                .build();
            grupoRepository.save(grupo);
            UserData.getInstance().setGrupo(grupo);

            final var curso1 = Curso.builder()
                .codigo("cur1")
                .cuatrimestre(TipoCuatrimestre.PRIMERO)
                .grupo(grupo)
                .build();
            final var curso2 = Curso.builder()
                .codigo("cur2")
                .cuatrimestre(TipoCuatrimestre.SEGUNDO)
                .grupo(grupo)
                .build();
            final var curso3 = Curso.builder()
                .codigo("cur3")
                .cuatrimestre(TipoCuatrimestre.ANUAL)
                .grupo(grupo)
                .build();
            final var curso4 = Curso.builder()
                .codigo("cur4")
                .cuatrimestre(TipoCuatrimestre.PRIMERO)
                .grupo(grupo)
                .build();
            final var curso5 = Curso.builder()
                .codigo("cur5")
                .cuatrimestre(TipoCuatrimestre.SEGUNDO)
                .grupo(grupo)
                .build();
            final var curso6 = Curso.builder()
                .codigo("cur6")
                .cuatrimestre(TipoCuatrimestre.ANUAL)
                .grupo(grupo)
                .build();

            cursoRepository.save(curso1);
            cursoRepository.save(curso2);
            cursoRepository.save(curso3);
            cursoRepository.save(curso4);
            cursoRepository.save(curso5);
            cursoRepository.save(curso6);

            final var asignatura1 = Asignatura.builder()
                .codigo("asi1")
                .nombre("Asignatura1")
                .centro(centro1)
                .build();
            final var asignatura2 = Asignatura.builder()
                .codigo("asi2")
                .nombre("Asignatura2")
                .centro(centro1)
                .build();
            final var asignatura3 = Asignatura.builder()
                .codigo("asi3")
                .nombre("Asignatura3")
                .centro(centro2)
                .build();
            final var asignatura4 = Asignatura.builder()
                .codigo("asi4")
                .nombre("Asignatura4")
                .centro(centro2)
                .build();

            asignatura1.addCurso(curso1);
            asignatura1.addCurso(curso2);
            asignatura2.addCurso(curso3);
            asignatura2.addCurso(curso4);
            asignatura3.addCurso(curso5);
            asignatura4.addCurso(curso6);

            asignaturaRepository.save(asignatura1);
            asignaturaRepository.save(asignatura2);
            asignaturaRepository.save(asignatura3);
            asignaturaRepository.save(asignatura4);

            AlumnoImagen alumnoImagen1 = null;
            try {
                alumnoImagen1 = new AlumnoImagen("src/test/resources/imagenes/networking.jpg");
            } catch (final IOException e) {
                log.error(e.getLocalizedMessage());
            }
            final var alumno1 = Alumno.builder()
                .apellidos("Marín Barella")
                .nombre("Vanessa")
                .correo("vane@aplsoftware.com")
                .dni("78708608D")
                .enviarCorreo(true)
                .identificador(78708608)
                .movil("628946492")
                .niu("78708608")
                .observaciones("Sin observaciones")
                .sexo(TipoSexo.MUJER)
                .build();

            AlumnoImagen alumnoImagen2 = null;
            try {
                alumnoImagen2 = new AlumnoImagen("src/test/resources/imagenes/networking.jpg");
            } catch (final IOException e) {
                log.error(e.getLocalizedMessage());
            }
            final var alumno2 = Alumno.builder()
                .apellidos("Marín Ruiz")
                .nombre("Jesús")
                .correo("jesus@aplsoftware.com")
                .dni("03797140R")
                .enviarCorreo(true)
                .identificador(3797140)
                .movil("629622881")
                .niu("03797140")
                .observaciones("Sin observaciones")
                .sexo(TipoSexo.HOMBRE)
                .build();

            AlumnoImagen alumnoImagen3 = null;
            try {
                alumnoImagen3 = new AlumnoImagen("src/test/resources/imagenes/networking.jpg");
            } catch (final IOException e) {
                log.error(e.getLocalizedMessage());
            }
            final var alumno3 = Alumno.builder()
                .apellidos("Barella Pérez")
                .nombre("Concepción N.")
                .correo("conchi@aplsoftware.com")
                .dni("42165726X")
                .enviarCorreo(true)
                .identificador(42165726)
                .movil("616942305")
                .niu("42165726")
                .observaciones("Sin observaciones")
                .sexo(TipoSexo.MUJER)
                .build();

            AlumnoImagen alumnoImagen4 = null;
            try {
                alumnoImagen4 = new AlumnoImagen("src/test/resources/imagenes/networking.jpg");
            } catch (final IOException e) {
                log.error(e.getLocalizedMessage());
            }
            final var alumno4 = Alumno.builder()
                .apellidos("García Masses")
                .nombre("Siul")
                .correo("siul@aplsoftware.com")
                .dni("78722779")
                .enviarCorreo(true)
                .identificador(78722779)
                .movil("650348666")
                .niu("78722779")
                .observaciones("Sin observaciones")
                .sexo(TipoSexo.MUJER)
                .build();

            AlumnoImagen alumnoImagen5 = null;
            try {
                alumnoImagen5 = new AlumnoImagen("src/test/resources/imagenes/networking.jpg");
            } catch (final IOException e) {
                log.error(e.getLocalizedMessage());
            }
            final var alumno5 = Alumno.builder()
                .apellidos("García Bethencourt")
                .nombre("Jonathan")
                .correo("joni@aplsoftware.com")
                .dni("43822141")
                .enviarCorreo(true)
                .identificador(43822141)
                .movil("600230251")
                .niu("43822141")
                .observaciones("Sin observaciones")
                .sexo(TipoSexo.HOMBRE)
                .build();

            AlumnoImagen alumnoImagen6 = null;
            try {
                alumnoImagen6 = new AlumnoImagen("src/test/resources/imagenes/networking.jpg");
            } catch (final IOException e) {
                log.error(e.getLocalizedMessage());
            }
            final var alumno6 = Alumno.builder()
                .apellidos("Carrillo Broeder")
                .nombre("Daniel")
                .correo("dani@aplsoftware.com")
                .dni("42217824J")
                .enviarCorreo(true)
                .identificador(42217824)
                .movil("629956593")
                .niu("42217824")
                .observaciones("Sin observaciones")
                .sexo(TipoSexo.HOMBRE)
                .build();

            AlumnoImagen alumnoImagen7 = null;
            try {
                alumnoImagen7 = new AlumnoImagen("src/test/resources/imagenes/networking.jpg");
            } catch (final IOException e) {
                log.error(e.getLocalizedMessage());
            }
            final var alumno7 = Alumno.builder()
                .apellidos("Cruz Hernández")
                .nombre("Alba")
                .correo("alba@aplsoftware.com")
                .dni("42195341")
                .enviarCorreo(true)
                .identificador(42195341)
                .movil("600204060")
                .niu("42195341R")
                .observaciones("Sin observaciones")
                .sexo(TipoSexo.MUJER)
                .build();

            AlumnoImagen alumnoImagen8 = null;
            try {
                alumnoImagen8 = new AlumnoImagen("src/test/resources/imagenes/networking.jpg");
            } catch (final IOException e) {
                log.error(e.getLocalizedMessage());
            }
            final var alumno8 = Alumno.builder()
                .apellidos("Plasencia Ramos")
                .nombre("Raúl")
                .correo("raul@aplsoftware.com")
                .dni("54059527M")
                .enviarCorreo(true)
                .identificador(54059527)
                .movil("618962508")
                .niu("54059527")
                .observaciones("Sin observaciones")
                .sexo(TipoSexo.HOMBRE)
                .build();

            alumno1.addImagen(alumnoImagen1);
            alumno2.addImagen(alumnoImagen2);
            alumno3.addImagen(alumnoImagen3);
            alumno4.addImagen(alumnoImagen4);
            alumno5.addImagen(alumnoImagen5);
            alumno6.addImagen(alumnoImagen6);
            alumno7.addImagen(alumnoImagen7);
            alumno8.addImagen(alumnoImagen8);

            alumno1.addCurso(curso1);
            alumno1.addCurso(curso3);
            alumno2.addCurso(curso1);
            alumno2.addCurso(curso4);
            alumno3.addCurso(curso2);
            alumno3.addCurso(curso3);
            alumno4.addCurso(curso2);
            alumno4.addCurso(curso4);
            alumno5.addCurso(curso5);
            alumno5.addCurso(curso6);
            alumno6.addCurso(curso5);
            alumno6.addCurso(curso6);
            alumno7.addCurso(curso5);
            alumno8.addCurso(curso5);
            alumno8.addCurso(curso6);

            alumnoRepository.save(alumno1);
            alumnoRepository.save(alumno2);
            alumnoRepository.save(alumno3);
            alumnoRepository.save(alumno4);
            alumnoRepository.save(alumno5);
            alumnoRepository.save(alumno6);
            alumnoRepository.save(alumno7);
            alumnoRepository.save(alumno8);

            return null;
        });
    }

    @DisplayName("Si se llama a findAll, debería de hallar 15 alumnos")
    @Order(5)
    @Test
    void testFindAll() {
        log.warn(">>>> FIND_ALL");

        final var alumnos = alumnoCursoRepository.findAll();
        assertNotNull(alumnos, "No existen alumnos con cursos");

        final var expected = 15;
        final var received = alumnos.size();
        assertEquals(expected, received, "El número de alumnos no es 15");
    }

    @DisplayName(
        "Si se llama a findByIdCursoAndDni con idCurso = 4 y dni = 78722779, debería de hallar cen1 -> asi2 -> cur4 -> alu4"
    )
    @Order(10)
    @Test
    final void testFindByIdCursoAndDni() {
        log.warn(">>>> FIND_BY_ID_CURSO_AND_DNI");

        final var alumnoCurso = alumnoCursoRepository.findByIdCursoAndDni(4L, "78722779").orElse(null);
        assertNotNull(alumnoCurso, "No existe alu4 en el cur4");

        assertNotNull(alumnoCurso.getCurso().getAsignatura(), "Cur1 no está en asi1");
        assertNotNull(alumnoCurso.getCurso().getAsignatura().getCentro(), "Asi1 no está en cen1");
    }

    @DisplayName("Si se llama a findAlumnosBusquedaBEM con cursoId = null, debería de hallar alu5 en 2 cursos")
    @Order(47)
    @Test
    void testFindAlumnosBusquedaBEM() {
        log.warn(">>>> FIND_ALUMNOS_BUSQUEDA_BEM");

        final var alumnoCursos = alumnoCursoRepository.findAlumnosBusquedaBEM(5L, "JON GAR", null, null, false);
        assertNotNull(alumnoCursos, "No existe alumno");

        final var expected = 1;
        final var received = alumnoCursos.size();
        assertEquals(expected, received, "El alumno no está en 2 cursos");
    }

    @DisplayName("When you call findAlumnosBusquedaBEV using cursoId = null, should find alu5 in 1 course(s)")
    @Order(48)
    @Test
    void testFindAlumnosBusquedaBEV() {
        log.warn(">>>> FIND_ALUMNOS_BUSQUEDA_BEV");

        final var alumnoCursos = alumnoCursoRepository.findAlumnosBusquedaBEV(5L, "JON GAR", "43822141", 43822141,
            false);
        assertNotNull(alumnoCursos, "Student not found");

        final var expected = 1;
        final var received = alumnoCursos.size();
        assertEquals(expected, received, "Student is not in 1 course(s)");
    }

}
