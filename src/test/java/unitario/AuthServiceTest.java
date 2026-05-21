package unitario;


import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.mock.PanacheMock;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mockito;
import trabalho.ada.enums.Role;
import trabalho.ada.exception.AuthenticationException;
import trabalho.ada.model.Cliente;
import trabalho.ada.model.LoggedUser;
import trabalho.ada.resource.auth.dto.TokenResponse;
import trabalho.ada.service.AuthService;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class AuthServiceTest {

    @Inject
    AuthService authService;

    @InjectMock
    JsonWebToken jwt;

    @BeforeEach
    void setUp() {
        PanacheMock.mock(Cliente.class);
    }

    // ---------------- LOGIN ----------------

    @Test
    void deveRealizarLoginComSucesso() {

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Sivaldo");
        cliente.setEmail("sivaldo@ada.com");
        cliente.setRole(Role.CLIENTE);
        cliente.setSenha(BCrypt.hashpw("123456", BCrypt.gensalt()));

        PanacheQuery<Cliente> query = Mockito.mock(PanacheQuery.class);

        PanacheMock.doReturn(query)
                .when(Cliente.class)
                .find("email = ?1", "sivaldo@ada.com");

        Mockito.when(query.firstResult()).thenReturn(cliente);

        TokenResponse response =
                authService.login("sivaldo@ada.com", "123456");

        assertNotNull(response);
        assertNotNull(response.token());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExiste() {

        PanacheQuery<Cliente> query = Mockito.mock(PanacheQuery.class);

        PanacheMock.doReturn(query)
                .when(Cliente.class)
                .find("email = ?1", "naoexiste@ada.com");

        Mockito.when(query.firstResult()).thenReturn(null);

        assertThrows(AuthenticationException.class,
                () -> authService.login("naoexiste@ada.com", "123456"));
    }

    @Test
    void deveLancarExcecaoQuandoSenhaInvalida() {

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Sivaldo");
        cliente.setEmail("sivaldo@ada.com");
        cliente.setRole(Role.CLIENTE);
        cliente.setSenha(BCrypt.hashpw("123456", BCrypt.gensalt()));

        PanacheQuery<Cliente> query = Mockito.mock(PanacheQuery.class);

        PanacheMock.doReturn(query)
                .when(Cliente.class)
                .find("email = ?1", "sivaldo@ada.com");

        Mockito.when(query.firstResult()).thenReturn(cliente);

        assertThrows(AuthenticationException.class,
                () -> authService.login("sivaldo@ada.com", "senhaerrada"));
    }

    // ---------------- JWT / LOGGED USER ----------------

    @Test
    void deveLancarExcecaoQuandoNaoTemUsuarioLogado() {

        Mockito.when(jwt.getName()).thenReturn(null);

        assertThrows(jakarta.ws.rs.NotAuthorizedException.class,
                () -> authService.getLoggedUser());
    }

    @Test
    void deveRetornarUsuarioLogadoComSucesso() {

        Mockito.when(jwt.getName()).thenReturn("Sivaldo");
        Mockito.when(jwt.getClaim("id")).thenReturn(1L);
        Mockito.when(jwt.getClaim("email")).thenReturn("sivaldo@ada.com");
        Mockito.when(jwt.getGroups()).thenReturn(Set.of("CLIENTE"));

        LoggedUser user = authService.getLoggedUser();

        assertNotNull(user);
        assertEquals("Sivaldo", user.email());
    }

    @Test
    void deveUsarRolePadraoQuandoNaoTemGrupo() {

        Mockito.when(jwt.getName()).thenReturn("Sivaldo");
        Mockito.when(jwt.getClaim("id")).thenReturn(1L);
        Mockito.when(jwt.getClaim("email")).thenReturn("sivaldo@ada.com");
        Mockito.when(jwt.getGroups()).thenReturn(Set.of());

        LoggedUser user = authService.getLoggedUser();

        assertEquals(Role.CLIENTE, user.role());
    }

    @Test
    void deveRetornarRolePadraoQuandoNaoTemGrupo() {

        Mockito.when(jwt.getName()).thenReturn("user@ada.com");
        Mockito.when(jwt.getClaim("id")).thenReturn(1L);
        Mockito.when(jwt.getClaim("email")).thenReturn("user@ada.com");

        Mockito.when(jwt.getGroups()).thenReturn(Set.of()); // 👈 vazio

        LoggedUser user = authService.getLoggedUser();

        assertEquals(Role.CLIENTE, user.role());
    }
}