package com.visit.jw_ls_maps_visit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import com.visit.jw_ls_maps_visit.model.ContaUsuario;
import com.visit.jw_ls_maps_visit.repository.ContaUsuarioRepository;

@Configuration
public class BootstrapData {

    @Bean
    CommandLineRunner seed(
            ContaUsuarioRepository contaUsuarioRepository,
            PasswordEncoder encoder,
            TransactionTemplate transactionTemplate,
            @Value("${app.bootstrap.admin-email}") String email,
            @Value("${app.bootstrap.admin-name}") String name,
            @Value("${app.bootstrap.admin-password}") String password) {

        return args -> {

            transactionTemplate.executeWithoutResult(status -> {

                ContaUsuario usuario = contaUsuarioRepository
                        .findByEmailIgnoreCase(email.trim())
                        .orElseGet(ContaUsuario::new);

                boolean novo = usuario.getId() == null;

                usuario.setEmail(email.trim().toLowerCase());
                usuario.setNomePublicador(name);
                usuario.setRole("admin");
                usuario.setAtivo(true);

                /*
                 * A coleção permissao é LAZY.
                 * Como estamos dentro da transação, o Hibernate
                 * consegue inicializá-la normalmente.
                 */
                if (usuario.getPermissao() == null) {
                    usuario.setPermissao(new java.util.HashSet<>());
                }

                usuario.getPermissao().add("*");

                /*
                 * Em instalação nova ou quando a senha configurada
                 * não confere, grava a senha usando BCrypt.
                 */
                String hashAtual = usuario.getPasswordHash();
                boolean hashBcryptValido = hashAtual != null
                        && (hashAtual.startsWith("$2a$")
                            || hashAtual.startsWith("$2b$")
                            || hashAtual.startsWith("$2y$"));

                if (novo || !hashBcryptValido || !encoder.matches(password, hashAtual)) {
                    usuario.setPasswordHash(encoder.encode(password));
                }

                contaUsuarioRepository.save(usuario);

                System.out.println("========================================");
                System.out.println(" ADMINISTRADOR LS MAPAS VISIT DISPONÍVEL");
                System.out.println(" E-mail: " + email);
                System.out.println(" Senha: " + password);
                System.out.println(" Role: admin");
                System.out.println("========================================");
            });
        };
    }
}