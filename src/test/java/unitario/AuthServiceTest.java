package unitario;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.panache.mock.PanacheMock;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mockito;
import trabalho.ada.enums.Role;
import trabalho.ada.exception.AuthenticationException;
import trabalho.ada.model.Cliente;
import trabalho.ada.resource.auth.TokenResponse;
import trabalho.ada.service.AuthService;

import static org.junit.jupiter.api.Assertions.*;



@QuarkusTest
class AuthServiceTest {

    @Inject
    AuthService authService;

    @BeforeEach
    void setUp(){
        PanacheMock.mock(Cliente.class);
    }

    @Test
    void deveRealizarLoginComSucesso() {

        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Sivaldo");
        cliente.setEmail("sivaldo.aluno@alura.com");
        cliente.setRole(Role.CLIENTE);
        cliente.setSenha("$2a$10$DowJonesIndexExampleHash123456789"); //senha 123456

        Mockito.when(Cliente.find("email = ?1", "sivaldo.aluno@alura.com")).thenReturn(Mockito.mock(io.quarkus.hibernate.orm.panache.PanacheQuery.class));

        io.quarkus.hibernate.orm.panache.PanacheQuery<Cliente> query = Cliente.find("email = ?1", "sivaldo.aluno@alura.com");

        Mockito.when(query.firstResult()).thenReturn(cliente);

        // bcrypt correto
        cliente.setSenha(org.mindrot.jbcrypt.BCrypt.hashpw("123456", org.mindrot.jbcrypt.BCrypt.gensalt()));

        // Act
        TokenResponse response = authService.login("sivaldo.aluno@alura.com", "123456");

        // Assert
        assertNotNull(response);
        assertNotNull(response.token());

    }

    @Test
    void deveLancarExcecaoQuandoUsuarioInexistente() {

        // Arrange
        @SuppressWarnings("unchecked")
        PanacheQuery<Cliente> query =
                Mockito.mock(PanacheQuery.class);

        // mock do método estático Cliente.find(...)
        PanacheMock.doReturn(query)
                .when(Cliente.class)
                .find("email = ?1", "naoexiste@ada.com");

        // resultado da query
        Mockito.when(query.firstResult())
                .thenReturn(null);

        // Act + Assert
        assertThrows(
                AuthenticationException.class,
                () -> authService.login("naoexiste@ada.com", "123456")
        );
    }

    @Test
    void deveLacarExcecaoQuandoSenhaInvalida(){

        // Arrange

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Sivaldo");
        cliente.setEmail("sivaldo.aluno@alura.com");
        cliente.setRole(Role.CLIENTE);
        // senha correta 123456
        cliente.setSenha(BCrypt.hashpw("123456", BCrypt.gensalt()));

        PanacheQuery<Cliente> query = Mockito.mock(PanacheQuery.class);

        Mockito.when(Cliente.find("email = ?1", "sivaldo.aluno@alura.com")).thenReturn(Mockito.mock(PanacheQuery.class));

        Mockito.when(query.firstResult()).thenReturn(cliente);

        // Act + Assert
        assertThrows(AuthenticationException.class, () -> authService.login("sivaldo.aluno@alura.com", "senhaerrada"));
    }
}