package trabalho.ada.resource.transacao;

import com.fasterxml.jackson.annotation.JsonInclude;
import trabalho.ada.enums.TipoTransacao;
import trabalho.ada.model.Transacao;
import trabalho.ada.resource.transacao.ContaResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
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
                        : transacao.getContaDestino() != null
                        ? new ContaResponse(transacao.getContaDestino())
                        : null,
                transacao.getTipo().equals(TipoTransacao.TRANSFERENCIA)
                        ? new ContaResponse(transacao.getContaDestino())
                        : null
        );
    }
}
