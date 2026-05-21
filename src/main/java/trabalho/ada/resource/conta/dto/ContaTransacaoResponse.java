package trabalho.ada.resource.conta.dto;

import trabalho.ada.model.Conta;

public record ContaTransacaoResponse(
        Long id,
        String numero,
        ClienteResponse titular
) {
    public ContaTransacaoResponse(Conta conta){
        this(conta.getId(), conta.getNumero(), new ClienteResponse(conta.getCliente()));
    }
}
