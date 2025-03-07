package JaySports.controller;

import JaySports.authentication.ManagerUserSession;
import JaySports.dto.LoginData;
import JaySports.dto.RegistroData;
import JaySports.dto.UsuarioData;
import JaySports.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;

@Controller
public class LoginController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ManagerUserSession managerUserSession;

    @GetMapping("/")
    public String home(Model model) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (idUsuarioLogeado != null) {
            // Si el usuario está logueado, redirigir según su tipo
            if (managerUserSession.esAdministrador()) {
                return "redirect:/registrados";
            } else {
                return "redirect:/welcome";
            }
        }
        // Imágenes del carrusel
        List<String> carouselImages = List.of(
                "/images/image1.jpg",
                "/images/image2.jpg",
                "/images/image3.jpg"
        );

        // Imágenes de productos
        List<String> productImages = List.of(
                "/images/product1.jpg",
                "/images/product2.jpg",
                "/images/product3.jpg"
        );

        // Imágenes de "Descubre más"
        List<String> discoverImages = List.of(
                "/images/product4.jpg",
                "/images/product5.jpg",
                "/images/product6.jpg"
        );

        model.addAttribute("carouselImages", carouselImages);
        model.addAttribute("productImages", productImages);
        model.addAttribute("discoverImages", discoverImages);

        return "welcome";
    }


    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginData", new LoginData());
        return "formLogin";
    }

    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute LoginData loginData, Model model, HttpSession session) {
        // Llamada al servicio para comprobar si el login es correcto
        UsuarioService.LoginStatus loginStatus = usuarioService.login(loginData.geteMail(), loginData.getPassword());

        if (loginStatus == UsuarioService.LoginStatus.LOGIN_OK) {
            UsuarioData usuario = usuarioService.findByEmail(loginData.geteMail());

            // Guardamos el ID del usuario en la sesión y si es administrador
            managerUserSession.logearUsuario(usuario.getId(), usuario.getNombre(),usuario.isAdministrador());

            // Si el usuario es administrador, redirigimos a la lista de usuarios
            if (usuario.isAdministrador()) {
                return "redirect:/registrados";
            }

            // Si no es administrador, redirigimos a la nueva vista de bienvenida
            return "redirect:/welcome";
        } else if (loginStatus == UsuarioService.LoginStatus.USER_NOT_FOUND) {
            model.addAttribute("error", "No existe usuario");
        } else if (loginStatus == UsuarioService.LoginStatus.ERROR_PASSWORD) {
            model.addAttribute("error", "Contraseña incorrecta");
        } else if (loginStatus == UsuarioService.LoginStatus.USER_BLOCKED) {
            model.addAttribute("error", "Usuario bloqueado. Contacte con el administrador.");
        }

        return "formLogin";
    }


    @GetMapping("/registro")
    public String registroForm(Model model) {
        // Verificar si ya existe un administrador registrado
        boolean existeAdmin = usuarioService.existeAdministrador();
        model.addAttribute("existeAdmin", existeAdmin);
        model.addAttribute("registroData", new RegistroData());
        return "formRegistro";
    }

    @PostMapping("/registro")
    public String registroSubmit(@Valid RegistroData registroData, BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "formRegistro";
        }

        if (usuarioService.findByEmail(registroData.getEmail()) != null) {
            model.addAttribute("registroData", registroData);
            model.addAttribute("error", "El usuario " + registroData.getEmail() + " ya existe");
            return "formRegistro";
        }

        // Si ya existe un administrador, no permitimos registrar más
        if (registroData.isAdministrador() && usuarioService.existeAdministrador()) {
            model.addAttribute("error", "Ya existe un administrador registrado.");
            return "formRegistro";
        }

        UsuarioData usuario = new UsuarioData();
        usuario.setEmail(registroData.getEmail());
        usuario.setPassword(registroData.getPassword());
        usuario.setFechaNacimiento(registroData.getFechaNacimiento());
        usuario.setNombre(registroData.getNombre());
        usuario.setAdministrador(registroData.isAdministrador());

        usuarioService.registrar(usuario);
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        managerUserSession.logout();
        return "redirect:/login";
    }

    // Método para añadir atributos comunes a todas las vistas, como el usuario logueado
    @ModelAttribute
    public void addAttributes(Model model) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (idUsuarioLogeado != null) {
            UsuarioData usuario = usuarioService.findById(idUsuarioLogeado);
            if (usuario != null) {
                model.addAttribute("nombreUsuario", usuario.getNombre());
                model.addAttribute("usuarioId", usuario.getId());
            } else {
                model.addAttribute("nombreUsuario", null);
                model.addAttribute("usuarioId", null);
            }
        } else {
            model.addAttribute("nombreUsuario", null);
            model.addAttribute("usuarioId", null);
        }
    }
}
