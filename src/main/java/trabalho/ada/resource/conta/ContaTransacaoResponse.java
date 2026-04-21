package trabalho.ada.resource.conta;

import trabalho.ada.model.Conta;
import trabalho.ada.model.Transacao;

public record ContaTransacaoResponse(
        Long id,
        String numero,
        ClienteResponse titular
) {
    public ContaTransacaoResponse(Conta conta){
        this(conta.getId(), conta.getNumero(), new ClienteResponse(conta.getCliente()));
    }
}
