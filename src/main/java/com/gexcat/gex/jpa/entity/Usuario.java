package com.gexcat.gex.jpa.entity;

import static com.gexcat.gex.cfg.Constants.BOOLEAN_TYPE;
import static com.gexcat.gex.cfg.Constants.BT_FALSE;
import static com.gexcat.gex.cfg.Constants.SESENTA;
import static com.gexcat.gex.cfg.Constants.TREINTA;
import static com.gexcat.gex.cfg.Constants.UNO;

import java.io.Serializable;
import java.time.LocalDateTime;
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

import org.hibernate.annotations.Check;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.annotations.Type;
import org.hibernate.envers.NotAudited;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Usuarios con acceso a la aplicación.
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
    name = "usuario",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "usu_unq_codigo",
            columnNames = { "codigo" }
        ),
        @UniqueConstraint(
            name = "usu_unq_email",
            columnNames = { "email" }
        ) },
    indexes = { @Index(
        name = "usu_idx_nombre",
        columnList = "nombre"
    ) }
)
@ToString
@org.hibernate.annotations.Table(
    appliesTo = "usuario",
    comment = "Usuarios con acceso a la aplicación"
)
@Check(
    constraints = "es_admin IN ('T', 'F')"
)
public class Usuario
    implements Serializable, Cloneable {

    private static final long serialVersionUID = -1727050150865646371L;

    @Column(
        updatable = false,
        nullable = false
    )
    @SequenceGenerator(
        name = "usuarioSequence",
        sequenceName = "usu_sequence",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "usuarioSequence"
    )
    @ColumnDefault(value = "(next value for usu_sequence)")
    @Setter(AccessLevel.NONE)
    @Id
    private Long id;

    /**
     * Nombre corto o nick.
     */
    @Column(
        nullable = false,
        length = TREINTA
    )
    @NotNull
    @Size(max = TREINTA)
//    @NonNull
    private String codigo;

    @Column(length = SESENTA)
//    @ExtendedEmail
    @Size(max = SESENTA)
//    @NonNull
    private String email;

    @Column(
        nullable = false,
        length = SESENTA
    )
    @NotNull
    @Size(max = SESENTA)
    private String nombre;

    @Column(
        name = "es_admin",
        nullable = false,
        length = UNO
    )
    @ColumnDefault(BT_FALSE)
    @NotNull
    @Type(type = BOOLEAN_TYPE)
    @Default
    private boolean admin = false;

    @Column(
        name = "creado_por",
        length = 128,
        nullable = false,
        updatable = false
    )
    @Setter(AccessLevel.NONE)
    private String createdBy;

    @Column(
        name = "creado_fecha",
        nullable = false,
        updatable = false
    )
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdDate;

    @OneToMany(
        mappedBy = "usuario",
        cascade = { CascadeType.PERSIST, CascadeType.MERGE },
        orphanRemoval = true
    )
    @NotAudited
    @ToString.Exclude
    @Default
    private final Set<UsuarioGrupo> grupos = new HashSet<>();

    @Transient
    @Default
    private boolean coordinador = false;

//    public List<Grupo> getGruposOrdenados() {
//
//        if (grupos.isEmpty()) {
//            return new ArrayList<>();
//        }
//
//        final List<Grupo> gruposOrdenados = grupos.stream().map(UsuarioGrupo::getGrupo).collect(Collectors
//            .toList());
//        Collections.sort(gruposOrdenados, (g1, g2) -> MiscBase.compare(g1.getNombre(), g2.getNombre()));
//        return gruposOrdenados;
//    }
//
//    @PrePersist
//    private void newCreatedBy() {
//        this.createdBy = AuditorAwareImpl.USER;
//        this.createdDate = LocalDateTime.now();
//    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final var that = (Usuario) o;

        return this.email != null && this.email.equals(that.getEmail());
    }
}
