package unitario;

import io.quarkus.panache.mock.PanacheMock;
import io.quarkus.test.InjectMock;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import trabalho.ada.enums.TipoConta;
import trabalho.ada.model.Cliente;
import trabalho.ada.model.Conta;
import trabalho.ada.repository.ContaRepository;
import trabalho.ada.resource.conta.ClienteRequest;
import trabalho.ada.resource.conta.CreateContaRequest;
import trabalho.ada.service.ClienteService;
import trabalho.ada.service.ContaService;
import trabalho.ada.service.TransacaoService;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ContaServiceTest {

    @Inject
    ContaService contaService;

    @InjectMock
    ClienteService clienteService;

    @InjectMock
    ContaRepository contaRepository;

    @InjectMock
    TransacaoService transacaoService;

    @BeforeEach
    void setup(){
        PanacheMock.mock(Conta.class);
    }

    @Test
    void create() {
    }

    @Test
    @TestTransaction
    void deveCriarContaComSucesso(){
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Sivaldo");
        cliente.setEmail("sivaldo@ada.com");

        Mockito.when(clienteService.getRequiredCliente(1L)).thenReturn(cliente);
        Mockito.when(contaRepository.proximoNumero()).thenReturn(999L);

        CreateContaRequest request = new CreateContaRequest(TipoConta.CORRENTE, new ClienteRequest(1L));

        // Act
        Conta conta = contaService.create(request);

        // Assert
        assertNotNull(conta);
        assertEquals("0999-3", conta.getNumero());
        assertEquals(TipoConta.CORRENTE, conta.getTipo());
        assertEquals(cliente, conta.getCliente());
    }

    @Test
    void getConta() {
    }

    @Test
    void getRequiredConta() {
    }

    @Test
    void deposito() {
    }

    @Test
    void saque() {
    }

    @Test
    void transferencia() {
    }

    @Test
    void gerarNumeroConta() {
    }
}