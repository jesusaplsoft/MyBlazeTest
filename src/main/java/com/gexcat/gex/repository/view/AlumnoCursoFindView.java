package com.gexcat.gex.repository.view;

import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;
import com.gexcat.gex.jpa.entity.Alumno;
import com.gexcat.gex.jpa.entity.AlumnoCurso;
import com.gexcat.gex.jpa.entity.AlumnoCursoId;
import com.gexcat.gex.jpa.entity.Asignatura;
import com.gexcat.gex.jpa.entity.Centro;
import com.gexcat.gex.jpa.entity.Curso;

@EntityView(AlumnoCurso.class)
public interface AlumnoCursoFindView {

    @IdMapping
    public AlumnoCursoId alumnoCursoId = null;

    public AlumnoDataView getAlumno();

    public CursoCodigoAsignaturaView getCurso();

    @EntityView(Alumno.class)
    interface AlumnoDataView {
        @IdMapping
        public Long getId();

        public String getDni();

        public Integer getIdentificador();

        public String getNombre();

        public String getApellidos();
    }

    @EntityView(Curso.class)
    interface CursoCodigoAsignaturaView {
        @IdMapping
        public Long getId();

        public String getCodigo();

        public AsignaturaCodigoCentroView getAsignatura();

        @EntityView(Asignatura.class)
        interface AsignaturaCodigoCentroView {
            @IdMapping
            public Long getId();

            public String getCodigo();

            public CentroCodigoView getCentro();

            @EntityView(Centro.class)
            interface CentroCodigoView {
                @IdMapping
                public Long getId();

                public String getCodigo();
            }
        }
    }

}
