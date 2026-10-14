package br.com.feiraviva;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class GerarHashTest {
    @Test
    void geraHash() {
        System.out.println(new BCryptPasswordEncoder().encode("123456"));
    }
}

