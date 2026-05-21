package unitario;

import io.quarkus.panache.mock.PanacheMock;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import trabalho.ada.enums.Role;
import trabalho.ada.model.Cliente;
import trabalho.ada.model.Conta;
import trabalho.ada.service.ContaService;
import trabalho.ada.service.Service;

import java.util.Set;

import static org.wildfly.common.Assert.assertFalse;
import static org.wildfly.common.Assert.assertTrue;

@QuarkusTest
class ServiceTest {

    @Inject
    ContaService contaService;

    @InjectMock
    JsonWebToken jwt;

    @BeforeEach
    void setup(){
        PanacheMock.mock(Cliente.class);
    }

    @Test
    void deveRetornTrueParaContaPertenceneAoCliente() {

        //Arrange
        Mockito.when(jwt.getGroups()).thenReturn(Set.of("CLIENTE"));
        Mockito.when(jwt.getClaim("id")).thenReturn("1");

        Cliente cliente = new Cliente("Sivaldo", "12345678900", "sivaldo@ada.com", "123456");
        cliente.setId(1L);

        Conta contaConsultada = new Conta();
        contaConsultada.setId(1L);
        contaConsultada.setCliente(cliente);

        //Act
        boolean result = contaService.contaPertenceAoCliente(contaConsultada);

        //Assert
        assertTrue(result);
    }

    @Test
    void deveRetornarFalseParaContaNaoPertencenteAoCliente(){
        //Arrange
        Mockito.when(jwt.getGroups()).thenReturn(Set.of("CLIENTE"));
        Mockito.when(jwt.getClaim("id")).thenReturn("1");

        Cliente cliente = new Cliente("Sivaldo", "12345678900", "sivaldo@ada.com", "123456");
        cliente.setId(2L);

        Conta contaConsultada = new Conta();
        contaConsultada.setId(1L);
        contaConsultada.setCliente(cliente);

        //Act
        boolean result = contaService.contaPertenceAoCliente(contaConsultada);

        //Assert
        assertFalse(result);
    }
}