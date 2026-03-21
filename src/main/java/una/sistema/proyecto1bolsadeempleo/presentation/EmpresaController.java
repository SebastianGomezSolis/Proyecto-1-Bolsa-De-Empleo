package una.sistema.proyecto1bolsadeempleo.presentation;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import una.sistema.proyecto1bolsadeempleo.logic.ModeloDatos;
import una.sistema.proyecto1bolsadeempleo.logic.model.Empresa;
import una.sistema.proyecto1bolsadeempleo.logic.model.Puesto;

@Controller
@RequestMapping("/empresa")
<<<<<<<< HEAD:src/main/java/una/sistema/proyecto1bolsadeempleo/presentation/EmpresaController.java
public class EmpresaController {
========
public class EmpresaControler {
    @Autowired
    private HttpSession session;
>>>>>>>> 4b69ee2 (Cambios de nombres):src/main/java/una/sistema/proyecto1bolsadeempleo/presentation/EmpresaControler.java

    @Autowired
    private HttpSession session;

    @Autowired
    private ModeloDatos gestorDatos;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
<<<<<<<< HEAD:src/main/java/una/sistema/proyecto1bolsadeempleo/presentation/EmpresaController.java
        Empresa empresa = getEmpresaEnSesion();
========
        Empresa empresa = (Empresa) session.getAttribute("empresa");
>>>>>>>> 4b69ee2 (Cambios de nombres):src/main/java/una/sistema/proyecto1bolsadeempleo/presentation/EmpresaControler.java

        if (empresa == null) {
            return "redirect:/ingresar";
        }

        model.addAttribute("empresa", empresa);
        return "empresa/dashboard-empresa";
    }

    @GetMapping("/puestos")
    public String misPuestos(Model model) {
<<<<<<<< HEAD:src/main/java/una/sistema/proyecto1bolsadeempleo/presentation/EmpresaController.java
        Empresa empresa = getEmpresaEnSesion();
========
        Empresa empresa = (Empresa) session.getAttribute("empresa");
>>>>>>>> 4b69ee2 (Cambios de nombres):src/main/java/una/sistema/proyecto1bolsadeempleo/presentation/EmpresaControler.java

        if (empresa == null) {
            return "redirect:/ingresar";
        }

        model.addAttribute("empresa", empresa);
        model.addAttribute("puestos", gestorDatos.getPuestoService().findByEmpresa(empresa.getId()));
        return "empresa/misPuestos-empresa";
    }

    @GetMapping("/puestos/{id}/candidatos")
    public String verCandidatos(@PathVariable("id") Integer id, Model model) {
<<<<<<<< HEAD:src/main/java/una/sistema/proyecto1bolsadeempleo/presentation/EmpresaController.java
        Empresa empresa = getEmpresaEnSesion();
========
        Empresa empresa = (Empresa) session.getAttribute("empresa");
>>>>>>>> 4b69ee2 (Cambios de nombres):src/main/java/una/sistema/proyecto1bolsadeempleo/presentation/EmpresaControler.java

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

    private Empresa getEmpresaEnSesion() {
        return (Empresa) session.getAttribute("empresa");
    }
}