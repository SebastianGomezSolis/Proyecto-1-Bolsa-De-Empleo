package una.sistema.proyecto1bolsadeempleo.presentation;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import una.sistema.proyecto1bolsadeempleo.logic.ModeloDatos;
import una.sistema.proyecto1bolsadeempleo.logic.model.Administrador;
import una.sistema.proyecto1bolsadeempleo.logic.model.Caracteristica;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdministradorController {
    @Autowired
    private HttpSession session;

    @Autowired
    private ModeloDatos gestorDatos;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Administrador admin = getAdminEnSesion();

        if (admin == null) {
            return "redirect:/ingresar";
        }

        model.addAttribute("admin", admin);
        model.addAttribute("activeNav", "dashboard");
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
        model.addAttribute("activeNav", "empresasPendientes");
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
        model.addAttribute("activeNav", "oferentesPendientes");
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
        model.addAttribute("activeNav", "reportes");
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
        response.setHeader("Content-Disposition", "inline; filename=reporte_puestos_" + mes + "_" + anio + ".pdf");
        response.getOutputStream().write(pdf);
        response.getOutputStream().flush();
    }

    @GetMapping("/caracteristicas")
    public String caracteristicas(@RequestParam(value = "actualId", required = false) Integer actualId, Model model) {

        Administrador admin = getAdminEnSesion();
        if (admin == null) {
            return "redirect:/ingresar";
        }

        Caracteristica actual = null;

        List<Caracteristica> subcategorias;

        if (actualId == null) {
            subcategorias = gestorDatos.getCaracteristicaService().findRaices();
        } else {
            actual = gestorDatos.getCaracteristicaService().findById(actualId);

            if (actual == null) {
                subcategorias = gestorDatos.getCaracteristicaService().findRaices();
            } else {
                subcategorias = gestorDatos.getCaracteristicaService().findHijos(actualId);
            }
        }

        List<Caracteristica> ruta = construirRuta(actual);

        model.addAttribute("admin", admin);
        model.addAttribute("actual", actual);
        model.addAttribute("ruta", ruta);
        model.addAttribute("subcategorias", subcategorias);
        model.addAttribute("todas", gestorDatos.getCaracteristicaService().findAll());
        model.addAttribute("activeNav", "caracteristicas");
        return "administrador/caracteristicas-administrador";
    }

    @PostMapping("/caracteristicas/crear")
    public String crearCaracteristica(@RequestParam("nombre") String nombre,
                                      @RequestParam(value = "padreId", required = false) Integer padreId,
                                      @RequestParam(value = "actualId", required = false) Integer actualId,
                                      RedirectAttributes redirectAttributes) {

        Administrador admin = getAdminEnSesion();
        if (admin == null) {
            return "redirect:/ingresar";
        }

        if (nombre == null || nombre.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "El nombre de la característica es obligatorio.");

            if (actualId != null) {
                return "redirect:/admin/caracteristicas?actualId=" + actualId;
            }
            return "redirect:/admin/caracteristicas";
        }

        String nombreLimpio = nombre.trim();

        if (gestorDatos.getCaracteristicaService().existeEnMismoNivel(nombreLimpio, padreId)) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Ya existe una característica con ese nombre bajo el mismo padre."
            );

            if (actualId != null) {
                return "redirect:/admin/caracteristicas?actualId=" + actualId;
            }
            return "redirect:/admin/caracteristicas";
        }

        Caracteristica caracteristica = new Caracteristica();
        caracteristica.setNombre(nombreLimpio);

        if (padreId != null) {
            Caracteristica padre = gestorDatos.getCaracteristicaService().findById(padreId);
            if (padre != null) {
                caracteristica.setPadre(padre);
            }
        }

        gestorDatos.getCaracteristicaService().save(caracteristica);

        redirectAttributes.addFlashAttribute("exito", "Característica creada correctamente.");

        if (actualId != null) {
            return "redirect:/admin/caracteristicas?actualId=" + actualId;
        }

        return "redirect:/admin/caracteristicas";
    }

    private List<Caracteristica> construirRuta(Caracteristica actual) {
        List<Caracteristica> ruta = new ArrayList<>();

        Caracteristica cursor = actual;
        while (cursor != null) {
            ruta.add(0, cursor);
            cursor = cursor.getPadre();
        }

        return ruta;
    }

    private Administrador getAdminEnSesion() {
        return (Administrador) session.getAttribute("administrador");
    }
}
