package trabalho.ada.resource.conta;

import jakarta.validation.constraints.NotNull;
import trabalho.ada.enums.TipoConta;

public record CreateContaRequest(
        @NotNull(message = "Tipo de conta obrigatório")
        TipoConta tipo,

        @NotNull(message = "O cliente é obrigatório")
        ClienteRequest cliente
) {
}
