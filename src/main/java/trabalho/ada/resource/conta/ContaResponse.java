package trabalho.ada.resource.conta;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.core.UriInfo;
import trabalho.ada.enums.TipoConta;
import trabalho.ada.model.Conta;
import trabalho.ada.resource.Link;
import trabalho.ada.resource.cliente.ClienteResponse;

import java.math.BigDecimal;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ContaResponse(
        Long id,
        String numero,
        TipoConta  tipo,
        BigDecimal saldo,
        ClienteResponse titular,
        List <TransacaoResponse> transacoes,
        Link _links
) {
    public ContaResponse(@NotNull Conta conta, UriInfo uriInfo){
        this(
                conta.getId(),
                conta.getNumero(),
                conta.getTipo(),
                conta.getSaldo(),
                new ClienteResponse(conta.getCliente().getId(), conta.getCliente().getNome(), conta.getCliente().getEmail()),

                mapTransacoesHoje(conta),

                conta.getTrasacoes().size() > 1
                    ? _links(conta, uriInfo)
                    : null
        );
    }

    public static Link _links(Conta conta, UriInfo uriInfo){
        return new Link(
                uriInfo.getBaseUriBuilder()
                        .path("transacoes")
                        .queryParam("contaId", conta.getId())
                        .build()
                        .toString()
        );
    }

    private static List<TransacaoResponse> mapTransacoesHoje(Conta conta){
        var transacoesHoje = conta.getTransacoesHoje();

        if (transacoesHoje == null || transacoesHoje.isEmpty()) {
            return null; // 👈 faz sumir do JSON
        }

        return transacoesHoje.stream()
                .map(TransacaoResponse::new)
                .toList();
    }
}
