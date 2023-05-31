package com.gexcat.gex.jpa.entity;

import static com.gexcat.gex.cfg.Constants.SESENTA;
import static com.gexcat.gex.cfg.Constants.TREINTA;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.envers.NotAudited;

import com.gexcat.gex.tool.MiscBase;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Asociación de rol y grupos.
 *
 * @author JMR + vaneMB
 */
@AllArgsConstructor
@NoArgsConstructor
//@Audited
@Builder
@DynamicInsert
@DynamicUpdate
@Entity
@Getter
@OptimisticLocking(type = OptimisticLockType.DIRTY)
@Setter
@Table(
    name = "grupo",
    uniqueConstraints = { @UniqueConstraint(
        name = "grp_unq_codigo",
        columnNames = "codigo"
    ) },
    indexes = { @Index(
        name = "grp_idx_nombre",
        columnList = "nombre"
    ) }
)
@ToString
@org.hibernate.annotations.Table(
    appliesTo = "grupo",
    comment = "Grupos de los usuarios"
)
public class Grupo
    implements Serializable, Cloneable, Comparable<Grupo> {

    private static final long serialVersionUID = 6555855134876296430L;

    @Column(
        updatable = false,
        nullable = false
    )
    @SequenceGenerator(
        name = "grupoSequence",
        sequenceName = "grp_sequence",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "grupoSequence"
    )
    @ColumnDefault(value = "(next value for grp_sequence)")
    @Setter(AccessLevel.NONE)
    @Id
    private Long id;

    @Column(
        name = "codigo",
        nullable = false,
        length = TREINTA
    )
    @NotNull
    @Size(max = TREINTA)
//    @NonNull
    private String codigo;

    @Column(
        name = "nombre",
        nullable = false,
        length = SESENTA
    )
    @NotNull
    @Size(max = SESENTA)
    private String nombre;

    // Relación inversa
    @OneToMany(
        mappedBy = "grupo",
        cascade = { CascadeType.PERSIST, CascadeType.MERGE }
    )
    @NotAudited
    @ToString.Exclude
    @Default
    private final Set<Curso> cursos = new HashSet<>();

    @OneToMany(
        mappedBy = "grupo",
        cascade = { CascadeType.PERSIST, CascadeType.MERGE },
        orphanRemoval = true
    )
    @NotAudited
    @ToString.Exclude
    @Default
    private final Set<UsuarioGrupo> usuarios = new HashSet<>();

    // Cuidado con este campo que es transiente y
    // no se propaga entre las diferentes instancias de
    // este Grupo.
    @Transient
    @Default
    private Set<Long> idsCursosRemove = new HashSet<>();

    @Transient
    @Default
    private Set<Long> idsCursosAdd = new HashSet<>();

    public void addCurso(final Long idCurso) {
        idsCursosAdd.add(idCurso);
    }

    public void removeCurso(final Long idCurso) {
        idsCursosRemove.add(idCurso);
    }

    public void addUsuario(final Usuario usuario, final boolean coordinador) {
        final var usuarioGrupo = new UsuarioGrupo(usuario, this, coordinador);

        if (usuarios.contains(usuarioGrupo)) {
            usuarios.remove(usuarioGrupo);
        }

        usuarios.add(usuarioGrupo);
    }

    public void removeUsuario(final Usuario usuario) {
        for (final UsuarioGrupo usuarioGrupo : usuarios) {

            if (usuarioGrupo.getUsuario().getId().equals(usuario.getId())) {
                usuarios.remove(usuarioGrupo);
                break;
            }
        }
    }

//    public List<Usuario> getUsuariosOrdenados() {
//
//        if (usuarios.isEmpty()) {
//            return new ArrayList<>();
//        }
//
//        final List<Usuario> usuariosOrdenados = usuarios.stream().map(ug -> {
//            final var u = ug.getUsuario();
//            u.setCoordinador(ug.isCoordinador());
//            return u;
//        }).collect(Collectors.toList());
//
//        Collections.sort(usuariosOrdenados, (u1, u2) -> MiscBase.compare(u1.getCodigo(), u2.getCodigo()));
//        return usuariosOrdenados;
//    }

    @Override
    public int hashCode() {

        return Objects.hash(codigo);
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final var that = (Grupo) o;

        return this.codigo != null && this.codigo.equals(that.getCodigo());
    }

    @Override
    public int compareTo(final Grupo g) {
        return MiscBase.compare(this.codigo, g.getCodigo());
    }
}
