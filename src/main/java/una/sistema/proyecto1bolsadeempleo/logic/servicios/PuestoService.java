package una.sistema.proyecto1bolsadeempleo.logic.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import una.sistema.proyecto1bolsadeempleo.data.PuestoRepository;
import una.sistema.proyecto1bolsadeempleo.logic.model.Caracteristica;
import una.sistema.proyecto1bolsadeempleo.logic.model.Empresa;
import una.sistema.proyecto1bolsadeempleo.logic.model.Puesto;
import una.sistema.proyecto1bolsadeempleo.logic.model.PuestoCaracteristica;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class PuestoService {
    @Autowired
    private PuestoRepository puestoRepository;

    @Autowired
    private CaracteristicaService caracteristicaService;

    @Autowired
    private PuestoCaracteristicaService puestoCaracteristicaService;

    public Puesto findById(Integer id) {
        return puestoRepository.findById(id).orElse(null);
    }

    public List<Puesto> findByEmpresa(Integer empresaId) {
        return puestoRepository.findByEmpresaId(empresaId);
    }

    public Puesto crear(Puesto puesto) {
        puesto.setActivo(true);
        puesto.setFechaRegistro(Instant.now());
        return puestoRepository.save(puesto);
    }

    @Transactional
    public List<Puesto> findUltimos5Publicos() {
        List<Puesto> puestos =
                puestoRepository.findTop5ByTipoPublicacionAndActivoOrderByFechaRegistroDesc("publico", true);

        puestos.forEach(p -> p.getCaracteristicas().size());

        return puestos;
    }

    public List<Puesto> findPublicosActivos() {
        return puestoRepository.findByTipoPublicacionAndActivo("publico", true);
    }

    public List<Puesto> findPorCaracteristicas(List<Integer> ids) {
        return puestoRepository.findPublicosPorCaracteristicas(ids);
    }

    public List<Puesto> findPorFechaRegistroEntre(Instant inicio, Instant fin) {
        return puestoRepository.findByFechaRegistroBetween(inicio, fin);
    }

    @Transactional
    public Puesto desactivar(Integer id) {
        Puesto puesto = findById(id);
        if (puesto != null) {
            puesto.setActivo(false);
            puestoRepository.save(puesto);
        }
        return puesto;
    }

    @Transactional
    public Puesto activar(Integer id) {
        Puesto puesto = findById(id);
        if (puesto != null) {
            puesto.setActivo(true);
            puestoRepository.save(puesto);
        }
        return puesto;
    }

    @Transactional
    public Puesto crearConCaracteristicas(String descripcion, Double salario,
                                          String tipoPublicacion,
                                          Empresa empresa,
                                          List<Integer> caracteristicaIds,
                                          Map<String, String> parametrosFormulario) {

        // Validación de seguridad: una empresa autenticada es obligatoria
        if (empresa == null) {
            throw new IllegalArgumentException("La empresa autenticada es obligatoria.");
        }

        // Validación básica de descripción
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripción del puesto es obligatoria.");
        }

        // Validación básica de salario
        if (salario == null || salario <= 0) {
            throw new IllegalArgumentException("El salario debe ser mayor que cero.");
        }

        // Si no mandan tipo, por defecto se usa público
        String tipo = (tipoPublicacion == null || tipoPublicacion.isBlank())
                ? "publico"
                : tipoPublicacion.trim().toLowerCase();

        // Solo se permiten los tipos definidos por el proyecto
        if (!tipo.equals("publico") && !tipo.equals("privado")) {
            throw new IllegalArgumentException("El tipo de publicación no es válido.");
        }

        // El puesto debe tener al menos una característica requerida
        if (caracteristicaIds == null || caracteristicaIds.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos una característica requerida.");
        }

        // Se crea y guarda primero el puesto principal
        Puesto puesto = new Puesto();
        puesto.setDescripcion(descripcion.trim());
        puesto.setSalario(salario);
        puesto.setTipoPublicacion(tipo);
        puesto.setActivo(true);
        puesto.setEmpresa(empresa);
        puesto.setFechaRegistro(Instant.now());

        // Guardamos el puesto para obtener su id y poder asociarle características
        puesto = puestoRepository.save(puesto);

        // Evita registrar duplicados si por alguna razón viene repetida una característica
        Set<Integer> caracteristicasProcesadas = new HashSet<>();

        for (Integer caracteristicaId : caracteristicaIds) {

            // Protección contra valores nulos o repetidos
            if (caracteristicaId == null || !caracteristicasProcesadas.add(caracteristicaId)) {
                continue;
            }

            // Se busca la característica en base de datos
            Caracteristica caracteristica = caracteristicaService.findById(caracteristicaId);

            if (caracteristica == null) {
                throw new IllegalArgumentException("Se seleccionó una característica que no existe.");
            }

            // El anexo permite seleccionar solo nodos finales del árbol
            if (!caracteristica.isHoja()) {
                throw new IllegalArgumentException(
                        "Solo se pueden seleccionar características finales (hojas) del árbol."
                );
            }

            // Cada nivel viaja en el formulario como nivel_{idCaracteristica}
            String nombreParametroNivel = "nivel_" + caracteristicaId;
            String nivelTexto = parametrosFormulario.get(nombreParametroNivel);

            if (nivelTexto == null || nivelTexto.isBlank()) {
                throw new IllegalArgumentException(
                        "Debe indicar el nivel requerido para cada característica seleccionada."
                );
            }

            int nivel;
            try {
                nivel = Integer.parseInt(nivelTexto);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("El nivel requerido debe ser un número válido.");
            }

            // El proyecto trabaja niveles de 1 a 5
            if (nivel < 1 || nivel > 5) {
                throw new IllegalArgumentException("El nivel requerido debe estar entre 1 y 5.");
            }

            // Se crea la relación entre el puesto y la característica requerida
            PuestoCaracteristica puestoCaracteristica = new PuestoCaracteristica();
            puestoCaracteristica.setPuesto(puesto);
            puestoCaracteristica.setCaracteristica(caracteristica);
            puestoCaracteristica.setNivelRequerido(nivel);

            // Se guarda la relación en la tabla puestocaracteristica
            puestoCaracteristicaService.save(puestoCaracteristica);
        }

        return puesto;
    }
}
