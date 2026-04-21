package trabalho.ada.resource.conta;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferenciaResquest(

        ContaDestinoRequest contaDestino,

        @NotNull(message = "O valor da transação é obrigatório")
        BigDecimal valor
) {
}
