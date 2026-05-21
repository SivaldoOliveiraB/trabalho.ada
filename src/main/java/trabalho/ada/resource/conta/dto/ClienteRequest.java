package trabalho.ada.resource.conta.dto;

import jakarta.validation.constraints.NotNull;

public record ClienteRequest(
        @NotNull(message = "O id do cliente é obrigatório")
        Long id
) {
}
