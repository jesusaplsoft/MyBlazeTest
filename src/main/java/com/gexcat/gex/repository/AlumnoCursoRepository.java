package com.gexcat.gex.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gexcat.gex.jpa.entity.AlumnoCurso;
import com.gexcat.gex.jpa.entity.AlumnoCursoId;
import com.gexcat.gex.jpa.misc.GexCatMisc;
import com.gexcat.gex.repository.dto.AlumnoCursoDto;
import com.gexcat.gex.repository.dto.AlumnoCursoExportarDto;
import com.gexcat.gex.repository.dto.AlumnoTreeNodeDto;
import com.gexcat.gex.repository.dto.NotaDto;

/**
 * AlumnoCursoRepository.
 *
 * @author jesús + vaneMB
 */
@Repository
@Transactional(readOnly = true)
public interface AlumnoCursoRepository extends JpaRepository<AlumnoCurso, AlumnoCursoId>, CustomAlumnoCursoRepository {

    default Optional<AlumnoCurso> findAlumno(final AlumnoCurso alumnoCurso) {

        if (alumnoCurso.getAlumno().getId() != null) {
            return findByIdCursoAndIdAlumno(alumnoCurso.getCurso().getId(), alumnoCurso.getAlumno().getId());
        } else if (alumnoCurso.getAlumno().getIdentificador() == null) {
            final AlumnoCurso aux = findByIdCursoAndIdentificador(alumnoCurso.getCurso().getId(), GexCatMisc
                .getIdentificador(alumnoCurso.getAlumno().getDni())).orElse(null);
            return aux == null ? Optional.empty()
                : findByIdCursoAndIdAlumno(aux.getCurso().getId(), aux.getAlumno().getId());
        } else {
            return findByIdCursoAndIdentificador(alumnoCurso.getCurso().getId(), alumnoCurso.getAlumno()
                .getIdentificador());
        }
    }

    /**
     * Buscar un alumno por dni de una asignatura.
     *
     * @param idAsignatura
     * @param dni          del alumno
     *
     * @return AlumnoCurso
     */
    @Query("""
                  SELECT u
                    FROM AlumnoCurso u
        INNER JOIN FETCH u.alumno l
        INNER JOIN FETCH u.curso k
        INNER JOIN FETCH k.asignatura a
        INNER JOIN FETCH a.centro c
                   WHERE l.dni = :dni
                     AND a.id = :idAsignatura
                """)
    Optional<AlumnoCurso> findByIdAsignaturaAndDni(final Long idAsignatura, final String dni);

    /**
     * Buscar el árbol de un curso.
     *
     * @return List<AlumnoTreeNodeDto>
     */
    @Query("""
            SELECT DISTINCT new com.gexcat.gex.repository.dto.AlumnoTreeNodeDto (
                a.id, a.nombre, a.apellidos, a.dni, r.curso.id)
              FROM AlumnoCurso r
        INNER JOIN r.alumno a
             WHERE r.curso.id IN (SELECT k.id
                      FROM Grupo g
                INNER JOIN g.cursos k
                     WHERE g.id = :idGrupo)
          ORDER BY a.apellidos, a.nombre
            """)
    List<AlumnoTreeNodeDto> findTree(final Long idGrupo);

    /**
     * Buscar los alumnos de un curso.
     *
     * @param idCurso
     *
     * @return List<AlumnoCursoDto>
     */
    @Query("""
            SELECT new com.gexcat.gex.repository.dto.AlumnoCursoDto (
                    l.id, l.dni, l.identificador, l.nombre, l.apellidos, l.enviarCorreo, l.correo, k.id )
              FROM AlumnoCurso u
        INNER JOIN u.alumno l
        INNER JOIN u.curso k
             WHERE k.id = :idCurso
          ORDER BY l.apellidos, l.nombre
                   """)
    List<AlumnoCursoDto> findAlumnos(final Long idCurso);

    /**
     * Buscar los datos de los alumnos que se van a exportar.
     *
     * @param idCurso
     *
     * @return List<AlumnoCursoExportarDto>
     */
    @Query(
        """
                SELECT new com.gexcat.gex.repository.dto.AlumnoCursoExportarDto (
                      l.id, l.dni, l.identificador, l.nombre, l.apellidos, l.movil, l.sexo, l.niu, l.correo,
                      l.observaciones )
                  FROM AlumnoCurso u
            INNER JOIN u.alumno l
            INNER JOIN u.curso k
                 WHERE k.id = :idCurso
              ORDER BY l.apellidos, l.nombre
                     """
    )
    List<AlumnoCursoExportarDto> findAlumnosExportar(final Long idCurso);

