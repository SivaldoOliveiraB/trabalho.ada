package trabalho.ada.resource.conta;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ValorTransacaoRequest(
        @NotNull(message = "O valor da transação é obrigatório")
        BigDecimal valor
) {}