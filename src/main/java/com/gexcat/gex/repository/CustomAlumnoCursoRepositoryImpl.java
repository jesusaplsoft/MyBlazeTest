package com.gexcat.gex.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.WhereOrBuilder;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.gexcat.gex.jpa.entity.Alumno;
import com.gexcat.gex.jpa.entity.AlumnoCurso;
import com.gexcat.gex.jpa.misc.GexCatMisc.Formato;
import com.gexcat.gex.jpa.misc.UserData;
import com.gexcat.gex.repository.dto.AlumnoCursoExportarDto;
import com.gexcat.gex.repository.dto.AlumnoCursoFindDto;
import com.gexcat.gex.repository.dto.AlumnoCursoFindRecord;
import com.gexcat.gex.repository.view.AlumnoCursoFindView;
import com.gexcat.gex.type.AlumnoField;

/**
 * CustomAlumnoCursoRepositoryImpl.
 *
 * @author vaneMB
 */
public class CustomAlumnoCursoRepositoryImpl implements CustomAlumnoCursoRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private CriteriaBuilderFactory cbf;

    @Autowired
    private EntityViewManager evm;

    /**
     * Buscar un alumno dependiendo de los filtros que se apliquen.
     *
     * @param cursoId       con id del curso
     * @param nombre        con/sin apellidos del alumno
     * @param dni           con/sin letra del alumno
     * @param identificador del alumno
     * @param ignoreCase    si true, se ignoran mayúsculas y minúsculas
     *
     * @return List<AlumnoCursoFindDto>
     */
    @Override
    public List<AlumnoCursoFindRecord> findAlumnosBusquedaBEM(
        final Long idCurso,
        final String nombre,
        final String dni,
        final Integer identificador,
        final boolean isCaseSensitive) {

        final CriteriaBuilder<Tuple> query = cbf.create(entityManager, Tuple.class)
            .from(AlumnoCurso.class, "ac")
            .select("a.dni", "dni")
            .select("a.identificador", "identificador")
            .select("a.nombre", "nombre")
            .select("a.apellidos", "apellidos")
            .select("cac.codigo", "codigoCentro")
            .select("ca.codigo", "codigoAsignatura")
            .select("c.id", "idCurso")
            .select("c.codigo", "codigoCurso")
            .innerJoinDefault("alumno", "a")
            .innerJoinDefault("curso", "c")
            .innerJoinDefault("curso.asignatura", "ca")
            .innerJoinDefault("curso.asignatura.centro", "cac")
            .innerJoin("curso.grupo", "g");

        if (idCurso == null) {
            query.where("g.id").eq(UserData.getInstance().getGrupo().getId());
        } else {
            query.where("c.id").eq(idCurso);
        }

        if (dni != null) {
            query.where("a.dni").like().expression("'" + dni + "%'").noEscape();
        }

        if (identificador != null) {
            query.where("a.identificador").eq(identificador);
        }

        if (nombre != null) {

            final WhereOrBuilder<CriteriaBuilder<Tuple>> orBuilder = query.whereOr();

            for (final String aux : nombre.split(" ")) {

                if (aux.length() == 0) {
                    continue;
                }

                final String pValue = "'%" + aux + "%'";

                orBuilder.where("a.nombre").like(isCaseSensitive).expression(pValue).noEscape()
                    .where("a.apellidos").like(isCaseSensitive).expression(pValue).noEscape();
            }
            orBuilder.endOr();

        }

        query.orderByAsc("a.apellidos")
            .orderByAsc("a.nombre")
            .orderByAsc("cac.codigo")
            .orderByAsc("ca.codigo")
            .orderByAsc("c.codigo");

        final List<AlumnoCursoFindRecord> l = new ArrayList<>();
        final List<Tuple> ts = query.getResultList();
        int i = 0;
        if (!ts.isEmpty())
            for (final Tuple t : ts) {
                l.add(i++, new AlumnoCursoFindRecord(
                    t.get("dni", String.class),
                    t.get("identificador", Integer.class),
                    t.get("nombre", String.class),
                    t.get("apellidos", String.class),
                    t.get("codigoCentro", String.class),
                    t.get("codigoAsignatura", String.class),
                    t.get("idCurso", Long.class),
                    t.get("codigoCurso", String.class)));
            }
        return l;

    }

    /**
     * Buscar un alumno dependiendo de los filtros que se apliquen.
     *
     * @param cursoId       con id del curso
     * @param nombre        con/sin apellidos del alumno
     * @param dni           con/sin letra del alumno
     * @param identificador del alumno
     * @param ignoreCase    si true, se ignoran mayúsculas y minúsculas
     *
     * @return List<AlumnoCursoFindDto>
     */
    @Override
    public List<AlumnoCursoFindView> findAlumnosBusquedaBEV(
        final Long idCurso,
        final String nombre,
        final String dni,
        final Integer identificador,
        final boolean isCaseSensitive) {

        final var query = cbf.create(entityManager, AlumnoCurso.class)
            .from(AlumnoCurso.class, "ac")
            .innerJoinDefault("alumno", "a")
            .innerJoinDefault("curso", "c")
            .innerJoinDefault("curso.asignatura", "ca")
            .innerJoinDefault("curso.asignatura.centro", "cac")
            .innerJoin("curso.grupo", "cg");

        if (idCurso == null) {
            query.where("cg.id").eq(UserData.getInstance().getGrupo().getId());
        } else {
            query.where("c.id").eq(idCurso);
        }

        if (dni != null) {
            query.where("a.dni").like().expression("'" + dni + "%'").noEscape();
        }

        if (identificador != null) {
            query.where("a.identificador").eq(identificador);
        }

        if (nombre != null) {

            final var orBuilder = query.whereOr();

            for (final var aux : nombre.split(" ")) {

                if (aux.length() == 0) {
                    continue;
                }

                final var pValue = "'%" + aux + "%'";

                orBuilder.where("a.nombre").like(isCaseSensitive).expression(pValue).noEscape()
                    .where("a.apellidos").like(isCaseSensitive).expression(pValue).noEscape();
            }
            orBuilder.endOr();

        }

        query.orderByAsc("a.apellidos")
            .orderByAsc("a.nombre")
            .orderByAsc("cac.codigo")
            .orderByAsc("ca.codigo")
            .orderByAsc("c.codigo");

        final var acBuilder = evm.applySetting(EntityViewSetting.create(
            AlumnoCursoFindView.class), query);
        return acBuilder.getResultList();

    }

    /**
     * Buscar el primer alumno del curso.
     *
     * @param idCurso
     *
     * @return Optional<AlumnoCurso>
     */
    @Override
    public Optional<AlumnoCurso> findFirst(final Long idCurso) {
        return entityManager.createQuery("""
                      SELECT u
                        FROM AlumnoCurso u
            INNER JOIN FETCH u.alumno l
            INNER JOIN FETCH u.curso k
            INNER JOIN FETCH k.asignatura a
            INNER JOIN FETCH a.centro c
                       WHERE k.id = :idCurso
                    ORDER BY l.apellidos, l.nombre
            """, AlumnoCurso.class)
            .setParameter("idCurso", idCurso)
            .getResultStream()
            .findFirst();
    }

    /**
     * Buscar el anterior alumno del curso a partir del actual.
     *
     * @param idCurso
     * @param alumno  actual
     *
     * @return Optional<AlumnoCurso>
     */
    @Override
    public Optional<AlumnoCurso> findPrevious(final Long idCurso, final Alumno alumno) {
        return entityManager.createQuery("""
                      SELECT u
                        FROM AlumnoCurso u
            INNER JOIN FETCH u.alumno l
            INNER JOIN FETCH u.curso k
            INNER JOIN FETCH k.asignatura a
            INNER JOIN FETCH a.centro c
                       WHERE k.id = :idCurso
                         AND (l.apellidos < :apellidos OR (l.apellidos = :apellidos AND l.nombre < :nombre))
               ORDER BY l.apellidos DESC, l.nombre DESC
            """, AlumnoCurso.class)
            .setParameter("idCurso", idCurso)
            .setParameter("apellidos", alumno.getApellidos())
            .setParameter("nombre", alumno.getNombre())
            .getResultStream()
            .findFirst();
    }

    /**
     * Buscar el siguiente alumno del curso a partir del actual.
     *
     * @param idCurso
     * @param alumno  actual
     *
     * @return Optional<AlumnoCurso>
     */
    @Override
    public Optional<AlumnoCurso> findNext(final Long idCurso, final Alumno alumno) {
        return entityManager.createQuery("""
                      SELECT u
                        FROM AlumnoCurso u
            INNER JOIN FETCH u.alumno l
            INNER JOIN FETCH u.curso k
            INNER JOIN FETCH k.asignatura a
            INNER JOIN FETCH a.centro c
                       WHERE k.id = :idCurso
                         AND (l.apellidos > :apellidos OR (l.apellidos = :apellidos AND l.nombre > :nombre))
               ORDER BY l.apellidos, l.nombre
            """, AlumnoCurso.class)
            .setParameter("idCurso", idCurso)
            .setParameter("apellidos", alumno.getApellidos())
            .setParameter("nombre", alumno.getNombre())
            .getResultStream()
            .findFirst();
    }

    /**
     * Buscar el último alumno del curso.
     *
     * @param idCurso
     *
     * @return Optional<AlumnoCurso>
     */
    @Override
    public Optional<AlumnoCurso> findLast(final Long idCurso) {
        return entityManager.createQuery("""
                      SELECT u
                        FROM AlumnoCurso u
            INNER JOIN FETCH u.alumno l
            INNER JOIN FETCH u.curso k
            INNER JOIN FETCH k.asignatura a
            INNER JOIN FETCH a.centro c
                       WHERE k.id = :idCurso
               ORDER BY l.apellidos DESC, l.nombre DESC
            """, AlumnoCurso.class)
            .setParameter("idCurso", idCurso)
            .getResultStream()
            .findFirst();
    }

    /**
     * Preparar los datos de los alumnos que se van a exportar.
     *
     * @param lista    con los alumnos que se quieren exportar
     * @param cabecera con la información que se quiere exportar
     *
     * @return List<String[]>
     */
    private List<String[]> preparar(final List<AlumnoCursoExportarDto> lista, final List<String> cabecera) {
        final List<String[]> datosExportacion = new ArrayList<>();
        final List<String> linea = new ArrayList<>();

        for (final AlumnoCursoExportarDto fv : lista) {
            linea.add(fv.getDni());

            if (cabecera.contains(AlumnoField.IDENTIFICADOR.getTexto())) {
                linea.add(String.valueOf(fv.getIdentificador()));
            }

            if (cabecera.contains(AlumnoField.NIU.getTexto())) {
                linea.add(fv.getNiu() == null ? "" : fv.getNiu());
            }

            if (cabecera.contains(AlumnoField.NOMBRE.getTexto())) {
                linea.add(fv.getNombre());
            }

            if (cabecera.contains(AlumnoField.APELLIDOS.getTexto())) {
                linea.add(fv.getApellidos());
            }

            if (cabecera.contains(AlumnoField.APELLIDO_1.getTexto())) {
                linea.add(fv.getApellido1());
            }

            if (cabecera.contains(AlumnoField.APELLIDO_2.getTexto())) {
                linea.add(fv.getApellido2());
            }

            if (cabecera.contains(AlumnoField.NOMBRE_COMPLETO.getTexto())) {
                linea.add(fv.getNombreCompleto());
            }

            if (cabecera.contains(AlumnoField.CORREO.getTexto())) {
                linea.add(fv.getCorreo());
            }

            if (cabecera.contains(AlumnoField.SEXO.getTexto())) {
                linea.add(fv.getSexo().getTipo());
            }

            if (cabecera.contains(AlumnoField.MOVIL.getTexto())) {
                linea.add(fv.getMovil());
            }

            if (cabecera.contains(AlumnoField.OBSERVACIONES_ALUMNO.getTexto())) {
                linea.add(fv.getObservaciones());
            }

            datosExportacion.add(linea.toArray(new String[0]));
            linea.clear();
        }

        return datosExportacion;
    }

    /**
     * Comprobar si un alumno existe en algún curso.
     *
     * @param idAlumno
     *
     * @return boolean
     */
    @Override
    public long countByIdAlumno(final Long idAlumno) {
        return entityManager.createQuery("""
            SELECT COUNT(u)
                    FROM AlumnoCurso u
                   WHERE u.alumno.id = :idAlumno
                """, Long.class)
            .setParameter("idAlumno", idAlumno)
            .getSingleResult();
    }

    /**
     * Borrar un alumno de un curso.
     *
     * @param alumnoCurso
     * @param borrar      con borrar = false, borra el alumno del curso pasado y con borrar = true, borra el alumno de
     *                    cualquier curso
     *
     * @return void
     */
    @Override
    @Transactional
    public void delete(final AlumnoCurso alumnoCurso, final boolean borrar) {
        final var ac = findByIdCursoAndIdAlumno(alumnoCurso.getCurso().getId(), alumnoCurso.getAlumno().getId())
            .orElse(null);
        ac.getNotas().forEach(n -> {
            entityManager.remove(n.getCorreccion());
            entityManager.remove(n);
        });

        entityManager.remove(ac);

        if (borrar) {
            entityManager.remove(ac.getAlumno());
        }

        UserData.getInstance().setLimiteAlumnos(alumnoCurso.getCurso().getId(), UserData.getInstance().getLimiteAlumnos(
            alumnoCurso.getCurso().getId()) + 1);
    }

    /**
     * Buscar un alumno por dni en un curso.
     *
     * @param idCurso
     * @param dni     del alumno
     *
     * @return AlumnoCurso
     */
    @Override
    public Optional<AlumnoCurso> findByIdCursoAndDni(final Long idCurso, final String dni) {
        return entityManager.createQuery("""
                      SELECT u
                        FROM AlumnoCurso u
            INNER JOIN FETCH u.alumno l
            INNER JOIN FETCH u.curso k
            INNER JOIN FETCH k.asignatura a
            INNER JOIN FETCH a.centro c
            LEFT JOIN FETCH l.imagen i
            LEFT JOIN FETCH c.imagen m
                       WHERE l.dni = :dni
                         AND k.id = :idCurso
                """, AlumnoCurso.class)
            .setParameter("idCurso", idCurso)
            .setParameter("dni", dni)
            .getResultStream()
            .findFirst();
    }

    /**
     * Buscar un alumno por id en un curso.
     *
     * @param idCurso
     * @param idAlumno
     *
     * @return AlumnoCurso
     */
    @Override
    public Optional<AlumnoCurso> findByIdCursoAndIdAlumno(final Long idCurso, final Long idAlumno) {
        return entityManager.createQuery("""
                      SELECT u
                        FROM AlumnoCurso u
            INNER JOIN FETCH u.alumno l
            INNER JOIN FETCH u.curso k
            INNER JOIN FETCH k.asignatura a
            INNER JOIN FETCH a.centro c
            LEFT JOIN FETCH l.imagen i
            LEFT JOIN FETCH c.imagen m
                      WHERE k.id = :idCurso
                        AND l.id = :idAlumno
                """, AlumnoCurso.class)
            .setParameter("idCurso", idCurso)
            .setParameter("idAlumno", idAlumno)
            .getResultStream()
            .findFirst();
    }

    /**
     * Buscar un alumno por identificador de un curso.
     *
     * @param idCurso
     * @param identificador del alumno
     *
     * @return AlumnoCurso
     */
    @Override
    public Optional<AlumnoCurso> findByIdCursoAndIdentificador(final Long idCurso, final Integer identificador) {
        return entityManager.createQuery("""
                      SELECT u
                        FROM AlumnoCurso u
            INNER JOIN FETCH u.curso k
            INNER JOIN FETCH u.alumno l
                       WHERE k.id = :idCurso
                         AND l.identificador = :identificador
                            """, AlumnoCurso.class)
            .setParameter("idCurso", idCurso)
            .setParameter("identificador", identificador)
            .getResultStream()
            .findFirst();
    }

    /**
     * Guardar un alumno en un curso.
     *
     * @param alumnoCurso
     *
     * @return AlumnoCurso
     */
    @Override
    @Transactional
    public AlumnoCurso guardarAlumno(final AlumnoCurso alumnoCurso) {
        final int alumnos = UserData.getInstance().getLimiteAlumnos(alumnoCurso.getCurso().getId());
        final var alu = alumnoCurso.getAlumno();

        if (alu.getId() == null) {
            final var curso = cursoRepository.findById(alumnoCurso.getCurso().getId()).orElse(null);
            alu.addCurso(curso);
            entityManager.persist(alu);
            entityManager.flush();
        } else {
            entityManager.merge(alumnoCurso.getAlumno());
            entityManager.merge(alumnoCurso);
        }

        if (alumnos < 0) {
            ;
//            throw new PermissionException(Misc.getBundle("Alumnos.msg.limite"));
        }

        UserData.getInstance().setLimiteAlumnos(alumnoCurso.getCurso().getId(), alumnos - 1);
        return findByIdCursoAndDni(alumnoCurso.getCurso().getId(), alumnoCurso.getAlumno().getDni()).orElse(null);
    }

    /**
     * Formatear dni.
     *
     * @param dni
     *
     * @return String
     */
    private String formatearDni(final String dni) {

        if (dni == null) {
            return null;
        }

        if ("0".equals(dni)) {
            return dni;
        }

        final var aux = dni.replaceAll("[^\\d]", "");
        return aux.length() == 0 ? dni : String.format("%08d", Long.parseLong(aux));
    }

    @Override
    public boolean existsByIdAlumnoAndIdCursoAndIdentificador(final Long idAlumno, final Long idCurso,
        final Integer identificador) {
        return entityManager.createQuery("""
                SELECT COUNT(u)
                  FROM AlumnoCurso u
            INNER JOIN u.alumno l
                 WHERE l.id <> :idAlumno
                   AND l.identificador = :identificador
                   AND u.curso.id = :idCurso
                """, Long.class)
            .setParameter("idAlumno", idAlumno)
            .setParameter("identificador", identificador)
            .setParameter("idCurso", idCurso)
            .getResultList().get(0) > 0;
    }

    @Override
    public List<AlumnoCursoFindDto> findAlumnosBusqueda(Long cursoId, String nombre, String dni, Integer identificador,
        boolean mayusculas) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteAlumnos(Long idCurso) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean guardarIdentidad(Object[] datos, int numNotas) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Object[] importarIdentidades(String fichero, Formato formato, char separador) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AlumnoCurso guardarNoAsignado(AlumnoCurso alumnoCurso, Long idNota, Integer identificador) {
        // TODO Auto-generated method stub
        return null;
    }

}
