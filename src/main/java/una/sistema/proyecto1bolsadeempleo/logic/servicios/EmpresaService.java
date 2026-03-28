package una.sistema.proyecto1bolsadeempleo.logic.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import una.sistema.proyecto1bolsadeempleo.data.EmpresaRepository;
import una.sistema.proyecto1bolsadeempleo.logic.model.Empresa;
import java.util.List;

@Service("empresaService")
public class EmpresaService {
    @Autowired
    private EmpresaRepository empresaRepository;

    public Iterable<Empresa> findAll() {
        return empresaRepository.findAll();
    }

    public Empresa findById(Integer id) {
        return empresaRepository.findById(id).orElse(null);
    }

    public Empresa findByCorreo(String correo) {
        return empresaRepository.findByCorreo(correo).orElse(null);
    }

    public Empresa findByNombre(String nombre) {
        return empresaRepository.findByNombre(nombre).orElse(null);
    }

    public List<Empresa> findPendientes() {
        return empresaRepository.findByAutorizado(false);
    }

    public Empresa save(Empresa empresa) {
        return empresaRepository.save(empresa);
    }

    public Empresa registrar(Empresa empresa) {
        if (empresaRepository.existsByCorreo(empresa.getCorreo())) {
            throw new RuntimeException("El correo ya existe.");
        }

        if (empresaRepository.existsByNombre(empresa.getNombre())) {
            throw new RuntimeException("El nombre ya existe.");
        }

        empresa.setAutorizado(false);
        return empresaRepository.save(empresa);
    }

    public Empresa autorizar(Integer id) {
        Empresa empresa = findById(id);
        if (empresa != null) {
            empresa.setAutorizado(true);
            empresaRepository.save(empresa);
        }
        return empresa;
    }
}
