package unitario;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.mock.PanacheMock;
import io.quarkus.test.InjectMock;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.mockito.Mockito;
import trabalho.ada.enums.TipoConta;
import trabalho.ada.enums.TipoTransacao;
import trabalho.ada.exception.BusinessException;
import trabalho.ada.model.Conta;
import trabalho.ada.model.Cliente;
import trabalho.ada.model.Transacao;
import trabalho.ada.service.TransacaoService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.wildfly.common.Assert.assertNotNull;

@QuarkusTest
class TransacaoServiceTest {

    @Inject
    TransacaoService transacaoService;

    @InjectMock
    JsonWebToken jwt;

    @BeforeEach
    void setup(){
        PanacheMock.mock(Transacao.class);
    }

    @Test
    @TestTransaction
    void crate() {

        Conta contaOrigem = new Conta();
        Conta contaDestino = new Conta();
        BigDecimal valor = new BigDecimal("50.00");

        Transacao result = transacaoService.crate(
                TipoTransacao.DEPOSITO,
                valor,
                contaOrigem,
                contaDestino
        );

        assertEquals(TipoTransacao.DEPOSITO, result.getTipo());
        assertEquals(valor, result.getValor());
        assertEquals(contaOrigem, result.getContaOrigem());
        assertEquals(contaDestino, result.getContaDestino());
    }

    @Test
    void deveDevolverUmaTansacao(){
        //Arrange
        Transacao transacao = new Transacao(TipoTransacao.DEPOSITO, BigDecimal.valueOf(10));

        PanacheMock.doReturn(transacao).when(Transacao.class).findById(1L);

        //Act
        Transacao transacaoEsperada = transacaoService.getRequiredTransacao(1L);

        //Assert
        assertNotNull(transacaoEsperada);

    }

    @Test
    void deveDevolverUmaExceptionSeTransacaoNaoForEncontrada(){
        //Arrange
        PanacheMock.doReturn(null).when(Transacao.class).findById(1L);

        //Act + Assert
        assertThrows(NotFoundException.class, () -> transacaoService.getRequiredTransacao(1L));
    }

    @Test
    @TestTransaction
    void deveRetornarUmaListaDeTransacoes(){
        //Arrange
        PanacheMock.mock(Conta.class);
        when(jwt.getClaim("id")).thenReturn("1");
        when(jwt.getGroups()).thenReturn(Set.of("CLIENTE"));

        Cliente cliente = new Cliente("Sivaldo", "12345678900", "sivaldo@ada.com", "123456");
        cliente.setId(1L);

        Conta conta = new Conta();
        conta.setTipo(TipoConta.CORRENTE);
        conta.setCliente(cliente);
        conta.setNumero("0001-1");
        conta.setId(1L);
        conta.setSaldo(BigDecimal.valueOf(100));

        Transacao transacao1 = new Transacao(TipoTransacao.DEPOSITO, BigDecimal.valueOf(50));
        Transacao transacao2 = new Transacao(TipoTransacao.DEPOSITO, BigDecimal.valueOf(150));
        Transacao transacao3 = new Transacao(TipoTransacao.DEPOSITO, BigDecimal.valueOf(250));

        List<Transacao> transacoes = List.of(transacao1, transacao2 , transacao3);
        conta.setTrasacoes(transacoes);

        PanacheMock.doReturn(conta).when(Conta.class).findById(1L);
        PanacheQuery<Transacao> query = Mockito.mock(PanacheQuery.class);

        Mockito.when(query.list()).thenReturn(transacoes);

        PanacheMock.doReturn(query)
                .when(Transacao.class)
                .find("contaId", 1L);

        //Act
        List<Transacao> transacaosRecebidas = transacaoService.getByContaId(1L);

        //Assert
        assertNotNull(transacaosRecebidas);
    }

}