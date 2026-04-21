package trabalho.ada.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;

import trabalho.ada.enums.TipoConta;
import trabalho.ada.enums.TipoTransacao;
import trabalho.ada.exception.BusinessException;
import trabalho.ada.model.Conta;
import trabalho.ada.model.Transacao;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class TransacaoService {

    public Transacao crate(TipoTransacao tipo, BigDecimal valor, Conta contaOrigem, Conta contaDestino){

        Transacao transacao = new Transacao(tipo, valor);

        // se for depósito a conta eum que vai entrar o dinheiro é a conta destino
        if(tipo.equals(TipoTransacao.DEPOSITO)){

            // conforme regra de negócio, não é permitido depósito em conta ELETRONICA
            if(contaDestino.getTipo().equals(TipoConta.ELETRONICA)){ // se a conta de destino for ele ELETRONICA
                throw new BusinessException("Conta do tipo ELETRONICA não permite depósitos.");
            }

            contaDestino.setSaldo(contaDestino.getSaldo().add(valor));
        }


        if(tipo.equals(TipoTransacao.SAQUE)){

            // conforme regra de negócio, não é permitido saque em conta ELETRONICA
            if(contaOrigem.getTipo().equals(TipoConta.ELETRONICA)){ // se a conta de destino for ele ELETRONICA
                throw new BusinessException("Conta do tipo ELETRONICA não permite saques.");
            }

            //verifica se a conta tem saldo
            if(valor.compareTo(contaOrigem.getSaldo()) > 0){
                throw new BusinessException("Saldo insuficiente para realizar o saque.");
            }

            contaOrigem.setSaldo(contaOrigem.getSaldo().add(valor));
        }

        if(tipo.equals(TipoTransacao.TRANSFERENCIA)){

            //verifica se a conta tem saldo
            if(valor.compareTo(contaOrigem.getSaldo()) > 0){
                throw new BusinessException("Saldo insuficiente para realizar a transferência.");
            }

            BigDecimal valorSaldoOrigem = valor.abs().negate(); // transforma o valor em negativo para debitar do saldo
            contaDestino.setSaldo(contaDestino.getSaldo().add(valor));
            contaOrigem.setSaldo(contaOrigem.getSaldo().add(valorSaldoOrigem));
        }

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
        return Transacao.findByContaId(contaId);
    }
}
