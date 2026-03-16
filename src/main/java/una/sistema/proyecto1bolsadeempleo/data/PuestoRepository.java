package una.sistema.proyecto1bolsadeempleo.data;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import una.sistema.proyecto1bolsadeempleo.logic.model.Puesto;

import java.util.List;

@Repository
public interface PuestoRepository extends CrudRepository<Puesto, Integer> {
    // Puestos de una empresa
    List<Puesto> findByEmpresaId(Integer empresaId);
    // Los 5 puestos públicos más recientes (página principal)
    List<Puesto> findTop5ByTipoPublicacionAndActivoOrderByFechaRegistroDesc(String tipoPublicacion, Boolean activo);
    // Puestos públicos activos (búsqueda pública)
    List<Puesto> findByTipoPublicacionAndActivo(String tipoPublicacion, Boolean activo);
    // PuestoRepository.java
    @Query("SELECT DISTINCT p FROM Puesto p " + "JOIN p.caracteristicas pc " + "WHERE p.tipoPublicacion = 'publico' " + "AND p.activo = true " + "AND pc.caracteristica.id IN :ids")
    List<Puesto> findPublicosPorCaracteristicas(@Param("ids") List<Integer> ids);
}
