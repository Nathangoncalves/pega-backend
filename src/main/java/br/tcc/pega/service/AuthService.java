package br.tcc.pega.service;

import br.tcc.pega.dto.AuthResponse;
import br.tcc.pega.dto.LoginRequest;
import br.tcc.pega.dto.RegisterRequest;
import br.tcc.pega.entity.User;
import br.tcc.pega.repository.UserRepository;
import br.tcc.pega.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço de autenticação: registro, login e carregamento de UserDetails para o Spring Security.
 * Implementa UserDetailsService para integração com o filtro JWT.
 */
@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // ===== UserDetailsService (usado pelo Spring Security) =====

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getSenhaHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

    // ===== Registro =====

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado: " + request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .nome(request.getNome())
                .senhaHash(passwordEncoder.encode(request.getSenha()))
                .role(request.getRole())
                .build();

        user = userRepository.save(user);
        String token = jwtUtil.generateToken(loadUserByUsername(user.getEmail()));
        return toAuthResponse(user, token);
    }

    // ===== Login =====

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        if (!passwordEncoder.matches(request.getSenha(), user.getSenhaHash())) {
            throw new BadCredentialsException("Senha incorreta");
        }

        String token = jwtUtil.generateToken(loadUserByUsername(user.getEmail()));
        return toAuthResponse(user, token);
    }

    // ===== Auxiliar =====

    private AuthResponse toAuthResponse(User user, String token) {
        return AuthResponse.builder()
                .token(token)
                .tipo("Bearer")
                .userId(user.getId())
                .nome(user.getNome())
                .email(user.getEmail())
                .role(user.getRole().name())
                .expiresIn(jwtUtil.getExpirationMs())
                .build();
    }
}
