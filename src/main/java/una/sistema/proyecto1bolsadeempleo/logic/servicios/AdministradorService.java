package una.sistema.proyecto1bolsadeempleo.logic.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import una.sistema.proyecto1bolsadeempleo.data.AdministradorRepository;
import una.sistema.proyecto1bolsadeempleo.logic.model.Administrador;

@Service
public class AdministradorService {
    @Autowired
    private AdministradorRepository administradorRepository;

    public Iterable<Administrador> findAll() {
        return administradorRepository.findAll();
    }

    public Administrador findById(String identificacion) {
        return administradorRepository.findById(identificacion).orElse(null);
    }

    public Administrador findByCorreo(String correo) {
        return administradorRepository.findByCorreo(correo).orElse(null);
    }
}
