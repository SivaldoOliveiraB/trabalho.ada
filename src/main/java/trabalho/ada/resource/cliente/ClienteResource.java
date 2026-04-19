package trabalho.ada.resource.cliente;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import trabalho.ada.model.Cliente;
import trabalho.ada.resource.PageResponse;
import trabalho.ada.service.ClienteService;
import java.net.URI;

@Path("/cliente")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClienteResource {

    @Inject
    ClienteService clienteService;

    @GET
    @PermitAll
    public PageResponse<ClienteResponse> list(
            @QueryParam("nome") String nome,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ){

        // FALTA IMPLEMENTAR O GET POR NOME
        return PageResponse.from(
                clienteService.list(page, size),
                this::toResponse
        );
    }

    @GET
    @Path("/{id}")
    @PermitAll
    public ClienteResponse findById(@PathParam("id") Long id){
        return toResponse(clienteService.findById(id));
    }

    @POST
    @Transactional
    public Response create(
            @Valid CreateClienteRequest request,
            @Context UriInfo uriInfo
    ){
        Cliente cliente = clienteService.create(request);
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(cliente.getId().toString())
                .build();

        return Response.created(location)
                .entity(toResponse(cliente))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public ClienteResponse ClienteResponse(
        @PathParam("id") Long id,
        @Valid UpdateClienteRequest request
    ){
        return toResponse(clienteService.update(id, request));
    }

    private ClienteResponse toResponse(Cliente cliente){
        return new ClienteResponse(cliente);
    }

}
