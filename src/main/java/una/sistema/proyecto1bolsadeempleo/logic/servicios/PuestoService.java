package una.sistema.proyecto1bolsadeempleo.logic.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import una.sistema.proyecto1bolsadeempleo.data.PuestoRepository;
import una.sistema.proyecto1bolsadeempleo.logic.model.Puesto;
import java.time.Instant;
import java.util.List;

@Service("puestoService")
public class PuestoService {
    @Autowired
    private PuestoRepository puestoRepository;

    public Puesto findById(Integer id) {
        return puestoRepository.findById(id).orElse(null);
    }

    public List<Puesto> findByEmpresa(Integer empresaId) {
        return puestoRepository.findByEmpresaId(empresaId);
    }

    public Puesto save(Puesto puesto) {
        puesto.setActivo(true);
        puesto.setFechaRegistro(Instant.now());
        return puestoRepository.save(puesto);
    }

    @Transactional
    public List<Puesto> findUltimos5Publicos() {
        List<Puesto> puestos = puestoRepository
                .findTop5ByTipoPublicacionAndActivoOrderByFechaRegistroDesc("publico", true);
        puestos.forEach(p -> p.getCaracteristicas().size());
        return puestos;
    }

    public List<Puesto> findPublicosActivos() {
        return puestoRepository.findByTipoPublicacionAndActivo("publico", true);
    }

    public List<Puesto> findPorCaracteristicas(List<Integer> ids) {
        return puestoRepository.findPublicosPorCaracteristicas(ids);
    }

    public Puesto desactivar(Integer id) {
        Puesto puesto = findById(id);
        if (puesto != null) {
            puesto.setActivo(false);
            puestoRepository.save(puesto);
        }
        return puesto;
    }
}
