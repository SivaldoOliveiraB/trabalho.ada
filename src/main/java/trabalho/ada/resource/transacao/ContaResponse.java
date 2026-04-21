package trabalho.ada.resource.transacao;

import jakarta.validation.constraints.NotNull;
import trabalho.ada.enums.TipoConta;
import trabalho.ada.model.Conta;
import trabalho.ada.resource.cliente.ClienteResponse;

public record ContaResponse(
        Long id,
        String numero,
        TipoConta tipo,
        ClienteResponse titular
) {
    public ContaResponse(@NotNull Conta conta){
        this(
                conta.getId(),
                conta.getNumero(),
                conta.getTipo(),
                new ClienteResponse(conta.getCliente().getId(), conta.getCliente().getNome(), conta.getCliente().getEmail())
        );
    }
}
