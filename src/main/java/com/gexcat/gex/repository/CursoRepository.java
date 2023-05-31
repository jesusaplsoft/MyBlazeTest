package com.gexcat.gex.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gexcat.gex.jpa.entity.Asignatura;
import com.gexcat.gex.jpa.entity.Curso;

@Repository
@Transactional(readOnly = true)
public interface CursoRepository extends JpaRepository<Curso, Long> {

    @Override
    @Query("""
                  SELECT k
                    FROM Curso k
        INNER JOIN FETCH k.asignatura a
        INNER JOIN FETCH a.centro c
         LEFT JOIN FETCH c.imagen i
         LEFT JOIN FETCH k.temas t
         LEFT JOIN FETCH k.grupo g
                   WHERE k.id = :idCurso
               """)
    Optional<Curso> findById(Long idCurso);

    @Query("""
                  SELECT k
                    FROM Curso k
        INNER JOIN FETCH k.asignatura a
        INNER JOIN FETCH a.centro c
         LEFT JOIN FETCH c.imagen i
         LEFT JOIN FETCH k.temas t
         LEFT JOIN FETCH k.grupo g
                   WHERE a.id = :idAsignatura
                     AND k.codigo = :codigoCurso
               """)
    Optional<Curso> findByCodigo(Long idAsignatura, String codigoCurso);

    default Optional<Curso> findCurso(final Curso curso) {

        if (curso.getId() == null) {
            return findByCodigo(curso.getAsignatura().getId(), curso.getCodigo());
        }
        return findById(curso.getId());
    }

    boolean existsByAsignatura(final Asignatura asignatura);

    @Modifying
    @Query("""
        DELETE FROM Asignatura a
              WHERE a.id = :id

        """)
    int deleteAsignaturaById(final Long id);

    @Transactional
    default void delete(final Long idCurso) {
        final var curso = findById(idCurso).orElse(null);
        deleteById(curso.getId());

        if (!existsByAsignatura(curso.getAsignatura())) {
            deleteAsignaturaById(curso.getAsignatura().getId());
        }

    }

    @Query("""
            SELECT COUNT(k) > 0
              FROM Curso k
        INNER JOIN k.asignatura a
             WHERE a.centro.id = :centroId
               AND a.id <> :asignaturaId
               AND LOWER (a.codigo) LIKE LOWER(:asignaturaCodigo)
                """)
    boolean existsByAsignaturaCodigo(final Long centroId, final Long asignaturaId, final String asignaturaCodigo);

    @Query("""
            SELECT COUNT(k) > 0
              FROM Curso k
        INNER JOIN k.asignatura a
             WHERE a.centro.id = :centroId
               AND LOWER (a.codigo) LIKE LOWER(:asignaturaCodigo)
               AND LOWER (k.codigo) LIKE LOWER(:cursoCodigo)
                """)
    boolean existsByAsignaturaCodigoAndCursoCodigo(final Long centroId, final String asignaturaCodigo,
        final String cursoCodigo);

    default boolean existsByCodigos(final Long idCentro,
        final Long idAsignatura,
        final String codigoAsignatura,
        final String codigoCurso) {

        if (existsByAsignaturaCodigoAndCursoCodigo(idCentro, codigoAsignatura, codigoCurso)) {
            return true;
        }

        return existsByAsignaturaCodigo(idCentro, idAsignatura, codigoAsignatura);
    }

    @Transactional
    default Curso update(final Curso curso) {
        Curso aux;

        if (curso.getId() != null) {
            aux = findById(curso.getId()).orElse(null);
        }

        aux = curso;
        return save(aux);
    }
}
