package com.gexcat.gex.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import com.gexcat.gex.jpa.entity.Alumno;
import com.gexcat.gex.jpa.entity.AlumnoCurso;
import com.gexcat.gex.jpa.misc.GexCatMisc.Formato;
import com.gexcat.gex.repository.dto.AlumnoCursoFindDto;
import com.gexcat.gex.repository.dto.AlumnoCursoFindRecord;
import com.gexcat.gex.repository.view.AlumnoCursoFindView;

/**
 * CustomAlumnoCursoRepository
 *
 * @author vaneMB
 */
@Transactional(readOnly = true)
public interface CustomAlumnoCursoRepository {

    List<AlumnoCursoFindDto> findAlumnosBusqueda(final Long cursoId,
        final String nombre,
        final String dni,
        final Integer identificador,
        final boolean mayusculas);

    List<AlumnoCursoFindRecord> findAlumnosBusquedaBEM(final Long cursoId,
        final String nombre,
        final String dni,
        final Integer identificador,
        final boolean mayusculas);

    List<AlumnoCursoFindView> findAlumnosBusquedaBEV(final Long cursoId,
        final String nombre,
        final String dni,
        final Integer identificador,
        final boolean mayusculas);

    Optional<AlumnoCurso> findFirst(Long idCurso);

    Optional<AlumnoCurso> findPrevious(Long idCurso, Alumno alumno);

    Optional<AlumnoCurso> findNext(Long idCurso, Alumno alumno);

    Optional<AlumnoCurso> findLast(Long idCurso);

    long countByIdAlumno(final Long idAlumno);

    @Transactional
    void delete(final AlumnoCurso alumnoCurso, final boolean borrar);

    @Transactional
    void deleteAlumnos(final Long idCurso);

    Optional<AlumnoCurso> findByIdCursoAndDni(final Long idCurso, final String dni);

    Optional<AlumnoCurso> findByIdCursoAndIdAlumno(final Long idCurso, final Long idAlumno);

    Optional<AlumnoCurso> findByIdCursoAndIdentificador(final Long idCurso, final Integer identificador);

    AlumnoCurso guardarAlumno(final AlumnoCurso alumnoCurso);

    @Transactional
    boolean guardarIdentidad(final Object[] datos, final int numNotas);

    Object[] importarIdentidades(final String fichero, final Formato formato, final char separador) throws Exception;

    boolean existsByIdAlumnoAndIdCursoAndIdentificador(final Long idAlumno,
        final Long idCurso,
        final Integer identificador);

    @Transactional
    AlumnoCurso guardarNoAsignado(final AlumnoCurso alumnoCurso, final Long idNota, final Integer identificador);
}