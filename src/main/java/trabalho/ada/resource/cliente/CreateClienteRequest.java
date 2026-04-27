package trabalho.ada.resource.cliente;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;

public record CreateClienteRequest(

        @NotNull(message = "Nome obrigatório.")
        String nome,

        @NotNull(message = "CPF obrigatório.")
        String cpf,

        @NotNull(message = "e-mail obrigatório.")
        @Email(message = "e-mail inválido.")
        String email,

        @NotNull(message = "senha obrigatória")
        String senha
) {
}
