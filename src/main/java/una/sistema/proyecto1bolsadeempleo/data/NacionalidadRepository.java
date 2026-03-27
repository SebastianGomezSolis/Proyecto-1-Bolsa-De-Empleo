package una.sistema.proyecto1bolsadeempleo.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import una.sistema.proyecto1bolsadeempleo.logic.model.Nacionalidad;

import java.util.List;

@Repository
public interface NacionalidadRepository extends CrudRepository<Nacionalidad, String> {
    List<Nacionalidad> findAllByOrderByNombreAsc();
}