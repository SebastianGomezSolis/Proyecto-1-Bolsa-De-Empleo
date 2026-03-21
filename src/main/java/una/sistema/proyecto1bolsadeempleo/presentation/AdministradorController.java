package una.sistema.proyecto1bolsadeempleo.presentation;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import una.sistema.proyecto1bolsadeempleo.logic.ModeloDatos;
import una.sistema.proyecto1bolsadeempleo.logic.model.Administrador;

@Controller
@RequestMapping("/admin")
public class AdministradorController {

    @Autowired
    private HttpSession session;

    @Autowired
    private ModeloDatos gestorDatos;

    // ── DASHBOARD ─────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Administrador admin = getAdminEnSesion();

        if (admin == null) {
            return "redirect:/ingresar";
        }

        model.addAttribute("admin", admin);
        return "administrador/dashboard-administrador";
    }

    // metodo auxiliar...
    private Administrador getAdminEnSesion() {
        return (Administrador) session.getAttribute("admin");
    }
}
