package unitario;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import trabalho.ada.enums.TipoTransacao;
import trabalho.ada.model.Conta;
import trabalho.ada.model.Transacao;
import trabalho.ada.service.TransacaoService;

import java.math.BigDecimal;

@QuarkusTest
class TransacaoServiceTest {

    @Inject
    TransacaoService transacaoService;

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

        Assertions.assertEquals(TipoTransacao.DEPOSITO, result.getTipo());
        Assertions.assertEquals(valor, result.getValor());
        Assertions.assertEquals(contaOrigem, result.getContaOrigem());
        Assertions.assertEquals(contaDestino, result.getContaDestino());
    }

    @Test
    void getRequiredTransacao() {
    }

    @Test
    void getByContaId() {
    }
}