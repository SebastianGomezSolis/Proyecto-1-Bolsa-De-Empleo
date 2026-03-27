package una.sistema.proyecto1bolsadeempleo.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import una.sistema.proyecto1bolsadeempleo.logic.model.Caracteristica;

import java.util.List;

@Repository
public interface CaracteristicaRepository extends CrudRepository<Caracteristica, Integer> {
    List<Caracteristica> findByPadreIsNull();
    List<Caracteristica> findByPadreId(Integer padreId);
    boolean existsByNombreIgnoreCaseAndPadreId(String nombre, Integer padreId);
    boolean existsByNombreIgnoreCaseAndPadreIsNull(String nombre);
}
