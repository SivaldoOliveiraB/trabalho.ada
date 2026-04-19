package trabalho.ada.resource.cliente;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import trabalho.ada.model.Cliente;
import trabalho.ada.resource.PageResponse;
import trabalho.ada.service.ClienteService;

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


    private ClienteResponse toResponse(Cliente cliente){
        return new ClienteResponse(cliente);
    }

}
