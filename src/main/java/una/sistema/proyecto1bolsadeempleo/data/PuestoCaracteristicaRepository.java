package una.sistema.proyecto1bolsadeempleo.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import una.sistema.proyecto1bolsadeempleo.logic.model.PuestoCaracteristica;

import java.util.List;

@Repository
public interface PuestoCaracteristicaRepository extends CrudRepository<PuestoCaracteristica, Integer> {
    List<PuestoCaracteristica> findByPuestoId(Integer puestoId);
}
