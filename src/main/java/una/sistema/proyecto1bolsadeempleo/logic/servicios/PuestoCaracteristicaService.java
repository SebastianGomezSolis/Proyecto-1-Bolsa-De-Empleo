package una.sistema.proyecto1bolsadeempleo.logic.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import una.sistema.proyecto1bolsadeempleo.data.PuestoCaracteristicaRepository;
import una.sistema.proyecto1bolsadeempleo.logic.model.PuestoCaracteristica;
import java.util.List;

@Service("puestoCaracteristicaService")
public class PuestoCaracteristicaService {
    @Autowired
    private PuestoCaracteristicaRepository puestoCaracteristicaRepository;

    public PuestoCaracteristica save(PuestoCaracteristica pc) {
        return puestoCaracteristicaRepository.save(pc);
    }

    public List<PuestoCaracteristica> findByPuesto(Integer puestoId) {
        return puestoCaracteristicaRepository.findByPuestoId(puestoId);
    }

    public void deleteById(Integer id) {
        puestoCaracteristicaRepository.deleteById(id);
    }
}
