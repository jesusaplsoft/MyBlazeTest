package com.gexcat.gex.jpa.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author siul
 */
@AllArgsConstructor
@Embeddable
@Getter
@NoArgsConstructor
@ToString
public class UsuarioGrupoId
    implements Serializable {

    private static final long serialVersionUID = -5377860977978429890L;

    @NotNull
    private Long usuarioId;

    @NotNull
    private Long grupoId;

    @Override
    public int hashCode() {
        return Objects.hash(usuarioId, grupoId);
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final var that = (UsuarioGrupoId) o;
        return Objects.equals(usuarioId, that.getUsuarioId()) && Objects.equals(grupoId, that.getGrupoId());
    }
}
