//package una.sistema.proyecto1bolsadeempleo.logic.servicios;
//
//import una.sistema.proyecto1bolsadeempleo.data.*;
//import una.sistema.proyecto1bolsadeempleo.logic.model.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class CustomUserDetailsService implements UserDetailsService {
//
//    // Aquí van los repositorios de cada tipo de usuario
//    @Autowired private EmpresaRepository empresaRepository;
//    @Autowired private OferenteRepository oferenteRepository;
//    @Autowired private AdministradorRepository administradorRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
//
//        // 1. Buscar en Empresa
//        Optional<Empresa> empresa = empresaRepository.findByCorreo(correo);
//        if (empresa.isPresent()) {
//            if (!empresa.get().getAutorizado()) {
//                throw new UsernameNotFoundException("Empresa no autorizada");
//            }
//            return new User(correo, empresa.get().getClave(),
//                    List.of(new SimpleGrantedAuthority("ROLE_EMPRESA")));
//        }
//
//        // 2. Buscar en Oferente
//        Optional<Oferente> oferente = oferenteRepository.findByCorreo(correo);
//        if (oferente.isPresent()) {
//            if (!oferente.get().getAutorizado()) {
//                throw new UsernameNotFoundException("Oferente no autorizado");
//            }
//            return new User(correo, oferente.get().getClave(),
//                    List.of(new SimpleGrantedAuthority("ROLE_OFERENTE")));
//        }
//
//        // 3. Buscar en Administrador
//        Optional<Administrador> admin = administradorRepository.findByCorreo(correo);
//        if (admin.isPresent()) {
//            return new User(correo, admin.get().getClave(),
//                    List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
//        }
//
//        throw new UsernameNotFoundException("Usuario no encontrado: " + correo);
//    }
//}