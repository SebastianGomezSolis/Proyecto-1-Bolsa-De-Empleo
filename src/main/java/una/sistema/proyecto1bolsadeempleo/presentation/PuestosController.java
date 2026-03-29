package una.sistema.proyecto1bolsadeempleo.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import una.sistema.proyecto1bolsadeempleo.logic.ModeloDatos;
import una.sistema.proyecto1bolsadeempleo.logic.model.Puesto;
import una.sistema.proyecto1bolsadeempleo.logic.model.TipoCambio;
import una.sistema.proyecto1bolsadeempleo.logic.servicios.TipoCambioServicio;

import java.util.List;

@Controller
public class PuestosController {

    @Autowired
    private ModeloDatos gestorDatos;

    @GetMapping("/puestos/buscar")
    public String buscarPuestos(
            @RequestParam(required = false) List<Integer> caracteristicaIds,
            Model model) {

        model.addAttribute("raices", gestorDatos.getCaracteristicaService().findRaices());
        model.addAttribute("caracteristicaIds", caracteristicaIds);

        TipoCambioServicio tcServicio = new TipoCambioServicio();
        TipoCambio tipoCambio = tcServicio.obtenerTipoCambio();
        model.addAttribute("tipoCambio", tipoCambio);

        if (caracteristicaIds == null || caracteristicaIds.isEmpty()) {
            model.addAttribute("puestos", null);
        } else {
            List<Puesto> puestos = gestorDatos.getPuestoService().findPublicosActivos().stream()
                    .filter(p -> p.getCaracteristicas().stream()
                            .anyMatch(pc -> caracteristicaIds.contains(pc.getCaracteristica().getId())))
                    .toList();

            model.addAttribute("puestos", puestos);
        }

        model.addAttribute("activeNav", "buscar");

        return "publico/buscar-puesto-publico";
    }
}