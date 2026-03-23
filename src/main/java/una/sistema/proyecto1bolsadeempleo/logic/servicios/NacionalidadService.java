package una.sistema.proyecto1bolsadeempleo.logic.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import una.sistema.proyecto1bolsadeempleo.data.NacionalidadRepository;
import una.sistema.proyecto1bolsadeempleo.logic.model.Nacionalidad;

import java.util.List;

@Service("nacionalidadService")
public class NacionalidadService {

    @Autowired
    private NacionalidadRepository nacionalidadRepository;

    public List<Nacionalidad> findAll() {
        return nacionalidadRepository.findAllByOrderByNombreAsc();
    }

    public Nacionalidad findByIso(String iso) {
        return nacionalidadRepository.findById(iso).orElse(null);
    }

    public long count() {
        return nacionalidadRepository.count();
    }

    public Nacionalidad save(Nacionalidad nacionalidad) {
        return nacionalidadRepository.save(nacionalidad);
    }
}