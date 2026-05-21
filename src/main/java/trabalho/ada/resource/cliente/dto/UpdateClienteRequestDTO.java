package trabalho.ada.resource.cliente.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateClienteRequestDTO(

        @NotNull(message = "Nome obrigatório.")
        String nome,

        @NotNull(message = "e-mail obrigatório.")
        String email,

        @NotNull(message = "senha obrigatória")
        String senha
) {
}
