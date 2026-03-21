package una.sistema.proyecto1bolsadeempleo.presentation;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import una.sistema.proyecto1bolsadeempleo.logic.ModeloDatos;
import una.sistema.proyecto1bolsadeempleo.logic.model.Oferente;

@Controller
@RequestMapping("/oferente")
public class OferenteController {
    @Autowired
    private HttpSession session;

    @Autowired
    private ModeloDatos gestorDatos;

    // ── DASHBOARD ─────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Oferente oferente = getOferenteEnSesion();

        if (oferente == null) {
            return "redirect:/ingresar";
        }

        model.addAttribute("oferente", oferente);
        return "oferente/dashboard-oferente";
    }

    // metodo auxiliar...
    private Oferente getOferenteEnSesion() {
        return (Oferente) session.getAttribute("oferente");
    }
}
