package trabalho.ada.resource.conta;

import trabalho.ada.enums.TipoTransacao;
import trabalho.ada.model.Transacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferenciaResponse(
        Long id,
        TipoTransacao tipo,
        BigDecimal valor,
        BigDecimal saldoAtual,
        LocalDateTime dataHora,
        ContaTransacaoResponse conta,
        ContaTransacaoResponse contaDestino
) {
    public TransferenciaResponse(Transacao transacao) {
        this(
                transacao.getId(),
                transacao.getTipo(),
                transacao.getValor().abs(),
                transacao.getContaOrigem().getSaldo(),
                transacao.getDataHora(),
                new ContaTransacaoResponse(transacao.getContaOrigem()),
                new ContaTransacaoResponse(transacao.getContaDestino())
        );
    }
}
