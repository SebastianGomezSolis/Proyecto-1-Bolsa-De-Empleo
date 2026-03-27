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

    // ── DASHBOARD ─────────────────────────────
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
        response.setHeader("Content-Disposition",
                "inline; filename=reporte_puestos_" + mes + "_" + anio + ".pdf");
        response.getOutputStream().write(pdf);
        response.getOutputStream().flush();
    }

    // metodo auxiliar...
    private Administrador getAdminEnSesion() {
        return (Administrador) session.getAttribute("administrador");
    }

    @GetMapping("/caracteristicas")
    public String caracteristicas(@RequestParam(value = "actualId", required = false) Integer actualId, Model model) {

        // Se valida que haya un admin en sesión
        Administrador admin = getAdminEnSesion();
        if (admin == null) {
            return "redirect:/ingresar";
        }

        // Se declara la característica actual (la que el admin está viendo)
        Caracteristica actual = null;

        // Lista de subcategorías que se mostrarán en pantalla
        List<Caracteristica> subcategorias;

        // Si no se recibe actualId, se muestran las raíces del árbol
        if (actualId == null) {
            subcategorias = gestorDatos.getCaracteristicaService().findRaices();
        } else {
            // Si sí se recibe actualId, se busca la característica actual
            actual = gestorDatos.getCaracteristicaService().findById(actualId);

            // Si no existe, se vuelve a mostrar la raíz del árbol
            if (actual == null) {
                subcategorias = gestorDatos.getCaracteristicaService().findRaices();
            } else {
                // Se muestran los hijos del nodo actual
                subcategorias = gestorDatos.getCaracteristicaService().findHijos(actualId);
            }
        }

        // Se construye la ruta de navegación
        List<Caracteristica> ruta = construirRuta(actual);

        // Se envían todos los datos que el HTML necesita
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

        // Se valida que exista un administrador autenticado en sesión
        Administrador admin = getAdminEnSesion();
        if (admin == null) {
            return "redirect:/ingresar";
        }

        // Validación básica del nombre
        // Se usa addFlashAttribute para enviar un mensaje temporal entre redirects
        if (nombre == null || nombre.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "El nombre de la característica es obligatorio.");

            if (actualId != null) {
                return "redirect:/admin/caracteristicas?actualId=" + actualId;
            }
            return "redirect:/admin/caracteristicas";
        }

        String nombreLimpio = nombre.trim();

        // Validación para evitar duplicados en el mismo nivel del árbol
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

        // Se crea la nueva característica
        Caracteristica caracteristica = new Caracteristica();
        caracteristica.setNombre(nombreLimpio);

        // Si se seleccionó un padre, se busca y se asigna
        if (padreId != null) {
            Caracteristica padre = gestorDatos.getCaracteristicaService().findById(padreId);
            if (padre != null) {
                caracteristica.setPadre(padre);
            }
        }

        // Se guarda la característica
        gestorDatos.getCaracteristicaService().save(caracteristica);

        // Mensaje de éxito opcional
        redirectAttributes.addFlashAttribute("exito", "Característica creada correctamente.");

        // Se redirige al nivel actual del árbol para mantener el contexto
        if (actualId != null) {
            return "redirect:/admin/caracteristicas?actualId=" + actualId;
        }

        return "redirect:/admin/caracteristicas";
    }

    private List<Caracteristica> construirRuta(Caracteristica actual) {

        // Lista donde se guardará la ruta desde la raíz hasta el nodo actual
        List<Caracteristica> ruta = new ArrayList<>();

        // Se recorre hacia arriba usando la relación padre
        Caracteristica cursor = actual;
        while (cursor != null) {
            ruta.addFirst(cursor);
            cursor = cursor.getPadre();
        }

        return ruta;
    }
}
