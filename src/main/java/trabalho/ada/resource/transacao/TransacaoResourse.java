package trabalho.ada.resource.transacao;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import trabalho.ada.model.Transacao;
import trabalho.ada.service.TransacaoService;
import java.util.List;

@Path("/transacoes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TransacaoResourse {

    @Inject
    TransacaoService transacaoService;

    @GET
    @Path("/{id}")
    public TransacaoResponse findById(
            @PathParam("id") Long id
    ){
            return toResponse(transacaoService.getRequiredTransacao(id));
    }

    @GET
    public List<TransacaoResponse> findByContaId(
            @QueryParam("contaId") Long contaId
    ) {
        if (contaId == null) {
            throw new WebApplicationException("contaId é obrigatório", 400);
        }

        return transacaoService.getByContaId(contaId)
                .stream()
                .map(this::toResponse)
                .toList();
    }
    private TransacaoResponse toResponse(Transacao transacao){ return new TransacaoResponse(transacao); }

}
