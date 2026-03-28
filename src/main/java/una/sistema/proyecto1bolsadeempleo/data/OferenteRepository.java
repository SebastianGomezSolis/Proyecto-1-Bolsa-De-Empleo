package una.sistema.proyecto1bolsadeempleo.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import una.sistema.proyecto1bolsadeempleo.logic.model.Oferente;

import java.util.List;
import java.util.Optional;

@Repository
public interface OferenteRepository extends CrudRepository<Oferente, String> {
    Optional<Oferente> findByCorreo(String correo);
    Optional<Oferente> findByIdentificacion(String cedula);
    boolean existsByCorreo(String correo);
    boolean existsByIdentificacion(String cedula);
    List<Oferente> findByAutorizado(Boolean autorizado);
}