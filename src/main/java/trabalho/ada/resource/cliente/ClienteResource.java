package trabalho.ada.resource.cliente;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;

import trabalho.ada.model.Cliente;
import trabalho.ada.resource.dto.PageResponseDTO;
import trabalho.ada.resource.cliente.dto.ClienteResponseDTO;
import trabalho.ada.resource.cliente.dto.CreateClienteRequestDTO;
import trabalho.ada.resource.cliente.dto.UpdateClienteRequestDTO;
import trabalho.ada.service.ClienteService;


@Path("/clientes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClienteResource {

    @Inject
    ClienteService clienteService;

    @GET
    @RolesAllowed("GERENTE")
    public PageResponseDTO<ClienteResponseDTO> list(
            @QueryParam("nome") String nome,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ){

        // FALTA IMPLEMENTAR O GET POR NOME
        return PageResponseDTO.from(
                clienteService.list(page, size),
                this::toResponse
        );
    }

    @GET
    @Path("/{id}")
    @RolesAllowed("GERENTE")
    public ClienteResponseDTO findById(@PathParam("id") Long id){
        return toResponse(clienteService.getRequiredCliente(id));
    }

    @POST
    @Transactional
    @RolesAllowed("GERENTE")
    public Response create(
            @Valid CreateClienteRequestDTO request,
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
    @RolesAllowed("GERENTE")
    public ClienteResponseDTO ClienteResponse(
        @PathParam("id") Long id,
        @Valid UpdateClienteRequestDTO request
    ){
        return toResponse(clienteService.update(id, request));
    }

    private ClienteResponseDTO toResponse(Cliente cliente){
        return new ClienteResponseDTO(cliente);
    }

}
