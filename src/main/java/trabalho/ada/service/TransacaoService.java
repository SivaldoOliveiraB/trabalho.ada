package trabalho.ada.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;

import trabalho.ada.enums.TipoTransacao;
import trabalho.ada.model.Conta;
import trabalho.ada.model.Transacao;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class TransacaoService extends Service{

    public Transacao crate(TipoTransacao tipo, BigDecimal valor, Conta contaOrigem, Conta contaDestino){

        Transacao transacao = new Transacao(tipo, valor);

        transacao.setContaOrigem(contaOrigem);
        transacao.setContaDestino(contaDestino);
        transacao.persist();

        return transacao;
    }

    public Transacao getRequiredTransacao(Long id){
        Transacao transacao = Transacao.findById(id);

        if (transacao == null){
            throw new NotFoundException("Transacao com o id " + id + " não encontrada");
        }

        return transacao;
    }

    public List<Transacao> getByContaId(Long contaId) {
        Conta conta = Conta.findById(contaId);

        this.verificaDonoDaConta(conta);

        return Transacao.findByContaId(contaId);
    }
}
