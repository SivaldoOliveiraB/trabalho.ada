package unitario;

import io.quarkus.panache.mock.PanacheMock;
import io.quarkus.test.InjectMock;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import trabalho.ada.exception.BusinessException;
import trabalho.ada.model.Cliente;
import trabalho.ada.resource.cliente.CreateClienteRequest;
import trabalho.ada.resource.cliente.UpdateClienteRequest;
import trabalho.ada.service.ClienteService;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ClienteServiceTest {


    @Inject
    ClienteService clienteService;

    @InjectMock
    JsonWebToken jwt;

    @BeforeEach
    void setup(){
        PanacheMock.mock(Cliente.class);
    }

    @Test
    @TestTransaction
    void deveCriarClienteComSucesso(){

        // Arrange
        Mockito.when(jwt.getGroups()).thenReturn(Set.of("GERENTE"));
        Mockito.when(Cliente.count("LOWER(cpf) = LOWER(?1)", "12345678900")).thenReturn(0L);
        Mockito.when(Cliente.count("LOWER(email) = LOWER(?1)", "teste@ada.com")).thenReturn(0L);

        CreateClienteRequest request = new CreateClienteRequest( "Sivaldo","12345678900","teste@ada.com","123456");

        // Act
        Cliente cliente = clienteService.create(request);

        // Assert
        assertNotNull(cliente);
        assertEquals("Sivaldo", cliente.getNome());
        assertEquals("12345678900", cliente.getCpf());
        assertEquals("teste@ada.com", cliente.getEmail());
        assertNotEquals("123456", cliente.getSenha());
        assertTrue(org.mindrot.jbcrypt.BCrypt.checkpw("123456", cliente.getSenha()));

    }

    @Test
    @TestTransaction
    void deveLancarExcecaoQuandoNaoForGerenteAbrindoAConta(){

        // Arrange
        Mockito.when(jwt.getGroups()).thenReturn(Set.of("CLIENTE"));

        CreateClienteRequest request = new CreateClienteRequest("Sivaldo","12345678900","teste@email.com","123456");

        // Act + Assert
        assertThrows(BusinessException.class, () -> clienteService.create(request));
    }

    @Test
    @TestTransaction
    void deveLancarcarExcecaoQuandoCpfOuEmailJaExistir(){
        Mockito.when(jwt.getGroups()).thenReturn(Set.of("GERENTE"));
        Mockito.when(Cliente.count("LOWER(cpf) = LOWER(?1)", "12345678900")).thenReturn(1L);
        Mockito.when(Cliente.count("LOWER(email) = LOWER(?1)", "teste@ada.com")).thenReturn(1L);

        CreateClienteRequest request =
                new CreateClienteRequest(
                        "Sivaldo",
                        "12345678900",
                        "teste@email.com",
                        "123456"
                );

        // Act + Assert
        assertThrows(BadRequestException.class, () -> clienteService.create(request));
    }

    @Test
    void deveBuscarClientePorId(){

        //Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Sivaldo");

        PanacheMock.doReturn(cliente).when(Cliente.class).findById(1L);

        // Act
        Cliente result = clienteService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Sivaldo", result.getNome());
    }

    @Test
    void buscaPorIdDeveLancarExcecaoQuandoIdNaoExiste(){
        // Arange
        PanacheMock.doReturn(null).when(Cliente.class).findById(99L);

        // Act + Assert
        assertThrows(NotFoundException.class, () -> clienteService.findById(99L));
    }

    @Test
    @TestTransaction
    void deveAtualizarClienteComSucesso(){
        // Arrange
        Mockito.when(jwt.getGroups()).thenReturn(Set.of("GERENTE"));

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Sivaldo");
        cliente.setSenha("123456");


        PanacheMock.doReturn(cliente).when(Cliente.class).findById(1L);

        Mockito.when(Cliente.count("LOWER(email) = LOWER(?1) AND id != ?2", "novo@email.com", 1L)).thenReturn(0L);

        UpdateClienteRequest request = new UpdateClienteRequest("Novo Nome","novo@email.com","456789");

        // Act
        Cliente updated = clienteService.update(1L, request);

        // Assert
        assertEquals("Novo Nome", updated.getNome());
        assertEquals("novo@email.com", updated.getEmail());
        assertTrue(org.mindrot.jbcrypt.BCrypt.checkpw("456789", cliente.getSenha()));
    }

    @Test
    @TestTransaction
    void deveLancarExcecaoSeNaoForGerenteAtualizando(){
        // Arrange
        Mockito.when(jwt.getGroups()).thenReturn(Set.of("CLIENTE"));

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Sivaldo");
        cliente.setSenha("123456");

        PanacheMock.doReturn(cliente).when(Cliente.class).findById(1L);

        Mockito.when(Cliente.count("LOWER(email) = LOWER(?1) AND id != ?2", "novo@email.com", 1L)).thenReturn(0L);

        UpdateClienteRequest request = new UpdateClienteRequest("Novo Nome","novo@email.com","456789");

        // Act + Assert
        assertThrows(BusinessException.class, () -> clienteService.update(1L, request));
    }

    @Test
    @TestTransaction
    void deveLancarExcecaoSeOEmailJaUsadoPorOutroUsuario(){

        //Arrange
        Mockito.when(jwt.getGroups()).thenReturn(Set.of("GERENTE"));

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Sivaldo");
        cliente.setEmail("old@amial.com");

        PanacheMock.doReturn(cliente).when(Cliente.class).findById(1L);

        Mockito.when(Cliente.count("LOWER(email) = LOWER(?1) AND id != ?2", "novo@email.com", 1L)).thenReturn(1L);

        UpdateClienteRequest request = new UpdateClienteRequest("Novo Nome","novo@email.com","456789");

        // Act + Assert
        assertThrows(BadRequestException.class, () -> clienteService.update(1L, request));

    }

}