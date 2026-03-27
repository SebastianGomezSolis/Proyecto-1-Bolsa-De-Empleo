package una.sistema.proyecto1bolsadeempleo.data;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import una.sistema.proyecto1bolsadeempleo.logic.model.Puesto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PuestoRepository extends CrudRepository<Puesto, Integer> {
    List<Puesto> findByEmpresaId(Integer empresaId);
    List<Puesto> findTop5ByTipoPublicacionAndActivoOrderByFechaRegistroDesc(String tipoPublicacion, Boolean activo);
    List<Puesto> findByTipoPublicacionAndActivo(String tipoPublicacion, Boolean activo);
    @Query("SELECT DISTINCT p FROM Puesto p " + "JOIN p.caracteristicas pc " + "WHERE p.tipoPublicacion = 'publico' " + "AND p.activo = true " + "AND pc.caracteristica.id IN :ids")
    List<Puesto> findPublicosPorCaracteristicas(@Param("ids") List<Integer> ids);
    List<Puesto> findByFechaRegistroBetween(Instant inicio, Instant fin);
}
