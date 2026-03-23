/*package una.sistema.proyecto1bolsadeempleo.presentation.publico;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import una.sistema.proyecto1bolsadeempleo.logic.model.Caracteristica;
import una.sistema.proyecto1bolsadeempleo.logic.model.*;
import una.sistema.proyecto1bolsadeempleo.logic.servicios.PuestoService;
import java.util.List;

@Controller
public class PublicoController {
    @Autowired
    private PuestoService puestoService;

    // localhost:8080 → muestra pagina-principal
    @GetMapping("/")
    public String paginaPrincipal(Model model) {
        // Cuando tengas los servicios listos descomenta esta línea
        // model.addAttribute("puestos", puestoService.findUltimos5Publicos());

        // Para pruebas sin servicios, manda lista vacía
        model.addAttribute("puestos", List.of());
        return "publico/pagina-principal";
    }

    @GetMapping("/puestos/buscar")
    public String buscarPuestos(
            @RequestParam(required = false) List<Integer> caracteristicaIds,
            Model model) {

        // ── Características de prueba ──────────────────────────────
        Caracteristica lenguajes = new Caracteristica();
        lenguajes.setId(1);
        lenguajes.setNombre("Lenguajes de programación");

        Caracteristica java = new Caracteristica();
        java.setId(2);
        java.setNombre("Java");
        java.setPadre(lenguajes);

        Caracteristica csharp = new Caracteristica();
        csharp.setId(3);
        csharp.setNombre("C#");
        csharp.setPadre(lenguajes);

        Caracteristica webTech = new Caracteristica();
        webTech.setId(4);
        webTech.setNombre("Tecnologías Web");

        Caracteristica html = new Caracteristica();
        html.setId(5);
        html.setNombre("HTML");
        html.setPadre(webTech);

        lenguajes.setHijos(List.of(java, csharp));
        webTech.setHijos(List.of(html));

        model.addAttribute("caracteristicas", List.of(lenguajes, webTech));

        // ── Puestos de prueba ──────────────────────────────────────
        model.addAttribute("puestos", List.of());
        // model.addAttribute("caracteristicas", caracteristicaService.findAll()); // Cuando ya tengamos todo
        return "publico/buscar-puesto-publica";
    }

    @GetMapping("/registro/empresa")
    public String registroEmpresaForm(Model model) {
        model.addAttribute("empresa", new Empresa());
        return "publico/registrar-empresa-publica";
    }

    @PostMapping("/registro/empresa")
    public String registroEmpresaGuardar(@ModelAttribute Empresa empresa, Model model) {
        // Simulación: solo muestra éxito
        model.addAttribute("exito", "Registro exitoso. Espere la aprobación del administrador.");
        model.addAttribute("empresa", new Empresa());
        return "publico/registrar-empresa-publica";
    }

    @GetMapping("/registro/oferente")
    public String registroOferenteForm(Model model) {
        model.addAttribute("oferente", new Oferente());
        return "publico/registrar-oferente-publica";
    }

    @PostMapping("/registro/oferente")
    public String registroOferenteGuardar(@ModelAttribute Oferente oferente, Model model) {
        // Simulación: solo muestra éxito
        model.addAttribute("exito", "Registro exitoso. Espere la aprobación del administrador.");
        model.addAttribute("oferente", new Oferente());
        return "publico/registrar-oferente-publica";
    }

    // localhost:8080/login → muestra login
    @GetMapping("/login")
    public String login() {
        return "publico/login";
    }
}*/

package una.sistema.proyecto1bolsadeempleo.presentation;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import una.sistema.proyecto1bolsadeempleo.logic.ModeloDatos;
import una.sistema.proyecto1bolsadeempleo.logic.model.Administrador;
import una.sistema.proyecto1bolsadeempleo.logic.model.Empresa;
import una.sistema.proyecto1bolsadeempleo.logic.model.Oferente;
import una.sistema.proyecto1bolsadeempleo.logic.model.TipoCambio;
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

    // ── PÁGINA PRINCIPAL ──────────────────────────────────────
    @GetMapping("/")
    public String paginaPrincipal(Model model) {
        model.addAttribute("puestos", gestorDatos.getPuestoService().findUltimos5Publicos());
        TipoCambioServicio tcServicio = new TipoCambioServicio();
        TipoCambio tipoCambio = tcServicio.obtenerTipoCambio();
        model.addAttribute("tipoCambio", tipoCambio);
        return "publico/pagina-principal";
    }

    // ── BUSCAR PUESTOS ────────────────────────────────────────
    @GetMapping("/puestos/buscar")
    public String buscarPuestos(
            @RequestParam(required = false) List<Integer> caracteristicaIds,
            Model model) {
        model.addAttribute("caracteristicas", gestorDatos.getCaracteristicaService().findRaices());
        if (caracteristicaIds == null || caracteristicaIds.isEmpty()) {
            model.addAttribute("puestos", List.of());
        } else {
            model.addAttribute("puestos",
                    gestorDatos.getPuestoService().findPorCaracteristicas(caracteristicaIds));
        }
        TipoCambioServicio tcServicio = new TipoCambioServicio();
        TipoCambio tipoCambio = tcServicio.obtenerTipoCambio();
        model.addAttribute("tipoCambio", tipoCambio);
        return "publico/buscar-puesto-publica";
    }

    // ── REGISTRO EMPRESA ──────────────────────────────────────
    @GetMapping("/registro/empresa")
    public String registroEmpresaForm(Model model) {
        model.addAttribute("empresa", new Empresa());
        return "publico/registrar-empresa-publica";
    }

    @PostMapping("/registro/empresa")
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
        return "publico/registrar-empresa-publica";
    }

    // ── REGISTRO OFERENTE ─────────────────────────────────────
    @GetMapping("/registro/oferente")
    public String registroOferenteForm(Model model) {

        // Se cargan las nacionalidades desde la base de datos
        model.addAttribute("oferente", new Oferente());
        model.addAttribute("nacionalidades", gestorDatos.getNacionalidadService().findAll());

        return "publico/registrar-oferente-publica";
    }

    @PostMapping("/registro/oferente")
    public String registroOferenteGuardar(@ModelAttribute Oferente oferente, Model model) {

        if (gestorDatos.getOferenteService().findById(oferente.getIdentificacion()) != null) {
            model.addAttribute("error", "La identificación ya está registrada.");
            model.addAttribute("oferente", oferente);
            model.addAttribute("nacionalidades", gestorDatos.getNacionalidadService().findAll());
            return "publico/registrar-oferente-publica";
        }

        if (gestorDatos.getOferenteService().findByCorreo(oferente.getCorreo()) != null) {
            model.addAttribute("error", "El correo ya está registrado.");
            model.addAttribute("oferente", oferente);
            model.addAttribute("nacionalidades", gestorDatos.getNacionalidadService().findAll());
            return "publico/registrar-oferente-publica";
        }

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

        // Se valida que el ISO seleccionado exista realmente en la base
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

        return "publico/registrar-oferente-publica";
    }

    // ── LOGIN ─────────────────────────────────────────────────
    @GetMapping("/ingresar")
    public String login() {
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
        return "publico/login";
    }

    @GetMapping("/salir")
    public String salir(HttpSession session) {
        session.invalidate();
        return "redirect:/ingresar";
    }

}
