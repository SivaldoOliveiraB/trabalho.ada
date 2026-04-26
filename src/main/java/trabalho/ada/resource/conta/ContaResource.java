package trabalho.ada.resource.conta;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import trabalho.ada.exception.ErrorResponse;
import trabalho.ada.model.Conta;
import trabalho.ada.model.Transacao;
import trabalho.ada.service.ContaService;

import java.net.URI;

@Path("/contas")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ContaResource {

    @Inject
    ContaService contaService;

    @POST
    @Transactional
    @RolesAllowed({"GERENTE"})
    public Response create(
            @Valid CreateContaRequest request,
            @Context UriInfo uriInfo
    ){
        Conta conta = contaService.create(request);
        URI location = uriInfo.getAbsolutePathBuilder().path(conta.getId().toString()).build();
        return Response.created(location).entity(toResponse(conta, uriInfo)).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"GERENTE", "CLIENTE"})
    public Response getContas(
            @PathParam("id") Long contaId,
            @Context UriInfo uriInfo
    ){
        Conta conta = contaService.getConta(contaId);

        return Response.ok(toResponse(conta, uriInfo)).build();
    }

    @POST
    @Path("/{id}/deposito")
    @Transactional
    @RolesAllowed({"GERENTE", "CLIENTE"})
    public Response deposita(
            @Valid ValorTransacaoRequest request,
            @PathParam("id") Long contaId,
            @Context UriInfo uriInfo
    ){
        if (request == null || request.valor() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Campo 'valor' é obrigatório"))
                    .build();
        }
        Transacao transacao = contaService.deposito(request.valor(), contaId);
        URI location = uriInfo.getAbsolutePathBuilder().path(transacao.getId().toString()).build();
        return Response.created(location).entity(toResponseContaTransacao(transacao)).build();
    }

    @POST
    @Path("/{id}/saque")
    @Transactional
    @RolesAllowed({"GERENTE", "CLIENTE"})
    public Response saca(
            @Valid ValorTransacaoRequest request,
            @PathParam("id") Long contaId,
            @Context UriInfo uriInfo
    ){
        Transacao transacao = contaService.saque(request.valor(), contaId);
        URI location = uriInfo.getAbsolutePathBuilder().path(transacao.getId().toString()).build();
        return Response.created(location).entity(toResponseContaTransacao(transacao)).build();
    }

    @POST
    @Path("/{id}/transferencia")
    @Transactional
    @RolesAllowed({"GERENTE", "CLIENTE"})
    public Response transfere(
            @Valid TransferenciaResquest resquest,
            @PathParam("id") Long contaOrigemId,
            @Context UriInfo uriInfo
    ){
        Transacao transacao = contaService.transferencia(resquest.valor(), contaOrigemId, resquest.contaDestino().id());

        URI location = uriInfo.getAbsolutePathBuilder().path(transacao.getId().toString()).build();
        return Response.created(location).entity(toResponseTransferencia(transacao)).build();
    }

    private ContaResponse toResponse(Conta conta, UriInfo uriInfo) { return new ContaResponse(conta, uriInfo); }

    private TransacaoResponse toResponseContaTransacao(Transacao transacao) { return  new TransacaoResponse(transacao); }

    private TransferenciaResponse toResponseTransferencia(Transacao transacao) { return new TransferenciaResponse(transacao); }

}
