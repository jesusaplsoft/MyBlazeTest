package com.gexcat.gex.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.gexcat.gex.AuditorAwareImpl;
import com.gexcat.gex.cfg.GexCatTestConfiguration;
import com.gexcat.gex.jpa.entity.Alumno;
import com.gexcat.gex.jpa.entity.Asignatura;
import com.gexcat.gex.jpa.entity.Centro;
import com.gexcat.gex.jpa.entity.Curso;
import com.gexcat.gex.jpa.entity.Grupo;
import com.gexcat.gex.jpa.entity.image.AlumnoImagen;
import com.gexcat.gex.jpa.entity.image.CentroImagen;
import com.gexcat.gex.jpa.misc.UserData;
import com.gexcat.gex.type.TipoCuatrimestre;
import com.gexcat.gex.type.TipoSexo;

import lombok.extern.log4j.Log4j2;

/**
 * Test de CreateDb
 * 
 * NOTA.- Comentadas anotaciones que son exclusivas de Spring Boot
 *
 * @author jesus
 */
//@DataJpaTest(
//    properties = "spring.jpa.hibernate.ddl-auto=create"
//)
//@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = GexCatTestConfiguration.class)
@Log4j2
@DisplayName("Al ejecutar Create Db")
class CreateDb {

    @DisplayName("Si se llama a testSchemaCreation, debería de crear la Bd")
    @Order(5)
    @Test
    public void testSchemaCreation() {}

}
