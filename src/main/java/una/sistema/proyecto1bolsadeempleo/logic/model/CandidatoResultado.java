package una.sistema.proyecto1bolsadeempleo.logic.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CandidatoResultado {
    private Oferente oferente;
    private double similitud;
    private double porcentaje;
    private int requisitosCumplidos;
    private int totalRequisitos;
}

// Esta clase va a guardar el resultado de comparar un oferente contra un puesto...
// Con esta clase, cada fila del resultado ya trae todo junto...