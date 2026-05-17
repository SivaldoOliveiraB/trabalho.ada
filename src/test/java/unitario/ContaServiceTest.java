package unitario;

import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.panache.mock.PanacheMock;
import io.quarkus.test.InjectMock;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import trabalho.ada.enums.Role;
import trabalho.ada.enums.TipoConta;
import trabalho.ada.enums.TipoTransacao;
import trabalho.ada.exception.BusinessException;
import trabalho.ada.model.Cliente;
import trabalho.ada.model.Conta;
import trabalho.ada.model.Transacao;
import trabalho.ada.repository.ContaRepository;
import trabalho.ada.resource.conta.ClienteRequest;
import trabalho.ada.resource.conta.CreateContaRequest;
import trabalho.ada.service.ClienteService;
import trabalho.ada.service.ContaService;
import trabalho.ada.service.TransacaoService;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@QuarkusTest
class ContaServiceTest {

    @Inject
    ContaService contaService;

    @InjectMock
    ClienteService clienteService;

    @InjectMock
    ContaRepository contaRepository;

    @Inject
    EntityManager entityManager;

    @InjectMock
    JsonWebToken jwt;

    @InjectMock
    TransacaoService transacaoService;


    @BeforeEach
    void setup(){
        PanacheMock.mock(Conta.class);
    }

    @Test
    @TestTransaction
    void deveCriarContaComSucesso(){
        // Arrange
        Cliente cliente = new Cliente("Sivaldo", "12345678900", "sivaldo@ada.com", "123456");
        cliente.persist();

        when(clienteService.getRequiredCliente(cliente.getId())).thenReturn(cliente);
        when(contaRepository.proximoNumero()).thenReturn(1L);

        CreateContaRequest request = new CreateContaRequest(TipoConta.CORRENTE, new ClienteRequest(cliente.getId()));

        // Act
        Conta conta = contaService.create(request);

        // Assert
        assertNotNull(conta);
        assertEquals("0001-1", conta.getNumero());
        assertEquals(TipoConta.CORRENTE, conta.getTipo());
        assertEquals(cliente.getId(), conta.getCliente().getId());
    }

    @Test
    void deveRetornarUmaConta(){

        // Arrange
        Conta conta = new Conta();
        conta.setId(1L);

        when(Conta.findById(1L)).thenReturn(conta);

        // Act
        Conta result = contaService.getRequiredConta(1L);

        // Assert
        assertNotNull(result);
        assertEquals(conta.getId(), result.getId());

    }

    @Test
    @TestTransaction
    void deveFazerDepositoComSucesso(){

        //Arrange
        when(jwt.getClaim("id")).thenReturn("1");
        when(jwt.getGroups()).thenReturn(Set.of("CLIENTE"));

        Cliente cliente = new Cliente("Sivaldo", "12345678900", "sivaldo@ada.com", "123456");
        cliente.setId(1L);

        Conta contaDestino = new Conta();
        contaDestino.setTipo(TipoConta.CORRENTE);
        contaDestino.setCliente(cliente);
        contaDestino.setNumero("0001-1");
        contaDestino.setId(1L);
        contaDestino.setSaldo(BigDecimal.ZERO);

        PanacheMock.doReturn(contaDestino).when(Conta.class).findById(1L);

        Transacao transacaoMock = new Transacao(TipoTransacao.DEPOSITO, BigDecimal.valueOf(50));

        when(transacaoService.crate(
                eq(TipoTransacao.DEPOSITO),
                eq(BigDecimal.valueOf(50)),
                isNull(),
                eq(contaDestino)
        )).thenReturn(transacaoMock);

        //Act
        Transacao transacao = contaService.deposito(BigDecimal.valueOf(50), contaDestino.getId());

        //Assert
        assertNotNull(transacao);
        assertEquals(BigDecimal.valueOf(50), transacao.getValor());

    }

    @Test
    void deveGerarNumeroConta() {

        //Arrange
        Long numero = 10L;

        //Act
        String numeroDaConta = contaService.gerarNumeroConta(numero);

        //Assert
        assertEquals("0010-1", numeroDaConta);
    }

