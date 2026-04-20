package trabalho.ada.resource.conta;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DepositoRequest(
        @NotNull(message = "O valor do depósito é obrigatório")
        BigDecimal valor
) {}