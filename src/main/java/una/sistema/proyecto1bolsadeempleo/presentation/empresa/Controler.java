package una.sistema.proyecto1bolsadeempleo.presentation.empresa;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import una.sistema.proyecto1bolsadeempleo.logic.model.Empresa;
import una.sistema.proyecto1bolsadeempleo.logic.model.Puesto;
import una.sistema.proyecto1bolsadeempleo.logic.servicios.MatchingService;
import una.sistema.proyecto1bolsadeempleo.logic.servicios.PuestoService;

@Controller
@RequestMapping("/empresa")
public class Controler {

    @Autowired
    private MatchingService matchingService;

    @Autowired
    private PuestoService puestoService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Empresa empresa = (Empresa) session.getAttribute("empresa");

        if (empresa == null) {
            return "redirect:/ingresar";
        }

        model.addAttribute("empresa", empresa);
        return "empresa/dashboard-empresa";
    }

    @GetMapping("/puestos")
    public String misPuestos(HttpSession session, Model model) {
        Empresa empresa = (Empresa) session.getAttribute("empresa");

        if (empresa == null) {
            return "redirect:/ingresar";
        }

        model.addAttribute("empresa", empresa);
        model.addAttribute("puestos", puestoService.findByEmpresa(empresa.getId()));
        return "empresa/misPuestos-empresa";
    }

    @GetMapping("/puestos/{id}/candidatos")
    public String verCandidatos(@PathVariable("id") Integer id, HttpSession session, Model model) {
        Empresa empresa = (Empresa) session.getAttribute("empresa");

        if (empresa == null) {
            return "redirect:/ingresar";
        }

        Puesto puesto = puestoService.findById(id);

        model.addAttribute("empresa", empresa);
        model.addAttribute("puesto", puesto);
        model.addAttribute("candidatos", matchingService.buscarCandidatosPorPuesto(id));

        return "empresa/buscar-candidatos-empresa";
    }
}