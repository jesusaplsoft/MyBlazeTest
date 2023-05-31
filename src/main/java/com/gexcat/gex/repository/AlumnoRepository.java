package com.gexcat.gex.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gexcat.gex.jpa.entity.Alumno;

/**
 * AlumnoRepository
 *
 * @author jesús + vaneMB
 */
@Repository
@Transactional(readOnly = true)
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {

    /**
     * Buscar un alumno.
     *
     * @param idAlumno
     *
     * @return Optional<Alumno>
     */
    @Override
    @Query("""
                  SELECT l
                    FROM Alumno l
        INNER JOIN FETCH l.cursos u
        INNER JOIN FETCH u.curso k
        INNER JOIN FETCH k.asignatura a
        LEFT JOIN FETCH l.imagen i
                   WHERE l.id = :idAlumno
               """)
    Optional<Alumno> findById(Long idAlumno);

}
