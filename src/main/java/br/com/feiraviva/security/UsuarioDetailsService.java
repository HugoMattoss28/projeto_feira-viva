package br.com.feiraviva.security;

import br.com.feiraviva.repository.ClienteRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final ClienteRepository clienteRepository;

    public UsuarioDetailsService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Cliente não encontrado: " + email));
        return new User(cliente.getEmail(), cliente.getSenhaHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + cliente.getPapel())));
    }
}

