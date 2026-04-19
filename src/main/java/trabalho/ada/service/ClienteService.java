package trabalho.ada.service;

import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import trabalho.ada.model.Cliente;
import trabalho.ada.model.PageResult;
import trabalho.ada.resource.cliente.CreateClienteRequest;
import trabalho.ada.resource.cliente.UpdateClienteRequest;

@ApplicationScoped
public class ClienteService {

    public PageResult<Cliente> list(int page, int size){
        var query = Cliente.findAll(Sort.by("nome"));
        var result = query.page(Page.of(page, size));
        return new PageResult<>(result.list(), page, size, result.count());
    }

    public Cliente findById(Long id){ return getRequiredCliente(id); }

    public Cliente getRequiredCliente(Long id) {
        Cliente cliente = Cliente.findById(id);
        if (cliente == null) {
            throw new NotFoundException("Cliente com o id " + id + " não encontrado");
        }
        return cliente;
    }

    public Cliente create (CreateClienteRequest request){
        validateUniqueCPf(request.cpf(), null);
        validateUniqueEmail(request.email(), null);
        Cliente cliente = new Cliente(request.nome().trim(), request.cpf().trim(), request.email().trim(), request.senha().trim());
        cliente.persist();
        return cliente;
    }

    public Cliente update(Long id, UpdateClienteRequest request){
        Cliente cliente = getRequiredCliente(id);
        validateUniqueEmail(request.email(), id);
        cliente.setNome(request.nome());
        cliente.setEmail(request.email());
        cliente.setSenha(request.senha());
        return cliente;
    }

    private void validateUniqueCPf(String cpf, Long currentId) {
        String normalized = cpf.trim();
        long count = (currentId == null)
                ? Cliente.count("LOWER(cpf) = LOWER(?1)", normalized)
                : Cliente.count("LOWER(cpf) = LOWER(?1) AND id != ?2", normalized, currentId);
        if (count > 0) {
            throw new BadRequestException("Já existe um cliente cadastrado no sistema com esse CPF");
        }
    }

    private void validateUniqueEmail(String email, Long currentId) {
        String normalized = email.trim();
        long count = (currentId == null)
                ? Cliente.count("LOWER(email) = LOWER(?1)", normalized)
                : Cliente.count("LOWER(email) = LOWER(?1) AND id != ?2", normalized, currentId);
        if (count > 0) {
            throw new BadRequestException("O e-mail fonecido já está em uso");
        }
    }
}
