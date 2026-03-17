package una.sistema.proyecto1bolsadeempleo.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import una.sistema.proyecto1bolsadeempleo.logic.model.Administrador;
import java.util.Optional;

@Repository
public interface AdministradorRepository extends CrudRepository<Administrador, String> {
    Optional<Administrador> findByCorreo(String correo);
    String Hola;
}
