package una.sistema.proyecto1bolsadeempleo.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import una.sistema.proyecto1bolsadeempleo.logic.model.Habilidad;

import java.util.List;

@Repository
public interface HabilidadRepository extends CrudRepository<Habilidad, Integer> {
    // Todas las habilidades de un oferente
    List<Habilidad> findByOferenteIdentificacion(String identificacion);
}
