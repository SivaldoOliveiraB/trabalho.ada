package trabalho.ada.resource.conta;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import trabalho.ada.model.Conta;
import trabalho.ada.service.ContaService;

import java.math.BigDecimal;
import java.net.URI;

@Path("/contas")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ContaResource {

    @Inject
    ContaService contaService;

    @POST
    @Transactional
    public Response create(
            @Valid CreateContaRequest request,
            @Context UriInfo uriInfo
    ){
        Conta conta = contaService.create(request);
        URI location = uriInfo.getAbsolutePathBuilder().path(conta.getId().toString()).build();
        return Response.created(location).entity(toResponse(conta)).build();
    }

    @POST
    @Path("/{id}/deposito")
    @Transactional
    public Response deposita(
            @Valid DepositoRequest request,
            @PathParam("id") Long contaId,
            @Context UriInfo uriInfo
    ){
        Conta conta = contaService.deposita(request.valor(), contaId);
        URI location = uriInfo.getAbsolutePathBuilder().path(conta.getId().toString()).build();
        return Response.created(location).entity(toResponseContaTransacao(conta)).build();
    }

    private ContaResponse toResponse(Conta conta) {
        return new ContaResponse(conta);
    }

    private ContaTransacaoResponse toResponseContaTransacao(Conta conta) { return  new ContaTransacaoResponse(conta); }
}
