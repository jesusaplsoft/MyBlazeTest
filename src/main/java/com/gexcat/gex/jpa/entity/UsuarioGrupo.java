package com.gexcat.gex.jpa.entity;

import static com.gexcat.gex.cfg.Constants.BOOLEAN_TYPE;
import static com.gexcat.gex.cfg.Constants.BT_FALSE;
import static com.gexcat.gex.cfg.Constants.UNO;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Información de usuarios y grupos
 *
 * @author siul
 */
@AllArgsConstructor
//@Audited
@Entity
@Getter
@Builder
@NoArgsConstructor
@Setter
@Table(name = "usuario_grupo")
@ToString
@org.hibernate.annotations.Table(
    appliesTo = "usuario_grupo",
    comment = "Usuarios de la aplicación y grupos a los que pertenece"
)
public class UsuarioGrupo
    implements Serializable {

    private static final long serialVersionUID = 1685628543515633921L;

    @EmbeddedId
    private UsuarioGrupoId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "usuario_id",
        updatable = false,
        nullable = false,
        foreignKey = @ForeignKey(
            name = "usug_fk_usuario"
        )
    )
    @MapsId("usuarioId")
    @NotNull
    @ToString.Exclude
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "grupo_id",
        updatable = false,
        nullable = false,
        foreignKey = @ForeignKey(
            name = "usug_fk_grupo"
        )
    )
    @MapsId("grupoId")
    @NotNull
    @ToString.Exclude
    private Grupo grupo;

    @Column(
        name = "es_coordinador",
        nullable = false,
        length = UNO
    )
    @ColumnDefault(BT_FALSE)
    @NotNull
    @Type(type = BOOLEAN_TYPE)
    @Default
    private boolean coordinador = false;

    public UsuarioGrupo(final Usuario usuario, final Grupo grupo, final boolean coordinador) {
        this.usuario = usuario;
        this.grupo = grupo;
        this.coordinador = coordinador;
        this.id = new UsuarioGrupoId(usuario.getId(), grupo.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuario, grupo);
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final var that = (UsuarioGrupo) o;
        return Objects.equals(usuario, that.getUsuario())
            && Objects.equals(grupo, that.getGrupo());
    }
}
