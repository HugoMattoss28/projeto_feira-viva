package br.com.feiraviva.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey chave;
    private final long expiracaoMs;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-ms}") long expiracaoMs) {
        this.chave = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiracaoMs = expiracaoMs;
    }

    public String gerarToken(Long clienteId, String email, String papel) {
        var agora = new Date();
        return Jwts.builder()
                .subject(email)                              // sub
                .claim("clienteId", clienteId)               // claims customizados
                .claim("papel", papel)
                .issuedAt(agora)
                .expiration(new Date(agora.getTime() + expiracaoMs))
                .signWith(chave)                             // HS256
                .compact();
    }

    public Claims validarToken(String token) {
        return Jwts.parser()
                .verifyWith(chave)                           // verifica assinatura + expiração
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getExpiracaoMs() {
        return expiracaoMs;
    }
}

