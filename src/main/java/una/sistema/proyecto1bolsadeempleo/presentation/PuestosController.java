package una.sistema.proyecto1bolsadeempleo.presentation;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import una.sistema.proyecto1bolsadeempleo.logic.ModeloDatos;
import una.sistema.proyecto1bolsadeempleo.logic.model.Empresa;
import una.sistema.proyecto1bolsadeempleo.logic.model.Oferente;
import una.sistema.proyecto1bolsadeempleo.logic.model.Puesto;
import una.sistema.proyecto1bolsadeempleo.logic.model.TipoCambio;
import una.sistema.proyecto1bolsadeempleo.logic.servicios.TipoCambioServicio;

import java.util.List;
import java.util.Map;

@Controller
public class PuestosController {
    @Autowired
    private HttpSession session;

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

    @GetMapping("/empresa/puestos")
    public String misPuestos(Model model) {
        Empresa empresa = getEmpresaEnSesion();
        if (empresa == null) return "redirect:/ingresar";

        model.addAttribute("empresa", empresa);
        model.addAttribute("puestos", gestorDatos.getPuestoService().findByEmpresa(empresa.getId()));
        model.addAttribute("activeNav", "misPuestos");
        return "empresa/misPuestos-empresa";
    }

    @GetMapping("/empresa/puestos/{id}/candidatos")
    public String verCandidatos(@PathVariable("id") Integer id, Model model) {
        Empresa empresa = getEmpresaEnSesion();
        if (empresa == null) return "redirect:/ingresar";

        Puesto puesto = gestorDatos.getPuestoService().findById(id);
        if (puesto == null) return "redirect:/empresa/puestos";
        if (puesto.getEmpresa() == null || !puesto.getEmpresa().getId().equals(empresa.getId()))
            return "redirect:/empresa/puestos";

        model.addAttribute("empresa", empresa);
        model.addAttribute("puesto", puesto);
        model.addAttribute("candidatos", gestorDatos.getMatchingService().buscarCandidatosPorPuesto(id));
        model.addAttribute("activeNav", "misPuestos");
        return "empresa/buscar-candidatos-empresa";
    }

    @GetMapping("/empresa/puestos/publicar")
    public String publicarPuestoForm(Model model) {
        Empresa empresa = getEmpresaEnSesion();
        if (empresa == null) return "redirect:/ingresar";

        model.addAttribute("empresa", empresa);
        model.addAttribute("raices", gestorDatos.getCaracteristicaService().findRaices());
        model.addAttribute("activeNav", "publicarPuesto");
        return "empresa/publicar-puesto-empresa";
    }

    @PostMapping("/empresa/puestos/publicar")
    public String publicarPuestoGuardar(
            @RequestParam("descripcion") String descripcion,
            @RequestParam("salario") Double salario,
            @RequestParam("tipoPublicacion") String tipoPublicacion,
            @RequestParam(value = "caracteristicaIds", required = false) List<Integer> caracteristicaIds,
            @RequestParam Map<String, String> parametrosFormulario,
            Model model) {

        Empresa empresa = getEmpresaEnSesion();
        if (empresa == null) return "redirect:/ingresar";

        try {
            gestorDatos.getPuestoService().crearConCaracteristicas(descripcion, salario, tipoPublicacion, empresa, caracteristicaIds, parametrosFormulario);
            return "redirect:/empresa/puestos";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("empresa", empresa);
            model.addAttribute("raices", gestorDatos.getCaracteristicaService().findRaices());
            model.addAttribute("activeNav", "publicarPuesto");
            return "empresa/publicar-puesto-empresa";
        }
    }

    @GetMapping("/empresa/candidatos/{id}")
    public String verDetalleCandidato(@PathVariable("id") String id, @RequestParam("puestoId") Integer puestoId, Model model) {
        Empresa empresa = getEmpresaEnSesion();
        if (empresa == null) return "redirect:/ingresar";

        Oferente oferente = gestorDatos.getOferenteService().findById(id);
        if (oferente == null) return "redirect:/empresa/puestos";

        model.addAttribute("empresa", empresa);
        model.addAttribute("oferente", oferente);
        model.addAttribute("habilidades", gestorDatos.getHabilidadService().findByOferente(oferente.getIdentificacion()));
        model.addAttribute("puesto", gestorDatos.getPuestoService().findById(puestoId));
        model.addAttribute("activeNav", "misPuestos");
        return "empresa/ver-detalles-candidatos-empresa";
    }

    @PostMapping("/empresa/puestos/desactivar/{id}")
    public String desactivarPuesto(@PathVariable("id") Integer id) {
        Empresa empresa = getEmpresaEnSesion();
        if (empresa == null) return "redirect:/ingresar";

        Puesto puesto = gestorDatos.getPuestoService().findById(id);
        if (puesto == null || puesto.getEmpresa() == null || !puesto.getEmpresa().getId().equals(empresa.getId()))
            return "redirect:/empresa/puestos";

        gestorDatos.getPuestoService().desactivar(id);
        return "redirect:/empresa/puestos";
    }

    @PostMapping("/empresa/puestos/activar/{id}")
    public String activarPuesto(@PathVariable("id") Integer id) {
        Empresa empresa = getEmpresaEnSesion();
        if (empresa == null) return "redirect:/ingresar";

        Puesto puesto = gestorDatos.getPuestoService().findById(id);
        if (puesto == null || puesto.getEmpresa() == null || !puesto.getEmpresa().getId().equals(empresa.getId()))
            return "redirect:/empresa/puestos";

        gestorDatos.getPuestoService().activar(id);
        return "redirect:/empresa/puestos";
    }

    private Empresa getEmpresaEnSesion() {
        return (Empresa) session.getAttribute("empresa");
    }
}