package trabalho.ada.service;

import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.mindrot.jbcrypt.BCrypt;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import trabalho.ada.exception.BusinessException;
import trabalho.ada.model.Cliente;
import trabalho.ada.model.PageResult;
import trabalho.ada.resource.cliente.CreateClienteRequest;
import trabalho.ada.resource.cliente.UpdateClienteRequest;

@ApplicationScoped
public class ClienteService {

    @Inject
    JsonWebToken jwt;

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

        if ( !(jwt.getGroups().contains("GERENTE") ) ){
            throw new BusinessException("O perfil " + jwt.getGroups().toString() + " não tem permissão para abrir conta");
        }

        validateUniqueCPf(request.cpf(), null);
        validateUniqueEmail(request.email(), null);
        Cliente cliente = new Cliente(request.nome().trim(), request.cpf().trim(), request.email().trim(), request.senha().trim());
        cliente.setSenha(BCrypt.hashpw(cliente.getSenha(), BCrypt.gensalt(10)));
        cliente.persist();
        return cliente;
    }

    public Cliente update(Long id, UpdateClienteRequest request){

        if ( !(jwt.getGroups().contains("GERENTE") ) ){
            throw new BusinessException("O perfil " + jwt.getGroups().toString() + " não tem permissão para alterar dados de uma conta");
        }

        Cliente cliente = getRequiredCliente(id);
        validateUniqueEmail(request.email(), id);
        cliente.setNome(request.nome());
        cliente.setEmail(request.email());
        cliente.setSenha(BCrypt.hashpw(request.senha(), BCrypt.gensalt(10)));
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
