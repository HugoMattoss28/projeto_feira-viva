package br.com.feiraviva.controller;

import br.com.feiraviva.dto.LoginDTO;
import br.com.feiraviva.dto.TokenResponseDTO;
import br.com.feiraviva.repository.ClienteRepository;
import br.com.feiraviva.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Login e geração de token JWT")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ClienteRepository clienteRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          ClienteRepository clienteRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.clienteRepository = clienteRepository;
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar cliente", description = "Valida e-mail e senha (BCrypt) e retorna um token JWT assinado.")
    @ApiResponse(responseCode = "200", description = "Login realizado com sucesso")
    @ApiResponse(responseCode = "400", description = "DTO inválido")
    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    public TokenResponseDTO login(@Valid @RequestBody LoginDTO dto) {
        // valida credenciais (BCrypt via UsuarioDetailsService); lança BadCredentials se errado
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.email(), dto.senha()));

        var cliente = clienteRepository.findByEmail(dto.email()).orElseThrow();
        var token = jwtService.gerarToken(cliente.getId(), cliente.getEmail(), cliente.getPapel());

        return new TokenResponseDTO(token, "Bearer",
                System.currentTimeMillis() + jwtService.getExpiracaoMs(),
                cliente.getId(), cliente.getEmail());
    }
}

