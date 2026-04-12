package una.sistema.proyecto1bolsadeempleo.presentation;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import una.sistema.proyecto1bolsadeempleo.logic.ModeloDatos;
import una.sistema.proyecto1bolsadeempleo.logic.model.Empresa;
import una.sistema.proyecto1bolsadeempleo.logic.servicios.PasswordHash;

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

    @GetMapping("/registro")
    public String registroEmpresaForm(Model model) {
        model.addAttribute("empresa", new Empresa());
        model.addAttribute("activeNav", "regEmpresa");
        return "publico/registrar-empresa-publica";
    }

    @PostMapping("/registro")
    public String registroEmpresaGuardar(@ModelAttribute Empresa empresa, Model model) {
        try {
            if (empresa.getClave() == null || empresa.getClave().isBlank()) {
                model.addAttribute("error", "La clave es obligatoria.");
                model.addAttribute("empresa", empresa);
                return "publico/registrar-empresa-publica";
            }

            empresa.setClave(passwordHash.hash(empresa.getClave()));
            gestorDatos.getEmpresaService().registrar(empresa);

            model.addAttribute("exito", "Registro exitoso. Espere la aprobación del administrador.");
            model.addAttribute("empresa", new Empresa());
            model.addAttribute("activeNav", "regEmpresa");
            return "publico/registrar-empresa-publica";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("empresa", empresa);
            return "publico/registrar-empresa-publica";
        }
    }

    private Empresa getEmpresaEnSesion() {
        return (Empresa) session.getAttribute("empresa");
    }
}
