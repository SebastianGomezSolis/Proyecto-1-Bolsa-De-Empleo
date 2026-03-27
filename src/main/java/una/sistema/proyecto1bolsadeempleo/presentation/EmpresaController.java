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
import una.sistema.proyecto1bolsadeempleo.logic.servicios.PasswordHash;

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

    @Autowired
    private PasswordHash passwordHash;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Empresa empresa = getEmpresaEnSesion();

        if (empresa == null) {
            return "redirect:/ingresar";
        }

        model.addAttribute("empresa", empresa);
        model.addAttribute("activeNav", "dashboard");
        return "empresa/dashboard-empresa";
    }

    // En EmpresaController.java
    @GetMapping("/registro")
    public String registroEmpresaForm(Model model) {
        model.addAttribute("empresa", new Empresa());
        model.addAttribute("activeNav", "regEmpresa");
        return "publico/registrar-empresa-publica";
    }

    @PostMapping("/registro")
    public String registroEmpresaGuardar(@ModelAttribute Empresa empresa, Model model) {
        if (gestorDatos.getEmpresaService().findByCorreo(empresa.getCorreo()) != null) {
            model.addAttribute("error", "El correo ya está registrado.");
            model.addAttribute("empresa", empresa);
            return "publico/registrar-empresa-publica";
        }

        if (empresa.getClave() == null || empresa.getClave().isBlank()) {
            model.addAttribute("error", "La clave es obligatoria.");
            model.addAttribute("empresa", empresa);
            return "publico/registrar-empresa-publica";
        }

        empresa.setClave(passwordHash.hash(empresa.getClave()));
        empresa.setAutorizado(false);
        gestorDatos.getEmpresaService().save(empresa);

        model.addAttribute("exito", "Registro exitoso. Espere la aprobación del administrador.");
        model.addAttribute("empresa", new Empresa());
        model.addAttribute("activeNav", "regEmpresa");
        return "publico/registrar-empresa-publica";
    }

    @GetMapping("/puestos")
    public String misPuestos(Model model) {
        Empresa empresa = getEmpresaEnSesion();

        if (empresa == null) {
            return "redirect:/ingresar";
        }

        model.addAttribute("empresa", empresa);
        model.addAttribute("puestos", gestorDatos.getPuestoService().findByEmpresa(empresa.getId()));
        model.addAttribute("activeNav", "misPuestos");
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
        model.addAttribute("activeNav", "misPuestos");

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
        model.addAttribute("activeNav", "publicarPuesto");
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
            model.addAttribute("activeNav", "publicarPuesto");
            return "empresa/publicar-puesto-empresa";
        }

    }

    @GetMapping("/candidatos/{id}")
    public String verDetalleCandidato(@PathVariable("id") String id, Model model) {
        Empresa empresa = getEmpresaEnSesion();
        if (empresa == null) {
            return "redirect:/ingresar";
        }

        Oferente oferente = gestorDatos.getOferenteService().findById(id);
        if (oferente == null) {
            return "redirect:/empresa/puestos";
        }

        model.addAttribute("empresa", empresa);
        model.addAttribute("oferente", oferente);
        model.addAttribute("habilidades", gestorDatos.getHabilidadService().findByOferente(oferente.getIdentificacion()));
        model.addAttribute("activeNav", "misPuestos");
        return "empresa/ver-detalles-candidatos-empresa";
    }

    private Empresa getEmpresaEnSesion() {
        return (Empresa) session.getAttribute("empresa");
    }

    // DESACTIVAR PUESTO
    @PostMapping("/puestos/desactivar/{id}")
    public String desactivarPuesto(@PathVariable("id") Integer id) {
        Empresa empresa = getEmpresaEnSesion();
        if (empresa == null) return "redirect:/ingresar";

        Puesto puesto = gestorDatos.getPuestoService().findById(id);
        if (puesto == null) {
            return "redirect:/empresa/puestos";
        }

        if (puesto.getEmpresa() == null || !puesto.getEmpresa().getId().equals(empresa.getId()))
            return "redirect:/empresa/puestos";

        gestorDatos.getPuestoService().desactivar(id);
        return "redirect:/empresa/puestos";
    }

    // ACTIVAR PUESTO
    @PostMapping("/puestos/activar/{id}")
    public String activarPuesto(@PathVariable("id") Integer id) {
        Empresa empresa = getEmpresaEnSesion();
        if (empresa == null) return "redirect:/ingresar";

        Puesto puesto = gestorDatos.getPuestoService().findById(id);
        if (puesto == null) return "redirect:/empresa/puestos";
        if (puesto.getEmpresa() == null || !puesto.getEmpresa().getId().equals(empresa.getId()))
            return "redirect:/empresa/puestos";

        gestorDatos.getPuestoService().activar(id);
        return "redirect:/empresa/puestos";
    }
}