    @Test
    @TestTransaction
    void deveRealizarSaqueComSucesso(){
        //Arrange
        when(jwt.getClaim("id")).thenReturn("1");
        when(jwt.getGroups()).thenReturn(Set.of("CLIENTE"));

        Cliente cliente = new Cliente("Sivaldo", "12345678900", "sivaldo@ada.com", "123456");
        cliente.setId(1L);

        Conta contaOrigem = new Conta();
        contaOrigem.setTipo(TipoConta.CORRENTE);
        contaOrigem.setCliente(cliente);
        contaOrigem.setNumero("0001-1");
        contaOrigem.setId(1L);
        contaOrigem.setSaldo(BigDecimal.valueOf(100));

        PanacheMock.doReturn(contaOrigem).when(Conta.class).findById(1L);

        Transacao transacaoMock = new Transacao(TipoTransacao.SAQUE, BigDecimal.valueOf(50));

        when(transacaoService.crate(
                eq(TipoTransacao.SAQUE),
                eq(BigDecimal.valueOf(-50)),
                eq(contaOrigem),
                isNull()
        )).thenReturn(transacaoMock);

        //Act
        Transacao transacao = contaService.saque(BigDecimal.valueOf(50), contaOrigem.getId());

        //Assert
        assertNotNull(transacao);
        assertEquals(BigDecimal.valueOf(50), transacao.getValor());
        assertEquals(BigDecimal.valueOf(50), contaOrigem.getSaldo());
    }

    @Test
    @TestTransaction
    void deveLancarExcecaoQuandoNaoHouverSaldoParaSaque(){
        //Arrange
        when(jwt.getClaim("id")).thenReturn("1");
        when(jwt.getGroups()).thenReturn(Set.of("CLIENTE"));

        Cliente cliente = new Cliente("Sivaldo", "12345678900", "sivaldo@ada.com", "123456");
        cliente.setId(1L);

        Conta contaOrigem = new Conta();
        contaOrigem.setTipo(TipoConta.CORRENTE);
        contaOrigem.setCliente(cliente);
        contaOrigem.setNumero("0001-1");
        contaOrigem.setId(1L);
        contaOrigem.setSaldo(BigDecimal.valueOf(10));

        PanacheMock.doReturn(contaOrigem).when(Conta.class).findById(1L);

        Transacao transacaoMock = new Transacao(TipoTransacao.SAQUE, BigDecimal.valueOf(50));

        when(transacaoService.crate(
                eq(TipoTransacao.SAQUE),
                eq(BigDecimal.valueOf(-50)),
                eq(contaOrigem),
                isNull()
        )).thenReturn(transacaoMock);

        // Act + Assert
        assertThrows(BusinessException.class, () -> contaService.saque(BigDecimal.valueOf(50), contaOrigem.getId()));
    }

    @Test
    @TestTransaction
    void deveLancarExcecaoSeContaForELETRONICA(){
        //Arrange
        when(jwt.getClaim("id")).thenReturn("1");
        when(jwt.getGroups()).thenReturn(Set.of("CLIENTE"));

        Cliente cliente = new Cliente("Sivaldo", "12345678900", "sivaldo@ada.com", "123456");
        cliente.setId(1L);

        Conta contaOrigem = new Conta();
        contaOrigem.setTipo(TipoConta.ELETRONICA);
        contaOrigem.setCliente(cliente);
        contaOrigem.setNumero("0001-1");
        contaOrigem.setId(1L);
        contaOrigem.setSaldo(BigDecimal.valueOf(10));

        PanacheMock.doReturn(contaOrigem).when(Conta.class).findById(1L);

        Transacao transacaoMock = new Transacao(TipoTransacao.SAQUE, BigDecimal.valueOf(50));

        when(transacaoService.crate(
                eq(TipoTransacao.SAQUE),
                eq(BigDecimal.valueOf(-50)),
                eq(contaOrigem),
                isNull()
        )).thenReturn(transacaoMock);

        // Act + Assert
        assertThrows(BusinessException.class, () -> contaService.saque(BigDecimal.valueOf(50), contaOrigem.getId()));
    }

