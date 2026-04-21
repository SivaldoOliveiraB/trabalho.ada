package trabalho.ada.resource.conta;

import jakarta.validation.constraints.NotNull;

public record ContaDestinoRequest(
        @NotNull(message = "O id da conta é obrigatório")
        Long id
) {
}
