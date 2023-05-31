package com.gexcat.gex.repository;

import java.util.Optional;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gexcat.gex.jpa.entity.Centro;

@Repository
@Transactional(readOnly = true)
public interface CentroRepository
    extends JpaRepository<Centro, Long> {

    @Override
    @Query("""
                 SELECT c
                   FROM Centro c
        LEFT JOIN FETCH c.imagen i
                  WHERE c.id = :idCentro
                """)
    @QueryHints(value = { @QueryHint(name = "hibernate.query.passDistinctThrough", value = "false") })
    Optional<Centro> findById(Long idCentro);

    @Query("""
                 SELECT c
                   FROM Centro c
        LEFT JOIN FETCH c.imagen i
                  WHERE c.codigo = :codigoCentro
                """)
    @QueryHints(value = { @QueryHint(name = "hibernate.query.passDistinctThrough", value = "false") })
    Optional<Centro> findByCodigo(String codigoCentro);

    default Optional<Centro> findCentro(final Centro centro) {

        if (centro.getId() != null) {
            return findById(centro.getId());
        }

        return findByCodigo(centro.getCodigo());
    }

    @Transactional
    default void delete(final Long idCentro) {
        final var centro = findById(idCentro).orElse(null);
        deleteById(centro.getId());
    }

    boolean existsByCodigo(final String codigoCentro);

    int countByCodigoLessThanEqualOrderByCodigo(final String codigo);

    default int findNumeroCentro(final String codigoCentro) {
        return countByCodigoLessThanEqualOrderByCodigo(codigoCentro);
    }

}
