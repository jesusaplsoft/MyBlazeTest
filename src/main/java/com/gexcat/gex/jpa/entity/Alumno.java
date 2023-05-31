package com.gexcat.gex.jpa.entity;

import static com.gexcat.gex.cfg.Constants.BOOLEAN_TYPE;
import static com.gexcat.gex.cfg.Constants.BT_TRUE;
import static com.gexcat.gex.cfg.Constants.CATORCE;
import static com.gexcat.gex.cfg.Constants.CIEN;
import static com.gexcat.gex.cfg.Constants.CINCUENTA;
import static com.gexcat.gex.cfg.Constants.QUINCE;
import static com.gexcat.gex.cfg.Constants.QUINIENTOS_DOCE;
import static com.gexcat.gex.cfg.Constants.SETENTA;
import static com.gexcat.gex.cfg.Constants.UNO;
import static com.gexcat.gex.cfg.Constants.VEINTE;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Check;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.envers.NotAudited;

import com.gexcat.gex.jpa.entity.image.AlumnoImagen;
import com.gexcat.gex.type.EnumType;
import com.gexcat.gex.type.TipoSexo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Tolerate;

/**
 * Información del alumno.
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
    name = "alumno",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "alu_unq_dni",
            columnNames = { "dni" }
        ),
        @UniqueConstraint(
            name = "alu_unq_niu",
            columnNames = { "niu" }
        ),
        @UniqueConstraint(
            name = "alu_unq_correo",
            columnNames = { "correo" }
        ),
        @UniqueConstraint(
            name = "alu_unq_identificador",
            columnNames = { "identificador" }
        )
    },
    indexes = { @Index(
        name = "alu_idx_apellidos_nombre",
        columnList = "apellidos, nombre"
    ) }
)
@ToString
@org.hibernate.annotations.Table(
    appliesTo = "alumno",
    comment = "Información del alumno"
)
@Check(
    constraints = "es_enviar_correo IN ('T', 'F') "
        + "AND sexo IN ('H', 'M', '')"
)
public class Alumno
    implements Serializable, Cloneable {

    private static final long serialVersionUID = 3684056232753920937L;

    @Column(
        updatable = false,
        nullable = false
    )
    @SequenceGenerator(
        name = "alumnoSequence",
        sequenceName = "alu_sequence",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "alumnoSequence"
    )
    @ColumnDefault(value = "(next value for alu_sequence)")
    @Setter(AccessLevel.NONE)
    @Id
    private Long id;

    @Column(
        name = "dni",
        nullable = false,
        length = VEINTE
    )
    @NotNull
    @Size(max = VEINTE)
//    @NonNull
    private String dni;

    @Column(
        name = "apellidos",
        nullable = false,
        length = CIEN
    )
    @NotNull
    @Size(max = CIEN)
    private String apellidos;

    @Column(
        name = "correo",
        length = SETENTA
    )
    @Size(max = SETENTA)
    private String correo;

    @Column(
        name = "es_enviar_correo",
        nullable = false,
        length = UNO
    )
    @ColumnDefault(BT_TRUE)
    @NotNull
    @Type(type = BOOLEAN_TYPE)
    @Default
    private boolean enviarCorreo = true;

    @Column(
        name = "identificador",
        nullable = false,
        precision = 8
    )
    @ColumnDefault("0")
    @Digits(
        integer = 8,
        fraction = 0
    )
    @Max(99999999)
    @Min(0)
    @NotNull
    @Default
    private Integer identificador = 0;

    @Column(
        name = "movil",
        length = CATORCE
    )
    @Size(max = CATORCE)
    private String movil;

    @Column(
        name = "niu",
        length = QUINCE
    )
    @Size(max = QUINCE)
    private String niu;

    @Column(
        name = "nombre",
        nullable = false,
        length = CINCUENTA
    )
    @NotNull
    @Size(max = CINCUENTA)
    private String nombre;

    @Column(
        name = "observaciones",
        length = QUINIENTOS_DOCE
    )
    @Size(max = QUINIENTOS_DOCE)
    private String observaciones;

    @Column(
        name = "sexo",
        nullable = false,
        length = UNO
    )
    @Type(
        type = EnumType.TYPE,
        parameters = {
            @Parameter(
                name = EnumType.CLASS,
                value = "com.gexcat.gex.type.TipoSexo"
            )
        }
    )
    @NotNull
    @Default
    private TipoSexo sexo = TipoSexo.INDEFINIDO;

    @OneToMany(
        mappedBy = "alumno",
        cascade = { CascadeType.PERSIST, CascadeType.MERGE },
        orphanRemoval = true
    )
    @NotAudited
    @ToString.Exclude
    @Default
    private final Set<AlumnoCurso> cursos = new HashSet<>();

    @OneToOne(
        mappedBy = "alumno",
        cascade = CascadeType.ALL,
        fetch = FetchType.LAZY,
        orphanRemoval = true
    )
    @NotAudited
    @ToString.Exclude
    private AlumnoImagen imagen;

    public String getSexoString() {
        return sexo.toString().substring(0, 1);
    }

    public void setDni(final String dni) {
        this.dni = dni;
    }

    @Tolerate
    public void setEnviarCorreo(final String enviarCorreo) {

        if ("T".equalsIgnoreCase(enviarCorreo)) {
            this.enviarCorreo = true;
        } else {
            this.enviarCorreo = false;
        }
    }

    @Tolerate
    public void setSexo(final String sexo) {

        if ("H".equalsIgnoreCase(sexo)) {
            setSexo(TipoSexo.HOMBRE);
        } else if (sexo == "M") {
            setSexo(TipoSexo.MUJER);
        } else {
            setSexo(TipoSexo.INDEFINIDO);
        }
    }

    public void addCurso(final Curso curso) {
        final var alumnoCurso = new AlumnoCurso(this, curso);
        this.cursos.add(alumnoCurso);
    }

    public void removeCurso(final Curso curso) {
        for (final AlumnoCurso alumnoCurso : cursos) {

            if (alumnoCurso.getCurso().getId().equals(curso.getId())) {
                this.cursos.remove(alumnoCurso);
                break;
            }
        }
    }

    public void addImagen(final AlumnoImagen ai) {

        if (this.imagen == null) {
            this.imagen = ai;
        } else {
            this.imagen.setImagen(ai.getImagen());
            this.imagen.setOriginal(ai.getOriginal());
        }

        ai.setAlumno(this);
    }

    public void removeImagen(final AlumnoImagen ai) {

        if (ai != null) {
            ai.setAlumno(null);
        }
        this.imagen = null;
    }

    @Override
    public int hashCode() {

        return Objects.hash(dni);
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final var that = (Alumno) o;

        return this.dni != null && this.dni.equals(that.getDni());
    }

}
