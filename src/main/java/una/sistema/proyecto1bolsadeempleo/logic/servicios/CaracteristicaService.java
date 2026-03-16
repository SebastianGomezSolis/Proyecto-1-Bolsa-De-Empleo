package una.sistema.proyecto1bolsadeempleo.logic.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import una.sistema.proyecto1bolsadeempleo.data.CaracteristicaRepository;
import una.sistema.proyecto1bolsadeempleo.logic.model.Caracteristica;

import java.util.List;

@Service
public class CaracteristicaService {
    @Autowired
    private CaracteristicaRepository caracteristicaRepository;

    public Iterable<Caracteristica> findAll() {
        return caracteristicaRepository.findAll();
    }

    public Caracteristica findById(Integer id) {
        return caracteristicaRepository.findById(id).orElse(null);
    }

    public Caracteristica save(Caracteristica caracteristica) {
        return caracteristicaRepository.save(caracteristica);
    }

    public List<Caracteristica> findRaices() {
        return caracteristicaRepository.findByPadreIsNull();
    }

    public List<Caracteristica> findHijos(Integer padreId) {
        return caracteristicaRepository.findByPadreId(padreId);
    }
}
