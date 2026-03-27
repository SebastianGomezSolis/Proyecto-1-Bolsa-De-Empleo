package una.sistema.proyecto1bolsadeempleo.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import una.sistema.proyecto1bolsadeempleo.logic.model.Empresa;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpresaRepository extends CrudRepository<Empresa, Integer> {
    Optional<Empresa> findByCorreo(String correo);
    Optional<Empresa> findByNombre(String nombre);
    List<Empresa> findByAutorizado(boolean b);
}
