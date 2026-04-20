package trabalho.ada.resource.conta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateContaRequest(
        @NotBlank(message = "Tipo de conta obrigatório")
        String tipo,

        @NotNull(message = "O cliente é obrigatório")
        ClienteRequest cliente
) {
}
