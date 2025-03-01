package JaySports.service;

import JaySports.dto.UsuarioData;
import JaySports.model.Carrito;
import JaySports.model.Usuario;
import JaySports.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    public enum LoginStatus {LOGIN_OK, USER_NOT_FOUND, ERROR_PASSWORD, USER_BLOCKED}

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CarritoService carritoService;

    // Método para el login del usuario
    @Transactional(readOnly = true)
    public LoginStatus login(String eMail, String password) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(eMail);

        if (!usuario.isPresent()) {
            return LoginStatus.USER_NOT_FOUND;
        } else if (usuario.get().isBloqueado()) {
            return LoginStatus.USER_BLOCKED;
        } else if (!usuario.get().getPassword().equals(password)) {
            return LoginStatus.ERROR_PASSWORD;
        } else {
            return LoginStatus.LOGIN_OK;
        }
    }

    // Método para registrar un nuevo usuario
    @Transactional
    public UsuarioData registrar(UsuarioData usuarioData) {
        Optional<Usuario> usuarioBD = usuarioRepository.findByEmail(usuarioData.getEmail());
        if (usuarioBD.isPresent()) {
            throw new UsuarioServiceException("El usuario " + usuarioData.getEmail() + " ya está registrado");
        } else if (usuarioData.getEmail() == null) {
            throw new UsuarioServiceException("El usuario no tiene email");
        } else if (usuarioData.getPassword() == null) {
            throw new UsuarioServiceException("El usuario no tiene password");
        }

        if (usuarioData.isAdministrador() && existeAdministrador()) {
            throw new UsuarioServiceException("Ya existe un administrador registrado.");
        }

        Usuario usuarioNuevo = modelMapper.map(usuarioData, Usuario.class);
        usuarioNuevo = usuarioRepository.save(usuarioNuevo);

        // Crear un carrito vacío para el nuevo usuario
        carritoService.obtenerCarritoPorUsuario(usuarioNuevo);

        return modelMapper.map(usuarioNuevo, UsuarioData.class);
    }

    // Método para actualizar un usuario existente
    @Transactional
    public UsuarioData actualizarUsuario(Long id, UsuarioData usuarioData) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioServiceException("No existe usuario con id " + id));

        usuarioExistente.setNombre(usuarioData.getNombre());
        usuarioExistente.setEmail(usuarioData.getEmail());
        usuarioExistente.setPassword(usuarioData.getPassword());
        usuarioExistente.setFechaNacimiento(usuarioData.getFechaNacimiento());
        usuarioExistente.setAdministrador(usuarioData.isAdministrador());

        usuarioExistente = usuarioRepository.save(usuarioExistente);
        return modelMapper.map(usuarioExistente, UsuarioData.class);
    }

    // Método para eliminar un usuario por ID
    @Transactional
    public void eliminarUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsuarioServiceException("No existe usuario con id " + idUsuario));

        // Eliminar el carrito asociado
        carritoService.eliminarCarritoPorUsuario(usuario);

        usuarioRepository.delete(usuario);
    }

    // Método para encontrar un usuario por su email
    @Transactional(readOnly = true)
    public UsuarioData findByEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        return usuario != null ? modelMapper.map(usuario, UsuarioData.class) : null;
    }

    // Método para encontrar un usuario por su ID
    @Transactional(readOnly = true)
    public UsuarioData findById(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        return usuario != null ? modelMapper.map(usuario, UsuarioData.class) : null;
    }

    // Método para obtener la lista de todos los usuarios
    @Transactional(readOnly = true)
    public List<UsuarioData> findAllUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios.stream()
                .map(usuario -> modelMapper.map(usuario, UsuarioData.class))
                .collect(Collectors.toList());
    }

    // Método para obtener una lista paginada de usuarios
    @Transactional(readOnly = true)
    public Page<UsuarioData> findAllUsuariosPaginados(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Usuario> usuariosPage = usuarioRepository.findAll(pageable);

        // Mapear cada entidad Usuario a UsuarioData
        return usuariosPage.map(usuario -> modelMapper.map(usuario, UsuarioData.class));
    }

    // Método para comprobar si ya existe un administrador en el sistema
    @Transactional(readOnly = true)
    public boolean existeAdministrador() {
        return usuarioRepository.findAll()
                .stream()
                .anyMatch(Usuario::isAdministrador);
    }

    // Método para bloquear o desbloquear un usuario
    @Transactional
    public void cambiarEstadoBloqueoUsuario(Long idUsuario, boolean bloquear) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsuarioServiceException("No existe usuario con id " + idUsuario));
        usuario.setBloqueado(bloquear);
        usuarioRepository.save(usuario);
    }

    // Método para verificar si un usuario está bloqueado
    @Transactional(readOnly = true)
    public boolean isUsuarioBloqueado(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        return usuario != null && usuario.isBloqueado();
    }

    // Método para saber si el usuario logueado es administrador
    @Transactional(readOnly = true)
    public boolean esAdministrador(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        return usuario != null && usuario.isAdministrador();
    }

    /**
     * Obtener un usuario por su ID.
     *
     * @param id ID del usuario.
     * @return Usuario encontrado o lanza una excepción si no existe.
     */
    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
    }
}
