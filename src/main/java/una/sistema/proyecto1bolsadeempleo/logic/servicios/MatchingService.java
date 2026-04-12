package una.sistema.proyecto1bolsadeempleo.logic.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import una.sistema.proyecto1bolsadeempleo.data.HabilidadRepository;
import una.sistema.proyecto1bolsadeempleo.data.OferenteRepository;
import una.sistema.proyecto1bolsadeempleo.data.PuestoRepository;
import una.sistema.proyecto1bolsadeempleo.logic.model.CandidatoResultado;
import una.sistema.proyecto1bolsadeempleo.logic.model.Habilidad;
import una.sistema.proyecto1bolsadeempleo.logic.model.Oferente;
import una.sistema.proyecto1bolsadeempleo.logic.model.Puesto;
import una.sistema.proyecto1bolsadeempleo.logic.model.PuestoCaracteristica;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("matchingService")
public class MatchingService {
    @Autowired
    private PuestoRepository puestoRepository;

    @Autowired
    private OferenteRepository oferenteRepository;

    @Autowired
    private HabilidadRepository habilidadRepository;

    public Map<Integer, Integer> construirVectorPuesto(Puesto puesto) {
        Map<Integer, Integer> vector = new HashMap<>();
        if (puesto == null || puesto.getCaracteristicas() == null) {
            return vector;
        }

        for (PuestoCaracteristica pc : puesto.getCaracteristicas()) {
            if (pc.getCaracteristica() != null && pc.getCaracteristica().getId() != null) {
                vector.put(pc.getCaracteristica().getId(), pc.getNivelRequerido());
            }
        }

        return vector;
    }

    public Map<Integer, Integer> construirVectorOferente(List<Habilidad> habilidades) {
        Map<Integer, Integer> vector = new HashMap<>();
        if (habilidades == null) {
            return vector;
        }

        for (Habilidad h : habilidades) {
            if (h.getCaracteristica() != null && h.getCaracteristica().getId() != null) {
                vector.put(h.getCaracteristica().getId(), h.getNivel());
            }
        }

        return vector;
    }

    public double calcularSimilitudCoseno(Map<Integer, Integer> vectorPuesto, Map<Integer, Integer> vectorOferente) {
        Set<Integer> dimensiones = new HashSet<>();
        dimensiones.addAll(vectorPuesto.keySet());
        dimensiones.addAll(vectorOferente.keySet());

        double productoPunto = 0.0;
        double normaPuesto = 0.0;
        double normaOferente = 0.0;

        for (Integer id : dimensiones) {
            int valorPuesto = vectorPuesto.getOrDefault(id, 0);
            int valorOferente = vectorOferente.getOrDefault(id, 0);

            productoPunto += valorPuesto * valorOferente;
            normaPuesto += valorPuesto * valorPuesto;
            normaOferente += valorOferente * valorOferente;
        }

        if (normaPuesto == 0 || normaOferente == 0) {
            return 0.0;
        }

        return productoPunto / (Math.sqrt(normaPuesto) * Math.sqrt(normaOferente));
    }

    public double calcularSimilitud(Puesto puesto, List<Habilidad> habilidades) {
        Map<Integer, Integer> vectorPuesto = construirVectorPuesto(puesto);
        Map<Integer, Integer> vectorOferente = construirVectorOferente(habilidades);
        return calcularSimilitudCoseno(vectorPuesto, vectorOferente);
    }

    public double calcularPorcentaje(Puesto puesto, List<Habilidad> habilidades) {
        return calcularSimilitud(puesto, habilidades) * 100.0;
    }

    public List<CandidatoResultado> buscarCandidatosPorPuesto(Integer puestoId) {
        List<CandidatoResultado> resultados = new ArrayList<>();
        Puesto puesto = puestoRepository.findById(puestoId).orElse(null);

        if (puesto == null) {
            return resultados;
        }

        List<Oferente> oferentes = oferenteRepository.findByAutorizado(true);

        for (Oferente oferente : oferentes) {
            List<Habilidad> habilidades = habilidadRepository.findByOferenteIdentificacion(oferente.getIdentificacion());

            double similitud = calcularSimilitud(puesto, habilidades);

            int requisitosCumplidos = 0;
            int totalRequisitos = puesto.getCaracteristicas().size();

            for (PuestoCaracteristica pc : puesto.getCaracteristicas()) {
                for (Habilidad h : habilidades) {
                    if (pc.getCaracteristica().getId().equals(h.getCaracteristica().getId())
                            && h.getNivel() >= pc.getNivelRequerido()) {
                        requisitosCumplidos++;
                        break;
                    }
                }
            }

            CandidatoResultado resultado = new CandidatoResultado();
            resultado.setOferente(oferente);
            resultado.setSimilitud(similitud);
            resultado.setPorcentaje(similitud * 100.0);
            resultado.setRequisitosCumplidos(requisitosCumplidos);
            resultado.setTotalRequisitos(totalRequisitos);

            resultados.add(resultado);
        }

        resultados.sort((a, b) -> Double.compare(b.getSimilitud(), a.getSimilitud()));

        return resultados;
    }
}