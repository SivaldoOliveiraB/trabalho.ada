package trabalho.ada.resource.conta;

import trabalho.ada.enums.TipoTransacao;
import trabalho.ada.model.Transacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransacaoResponse(
        Long id,
        TipoTransacao tipo,
        BigDecimal valor,
        BigDecimal saldoAtual,
        LocalDateTime dataHora,
        ContaTransacaoResponse conta
) {
    public TransacaoResponse (Transacao transacao){
        this(
                transacao.getId(),
                transacao.getTipo(),
                transacao.getValor(),
                transacao.getContaDestino().getSaldo(),
                transacao.getDataHora(),
                new ContaTransacaoResponse(transacao.getContaDestino())
        );
    }
}
