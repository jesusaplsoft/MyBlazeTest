package com.gexcat.gex.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gexcat.gex.jpa.entity.Grupo;
import com.gexcat.gex.jpa.misc.UserData;

@Repository
@Transactional(readOnly = true)
public interface GrupoRepository
    extends JpaRepository<Grupo, Long> {

    @Override
    @Query("""
                 SELECT g
                   FROM Grupo g
        LEFT JOIN FETCH g.usuarios s
        LEFT JOIN FETCH s.usuario u
                  WHERE g.id = :idGrupo
        """)
    Optional<Grupo> findById(Long idGrupo);

    @Query("""
                 SELECT g
                   FROM Grupo g
        LEFT JOIN FETCH g.usuarios s
        LEFT JOIN FETCH s.usuario u
                  WHERE g.id = :idGrupo
                    AND g.id IN (SELECT r.grupo.id
                                   FROM UsuarioGrupo r
                                  WHERE r.usuario.id = :idUsuario
                                    AND r.coordinador = 'T')
        """)
    Optional<Grupo> findById(Long idGrupo, Long idUsuario);

    @Query("""
                 SELECT g
                   FROM Grupo g
        LEFT JOIN FETCH g.usuarios s
        LEFT JOIN FETCH s.usuario u
                  WHERE g.codigo = :codigo
        """)
    Optional<Grupo> findByCodigo(String codigo);

    boolean existsByCodigo(final String codigo);

    @Query("""
                 SELECT g
                   FROM Grupo g
        LEFT JOIN FETCH g.usuarios s
        LEFT JOIN FETCH s.usuario u
                  WHERE g.codigo = :codigo
                    AND g.id IN (SELECT r.grupo.id
                                   FROM UsuarioGrupo r
                                  WHERE r.usuario.id = :idUsuario
                                    AND r.coordinador = 'T')
         """)
    Optional<Grupo> findByCodigo(String codigo, Long idUsuario);

    default Optional<Grupo> findGrupo(final Grupo grupo) {
        final var usuario = UserData.getInstance().getUsuario();

        if (grupo.getId() != null) {
            return usuario.isAdmin() ? findById(grupo.getId()) : findById(grupo.getId(), usuario.getId());
        }

        return usuario.isAdmin() ? findByCodigo(grupo.getCodigo()) : findByCodigo(grupo.getCodigo(), usuario.getId());
    }

    @Modifying
    @Query("""
            UPDATE Curso k
               SET k.grupo = null
             WHERE k.id IN :ids
        """)
    int updateCursoSetGrupoNullByCursoId(final Set<Long> ids);

    @Modifying
    @Query("""
            UPDATE Curso k
               SET k.grupo.id = :idGrupo
             WHERE k.id IN :ids
        """)
    int updateCursoSetGrupoByCursoId(final Set<Long> ids, final Long idGrupo);

    @Modifying
    @Query("""
            UPDATE Curso k
               SET k.grupo = null
             WHERE k.grupo.id = :idGrupo
        """)
    int updateCursoSetGrupoNullByGrupoId(final Long idGrupo);

    @Transactional
    default void delete(final Long idGrupo) {
        updateCursoSetGrupoNullByGrupoId(idGrupo);

        final var grupo = findById(idGrupo).orElse(null);
        deleteById(grupo.getId());
    }
}
