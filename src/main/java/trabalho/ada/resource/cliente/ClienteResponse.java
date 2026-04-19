package trabalho.ada.resource.cliente;

import jakarta.validation.constraints.NotNull;
import trabalho.ada.model.Cliente;

public record ClienteResponse(
         Long id,
         String nome,
         String email
) {

    public ClienteResponse(@NotNull Cliente cliente) {
        this(cliente.getId(), cliente.getNome(), cliente.getEmail());
    }
}
