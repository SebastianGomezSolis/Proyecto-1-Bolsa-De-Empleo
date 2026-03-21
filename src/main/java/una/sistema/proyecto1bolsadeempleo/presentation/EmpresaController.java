package una.sistema.proyecto1bolsadeempleo.presentation;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import una.sistema.proyecto1bolsadeempleo.logic.ModeloDatos;
import una.sistema.proyecto1bolsadeempleo.logic.model.Empresa;
import una.sistema.proyecto1bolsadeempleo.logic.model.Puesto;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/empresa")
public class EmpresaController {
    @Autowired
    private HttpSession session;

    @Autowired
    private ModeloDatos gestorDatos;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Empresa empresa = getEmpresaEnSesion();

        if (empresa == null) {
            return "redirect:/ingresar";
        }

        model.addAttribute("empresa", empresa);
        return "empresa/dashboard-empresa";
    }

    @GetMapping("/puestos")
    public String misPuestos(Model model) {
        Empresa empresa = getEmpresaEnSesion();

        if (empresa == null) {
            return "redirect:/ingresar";
        }

        model.addAttribute("empresa", empresa);
        model.addAttribute("puestos", gestorDatos.getPuestoService().findByEmpresa(empresa.getId()));
        return "empresa/misPuestos-empresa";
    }

    @GetMapping("/puestos/{id}/candidatos")
    public String verCandidatos(@PathVariable("id") Integer id, Model model) {
        Empresa empresa = getEmpresaEnSesion();

        if (empresa == null) {
            return "redirect:/ingresar";
        }

        Puesto puesto = gestorDatos.getPuestoService().findById(id);

        if (puesto == null) {
            return "redirect:/empresa/puestos";
        }

        if (puesto.getEmpresa() == null || !puesto.getEmpresa().getId().equals(empresa.getId())) {
            return "redirect:/empresa/puestos";
        }

        model.addAttribute("empresa", empresa);
        model.addAttribute("puesto", puesto);
        model.addAttribute("candidatos", gestorDatos.getMatchingService().buscarCandidatosPorPuesto(id));

        return "empresa/buscar-candidatos-empresa";
    }

    @GetMapping("/puestos/publicar")
    public String publicarPuestoForm(Model model) {
        Empresa empresa = getEmpresaEnSesion();
        if (empresa == null) {
            return "redirect:/ingresar";
        }

        model.addAttribute("empresa", empresa);
        model.addAttribute("raices", gestorDatos.getCaracteristicaService().findRaices());
        return "empresa/publicar-puesto-empresa";
    }

    @PostMapping("/puestos/publicar")
    public String publicarPuestoGuardar(
            @RequestParam("descripcion") String descripcion,
            @RequestParam("salario") double salario,
            @RequestParam("tipoPublicacion") String tipoPublicacion,
            @RequestParam(value = "caracteristicaIds", required = false) List<Integer> caracteristicaIds,
            @RequestParam(value = "nivelRequerido", required = false) List<Integer> nivelRequerido,
            Model model) {

        Empresa empresa = getEmpresaEnSesion();
        if (empresa == null) {
            return "redirect:/ingresar";
        }

        if (descripcion == null || descripcion.isBlank()) {
            model.addAttribute("error", "La descripción es obligatoria.");
            model.addAttribute("empresa", empresa);
            model.addAttribute("raices", gestorDatos.getCaracteristicaService().findRaices());
            return "empresa/publicar-puesto-empresa";
        }

        Puesto puesto = new Puesto();
        puesto.setDescripcion(descripcion);
        puesto.setSalario(salario);
        puesto.setTipoPublicacion(tipoPublicacion);
        puesto.setActivo(true);
        puesto.setEmpresa(empresa);

        gestorDatos.getPuestoService().crear(puesto);

        return "redirect:/empresa/puestos";
    }

    private Empresa getEmpresaEnSesion() {
        return (Empresa) session.getAttribute("empresa");
    }
}
