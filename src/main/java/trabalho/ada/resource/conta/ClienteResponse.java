package trabalho.ada.resource.conta;

import jakarta.validation.constraints.NotNull;
import trabalho.ada.model.Cliente;

public record ClienteResponse(
        Long id,
        String nome
) {
    public ClienteResponse(@NotNull Cliente cliente) {
        this(cliente.getId(), cliente.getNome());
    }
}
