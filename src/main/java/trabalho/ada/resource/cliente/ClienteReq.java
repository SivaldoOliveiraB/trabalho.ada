package trabalho.ada.resource.cliente;

import jakarta.validation.constraints.NotNull;

public record ClienteReq(
        @NotNull(message = "O id do cliente é obrigatório")
        Long id
) {
}
