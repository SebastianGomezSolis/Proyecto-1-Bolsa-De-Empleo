package una.sistema.proyecto1bolsadeempleo.logic.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import una.sistema.proyecto1bolsadeempleo.data.HabilidadRepository;
import una.sistema.proyecto1bolsadeempleo.logic.model.Habilidad;
import java.util.List;

@Service
public class HabilidadService {
    @Autowired
    private HabilidadRepository habilidadRepository;

    public Iterable<Habilidad> findAll() {
        return habilidadRepository.findAll();
    }

    public Habilidad findById(Integer id) {
        return habilidadRepository.findById(id).orElse(null);
    }

    public Habilidad save(Habilidad habilidad) {
        return habilidadRepository.save(habilidad);
    }

    public List<Habilidad> findByOferente(String identificacion) {
        return habilidadRepository.findByOferenteIdentificacion(identificacion);
    }

    public void deleteById(Integer id) {
        habilidadRepository.deleteById(id);
    }
}
