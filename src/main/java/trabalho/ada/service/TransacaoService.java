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


        if(contaOrigem == null){
            contaDestino.setSaldo(contaDestino.getSaldo().add(valor));
            transacao.setContaDestino(contaDestino);
        }else{
            contaOrigem.setSaldo(contaOrigem.getSaldo().add(valor));
            transacao.setContaOrigem(contaOrigem);
        }
        transacao.persist();

        return transacao;
    }
}
