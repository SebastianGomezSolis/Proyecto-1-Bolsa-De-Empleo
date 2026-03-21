package una.sistema.proyecto1bolsadeempleo.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import una.sistema.proyecto1bolsadeempleo.logic.servicios.CaracteristicaService;
import una.sistema.proyecto1bolsadeempleo.logic.servicios.EmpresaService;
import una.sistema.proyecto1bolsadeempleo.logic.servicios.HabilidadService;
import una.sistema.proyecto1bolsadeempleo.logic.servicios.MatchingService;
import una.sistema.proyecto1bolsadeempleo.logic.servicios.OferenteService;
import una.sistema.proyecto1bolsadeempleo.logic.servicios.PuestoService;

@Component
public class ModeloDatos {

    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private OferenteService oferenteService;

    @Autowired
    private PuestoService puestoService;

    @Autowired
    private MatchingService matchingService;

    @Autowired
    private CaracteristicaService caracteristicaService;

    @Autowired
    private HabilidadService habilidadService;

    public EmpresaService getEmpresaService() {
        return empresaService;
    }

    public OferenteService getOferenteService() {
        return oferenteService;
    }

    public PuestoService getPuestoService() {
        return puestoService;
    }

    public MatchingService getMatchingService() {
        return matchingService;
    }

    public CaracteristicaService getCaracteristicaService() {
        return caracteristicaService;
    }

    public HabilidadService getHabilidadService() {
        return habilidadService;
    }
}