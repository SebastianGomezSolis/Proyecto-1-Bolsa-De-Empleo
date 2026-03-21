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

    @GetMapping("/empresas/pendientes")
    public String empresasPendientes(Model model) {
        Administrador admin = getAdminEnSesion();
        if (admin == null) {
            return "redirect:/ingresar";
        }

        model.addAttribute("admin", admin);
        model.addAttribute("empresas", gestorDatos.getEmpresaService().findPendientes());
        return "administrador/empresas-pendientes-administrador";
    }

    @PostMapping("/empresas/autorizar/{id}")
    public String autorizarEmpresa(@PathVariable("id") Integer id) {
        Administrador admin = getAdminEnSesion();
        if (admin == null) {
            return "redirect:/ingresar";
        }

        gestorDatos.getEmpresaService().autorizar(id);
        return "redirect:/admin/empresas/pendientes";
    }

    @GetMapping("/oferentes/pendientes")
    public String oferentesPendientes(Model model) {
        Administrador admin = getAdminEnSesion();
        if (admin == null) {
            return "redirect:/ingresar";
        }

        model.addAttribute("admin", admin);
        model.addAttribute("oferentes", gestorDatos.getOferenteService().findPendientes());
        return "administrador/oferentes-pendientes-administrador";
    }

    @PostMapping("/oferentes/autorizar/{identificacion}")
    public String autorizarOferente(@PathVariable("identificacion") String identificacion) {
        Administrador admin = getAdminEnSesion();
        if (admin == null) {
            return "redirect:/ingresar";
        }

        gestorDatos.getOferenteService().autorizar(identificacion);
        return "redirect:/admin/oferentes/pendientes";
    }

    @GetMapping("/reportes")
    public String reportes(Model model) {
        Administrador admin = getAdminEnSesion();
        if (admin == null) {
            return "redirect:/ingresar";
        }

        model.addAttribute("admin", admin);
        return "administrador/reportes-administrador";
    }

    @GetMapping("/reportes/pdf")
    public void reportePuestosPdf(@RequestParam("mes") int mes,
                                  @RequestParam("anio") int anio,
                                  jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        Administrador admin = getAdminEnSesion();
        if (admin == null) {
            response.sendRedirect("/ingresar");
            return;
        }

        byte[] pdf = gestorDatos.getReporteService().generarPdfPuestosPorMesYAnio(mes, anio);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=reporte_puestos_" + mes + "_" + anio + ".pdf");
        response.getOutputStream().write(pdf);
        response.getOutputStream().flush();
    }

    // metodo auxiliar...
    private Administrador getAdminEnSesion() {
        return (Administrador) session.getAttribute("administrador");
    }
}
