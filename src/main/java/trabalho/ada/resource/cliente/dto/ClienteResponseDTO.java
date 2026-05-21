package trabalho.ada.resource.cliente.dto;

import jakarta.validation.constraints.NotNull;
import trabalho.ada.model.Cliente;

public record ClienteResponseDTO(
         Long id,
         String nome,
         String email
) {

    public ClienteResponseDTO(@NotNull Cliente cliente) {
        this(cliente.getId(), cliente.getNome(), cliente.getEmail());
    }
}
