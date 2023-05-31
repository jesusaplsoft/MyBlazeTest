package com.gexcat.gex.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gexcat.gex.jpa.entity.Asignatura;

@Repository
@Transactional(readOnly = true)
public interface AsignaturaRepository
    extends JpaRepository<Asignatura, Long> {

    @Override
    @Query("""
                  SELECT a
                    FROM Asignatura a
        INNER JOIN FETCH a.centro c
         LEFT JOIN FETCH c.imagen i
                   WHERE a.id = :idAsignatura
        """)
    Optional<Asignatura> findById(Long idAsignatura);

    @Query("""
                  SELECT a
                    FROM Asignatura a
        INNER JOIN FETCH a.centro c
         LEFT JOIN FETCH c.imagen i
                   WHERE c.id = :idCentro
                     AND a.codigo = :codigoAsignatura
        """)
    Optional<Asignatura> findByIdCentroAndCodigo(final Long idCentro, final String codigoAsignatura);

    default Optional<Asignatura> findAsignatura(final Asignatura asignatura) {

        if (asignatura.getId() == null) {
            return findByIdCentroAndCodigo(asignatura.getCentro().getId(), asignatura.getCodigo());
        }
        return findById(asignatura.getId());
    }

    @Transactional
    default void delete(final Long idAsignatura) {
        deleteById(idAsignatura);
    }

    boolean existsByCodigo(final String codigoAsignatura);

    long countByCodigoLessThanEqualOrderByCodigo(final String codigoAsignatura);

}
