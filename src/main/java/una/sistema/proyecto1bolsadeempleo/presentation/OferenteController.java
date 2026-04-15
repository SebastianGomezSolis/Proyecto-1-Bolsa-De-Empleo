package una.sistema.proyecto1bolsadeempleo.presentation;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import una.sistema.proyecto1bolsadeempleo.logic.ModeloDatos;
import una.sistema.proyecto1bolsadeempleo.logic.model.*;
import una.sistema.proyecto1bolsadeempleo.logic.servicios.PasswordHash;
import una.sistema.proyecto1bolsadeempleo.logic.servicios.TipoCambioServicio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/oferente")
public class OferenteController {
    @Autowired
    private HttpSession session;

    @Autowired
    private ModeloDatos gestorDatos;

    @Autowired
    private PasswordHash passwordHash;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Oferente oferente = getOferenteEnSesion();

        if (oferente == null) {
            return "redirect:/ingresar";
        }

        model.addAttribute("oferente", oferente);
        model.addAttribute("activeNav", "dashboard");
        return "oferente/dashboard-oferente";
    }

    @GetMapping("/registro")
    public String registroOferenteForm(Model model) {
        model.addAttribute("oferente", new Oferente());
        model.addAttribute("nacionalidades", gestorDatos.getNacionalidadService().findAll());
        model.addAttribute("activeNav", "regOferente");
        return "publico/registrar-oferente-publica";
    }

    @PostMapping("/registro")
    public String registroOferenteGuardar(@ModelAttribute Oferente oferente, Model model) {
        try {
            if (oferente.getClave() == null || oferente.getClave().isBlank()) {
                model.addAttribute("error", "La clave es obligatoria.");
                model.addAttribute("oferente", oferente);
                model.addAttribute("nacionalidades", gestorDatos.getNacionalidadService().findAll());
                return "publico/registrar-oferente-publica";
            }

            if (oferente.getNacionalidad() == null || oferente.getNacionalidad().isBlank()) {
                model.addAttribute("error", "Debe seleccionar una nacionalidad.");
                model.addAttribute("oferente", oferente);
                model.addAttribute("nacionalidades", gestorDatos.getNacionalidadService().findAll());
                return "publico/registrar-oferente-publica";
            }

            if (gestorDatos.getNacionalidadService().findByIso(oferente.getNacionalidad()) == null) {
                model.addAttribute("error", "La nacionalidad seleccionada no es válida.");
                model.addAttribute("oferente", oferente);
                model.addAttribute("nacionalidades", gestorDatos.getNacionalidadService().findAll());
                return "publico/registrar-oferente-publica";
            }

            oferente.setClave(passwordHash.hash(oferente.getClave()));
            gestorDatos.getOferenteService().registrar(oferente);

            model.addAttribute("exito", "Registro exitoso. Espere la aprobación del administrador.");
            model.addAttribute("oferente", new Oferente());
            model.addAttribute("nacionalidades", gestorDatos.getNacionalidadService().findAll());
            model.addAttribute("activeNav", "regOferente");
            return "publico/registrar-oferente-publica";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("oferente", oferente);
            model.addAttribute("nacionalidades", gestorDatos.getNacionalidadService().findAll());
            return "publico/registrar-oferente-publica";
        }
    }

    @GetMapping("/habilidades")
    public String habilidades(
            @RequestParam(value = "actualId", required = false) Integer actualId,
            @RequestParam(value = "selId", required = false) Integer selId,
            Model model) {

        Oferente oferente = getOferenteEnSesion();

        if (oferente == null) {
            return "redirect:/ingresar";
        }

        Caracteristica actual = null;
        List<Caracteristica> subcategorias;
        List<Caracteristica> ruta = new ArrayList<>();

        if (actualId == null) {
            subcategorias = gestorDatos.getCaracteristicaService().findRaices();
        } else {
            actual = gestorDatos.getCaracteristicaService().findById(actualId);

            if (actual == null) {
                return "redirect:/oferente/habilidades";
            }

            subcategorias = gestorDatos.getCaracteristicaService().findHijos(actualId);
            ruta = construirRuta(actual);
        }

        model.addAttribute("oferente", oferente);
        model.addAttribute("habilidades",
                gestorDatos.getHabilidadService().findByOferente(oferente.getIdentificacion()));
        model.addAttribute("actual", actual);
        model.addAttribute("subcategorias", subcategorias);
        model.addAttribute("ruta", ruta);
        model.addAttribute("selId", selId);
        model.addAttribute("activeNav", "habilidades");

        return "oferente/habilidades-oferente";
    }

    @PostMapping("/habilidades/agregar")
    public String agregarHabilidad(
            @RequestParam("caracteristicaId") Integer caracteristicaId,
            @RequestParam("nivel") Integer nivel,
            RedirectAttributes redirectAttributes) {

        Oferente oferente = getOferenteEnSesion();

        if (oferente == null) {
            return "redirect:/ingresar";
        }

        Caracteristica caracteristica = gestorDatos.getCaracteristicaService().findById(caracteristicaId);

        if (caracteristica == null) {
            return "redirect:/oferente/habilidades";
        }

        if (!caracteristica.isHoja()) {
            return "redirect:/oferente/habilidades";
        }

        if (nivel == null || nivel < 1 || nivel > 5) {
            return "redirect:/oferente/habilidades";
        }

        List<Habilidad> habilidades = gestorDatos.getHabilidadService().findByOferente(oferente.getIdentificacion());

        for (Habilidad h : habilidades) {
            if (h.getCaracteristica() != null &&
                    h.getCaracteristica().getId().equals(caracteristicaId)) {
                redirectAttributes.addFlashAttribute("error",
                        "La habilidad \"" + caracteristica.getNombre() + "\" ya está registrada.");
                return "redirect:/oferente/habilidades";
            }
        }

        Habilidad habilidad = new Habilidad();
        habilidad.setOferente(oferente);
        habilidad.setCaracteristica(caracteristica);
        habilidad.setNivel(nivel);

        gestorDatos.getHabilidadService().save(habilidad);

        return "redirect:/oferente/habilidades";
    }

    @PostMapping("/habilidades/eliminar/{id}")
    public String eliminarHabilidad(@PathVariable("id") Integer id) {
        Oferente oferente = getOferenteEnSesion();

        if (oferente == null) {
            return "redirect:/ingresar";
        }

        Habilidad habilidad = gestorDatos.getHabilidadService().findById(id);

        if (habilidad == null) {
            return "redirect:/oferente/habilidades";
        }

        if (habilidad.getOferente() == null ||
                !habilidad.getOferente().getIdentificacion().equals(oferente.getIdentificacion())) {
            return "redirect:/oferente/habilidades";
        }

        gestorDatos.getHabilidadService().deleteById(id);
        return "redirect:/oferente/habilidades";
    }

    @GetMapping("/cv")
    public String cv(@RequestParam(value = "subir", required = false, defaultValue = "false") boolean mostrarSubir, Model model) {

        Oferente oferente = getOferenteEnSesion();

        if (oferente == null) {
            return "redirect:/ingresar";
        }

        model.addAttribute("oferente", oferente);
        model.addAttribute("mostrarSubir", mostrarSubir);
        model.addAttribute("activeNav", "cv");
        return "oferente/cv-oferente";
    }

    @PostMapping("/cv/subir")
    public String subirCv(@RequestParam("archivo") MultipartFile archivo, Model model) {
        Oferente oferente = getOferenteEnSesion();

        if (oferente == null) {
            return "redirect:/ingresar";
        }

        if (archivo == null || archivo.isEmpty()) {
            model.addAttribute("oferente", oferente);
            model.addAttribute("mostrarSubir", true);
            model.addAttribute("error", "Debe seleccionar un archivo PDF.");
            model.addAttribute("activeNav", "cv");
            return "oferente/cv-oferente";
        }

        String nombreArchivo = archivo.getOriginalFilename();

        if (nombreArchivo == null || !nombreArchivo.toLowerCase().endsWith(".pdf")) {
            model.addAttribute("oferente", oferente);
            model.addAttribute("mostrarSubir", true);
            model.addAttribute("error", "Solo se permiten archivos PDF.");
            model.addAttribute("activeNav", "cv");
            return "oferente/cv-oferente";
        }

        try {
            File directorio = new File(System.getProperty("user.dir"), "uploads/curriculos");

            if (!directorio.exists()) {
                directorio.mkdirs();
            }

            String idSanitizado = oferente.getIdentificacion().replaceAll("[^a-zA-Z0-9_-]", "_");

            File destino = new File(directorio, idSanitizado + ".pdf");

            String canonicalDir = directorio.getCanonicalPath() + File.separator;
            if (!destino.getCanonicalPath().startsWith(canonicalDir)) {
                throw new IOException("Ruta de archivo no permitida.");
            }

            archivo.transferTo(destino);

            String rutaRelativa = "/uploads/curriculos/" + idSanitizado + ".pdf";

            gestorDatos.getOferenteService().actualizarCurriculum(oferente.getIdentificacion(), rutaRelativa);

            Oferente actualizado = gestorDatos.getOferenteService().findById(oferente.getIdentificacion());
            session.setAttribute("oferente", actualizado);

            return "redirect:/oferente/cv";

        } catch (IOException e) {
            model.addAttribute("oferente", oferente);
            model.addAttribute("mostrarSubir", true);
            model.addAttribute("error", "Ocurrió un error al subir el archivo.");
            model.addAttribute("activeNav", "cv");
            return "oferente/cv-oferente";
        }
    }

    @GetMapping("/buscar")
    public String buscarPuestos(
            @RequestParam(required = false) List<Integer> caracteristicaIds,
            Model model) {

        Oferente oferente = getOferenteEnSesion();
        if (oferente == null) {
            return "redirect:/ingresar";
        }

        TipoCambio tipoCambio = new TipoCambioServicio().obtenerTipoCambio();

        model.addAttribute("oferente", oferente);
        model.addAttribute("raices", gestorDatos.getCaracteristicaService().findRaices());
        model.addAttribute("tipoCambio", tipoCambio);
        model.addAttribute("activeNav", "buscar");

        if (caracteristicaIds == null || caracteristicaIds.isEmpty()) {
            model.addAttribute("puestos", null);
        } else {
            List<Puesto> todos = gestorDatos.getPuestoService().findActivosAmbostiposPorCaracteristicas(caracteristicaIds);
            model.addAttribute("puestos", todos);
            model.addAttribute("caracteristicaIds", caracteristicaIds);
        }

        return "oferente/buscar-puesto";
    }

    private List<Caracteristica> construirRuta(Caracteristica actual) {
        List<Caracteristica> ruta = new ArrayList<>();

        while (actual != null) {
            ruta.add(0, actual);
            actual = actual.getPadre();
        }

        return ruta;
    }

    private Oferente getOferenteEnSesion() {
        return (Oferente) session.getAttribute("oferente");
    }
}
