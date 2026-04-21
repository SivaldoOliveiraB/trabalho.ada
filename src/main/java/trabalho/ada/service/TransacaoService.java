package trabalho.ada.service;

import jakarta.enterprise.context.ApplicationScoped;
import trabalho.ada.enums.TipoTransacao;
import trabalho.ada.model.Conta;
import trabalho.ada.model.Transacao;

import java.math.BigDecimal;

@ApplicationScoped
public class TransacaoService {

    public Transacao crate(TipoTransacao tipo, BigDecimal valor, Conta contaDestino){
        Transacao transacao = new Transacao(tipo, valor);
        transacao.setContaDestino(contaDestino);
        transacao.persist();

        return transacao;
    }
}