    @Test
    @TestTransaction
    void deveLancarExcecaoSeContaNaoPertenceAoCliente(){
        //Arrange
        when(jwt.getClaim("id")).thenReturn("2");
        when(jwt.getGroups()).thenReturn(Set.of("CLIENTE"));

        Cliente cliente = new Cliente("Sivaldo", "12345678900", "sivaldo@ada.com", "123456");
        cliente.setId(1L);

        Conta contaOrigem = new Conta();
        contaOrigem.setTipo(TipoConta.ELETRONICA);
        contaOrigem.setCliente(cliente);
        contaOrigem.setNumero("0001-1");
        contaOrigem.setId(1L);
        contaOrigem.setSaldo(BigDecimal.valueOf(10));

        PanacheMock.doReturn(contaOrigem).when(Conta.class).findById(1L);

        Transacao transacaoMock = new Transacao(TipoTransacao.SAQUE, BigDecimal.valueOf(50));

        when(transacaoService.crate(
                eq(TipoTransacao.SAQUE),
                eq(BigDecimal.valueOf(-50)),
                eq(contaOrigem),
                isNull()
        )).thenReturn(transacaoMock);

        // Act + Assert
        assertThrows(BusinessException.class, () -> contaService.saque(BigDecimal.valueOf(50), contaOrigem.getId()));
    }

    @Test
    @TestTransaction
    void deveRealizarUmaTransferenciaComSucesso() {

        //Arrange
        when(jwt.getClaim("id")).thenReturn("1");
        when(jwt.getGroups()).thenReturn(Set.of("CLIENTE"));

        Cliente cliente = new Cliente("Sivaldo", "12345678900", "sivaldo@ada.com", "123456");
        cliente.setId(1L);

        Conta contaOrigem = new Conta();
        contaOrigem.setTipo(TipoConta.ELETRONICA);
        contaOrigem.setCliente(cliente);
        contaOrigem.setNumero("0001-1");
        contaOrigem.setId(1L);
        contaOrigem.setSaldo(BigDecimal.valueOf(100));

        PanacheMock.doReturn(contaOrigem).when(Conta.class).findById(1L);

        Cliente clienteRecebedor = new Cliente("Cliente 2", "00045678900", "cliente2@ada.com", "123456");
        clienteRecebedor.setId(2L);

        Conta contaDestino = new Conta();
        contaDestino.setTipo(TipoConta.CORRENTE);
        contaDestino.setCliente(cliente);
        contaDestino.setNumero("0009-0");
        contaDestino.setId(2L);
        contaDestino.setSaldo(BigDecimal.ZERO);

        PanacheMock.doReturn(contaDestino).when(Conta.class).findById(2L);

        Transacao transacaoMock = new Transacao(TipoTransacao.TRANSFERENCIA, BigDecimal.valueOf(50));

        when(transacaoService.crate(
                eq(TipoTransacao.TRANSFERENCIA),
                eq(BigDecimal.valueOf(50)),
                eq(contaOrigem),
                eq(contaDestino)
        )).thenReturn(transacaoMock);

        //Act
        Transacao transacao = contaService.transferencia(BigDecimal.valueOf(50), contaOrigem.getId(), contaDestino.getId());

        //Assert
        assertNotNull(transacao);
        assertEquals(BigDecimal.valueOf(50), transacao.getValor());
        assertEquals(BigDecimal.valueOf(50), contaOrigem.getSaldo());
        assertEquals(BigDecimal.valueOf(50), contaDestino.getSaldo());
    }

    @Test
    @TestTransaction
    void deveLancarExcecaoSeContaOrigemIgualContaDestino(){
        //Arrange
        when(jwt.getClaim("id")).thenReturn("1");
        when(jwt.getGroups()).thenReturn(Set.of("CLIENTE"));

        Cliente cliente = new Cliente("Sivaldo", "12345678900", "sivaldo@ada.com", "123456");
        cliente.setId(1L);

        Conta contaOrigem = new Conta();
        contaOrigem.setTipo(TipoConta.ELETRONICA);
        contaOrigem.setCliente(cliente);
        contaOrigem.setNumero("0001-1");
        contaOrigem.setId(1L);
        contaOrigem.setSaldo(BigDecimal.valueOf(100));

        PanacheMock.doReturn(contaOrigem).when(Conta.class).findById(1L);

        Transacao transacaoMock = new Transacao(TipoTransacao.TRANSFERENCIA, BigDecimal.valueOf(50));

        when(transacaoService.crate(
                eq(TipoTransacao.TRANSFERENCIA),
                eq(BigDecimal.valueOf(50)),
                eq(contaOrigem),
                eq(contaOrigem)
        )).thenReturn(transacaoMock);

        // Act + Assert
        assertThrows(BusinessException.class, () -> contaService.transferencia(BigDecimal.valueOf(50), contaOrigem.getId(), contaOrigem.getId()));
    }

}