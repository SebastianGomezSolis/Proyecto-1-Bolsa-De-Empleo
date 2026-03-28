package una.sistema.proyecto1bolsadeempleo.logic.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import una.sistema.proyecto1bolsadeempleo.data.OferenteRepository;
import una.sistema.proyecto1bolsadeempleo.logic.model.Oferente;
import java.util.List;

@Service("oferenteService")
public class OferenteService {
    @Autowired
    private OferenteRepository oferenteRepository;

    public Iterable<Oferente> findAll() {
        return oferenteRepository.findAll();
    }

    public Oferente findById(String identificacion) {
        return oferenteRepository.findById(identificacion).orElse(null);
    }

    public Oferente findByCorreo(String correo) {
        return oferenteRepository.findByCorreo(correo).orElse(null);
    }

    public Oferente findByIdentificacion(String identificacion) { return oferenteRepository.findByIdentificacion(identificacion).orElse(null); }

    public List<Oferente> findPendientes() {
        return oferenteRepository.findByAutorizado(false);
    }

    public Oferente save(Oferente oferente) {
        return oferenteRepository.save(oferente);
    }

    public Oferente registrar(Oferente oferente) throws Exception {
        if (oferenteRepository.existsByIdentificacion(oferente.getIdentificacion())) {
            throw new Exception("La identificacion ya existe.");
        }

        if (oferenteRepository.existsByCorreo(oferente.getCorreo())) {
            throw new Exception("El correo ya existe.");
        }

        oferente.setAutorizado(false);
        return oferenteRepository.save(oferente);
    }

    public Oferente autorizar(String identificacion) {
        Oferente oferente = findById(identificacion);
        if (oferente != null) {
            oferente.setAutorizado(true);
            oferenteRepository.save(oferente);
        }
        return oferente;
    }

    public Oferente actualizarCurriculum(String identificacion, String rutaPdf) {
        Oferente oferente = findById(identificacion);
        if (oferente != null) {
            oferente.setCurriculum(rutaPdf);
            oferenteRepository.save(oferente);
        }
        return oferente;
    }
}
