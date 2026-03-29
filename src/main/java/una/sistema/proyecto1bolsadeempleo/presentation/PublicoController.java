package una.sistema.proyecto1bolsadeempleo.presentation;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import una.sistema.proyecto1bolsadeempleo.logic.ModeloDatos;
import una.sistema.proyecto1bolsadeempleo.logic.model.*;
import una.sistema.proyecto1bolsadeempleo.logic.servicios.*;

import java.util.List;

@Controller
public class PublicoController {
    @Autowired
    private HttpSession session;

    @Autowired
    private ModeloDatos gestorDatos;

    @Autowired
    private PasswordHash passwordHash;

    @GetMapping("/")
    public String paginaPrincipal(Model model) {
        model.addAttribute("puestos", gestorDatos.getPuestoService().findUltimos5Publicos());
        TipoCambioServicio tcServicio = new TipoCambioServicio();
        TipoCambio tipoCambio = tcServicio.obtenerTipoCambio();
        model.addAttribute("tipoCambio", tipoCambio);
        model.addAttribute("activeNav", "inicio");
        return "publico/pagina-principal";
    }

    @GetMapping("/ingresar")
    public String login(Model model) {
        model.addAttribute("activeNav", "login");
        return "publico/login";
    }

    @PostMapping("/ingresar")
    public String login(@RequestParam("correo") String correo,
                        @RequestParam("clave") String clave,
                        Model model) {

        Administrador admin = gestorDatos.getAdministradorService().findByCorreo(correo);
        if (admin != null) {
            if (admin.getClave().equals(clave)) {
                session.setAttribute("administrador", admin);
                return "redirect:/admin/dashboard";
            }

            model.addAttribute("error", "Usuario o contraseña incorrectos");
            return "publico/login";
        }

        Oferente oferente = gestorDatos.getOferenteService().findByCorreo(correo);
        if (oferente != null) {
            if (!passwordHash.verify(clave, oferente.getClave())) {
                model.addAttribute("error", "Usuario o contraseña incorrectos");
                return "publico/login";
            }

            if (!oferente.getAutorizado()) {
                model.addAttribute("error", "El oferente aún no ha sido autorizado");
                return "publico/login";
            }

            session.setAttribute("oferente", oferente);
            return "redirect:/oferente/dashboard";
        }

        Empresa empresa = gestorDatos.getEmpresaService().findByCorreo(correo);
        if (empresa != null) {
            if (!passwordHash.verify(clave, empresa.getClave())) {
                model.addAttribute("error", "Usuario o contraseña incorrectos");
                return "publico/login";
            }

            if (!empresa.getAutorizado()) {
                model.addAttribute("error", "La empresa aún no ha sido autorizada");
                return "publico/login";
            }

            session.setAttribute("empresa", empresa);
            return "redirect:/empresa/dashboard";
        }

        model.addAttribute("error", "Usuario o contraseña incorrectos");
        model.addAttribute("activeNav", "login");
        return "publico/login";
    }

    @GetMapping("/salir")
    public String salir(HttpSession session) {
        session.invalidate();
        return "redirect:/ingresar";
    }

}
