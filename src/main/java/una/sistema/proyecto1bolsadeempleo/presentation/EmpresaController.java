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
import java.util.Map;

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
            @RequestParam("salario") Double salario,
            @RequestParam("tipoPublicacion") String tipoPublicacion,
            @RequestParam(value = "caracteristicaIds", required = false) List<Integer> caracteristicaIds,
            @RequestParam Map<String, String> parametrosFormulario,
            Model model) {

        Empresa empresa = getEmpresaEnSesion();
        if (empresa == null) {
            return "redirect:/ingresar";
        }

        try {
            // El controlador delega la lógica completa de creación al servicio
            gestorDatos.getPuestoService().crearConCaracteristicas(
                    descripcion,
                    salario,
                    tipoPublicacion,
                    empresa,
                    caracteristicaIds,
                    parametrosFormulario
            );

            // Si salió bien, se redirige al listado de puestos de la empresa
            return "redirect:/empresa/puestos";

        } catch (IllegalArgumentException e){

            // Si hubo una validación de negocio, se regresa al formulario con el error
            model.addAttribute("error", e.getMessage());
            model.addAttribute("empresa", empresa);
            model.addAttribute("raices", gestorDatos.getCaracteristicaService().findRaices());
            return "empresa/publicar-puesto-empresa";
        }

    }

    private Empresa getEmpresaEnSesion() {
        return (Empresa) session.getAttribute("empresa");
    }

    @PostMapping("/puestos/desactivar/{id}")
    public String desactivarPuesto(@PathVariable("id") Integer id) {

        // Se obtiene la empresa autenticada desde sesión
        Empresa empresa = getEmpresaEnSesion();
        if (empresa == null) {
            return "redirect:/ingresar";
        }

        // Se busca el puesto por id
        Puesto puesto = gestorDatos.getPuestoService().findById(id);

        // Si el puesto no existe, se regresa al listado
        if (puesto == null) {
            return "redirect:/empresa/puestos";
        }

        // Validación de que solo la empresa dueña del puesto pueda eliminarlo
        if (puesto.getEmpresa() == null || !puesto.getEmpresa().getId().equals(empresa.getId())) {
            return "redirect:/empresa/puestos";
        }

        // Se delega al servicio la desactivación lógica del puesto
        gestorDatos.getPuestoService().desactivar(id);

        // Se vuelve al listado de puestos de la empresa
        return "redirect:/empresa/puestos";
    }
}
