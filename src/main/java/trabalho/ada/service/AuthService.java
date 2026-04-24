package trabalho.ada.service;

import de.mkammerer.argon2.Argon2;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import javax.sql.DataSource;

import org.mindrot.jbcrypt.BCrypt;
import trabalho.ada.enums.Role;
import trabalho.ada.exception.AuthenticationException;
import trabalho.ada.model.Cliente;
import trabalho.ada.model.LoggedUser;
import trabalho.ada.resource.auth.TokenResponse;

import java.time.Duration;

@ApplicationScoped
public class AuthService  implements CurrentUserService{

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    @Inject
    JsonWebToken jwt;

    private Argon2 argon2;

    @Override
    public LoggedUser getLoggedUser() {
        if (jwt.getName() == null) {
            throw new NotAuthorizedException("Nenhum usuario autenticado na requisicao atual");
        }

        return new LoggedUser(
                jwt.getClaim("id"),
                jwt.getName(),
                jwt.getClaim("email"),
                getRole()
        );
    }

    public TokenResponse login(String username, String password) {
        Cliente cliente = Cliente.find("email = ?1", username).firstResult();

        if (cliente == null || !BCrypt.checkpw(password, cliente.getSenha())) {
            throw new AuthenticationException("Credenciais inválidas");
        }

        String token = generateToken(cliente);

        return new TokenResponse(token);

    }

    private Role getRole() {
        return jwt.getGroups()
                .stream()
                .findFirst()
                .map(Role::valueOf)
                .orElse(Role.CLIENTE);
    }

    private String generateToken(Cliente cliente) {
        return Jwt.issuer(issuer)
                .upn(cliente.getNome())
                .groups(cliente.getRole().name())
                .claim("id", cliente.getId())
                .claim("email", cliente.getEmail())
                .claim("email", cliente.getEmail())
                .expiresIn(Duration.ofMinutes(300))
                .sign();
    }

}
