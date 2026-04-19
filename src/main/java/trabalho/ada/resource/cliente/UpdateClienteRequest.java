package trabalho.ada.resource.cliente;

import jakarta.validation.constraints.NotNull;

public record UpdateClienteRequest(

        @NotNull(message = "Nome obrigatório.")
        String nome,

        @NotNull(message = "CPF obrigatório.")
        String cpf,

        @NotNull(message = "e-mail obrigatório.")
        String email,

        @NotNull(message = "senha obrigatória")
        String senha
) {
}
