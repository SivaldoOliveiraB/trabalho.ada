package trabalho.ada.resource.transacao;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import trabalho.ada.model.Transacao;
import trabalho.ada.service.TransacaoService;

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

    private TransacaoResponse toResponse(Transacao transacao){ return new TransacaoResponse(transacao); }

}
