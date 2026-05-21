package trabalho.ada.resource.transacao.dto;

import jakarta.validation.constraints.NotNull;
import trabalho.ada.enums.TipoConta;
import trabalho.ada.model.Conta;
import trabalho.ada.resource.cliente.dto.ClienteResponseDTO;

public record ContaResponse(
        Long id,
        String numero,
        TipoConta tipo,
        ClienteResponseDTO titular
) {
    public ContaResponse(@NotNull Conta conta){
        this(
                conta.getId(),
                conta.getNumero(),
                conta.getTipo(),
                new ClienteResponseDTO(conta.getCliente().getId(), conta.getCliente().getNome(), conta.getCliente().getEmail())
        );
    }
}