    /**
     * Buscar un curso.
     *
     * @param idCurso
     *
     * @return List<AlumnoCurso>
     */
    @Query("""
        SELECT u
          FROM AlumnoCurso u
         WHERE u.curso.id = :idCurso
        """)
    List<AlumnoCurso> findByIdCurso(final Long idCurso);

    /**
     * Comprobar si un curso existe.
     *
     * @param idCurso
     *
     * @return boolean
     */
    @Query("""
        SELECT COUNT(u) > 0
                FROM AlumnoCurso u
          INNER JOIN u.curso k
               WHERE k.id = :idCurso
                """)
    boolean existsByIdCurso(final Long idCurso);

    /**
     * Buscar el número correspondiente de un alumno en un curso.
     *
     * @param idCurso
     * @param apellidos del alumno
     * @param nombre    del alumno
     *
     * @return int
     */
    @Query("""
         SELECT COUNT(u)
                 FROM AlumnoCurso u
           INNER JOIN  u.alumno l
           INNER JOIN u.curso k
                WHERE k.id = :idCurso
                  AND (l.apellidos < :apellidos OR (l.apellidos = :apellidos AND l.nombre <= :nombre))
        """)
    int findNumeroAlumno(final Long idCurso, final String apellidos, final String nombre);

    default int findNumeroAlumno(final AlumnoCurso alumnoCurso) {
        return findNumeroAlumno(alumnoCurso.getCurso().getId(), alumnoCurso.getAlumno().getApellidos(), alumnoCurso
            .getAlumno().getNombre());
    }

    /**
     * Actualizar un curso.
     *
     * @param idCurso
     * @param idAlumno
     * @param idCursoAnterior
     *
     * @return int con el número de alumnos actualizados
     */
    @Modifying
    @Query("""
         UPDATE AlumnoCurso
            SET curso.id = :idCurso
          WHERE alumno.id = :idAlumno
            AND curso.id = :idCursoAnterior
        """)
    int updateCurso(final Long idCurso, final Long idAlumno, final Long idCursoAnterior);

    /**
     * Mover una lista de alumnos desde un curso en el que están a otro curso. Los alumnos no deben de existir en el
     * curso nuevo, luego la lista debe de venir filtrada.
     *
     * @param cursoId
     * @param lista   con los alumnos que se quieren mover
     *
     * @return int con el número de alumnos movidos
     */
    default int moveAlumnos(final Long idCurso, final List<AlumnoCursoDto> lista) {
        var retorno = 0;

        for (final AlumnoCursoDto alumnoCursoDto : lista) {

            if (!idCurso.equals(alumnoCursoDto.getCursoId())) {
                retorno += updateCurso(idCurso, alumnoCursoDto.getId(), alumnoCursoDto.getCursoId());
            }
        }

        return retorno;
    }

    /**
     * Insertar alumnos en un curso.
     *
     * @param idCurso
     * @param idAlumno
     *
     * @return int con el número de alumnos insertados
     */
    @Modifying
    @Query(value = """
         INSERT INTO alumno_curso(alumno_id, curso_id)
              VALUES(:idAlumno, :idCurso)
        """, nativeQuery = true)
    int insertCurso(final Long idCurso, final Long idAlumno);

    /**
     * Copiar una lista de alumnos desde un curso en el que están a otro curso. Los alumnos no deben de existir en el
     * curso nuevo, luego la lista debe de venir filtrada.
     *
     * @param cursoId
     * @param lista   con los alumnos que se quieren copiar
     *
     * @return int con el número de alumnos copiados
     */
    default int copyAlumnos(final Long idCurso, final List<AlumnoCursoDto> lista) {
        var retorno = 0;
        for (final AlumnoCursoDto alumnoCursoDto : lista) {
            if (idCurso != alumnoCursoDto.getCursoId()) {
                retorno += insertCurso(idCurso, alumnoCursoDto.getId());
            }
        }
        return retorno;
    }

    /**
     * Buscar las notas de un AlumnoCurso.
     *
     * @param idAlumnoCurso
     *
     * @return List<AlumnoCurso>
     */
    @Query(
        """
            SELECT new com.gexcat.gex.repository.dto.NotaDto (
                  n.id, n.codigoBarras, n.nota, e.id, e.fecha, e.descripcion, c.id, c.aciertos, c.fallos, c.blancos)
                  FROM Nota n
            INNER JOIN n.examen e
            INNER JOIN n.correccion c
                 WHERE n.alumnoCurso.id = :idAlumnoCurso
            """
    )
    List<NotaDto> findNotas(final AlumnoCursoId idAlumnoCurso);

    @Transactional
    default AlumnoCurso update(final AlumnoCurso alumnoCurso) {
        return guardarAlumno(alumnoCurso);
    }
}
