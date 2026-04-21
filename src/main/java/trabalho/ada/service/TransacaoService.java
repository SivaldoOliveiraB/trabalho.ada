package trabalho.ada.service;

import jakarta.enterprise.context.ApplicationScoped;
import trabalho.ada.enums.TipoTransacao;
import trabalho.ada.model.Conta;
import trabalho.ada.model.Transacao;

import java.math.BigDecimal;

@ApplicationScoped
public class TransacaoService {

    public Transacao crate(TipoTransacao tipo, BigDecimal valor, Conta contaOrigem, Conta contaDestino){
        Transacao transacao = new Transacao(tipo, valor);

        if(tipo.equals(TipoTransacao.DEPOSITO)){
            contaDestino.setSaldo(contaDestino.getSaldo().add(valor));
        }

        if(tipo.equals(TipoTransacao.SAQUE)){
            contaOrigem.setSaldo(contaOrigem.getSaldo().add(valor));
        }

        if(tipo.equals(TipoTransacao.TRANSFERENCIA)){
            BigDecimal valorSaldoOrigem = valor.abs().negate(); // transforma o valor em negativo para debitar do saldo
            contaDestino.setSaldo(contaDestino.getSaldo().add(valor));
            contaOrigem.setSaldo(contaOrigem.getSaldo().add(valorSaldoOrigem));
        }


        transacao.setContaOrigem(contaOrigem);
        transacao.setContaDestino(contaDestino);
        transacao.persist();

        return transacao;
    }
}
