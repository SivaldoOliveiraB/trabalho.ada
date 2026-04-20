package trabalho.ada.resource.conta;

import trabalho.ada.enums.TipoTransacao;
import trabalho.ada.model.Conta;

import java.math.BigDecimal;

public record TransacaoContaResponse(
        Long id,
        TipoTransacao tipo,
        BigDecimal valor,
        BigDecimal saldoAtual,
        ContaTransacaoResponse conta
) {
    public TransacaoContaResponse(Conta conta){
        this(conta.getDeposito().getId(),
                conta.getDeposito().getTipo(),
                conta.getDeposito().getValor(),
                conta.getSaldo(),
                new ContaTransacaoResponse(conta));
    }
}
