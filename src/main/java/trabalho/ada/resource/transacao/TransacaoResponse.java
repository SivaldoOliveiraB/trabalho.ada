package trabalho.ada.resource.transacao;

import trabalho.ada.enums.TipoTransacao;
import trabalho.ada.model.Transacao;
import trabalho.ada.resource.conta.ContaResponse;
import trabalho.ada.resource.conta.ContaTransacaoResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransacaoResponse(
        Long id,
        TipoTransacao tipo,
        BigDecimal valor,
        LocalDateTime dataHora,
        ContaResponse conta,
        ContaResponse contaDestino
) {
    public TransacaoResponse(Transacao transacao) {
        this(
                transacao.getId(),
                transacao.getTipo(),
                transacao.getValor().abs(),
                transacao.getDataHora(),
                transacao.getContaOrigem() != null
                        ? new ContaResponse(transacao.getContaOrigem())
                        : null,
                transacao.getContaDestino() != null
                        ? new ContaResponse(transacao.getContaDestino())
                        : null
        );
    }
}
