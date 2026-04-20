package trabalho.ada.resource.conta;

import jakarta.validation.constraints.NotNull;
import trabalho.ada.enums.TipoConta;
import trabalho.ada.model.Conta;
import trabalho.ada.resource.cliente.ClienteResponse;

import java.math.BigDecimal;

public record ContaResponse(
        Long id,
        String numero,
        TipoConta  tipo,
        BigDecimal saldo,
        ClienteResponse titular
) {
    public ContaResponse(@NotNull Conta conta){
        this(
                conta.getId(),
                conta.getNumero(),
                conta.getTipo(),
                conta.getSaldo(),
                new ClienteResponse(conta.getCliente().getId(), conta.getCliente().getNome(), conta.getCliente().getEmail())
        );
    }
}
