package una.sistema.proyecto1bolsadeempleo.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import una.sistema.proyecto1bolsadeempleo.logic.model.Caracteristica;

import java.util.List;

@Repository
public interface CaracteristicaRepository extends CrudRepository<Caracteristica, Integer> {
    // Todas las raíces (sin padre)
    List<Caracteristica> findByPadreIsNull();
    // Hijos de un padre específico
    List<Caracteristica> findByPadreId(Integer padreId);

    // Verifica si ya existe una característica con el mismo nombre bajo el mismo padre
    boolean existsByNombreIgnoreCaseAndPadreId(String nombre, Integer padreId);

    // Verifica si ya existe una característica raíz con el mismo nombre
    boolean existsByNombreIgnoreCaseAndPadreIsNull(String nombre);

    // Se usan esos nombres para poder usar spring
}
