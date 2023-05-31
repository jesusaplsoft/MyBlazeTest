package com.gexcat.gex.repository.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Value;

/**
 * @author vaneMB
 */
@Value
public class NotaDto
    implements Serializable {

    private static final long serialVersionUID = -6528281375839301749L;

    Long id;
    String codigoBarras;
    BigDecimal nota;
    Long idExamen;
    LocalDate fechaExamen;
    String descripcionExamen;
    Long idCorreccion;
    int aciertosCorreccion;
    int fallosCorreccion;
    int blancosCorreccion;
}
